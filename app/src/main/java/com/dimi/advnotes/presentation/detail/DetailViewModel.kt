package com.dimi.advnotes.presentation.detail

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dimi.advnotes.data.interactors.NoteUseCase
import com.dimi.advnotes.domain.model.CheckItem
import com.dimi.advnotes.domain.model.Note
import com.dimi.advnotes.domain.model.Reminder
import com.dimi.advnotes.presentation.common.ReminderManager
import com.dimi.advnotes.presentation.common.extensions.generateUniqueId
import com.dimi.advnotes.presentation.common.extensions.mutation
import com.dimi.advnotes.presentation.common.extensions.swap
import com.dimi.advnotes.util.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.util.*

class DetailViewModel @ViewModelInject constructor(
    private val noteUseCase: NoteUseCase,
    private val reminderManager: ReminderManager,
    private val applicationScope: CoroutineScope
) : ViewModel() {

    private val _note: MutableLiveData<Note> = MutableLiveData()
    val note: LiveData<Note> = _note

    // one time event -> MutableSharedFlow with 0 replays
    private val _event = MutableSharedFlow<DetailViewEvent>()
    val event = _event.asSharedFlow()

    // initial Note to check for changes is update needed
    private var initialNote: Note? = null

    // always use checkItems to check for live value, note.checkItems will give initial value
    private val _checkItems = MutableStateFlow<List<CheckItem>>(emptyList())
    val checkItems = _checkItems.asLiveData()

    fun loadNote(noteId: Long) {
        if (noteId != Constants.INVALID_PRIMARY_KEY) {
            if (_note.value != null) return // stop loading on rotation
            viewModelScope.launch {
                try {
                    _note.value = noteUseCase.fetchSingleNote(noteId)
                        .also { fetchedNote ->
                            val listCopy = fetchedNote.checkItems.map {
                                it.copy()
                            }
                            _checkItems.value = listCopy
                            val reminderCopy =
                                fetchedNote.reminder.copy(repeating = fetchedNote.reminder.repeating?.copy())
                            initialNote = fetchedNote.copy(reminder = reminderCopy)
                        }
                } catch (e: Exception) {
                    _note.value = Note()
                }
            }
        } else
            _note.value = Note()
    }

    fun deleteReminder() {
        _note.mutation {
            it?.let { note ->
                reminderManager.cancelReminder(note.reminder)
            }
        }
    }

    fun setReminder(reminder: Reminder) {
        _note.mutation {
            it?.let { note ->
                reminder.noteId = note.id
                reminder.requestCode = note.createdAt.generateUniqueId()
                note.reminder = reminder
                reminderManager.setReminder(note.reminder)
            }
        }
    }

    fun setNoteColor(color: Int) {
        _note.mutation {
            it?.color = color
        }
    }

    fun saveNote() {
        saveNoteInDb()
        viewModelScope.launch {
            _event.emit(DetailViewEvent.NavigateBack)
        }
    }

    private fun saveNoteInDb() {
        _note.value?.let { note ->
            note.checkItems = _checkItems.value
            if (isNoteChangedFromInitial()) {
                changeLastUpdateTime()
            } else
                return

            applicationScope.launch {
                noteUseCase.insertOrUpdateNote(note)
            }
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

    fun archiveNote() {
        archiveNoteDb()
        viewModelScope.launch {
            _event.emit(DetailViewEvent.NavigateBack)
        }
    }

    private fun archiveNoteDb() {
        _note.value?.let { note ->
            applicationScope.launch {
                noteUseCase.updateNotes(listOf(note.toggleArchive()))
            }
        }
    }

    fun deleteConfirmed() {
        deleteNoteFromDb()
        viewModelScope.launch {
            _event.emit(DetailViewEvent.NavigateBack)
        }
    }

    private fun deleteNoteFromDb() {
        _note.value?.let {
            applicationScope.launch {
                noteUseCase.deleteNote(it)
            }
        }
    }

    fun copyNote() {
        _note.mutation { note ->
            note?.let {
                if (note.copyNote()) {
                    _checkItems.value.onEach {
                        it.id = 0L
                        it.lastUpdated = 0L
                    }
                    saveNoteInDb()
                    viewModelScope.launch {
                        _event.emit(DetailViewEvent.CopyNote)
                    }
                }
            }
        }
    }

    fun removeCheckItem(checkItem: CheckItem) {
        _checkItems.value = _checkItems.value.toMutableList().apply {
            remove(checkItem)
        }
    }

    fun addNewCheckItem() {
        _checkItems.value = _checkItems.value.toMutableList().apply {
            add(CheckItem(focus = true))
        }
    }

    fun removeAllCheckItems() {
        _checkItems.value = emptyList()
    }

    fun unCheckCheckItems() {
        _checkItems.value = _checkItems.value.map {
            if (it.checked)
                it.copy(checked = false)
            else it
        }
    }

    fun swapCheckItems(indexFrom: Int, indexTo: Int) {
        _checkItems.value = _checkItems.value.swap(indexFrom, indexTo)
    }

    fun removeCheckedCheckItems() {
        val list = _checkItems.value.toMutableList().apply {
            removeAll { it.checked }
        }
        _checkItems.value = list
    }

    fun clearReminder() {
//        if (note.reminder.completed)
//            note.reminder.clearReminder()
    }

    fun pinOrUnpin() {
        _note.mutation {
            it?.togglePinned()
        }
    }
}