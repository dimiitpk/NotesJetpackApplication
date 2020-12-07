package com.dimi.advnotes.framework.database.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "note_entity")
data class NoteCacheEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val body: String,
    val color: Int,
    val pinned: Boolean,
    val createdAt: Date,
    val updatedAt: Date,
    @Embedded(prefix = "reminder_")
    val reminder: ReminderCacheEntity?
)