package com.dimi.advnotes.presentation.list

import com.dimi.advnotes.domain.model.Note

/**
 * Different interaction events for [NoteListFragment].
 */
sealed class NoteListViewEvent {

    object OpenCreateNoteView : NoteListViewEvent()

    object RefreshNotes : NoteListViewEvent()

    data class OpenEditNoteView(val note: Note, val position: Int) : NoteListViewEvent()
}
