package com.dimi.advnotes.data.interactors

import com.dimi.advnotes.data.database.CacheDataSource
import com.dimi.advnotes.domain.model.CheckItem
import com.dimi.advnotes.domain.model.Note
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DeleteCheckItemUseCase(
    private val cacheDataSource: CacheDataSource
) {
    suspend operator fun invoke(vararg checkItem: CheckItem) {
        withContext(Dispatchers.IO) {
            cacheDataSource.deleteCheckItems(checkItems = checkItem.asList())
        }
    }
}