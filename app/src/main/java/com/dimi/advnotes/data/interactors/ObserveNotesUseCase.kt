package com.dimi.advnotes.data.interactors

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import com.dimi.advnotes.data.database.CacheDataSource
import com.dimi.advnotes.domain.model.Note
import com.dimi.advnotes.presentation.list.adapter.model.UiModel
import kotlinx.coroutines.flow.Flow

class ObserveNotesUseCase(
    private val cacheDataSource: CacheDataSource
) {
    operator fun invoke(query: String) : Flow<PagingData<Note>> = cacheDataSource.observeNotes(query)
}