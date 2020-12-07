package com.dimi.advnotes.data.interactors

import com.dimi.advnotes.data.database.CacheDataSource

class ObserveRemindersUseCase(
    private val cacheDataSource: CacheDataSource
) {
    operator fun invoke() = cacheDataSource.observeReminderValue()
}