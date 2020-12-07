package com.dimi.advnotes.presentation.list.state

sealed class MultiSelectionState {
    data class Enabled(val size: Int) : MultiSelectionState()
    object Disabled : MultiSelectionState()
}
