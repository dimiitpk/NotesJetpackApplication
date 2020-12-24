package com.dimi.advnotes.data.interactors

import com.dimi.advnotes.data.database.CacheDataSource
import com.dimi.advnotes.domain.model.Note

class InsertOrUpdateNoteUseCase(
    private val cacheDataSource: CacheDataSource
) {
    suspend operator fun invoke(note: Note): Long {
        return cacheDataSource.insertOrUpdateIfExist(note)
    }
}