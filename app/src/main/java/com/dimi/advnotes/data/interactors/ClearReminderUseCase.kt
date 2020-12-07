package com.dimi.advnotes.data.interactors

import com.dimi.advnotes.data.database.CacheDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ClearReminderUseCase(
    private val cacheDataSource: CacheDataSource
) {
    suspend operator fun invoke(id: Long) {
        withContext(Dispatchers.IO) {
            cacheDataSource.clearReminder(id)
        }
    }
}