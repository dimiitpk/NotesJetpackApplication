package com.dimi.advnotes.presentation.list

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import com.dimi.advnotes.data.interactors.NoteUseCase
import com.dimi.advnotes.domain.model.Note
import com.dimi.advnotes.presentation.common.LAYOUT_MODE_LINEAR
import com.dimi.advnotes.presentation.common.LAYOUT_MODE_STAGGERED_GRID
import com.dimi.advnotes.presentation.common.ReminderManager
import com.dimi.advnotes.presentation.common.UserPreferencesRepository
import com.dimi.advnotes.presentation.list.adapter.model.UiModel
import com.dimi.advnotes.presentation.list.state.MultiSelectionManager
import com.dimi.advnotes.util.Constants.SEARCH_QUERY_DEBOUNCE_TIME
import com.dimi.advnotes.util.Constants.OTHERS_NOTES_NAME_TAG
import com.dimi.advnotes.util.Constants.PINNED_NOTES_NAME_TAG
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@FlowPreview
@ExperimentalCoroutinesApi
class ListViewModel @ViewModelInject constructor(
    private val noteUseCase: NoteUseCase,
    private val reminderManager: ReminderManager,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val multiSelectionManager: MultiSelectionManager,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private var _event = MutableSharedFlow<ListViewEvent>()
    val event = _event.asSharedFlow()

    val userPreferences = userPreferencesRepository.userPreferencesFlow.asLiveData()

    val multiSelectionState = multiSelectionManager.multiSelectionState

    init {
        checkIsThereAnyAlarm()
    }

    private fun checkIsThereAnyAlarm() {
        viewModelScope.launch {
            noteUseCase.observeReminders().collect {
                if (it == null) {
                    reminderManager.disableBootReceiver()
                }
            }
        }
    }

    private val _listType = MutableStateFlow(ListType.NOTES)
    fun setListType(listType: ListType) {
        _listType.value = listType
    }

    val noteList: Flow<PagingData<UiModel>> =
        _listType
            .debounce(SEARCH_QUERY_DEBOUNCE_TIME)
            .flatMapMerge { listType ->
                noteUseCase.observeNotes(listType.isArchive())
                    .map { pagingData ->
                        pagingData.map {
                            UiModel.NoteModel(it)
                        }
                            .insertSeparators { before, after ->
                                return@insertSeparators when {
                                    before == null ->
                                        if (after != null && after.note.pinned)
                                            UiModel.SeparatorModel(PINNED_NOTES_NAME_TAG)
                                        else
                                            null
                                    after == null -> null
                                    shouldSeparateNotes(before, after) ->
                                        UiModel.SeparatorModel(OTHERS_NOTES_NAME_TAG)
                                    else -> null
                                }
                            }
                    }
            }.cachedIn(viewModelScope)

    private fun shouldSeparateNotes(before: UiModel.NoteModel, after: UiModel.NoteModel) =
        before.note.pinned && !after.note.pinned

    fun setPendingNote(note: Note) {
        multiSelectionManager.setPendingNotes(note)
    }

    /**
     * Archive
     */
    fun archiveNote(note: Note) {
        viewModelScope.launch {
            setPendingNote(note)
            noteUseCase.updateNotes(listOf(note.toggleArchive()))
        }
    }

    fun archiveSelectedNotes() {
        viewModelScope.launch {
            multiSelectionManager.setPendingNotes(*getSelectedNotes().toTypedArray())
            val notes = getSelectedNotes().map {
                it.toggleArchive()
            }
            noteUseCase.updateNotes(notes)
        }
        disableMultiSelectionState()
    }

    fun undoPendingArchiveNotes() {
        val temporaryArchivedNotes = multiSelectionManager.getPendingNotes().toList()
        viewModelScope.launch {
            noteUseCase.updateNotes(temporaryArchivedNotes)
        }
        multiSelectionManager.clearPendingNotes()
    }

    /**
     * Delete
     */
    fun deleteSelectedNotes() {
        multiSelectionManager.setPendingNotes(*getSelectedNotes().toTypedArray())
        viewModelScope.launch {
            noteUseCase.deleteNotes(notes = getSelectedNotes().toList())
        }
        disableMultiSelectionState()
    }

    fun undoPendingDeleteNotes() {
        val temporaryDeletedNotes = multiSelectionManager.getPendingNotes().toList()
        viewModelScope.launch {
            noteUseCase.insertAllNotes(*temporaryDeletedNotes.toTypedArray())
        }
        multiSelectionManager.clearPendingNotes()
    }

    fun clearPendingNotes() {
        multiSelectionManager.clearPendingNotes()
    }

    private fun disableMultiSelectionState() {
        multiSelectionManager.disableMultiSelection()
    }

    fun disableMultiSelectionStateAndClearSelectedNotes() {
        clearSelectedNotes()
        disableMultiSelectionState()
    }

    private fun clearSelectedNotes() {
        unSelectAllSelectedNotes()
        multiSelectionManager.clearSelectedNotes()
    }

    private fun unSelectAllSelectedNotes() {
        getSelectedNotes().forEach {
            it.unSelect()
        }
    }

    fun updateAndSaveColorSelectedNotes(color: Int) {
        selectedNotesChangeColor(color)
        viewModelScope.launch {
            _event.emit(ListViewEvent.RefreshNotes)
            val notesToUpdate = getSelectedNotes().toList()
            noteUseCase.updateNotes(notesToUpdate)
        }
        disableMultiSelectionState()
    }

    private fun selectedNotesChangeColor(color: Int) {
        getSelectedNotes().forEach {
            it.color = color
        }
    }

    fun selectOrUnSelectNote(note: Note) {
        note.toggleSelection()
        multiSelectionManager.addOrRemoveIfExist(note)
    }

    fun isMultiSelectionEnabled() =
        multiSelectionManager.isMultiSelectionEnabled()

    fun getSelectedNotes() = multiSelectionManager.selectedNotes

    fun isSelectedNotesListEmpty() = multiSelectionManager.selectedNotes.isEmpty()

    fun changeViewLayout() {
        viewModelScope.launch {
            userPreferencesRepository
                .userPreferencesFlow.firstOrNull()?.let { preferences ->
                    val layoutMode = if (preferences.layoutMode == LAYOUT_MODE_LINEAR)
                        LAYOUT_MODE_STAGGERED_GRID
                    else
                        LAYOUT_MODE_LINEAR

                    userPreferencesRepository.saveLayoutMode(layoutMode)
                }
        }
    }
}