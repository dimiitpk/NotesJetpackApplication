package com.dimi.advnotes.data.interactors

import androidx.paging.PagingData
import com.dimi.advnotes.data.database.CacheDataSource
import com.dimi.advnotes.domain.model.Note
import kotlinx.coroutines.flow.Flow

class ObserveNotesUseCase(
    private val cacheDataSource: CacheDataSource
) {
    operator fun invoke(archived: Boolean): Flow<PagingData<Note>> = cacheDataSource.observeNotes(archived)
}