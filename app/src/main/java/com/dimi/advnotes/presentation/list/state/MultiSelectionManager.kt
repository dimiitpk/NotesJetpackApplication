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

    private val _pendingNotes = MutableStateFlow<List<Note>>(emptyList())

    private val _selectedNotes = MutableStateFlow<ArrayList<Note>>(ArrayList())
    val selectedNotes: List<Note>
        get() = _selectedNotes.value

    private fun enableMultiSelection() {
        _multiSelectionState.value = MultiSelectionState.Enabled(_selectedNotes.value.size)
    }

    fun disableMultiSelection() {
        _multiSelectionState.value = MultiSelectionState.Disabled
    }

    fun isMultiSelectionEnabled() = _multiSelectionState.value != MultiSelectionState.Disabled

    fun getPendingNotes(): List<Note> = _pendingNotes.value

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
        _selectedNotes.value.clear()
    }

    fun clearPendingNotes() {
        _pendingNotes.value = emptyList()
    }

    fun setPendingNotes(vararg notes: Note) {
        _pendingNotes.value = notes.toList()
    }
}