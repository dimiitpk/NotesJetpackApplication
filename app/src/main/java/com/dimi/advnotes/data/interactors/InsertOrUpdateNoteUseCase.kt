package com.dimi.advnotes.data.interactors

import com.dimi.advnotes.data.database.CacheDataSource
import com.dimi.advnotes.domain.model.Note
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class InsertOrUpdateNoteUseCase(
    private val cacheDataSource: CacheDataSource
) {
    suspend operator fun invoke(note: Note): Long {
//        return withContext(Dispatchers.IO) {
//            cacheDataSource.insertOrUpdateIfExist(note)
//        }
        return cacheDataSource.insertOrUpdateIfExist(note)
    }
}