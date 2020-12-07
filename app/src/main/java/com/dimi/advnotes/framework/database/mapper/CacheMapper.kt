package com.dimi.advnotes.framework.database.mapper

import com.dimi.advnotes.domain.model.Note
import com.dimi.advnotes.domain.model.Reminder
import com.dimi.advnotes.domain.util.EntityMapper
import com.dimi.advnotes.framework.database.model.CheckItemCacheEntity
import com.dimi.advnotes.framework.database.model.NoteCacheEntity
import com.dimi.advnotes.framework.database.model.ReminderCacheEntity
import com.dimi.advnotes.framework.database.relations.NoteCheckItems
import com.dimi.advnotes.presentation.common.extensions.generateUniqueId
import java.util.*
import javax.inject.Inject

class CacheMapper
@Inject
constructor(
    val checkItemsCacheMapper: CheckItemCacheMapper
) : EntityMapper<NoteCacheEntity, Note> {

    fun mapToEntityList(domainModels: List<Note>) = domainModels.map { domainModel ->
        mapToEntity(domainModel)
    }

    fun mapFromEntityList(entities: List<NoteCacheEntity>) = entities.map { entity ->
        mapFromEntity(entity)
    }

    fun mapFromEntityWithCheckItemsList(
        entities: List<NoteCheckItems>
    ) : List<Note> {
        return entities.map {
            mapFromEntityWithCheckItems(it.note, it.checkItems)
        }
    }

    fun mapFromEntityWithCheckItems(
        entity: NoteCacheEntity,
        checkItems: List<CheckItemCacheEntity>
    ): Note =
        Note(
            id = entity.id,
            title = entity.title,
            body = entity.body,
            color = entity.color,
            pinned = entity.pinned,
            reminder = createReminderFromEntity(entity),
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
            checkItems = checkItemsCacheMapper.mapFromEntityList(checkItems)
        )

    private fun createReminderFromEntity(entity: NoteCacheEntity): Reminder {
        val timeInMillis = getSavedTimeOrNull(entity.reminder?.timeInMillis)
        return Reminder(
            noteId = entity.id,
            requestCode = entity.createdAt.generateUniqueId(),
            timeInMillis = timeInMillis,
            repeating = entity.reminder?.repeating
        )
    }

    private fun getSavedTimeOrNull(timeInMillis: Long?): Long? {
        return timeInMillis?.let {
            val savedCalendar = Calendar.getInstance().apply {
                this.timeInMillis = it
            }
            val currentCalendar = Calendar.getInstance()
            if (savedCalendar.before(currentCalendar))
                null
            else it
        }
    }

    override fun mapFromEntity(entity: NoteCacheEntity): Note =
        Note(
            id = entity.id,
            title = entity.title,
            body = entity.body,
            color = entity.color,
            pinned = entity.pinned,
            reminder = createReminderFromEntity(entity),
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )

    override fun mapToEntity(domainModel: Note): NoteCacheEntity =
        NoteCacheEntity(
            id = domainModel.id,
            title = domainModel.title,
            body = domainModel.body,
            color = domainModel.color,
            pinned = domainModel.pinned,
            reminder = ReminderCacheEntity(timeInMillis = domainModel.reminder.timeInMillis, repeating = domainModel.reminder.repeating),
            createdAt = domainModel.createdAt,
            updatedAt = domainModel.updatedAt
        )
}