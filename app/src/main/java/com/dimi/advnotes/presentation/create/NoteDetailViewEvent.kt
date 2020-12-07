package com.dimi.advnotes.presentation.create

sealed class NoteDetailViewEvent {

    object OpenColorPicker : NoteDetailViewEvent()

    object OpenMoreOptionsDialog : NoteDetailViewEvent()

    object OpenReminderDialog : NoteDetailViewEvent()

    object CancellingReminderDialog : NoteDetailViewEvent()

    object CopyNote : NoteDetailViewEvent()

    object ConfirmDelete : NoteDetailViewEvent()

    object NavigateBack : NoteDetailViewEvent()
}
