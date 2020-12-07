package com.dimi.advnotes.presentation.create.dialogs

sealed class NoteReminderViewEvent {

    object Default : NoteReminderViewEvent()

    object OpenTimePicker : NoteReminderViewEvent()

    object OpenDatePicker : NoteReminderViewEvent()

    object DismissDialog : NoteReminderViewEvent()
}
