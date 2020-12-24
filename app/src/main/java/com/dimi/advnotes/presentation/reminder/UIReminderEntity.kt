package com.dimi.advnotes.presentation.reminder

import com.dimi.advnotes.domain.model.RepeatType
import com.dimi.advnotes.domain.model.Repeating
import com.dimi.advnotes.presentation.common.extensions.toMediumDateString
import com.dimi.advnotes.presentation.common.extensions.toShortTimeString
import java.util.*


data class UIReminderEntity(
    var calendar: Calendar = Calendar.getInstance().apply {
        set(Calendar.MINUTE, get(Calendar.MINUTE) + 1)
    },
    var repeat: Boolean = false,
    var repeating: Repeating = Repeating()
) {

    fun setRepeatingType(type: RepeatType) {
        repeating.type = type
        if (type == RepeatType.UNTIL_DATE) {
            if (repeating.untilCertainDate == null)
                repeating.untilCertainDate = Calendar.getInstance()
        }
    }

    fun getTimeString() = calendar.time.toShortTimeString()
    fun repeatIntervalString() = repeating.interval.printName
    fun repeatTypeString() = repeating.type.printName
    fun repeatUntilDateString() = repeating.untilCertainDate?.time?.toMediumDateString()
    fun getDateString() = calendar.time.toMediumDateString()

    fun toggleRepeating() {
        repeat = !repeat
    }

    fun resetRepeatSettings() {
        repeating.reset()
    }

    fun setTime(calendar: Calendar) {
        this.calendar.apply {
            set(Calendar.MINUTE, calendar.get(Calendar.MINUTE))
            set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY))
            set(Calendar.SECOND, 0)
        }
    }

    fun setDate(calendar: Calendar) {
        this.calendar.apply {
            set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH))
            set(Calendar.YEAR, calendar.get(Calendar.YEAR))
            set(Calendar.MONTH, calendar.get(Calendar.MONTH))
        }
    }

    fun reset() {
        calendar = Calendar.getInstance().apply {
            set(Calendar.MINUTE, get(Calendar.MINUTE) + 1)
        }
        repeat = false
    }
}