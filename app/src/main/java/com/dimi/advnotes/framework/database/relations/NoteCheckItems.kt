package com.dimi.advnotes.framework.database.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.dimi.advnotes.framework.database.model.CHECK_ITEM_NOTE_OWNER_COLUMN_NAME
import com.dimi.advnotes.framework.database.model.CheckItemCacheEntity
import com.dimi.advnotes.framework.database.model.NoteCacheEntity

data class NoteCheckItems(

    @Embedded val note: NoteCacheEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = CHECK_ITEM_NOTE_OWNER_COLUMN_NAME
    )
    val checkItems: List<CheckItemCacheEntity> = emptyList()
)