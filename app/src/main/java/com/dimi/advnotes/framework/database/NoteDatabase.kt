package com.dimi.advnotes.framework.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.dimi.advnotes.framework.database.converters.TimestampConverter
import com.dimi.advnotes.framework.database.dao.CheckItemDao
import com.dimi.advnotes.framework.database.dao.NoteDao
import com.dimi.advnotes.framework.database.model.CheckItemCacheEntity
import com.dimi.advnotes.framework.database.model.NoteCacheEntity

@Database(
    entities = [
        NoteCacheEntity::class,
        CheckItemCacheEntity::class
    ],
    version = 30,
    exportSchema = false
)
@TypeConverters(TimestampConverter::class)
abstract class NoteDatabase : RoomDatabase() {
    abstract fun getNoteDao(): NoteDao
    abstract fun getCheckItemDao(): CheckItemDao

    companion object {
        const val DATABASE_NAME = "note_db"
    }
}