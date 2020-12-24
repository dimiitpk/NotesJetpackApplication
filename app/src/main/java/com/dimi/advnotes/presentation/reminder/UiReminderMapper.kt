package com.dimi.advnotes.presentation.reminder

import com.dimi.advnotes.domain.model.Reminder
import com.dimi.advnotes.domain.model.RepeatType
import com.dimi.advnotes.domain.util.EntityMapper
import java.util.*
import javax.inject.Inject

class UiReminderMapper @Inject constructor() : EntityMapper<UIReminderEntity, Reminder> {

    /**
     * If repeat type is to repeat certain number of times,
     * must add +1, so the first of doesn't count
     */
    override fun mapFromEntity(entity: UIReminderEntity): Reminder {
        val repeating = if (entity.repeat) entity.repeating else null
        if (entity.repeat)
            if (entity.repeating.type == RepeatType.NO_OF_TIMES)
                entity.repeating.noOfTimes++

        return Reminder(
            timeInMillis = entity.calendar.timeInMillis,
            repeating = repeating
        )
    }

    override fun mapToEntity(domainModel: Reminder): UIReminderEntity {
        return UIReminderEntity().apply {
            if (domainModel.timeInMillis != null)
                calendar = Calendar.getInstance().apply {
                    timeInMillis = domainModel.timeInMillis!!
                }
            if (domainModel.repeating != null)
                repeating = domainModel.repeating!!
            repeat = domainModel.repeating != null
        }
    }
}