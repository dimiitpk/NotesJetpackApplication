package com.dimi.advnotes.framework.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.dimi.advnotes.util.Constants

const val CHECK_ITEM_NOTE_OWNER_COLUMN_NAME = "note_id"

@Entity(
    tableName = "check_item",
    foreignKeys = [
        ForeignKey(
            entity = NoteCacheEntity::class,
            parentColumns = ["id"],
            childColumns = [CHECK_ITEM_NOTE_OWNER_COLUMN_NAME],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(CHECK_ITEM_NOTE_OWNER_COLUMN_NAME)
    ]
)
data class CheckItemCacheEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = Constants.DEFAULT_PRIMARY_KEY,
    val body: String,
    val checked: Boolean,
    var lastUpdated: Long = 0L,

    @ColumnInfo(name = CHECK_ITEM_NOTE_OWNER_COLUMN_NAME)
    var noteOwnerId: Long = 0
)