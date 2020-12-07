package com.dimi.advnotes.framework.database.model

data class ReminderCacheEntity(
    var timeInMillis: Long? = null,
    var repeating: Long? = null
)