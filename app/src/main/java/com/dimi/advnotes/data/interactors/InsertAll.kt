package com.dimi.advnotes.data.interactors

import com.dimi.advnotes.data.database.CacheDataSource
import com.dimi.advnotes.domain.model.Note
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class InsertAll(
    private val cacheDataSource: CacheDataSource
) {
    suspend operator fun invoke(vararg notes: Note) {
        withContext(Dispatchers.IO) {
            cacheDataSource.insert(notes.toList())
        }
    }
}