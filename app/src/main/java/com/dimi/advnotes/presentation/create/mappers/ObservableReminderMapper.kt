package com.dimi.advnotes.presentation.create.mappers

import com.dimi.advnotes.domain.model.Reminder
import com.dimi.advnotes.domain.util.EntityMapper
import com.dimi.advnotes.presentation.create.model.ObservableReminder
import javax.inject.Inject

class ObservableReminderMapper @Inject constructor() : EntityMapper<ObservableReminder, Reminder> {
//
//    fun createReminder(entity: Note): Reminder {
//        val reminder = entity.reminder
//        val repeating = reminder?.let {
//            if (it.repeating == 0L) null
//            else it.repeating
//        }
//        return Reminder(
//            noteId = entity.id,
//            requestCode = entity.createdAt.generateUniqueId(),
//            timeInMillis = reminder?.calendar?.timeInMillis,
//            repeating = repeating
//        )
//    }

    override fun mapFromEntity(entity: ObservableReminder): Reminder {
        return Reminder(
            timeInMillis = entity.calendar.timeInMillis,
            repeating = entity.repeating
        )
    }

    override fun mapToEntity(domainModel: Reminder): ObservableReminder {
        val timeInMillis = domainModel.timeInMillis
        return ObservableReminder(
            repeating = domainModel.repeating ?: 0L
        ).apply {
            timeInMillis?.let {
                calendar.apply {
                    this.timeInMillis = it
                }
            }
            isInitialCalendarNull = timeInMillis == null
            completed = timeInMillis != null
        }
    }
}