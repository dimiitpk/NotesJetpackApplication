package com.dimi.advnotes.data.database

import com.dimi.advnotes.domain.model.Note

class CacheDataSourceImpl(
    private val dataSource: CacheDataSource
) : CacheDataSource {

    override suspend fun insert(note: Note) = dataSource.insert(note)

    override suspend fun insert(notes: List<Note>) = dataSource.insert(notes)

    override suspend fun insertOrUpdateIfExist(note: Note) = dataSource.insertOrUpdateIfExist(note)

    override suspend fun delete(note: Note) = dataSource.delete(note)

    override suspend fun delete(notes: List<Note>) = dataSource.delete(notes)

    override suspend fun update(note: Note) = dataSource.delete(note)

    override suspend fun update(notes: List<Note>) = dataSource.update(notes)

    override suspend fun fetchOne(id: Long) = dataSource.fetchOne(id)

    override suspend fun fetchAll() = dataSource.fetchAll()

    override fun observeNotes(archived: Boolean) = dataSource.observeNotes(archived)

    override fun observeReminderValue() = dataSource.observeReminderValue()

    override suspend fun clearReminder(noteId: Long) = dataSource.clearReminder(noteId)
}