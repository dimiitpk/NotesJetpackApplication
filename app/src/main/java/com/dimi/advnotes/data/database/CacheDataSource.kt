package com.dimi.advnotes.data.database

import androidx.paging.PagingData
import com.dimi.advnotes.domain.model.Note
import kotlinx.coroutines.flow.Flow

interface CacheDataSource {
    suspend fun insert(note: Note): Long
    suspend fun insertOrUpdateIfExist(note: Note): Long
    suspend fun insert(notes: List<Note>): List<Long>
    suspend fun delete(note: Note): Int
    suspend fun delete(notes: List<Note>)
    suspend fun update(note: Note): Int
    suspend fun update(notes: List<Note>)

    suspend fun fetchOne(id: Long): Note
    suspend fun fetchAll(): List<Note>

    fun observeNotes(archived: Boolean): Flow<PagingData<Note>>

    fun observeReminderValue(): Flow<Long?>
    suspend fun clearReminder(noteId: Long)
}