package com.dimi.advnotes.presentation.reminder

import com.dimi.advnotes.domain.model.Reminder

sealed class NoteReminderViewEvent {

    object OpenTimePicker : NoteReminderViewEvent()

    data class OpenDatePicker(val repeat: Boolean = false) : NoteReminderViewEvent()

    object DismissDialog : NoteReminderViewEvent()

    data class ConfirmReminder(
        val reminder: Reminder
    ) : NoteReminderViewEvent()

    object PastTimeError: NoteReminderViewEvent()
}
