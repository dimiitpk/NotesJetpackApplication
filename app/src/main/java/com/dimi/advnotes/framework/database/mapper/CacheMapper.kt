package com.dimi.advnotes.framework.database.mapper

import com.dimi.advnotes.domain.model.Note
import com.dimi.advnotes.domain.model.Reminder
import com.dimi.advnotes.domain.util.EntityMapper
import com.dimi.advnotes.framework.database.model.CheckItemCacheEntity
import com.dimi.advnotes.framework.database.model.NoteCacheEntity
import com.dimi.advnotes.framework.database.model.ReminderCacheEntity
import com.dimi.advnotes.presentation.common.extensions.generateUniqueId
import java.util.*
import javax.inject.Inject

class CacheMapper
@Inject
constructor(
    val checkItemsCacheMapper: CheckItemCacheMapper,
    private val reminderCacheMapper: ReminderCacheMapper
) : EntityMapper<NoteCacheEntity, Note> {

    fun mapToEntityList(domainModels: List<Note>) = domainModels.map { domainModel ->
        mapToEntity(domainModel)
    }

    fun mapFromEntityList(entities: List<NoteCacheEntity>) = entities.map { entity ->
        mapFromEntity(entity)
    }

    fun mapFromEntityWithCheckItems(
        entity: NoteCacheEntity,
        checkItems: List<CheckItemCacheEntity>
    ) : Note {
        val reminderce = reminderCacheMapper.createReminder(
            entity.id,
            entity.createdAt,
            entity.reminder
        )
        println("POVUKLO TU ${reminderce}")
        return Note(
            id = entity.id,
            title = entity.title,
            body = entity.body,
            color = entity.color,
            pinned = entity.pinned,
            archived = entity.archived,
            reminder = reminderce,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
            checkItems = checkItemsCacheMapper.mapFromEntityList(checkItems)
        )
    }


    override fun mapFromEntity(entity: NoteCacheEntity): Note =
        Note(
            id = entity.id,
            title = entity.title,
            body = entity.body,
            color = entity.color,
            archived = entity.archived,
            pinned = entity.pinned,
            reminder = reminderCacheMapper.createReminder(
                entity.id,
                entity.createdAt,
                entity.reminder
            ),
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )

    override fun mapToEntity(domainModel: Note): NoteCacheEntity =
        NoteCacheEntity(
            id = domainModel.id,
            title = domainModel.title,
            body = domainModel.body,
            color = domainModel.color,
            archived = domainModel.archived,
            pinned = domainModel.pinned,
            reminder = reminderCacheMapper.mapToEntity(domainModel.reminder),
            createdAt = domainModel.createdAt,
            updatedAt = domainModel.updatedAt
        )
}