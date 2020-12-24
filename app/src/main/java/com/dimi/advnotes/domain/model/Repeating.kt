package com.dimi.advnotes.domain.model

import android.app.AlarmManager
import android.os.Parcelable
import com.dimi.advnotes.presentation.common.extensions.toMediumDateString
import kotlinx.parcelize.Parcelize
import java.lang.StringBuilder
import java.util.*

enum class RepeatInterval(val value: Long, val printName: String) {
    HOUR(AlarmManager.INTERVAL_HOUR, "Hourly"),
    DAY(AlarmManager.INTERVAL_DAY, "Daily"),
    WEEK(AlarmManager.INTERVAL_DAY * 7, "Weekly"),
    MONTH(AlarmManager.INTERVAL_DAY * 30, "Monthly");

    companion object {
        fun from(value: Long?): RepeatInterval =
            values().find { it.value == value } ?: DAY

        fun from(printName: String): RepeatInterval =
            values().find { it.printName == printName } ?: DAY

        fun printNameValues(): List<String> = values().map {
            it.printName
        }
    }
}

enum class RepeatType(val printName: String) {
    FOREVER("Forever"),
    UNTIL_DATE("Until a certain date"),
    NO_OF_TIMES("Number of times");

    companion object {
        fun from(printName: String): RepeatType =
            values().find { it.printName == printName } ?: FOREVER

        fun printNameValues(): List<String> = values().map {
            it.printName
        }
    }
}

const val DEFAULT_REPEAT_NUMBER_TIMES = 1

@Parcelize
data class Repeating(
    var interval: RepeatInterval = RepeatInterval.DAY,
    var type: RepeatType = RepeatType.FOREVER,
    var noOfTimes: Int = DEFAULT_REPEAT_NUMBER_TIMES,
    var untilCertainDate: Calendar? = null
) : Parcelable {

    fun reset() {
        type = RepeatType.FOREVER
        noOfTimes = DEFAULT_REPEAT_NUMBER_TIMES
        untilCertainDate = null
        interval = RepeatInterval.DAY
    }

    override fun toString(): String {
        return StringBuilder("Repeating ").apply {
            append(interval.printName)
            if (type != RepeatType.FOREVER)
                append(", ")
            when (type) {
                RepeatType.NO_OF_TIMES ->
                    append(noOfTimes)
                        .append(" ")
                        .append("times")
                RepeatType.UNTIL_DATE ->
                    append("until ")
                        .append(untilCertainDate?.time?.toMediumDateString())
                else -> {
                }
            }
            if (type != RepeatType.FOREVER) append(".")
        }.toString()
    }
}