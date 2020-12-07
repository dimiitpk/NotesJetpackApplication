package com.dimi.advnotes.data.interactors

import com.dimi.advnotes.data.database.CacheDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FetchAllNotesUseCase(
    private val cacheDataSource: CacheDataSource
) {
    suspend operator fun invoke() =
        withContext(Dispatchers.IO) {
            cacheDataSource.fetchAll()
        }
}