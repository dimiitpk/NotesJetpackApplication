package com.dimi.advnotes.framework.database.model

import com.dimi.advnotes.domain.model.DEFAULT_REPEAT_NUMBER_TIMES
import com.dimi.advnotes.domain.model.RepeatType

data class ReminderCacheEntity(
    var timeInMillis: Long? = null,
    var repeatingInterval: Int? = null,
    var repeatingType: Int = RepeatType.FOREVER.ordinal,
    var repeatingNoOfTimes: Int = DEFAULT_REPEAT_NUMBER_TIMES,
    var repeatingUntilDate: Long? = null
)