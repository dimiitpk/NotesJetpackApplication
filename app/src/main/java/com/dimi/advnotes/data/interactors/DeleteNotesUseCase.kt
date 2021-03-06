package com.dimi.advnotes.data.interactors

import com.dimi.advnotes.data.database.CacheDataSource
import com.dimi.advnotes.domain.model.Note
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DeleteNotesUseCase(
    private val cacheDataSource: CacheDataSource
) {
    suspend operator fun invoke(notes: List<Note>) {
        withContext(Dispatchers.IO) {
            cacheDataSource.delete(notes)
        }
    }
}