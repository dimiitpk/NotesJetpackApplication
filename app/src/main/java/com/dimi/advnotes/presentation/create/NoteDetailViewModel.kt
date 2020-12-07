package com.dimi.advnotes.presentation.create

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dimi.advnotes.data.interactors.NoteUseCase
import com.dimi.advnotes.domain.model.CheckItem
import com.dimi.advnotes.domain.model.Note
import com.dimi.advnotes.presentation.common.ReminderManager
import com.dimi.advnotes.presentation.common.extensions.mutation
import com.dimi.advnotes.presentation.create.dialogs.NoteReminderViewEvent
import com.dimi.advnotes.presentation.create.mappers.ObservableReminderMapper
import com.dimi.advnotes.presentation.create.model.ObservableReminder
import com.dimi.advnotes.util.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.util.*


class NoteDetailViewModel @ViewModelInject constructor(
    private val observableReminderMapper: ObservableReminderMapper,
    private val noteUseCase: NoteUseCase,
    private val reminderManager: ReminderManager,
    private val applicationScope: CoroutineScope
) : ViewModel() {

    private val _note: MutableLiveData<Note?> = MutableLiveData(null)
    val note: LiveData<Note?> = _note

    private val _event = MutableSharedFlow<NoteDetailViewEvent>()
    val event = _event.asSharedFlow()

    private val _state = MutableStateFlow<NoteDetailViewState>(NoteDetailViewState.Initial)
    val state: LiveData<NoteDetailViewState>
        get() = _state.asLiveData()

    private val _reminderEvent = MutableLiveData<NoteReminderViewEvent>()
    val reminderEvent: LiveData<NoteReminderViewEvent>
        get() = _reminderEvent

    private var initialNote: Note? = null

    var reminder = ObservableReminder()

    fun loadNote(noteId: Long) {
        if (noteId != Constants.INVALID_PRIMARY_KEY)
            viewModelScope.launch {
                try {
                    _note.value = noteUseCase.fetchSingleNote(noteId)
                        .also { fetchedNote ->
                            val listCopy = fetchedNote.checkItems.map {
                                it.copy()
                            }
                            initialNote = fetchedNote.copy(checkItems = listCopy)
                            reminder = observableReminderMapper.mapToEntity(fetchedNote.reminder)
                        }
                } catch (e: Exception) {
                    _note.value = Note()
                }
            }
        else
            _note.value = Note()
    }

    fun openColorPicker() {
        viewModelScope.launch {
            _event.emit(NoteDetailViewEvent.OpenColorPicker)
        }
    }

    fun setNoteColor(color: Int) {
        _note.mutation {
            it?.color = color
        }
    }

    fun saveNote() {
        insertNoteInDb()
        viewModelScope.launch {
            _event.emit(NoteDetailViewEvent.NavigateBack)
        }
    }

    private fun insertNoteInDb() {
        _note.value?.let { note ->
            if (isNoteChangedFromInitial()) {
                changeLastUpdateTime()
            }
            else
                return
            applicationScope.launch {
                noteUseCase.insertOrUpdateNoteUseCase(note)
            }

            //reminderManager.setReminder(note.reminder)
        }
    }

    private fun isNoteChangedFromInitial(): Boolean {
        return _note.value?.let { note ->
            initialNote?.let { initNote ->
                note != initNote
            } ?: true
        } ?: false
    }

    private fun changeLastUpdateTime() {
        _note.value?.updatedAt = Date()
    }

    fun deleteConfirmed() {
        deleteNoteFromDb()
        viewModelScope.launch {
            _event.emit(NoteDetailViewEvent.NavigateBack)
        }
    }

    private fun deleteNoteFromDb() {
        _note.value?.let {
            applicationScope.launch {
                noteUseCase.deleteNoteUseCase(it)
            }
        }
    }

    fun copyNote() {
        _note.value?.let { note ->
            if (note.copyNote())
                viewModelScope.launch {
                    _event.emit(NoteDetailViewEvent.CopyNote)
                }
        }
    }

    fun deleteNote() {
        viewModelScope.launch {
            _event.emit(NoteDetailViewEvent.ConfirmDelete)
        }
    }

    fun openMoreOptionsDialog() {
        viewModelScope.launch {
            _event.emit(NoteDetailViewEvent.OpenMoreOptionsDialog)
        }
    }

    fun removeCheckItem(checkItem: CheckItem) {
        _note.mutation {
            it?.removeCheckItem(checkItem)
        }
    }

    fun addNewCheckItem() {
        _note.mutation {
            it?.addCheckItem()
        }.let {
            _state.value = NoteDetailViewState.NonFocusable
        }
    }

    fun removeAllCheckItems() {
        _note.mutation {
            it?.checkItems = emptyList()
        }
    }

    fun clearReminder() {
//        if (note.reminder.completed)
//            note.reminder.clearReminder()
    }

    fun openReminderDialog() {
        viewModelScope.launch {
            _event.emit(NoteDetailViewEvent.OpenReminderDialog)
        }
    }

    fun setInitialViewState() {
        _state.value = NoteDetailViewState.Initial
    }

    fun openTimePicker() {
        _reminderEvent.postValue(NoteReminderViewEvent.OpenTimePicker)
    }

    fun openDatePicker() {
        _reminderEvent.postValue(NoteReminderViewEvent.OpenDatePicker)
    }

    fun dismissDialog() {
        _note.value?.let { note ->
            _reminderEvent.postValue(NoteReminderViewEvent.DismissDialog)
            note.reminder.let { initialReminder ->
                reminder = observableReminderMapper.mapToEntity(initialReminder)
            }
        }
    }

    fun confirmReminder() {
        _note.value?.let { note ->
            _reminderEvent.postValue(NoteReminderViewEvent.DismissDialog)
            note.reminder = observableReminderMapper.mapFromEntity(reminder)
        }
    }

    fun resetReminder() {
        reminder.reset()
    }

    fun getReminderCurrentTime() = reminder.calendar

    fun setReminderTime(calendar: Calendar) {
        reminder.setTime(calendar)
    }

    fun setReminderDate(calendar: Calendar) {
        reminder.setDate(calendar)
    }

    fun setDefaultView() {
        _reminderEvent.postValue(NoteReminderViewEvent.Default)
    }

    fun pinOrUnpin() {
        _note.mutation {
            it?.togglePinned()
        }
    }
}