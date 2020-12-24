package com.dimi.advnotes.framework.database.mapper

import com.dimi.advnotes.domain.model.DEFAULT_REPEAT_NUMBER_TIMES
import com.dimi.advnotes.domain.model.Reminder
import com.dimi.advnotes.domain.model.RepeatInterval
import com.dimi.advnotes.domain.model.RepeatType
import com.dimi.advnotes.domain.model.Repeating
import com.dimi.advnotes.domain.util.EntityMapper
import com.dimi.advnotes.framework.database.model.ReminderCacheEntity
import com.dimi.advnotes.presentation.common.extensions.generateUniqueId
import java.util.*
import javax.inject.Inject

class ReminderCacheMapper @Inject constructor() : EntityMapper<ReminderCacheEntity, Reminder> {

    fun createReminder(noteId: Long, createdAt: Date, entity: ReminderCacheEntity?) =
        entity?.let {
            mapFromEntity(it).apply {
                this.noteId = noteId
                this.requestCode = createdAt.generateUniqueId()
            }
        } ?: Reminder(
            noteId = noteId,
            requestCode = createdAt.generateUniqueId()
        )

    override fun mapFromEntity(entity: ReminderCacheEntity): Reminder {
       // val timeInMillis = getSavedTimeOrNull(entity.timeInMillis)
        val timeInMillis = entity.timeInMillis
        val repeating = entity.repeatingInterval?.let { interval ->
            val untilDate = entity.repeatingUntilDate?.let {
                Calendar.getInstance().apply {
                    this.timeInMillis = it
                }
            }
            Repeating(
                type = RepeatType.values()[entity.repeatingType],
                interval = RepeatInterval.values()[interval],
                noOfTimes = entity.repeatingNoOfTimes,
                untilCertainDate = untilDate
            )
        }
        return Reminder(
            timeInMillis = timeInMillis,
            repeating = if (timeInMillis != null) repeating else null
        )
    }

    override fun mapToEntity(domainModel: Reminder): ReminderCacheEntity {
        return ReminderCacheEntity(
            timeInMillis = domainModel.timeInMillis,
            repeatingInterval = domainModel.repeating?.interval?.ordinal,
            repeatingNoOfTimes = domainModel.repeating?.noOfTimes ?: DEFAULT_REPEAT_NUMBER_TIMES,
            repeatingType = domainModel.repeating?.type?.ordinal ?: RepeatType.FOREVER.ordinal,
            repeatingUntilDate = domainModel.repeating?.untilCertainDate?.timeInMillis
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
}