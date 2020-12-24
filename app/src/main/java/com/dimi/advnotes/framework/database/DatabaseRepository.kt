package com.dimi.advnotes.framework.database

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import androidx.room.withTransaction
import com.dimi.advnotes.DISCARD_BLANK_NOTE_EXCEPTION_MESSAGE
import com.dimi.advnotes.data.database.CacheDataSource
import com.dimi.advnotes.domain.model.Note
import com.dimi.advnotes.framework.database.mapper.CacheMapper
import com.dimi.advnotes.util.Constants.INVALID_PRIMARY_KEY
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DatabaseRepository(
    private val database: NoteDatabase,
    private val cacheMapper: CacheMapper
) : CacheDataSource {

    private val noteDao = database.getNoteDao()
    private val checkItemDao = database.getCheckItemDao()

    override suspend fun insert(note: Note) = noteDao.insert(cacheMapper.mapToEntity(note))

    override suspend fun insert(notes: List<Note>) = database.withTransaction {

        val longList = mutableListOf<Long>()
        for (note in notes) {
            longList.add(insertOrUpdateIfExist(note))
        }
        longList
    }

    override suspend fun insertOrUpdateIfExist(note: Note): Long {

        val updateTime = System.currentTimeMillis()
        return database.withTransaction {

            if (!note.isNoteValid())
                throw Exception(DISCARD_BLANK_NOTE_EXCEPTION_MESSAGE)

            noteDao.upsert(cacheMapper.mapToEntity(note)).let { long ->
                if (long != INVALID_PRIMARY_KEY)
                    note.id = long

                val checkItems = cacheMapper.checkItemsCacheMapper.mapToEntityList(
                    note.checkItems,
                    note.id,
                    updateTime
                )
                checkItemDao.upsert(
                    checkItems,
                    noteId = note.id,
                    lastUpdated = updateTime
                )
                return@withTransaction long
            }
        }
    }

    override suspend fun delete(note: Note) = noteDao.delete(cacheMapper.mapToEntity(note))

    override suspend fun delete(notes: List<Note>) =
        noteDao.delete(cacheMapper.mapToEntityList(notes))

    override suspend fun update(note: Note) = noteDao.update(cacheMapper.mapToEntity(note))
    override suspend fun update(notes: List<Note>) {
        noteDao.update(cacheMapper.mapToEntityList(notes))
    }

    override suspend fun fetchOne(id: Long): Note {
        val noteWithCheckItems = noteDao.fetchOneNote(id)
        return cacheMapper.mapFromEntityWithCheckItems(
            noteWithCheckItems.note,
            noteWithCheckItems.checkItems.sortedBy { it.order }
        )
    }

    override suspend fun fetchAll() = cacheMapper.mapFromEntityList(noteDao.fetchAll())

    override fun observeNotes(archived: Boolean): Flow<PagingData<Note>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false,
                maxSize = 60
            )
        ) {
            noteDao.observeNotes(if (archived) 1 else 0)
        }.flow
            .map { pagingData ->
                pagingData.map {
                    cacheMapper.mapFromEntityWithCheckItems(
                        it.note,
                        it.checkItems.sortedBy { checkItem -> checkItem.order })
                }
            }
    }

    override fun observeReminderValue() = noteDao.observeReminderValue()

    override suspend fun clearReminder(noteId: Long) {
        noteDao.clearReminder(noteId)
    }
}