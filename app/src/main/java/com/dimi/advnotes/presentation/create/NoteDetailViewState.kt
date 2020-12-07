package com.dimi.advnotes.presentation.create

sealed class NoteDetailViewState {

    object Initial: NoteDetailViewState()

    object NonFocusable : NoteDetailViewState()

    fun isFocusable() = this !is NonFocusable
}