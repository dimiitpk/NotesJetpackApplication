package com.dimi.advnotes.presentation.list.adapter.model

import com.dimi.advnotes.domain.model.Note

sealed class UiModel {
    data class NoteModel(val note: Note) : UiModel()

    class SeparatorModel(val description: String) : UiModel()
}