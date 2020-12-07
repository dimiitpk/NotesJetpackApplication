package com.dimi.advnotes.presentation.list.state

import com.dimi.advnotes.domain.model.Note
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@ActivityRetainedScoped
class MultiSelectionManager @Inject constructor() {

    private val _multiSelectionState =
        MutableStateFlow<MultiSelectionState>(MultiSelectionState.Disabled)
    val multiSelectionState: StateFlow<MultiSelectionState>
        get() = _multiSelectionState

    private val _lastSelectedNotes = MutableStateFlow<List<Note>>(emptyList())

    private val _selectedNotes = MutableStateFlow<ArrayList<Note>>(ArrayList())
    val selectedNotes: StateFlow<ArrayList<Note>>
        get() = _selectedNotes

    private fun enableMultiSelection() {
        _multiSelectionState.value = MultiSelectionState.Enabled(_selectedNotes.value.size)
    }

    fun disableMultiSelection() {
        if (_multiSelectionState.value != MultiSelectionState.Disabled)
            _multiSelectionState.value = MultiSelectionState.Disabled
    }

    fun isMultiSelectionEnabled() = _multiSelectionState.value != MultiSelectionState.Disabled

    fun getSelectedNotes(): List<Note> = _selectedNotes.value

    fun getLastSelectedNotes(): List<Note> = _lastSelectedNotes.value

    fun isSelectedNotesEmpty() = _selectedNotes.value.size < 1

    fun addOrRemoveIfExist(note: Note) {

        val list = _selectedNotes.value

        if (list.contains(note)) {
            list.remove(note)
        } else {
            list.add(note)
        }

        if (list.size == 0)
            disableMultiSelection()
        else
            enableMultiSelection()
    }

    fun clearSelectedNotes() {
        _lastSelectedNotes.value = _selectedNotes.value.toList()
        _selectedNotes.value.clear()
    }

    fun clearLastSelectedNotes() {
        _lastSelectedNotes.value = emptyList()
    }

}