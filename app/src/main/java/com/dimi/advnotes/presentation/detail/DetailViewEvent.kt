package com.dimi.advnotes.presentation.detail

sealed class DetailViewEvent {

    object CancellingReminderDialog : DetailViewEvent()

    object CopyNote : DetailViewEvent()

    object NavigateBack : DetailViewEvent()
}