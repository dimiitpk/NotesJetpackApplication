package com.dimi.advnotes.framework.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.dimi.advnotes.framework.database.model.CheckItemCacheEntity
import com.dimi.advnotes.util.Constants
import kotlinx.coroutines.flow.Flow

@Dao
interface CheckItemDao : BaseDao<CheckItemCacheEntity> {

    /**
     * Check if items exist, if exist update them,
     * if not insert them
     */
    @Transaction
    suspend fun upsert(items: List<CheckItemCacheEntity>, lastUpdated: Long, noteId: Long) {
        insert(items).let { returnedValues ->
            returnedValues.forEachIndexed { index, l ->
                if (l == Constants.INVALID_PRIMARY_KEY) {
                    update(items[index])
                }
            }.let {
                deleteUnused(lastUpdated, noteId)
            }
        }
    }

    @Query(
        """
        DELETE FROM check_item
        WHERE lastUpdated != :lastUpdated 
        AND note_id = :noteId
    """
    )
    suspend fun deleteUnused(lastUpdated: Long, noteId: Long)

    @Query(
        """
        SELECT * FROM check_item
        WHERE note_id = :id
        ORDER BY order_ ASC
    """
    )
    fun observeCheckItemsByNote(id: Long): Flow<List<CheckItemCacheEntity>>
}