package com.dimi.advnotes.framework.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.dimi.advnotes.framework.database.model.NoteCacheEntity
import com.dimi.advnotes.framework.database.relations.NoteCheckItems
import com.dimi.advnotes.util.Constants.INVALID_PRIMARY_KEY
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao : BaseDao<NoteCacheEntity> {

    /**
     * Check if item exist, if exist update it,
     * if not insert it
     */
    @Transaction
    suspend fun upsert(item: NoteCacheEntity): Long {
        insert(item).let {
            if (it == INVALID_PRIMARY_KEY)
                update(item)
            return it
        }
    }

//    @Transaction
//    suspend fun upsert(items: List<NoteCacheEntity>): List<Long> {
//        val longList = mutableListOf<Long>()
//        for (item in items) {
//            val returnLong = insert(item).let {
//                if (it == INVALID_PRIMARY_KEY)
//                    update(item)
//                return@let it
//            }
//            longList.add(returnLong)
//        }
//        return longList
//    }

    @Transaction
    @Query(
        """
        SELECT * FROM note_entity WHERE id = :id
    """
    )
    suspend fun fetchOneNote(id: Long): NoteCheckItems

    @Query(
        """
        SELECT * FROM note_entity
    """
    )
    suspend fun fetchAll(): List<NoteCacheEntity>

    @Query(
        """
        UPDATE note_entity SET reminder_timeInMillis = NULL WHERE id = :id
    """
    )
    suspend fun clearReminder(id: Long)

    @Query(
        """
        SELECT SUM(reminder_timeInMillis) FROM note_entity
    """
    )
    fun observeReminderValue(): Flow<Long?>

    @Transaction
    @Query(
        """SELECT * FROM note_entity
            WHERE body LIKE '%' || :query || '%'
            OR title LIKE '%' || :query || '%'
        ORDER BY pinned DESC, createdAt DESC
    """
    )
    fun observeNotes(query: String): PagingSource<Int, NoteCheckItems>

    @Transaction
    @Query(
        """
        SELECT * FROM note_entity
        WHERE body LIKE '%' || :query || '%'
            OR title LIKE '%' || :query || '%'
        ORDER BY pinned DESC, createdAt DESC
        LIMIT :pageSize OFFSET ((:page-'1')*:pageSize)
    """
    )
    suspend fun observeNotes2(query: String, page: Int, pageSize: Int): List<NoteCheckItems>
}