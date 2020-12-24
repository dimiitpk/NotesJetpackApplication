package com.dimi.advnotes.presentation.list

/**
 * Different interaction events for [ListFragment].
 */
sealed class ListViewEvent {

    object RefreshNotes : ListViewEvent()
}
