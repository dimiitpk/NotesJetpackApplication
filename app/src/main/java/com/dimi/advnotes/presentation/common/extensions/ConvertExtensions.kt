package com.dimi.advnotes.presentation.common.extensions

import android.text.format.DateUtils
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

internal fun Int.toHex() = "#${Integer.toHexString(this)}"

fun Date?.toSimpleString(): String {
    return if (this != null)
        if (DateUtils.isToday(this.time)) {
            this.toHoursAndMinutes()
        } else this.toMonthAndDay()
    else ""
}

fun Date?.toHoursAndMinutes(): String {
    val outputFormat: DateFormat = SimpleDateFormat("HH:mm", Locale.US)
    return if (this != null) outputFormat.format(this) else ""
}

fun Date?.toMonthAndDay(): String {
    val outputFormat: DateFormat = SimpleDateFormat("dd MMM", Locale.US)
    return if (this != null) outputFormat.format(this) else ""
}

fun Date?.toBiggerMonthAndDay(): String {
    val outputFormat: DateFormat = SimpleDateFormat("dd MMMM", Locale.US)
    return if (this != null) outputFormat.format(this) else ""
}

fun Date.generateUniqueId() =
    Integer.parseInt(
        SimpleDateFormat("ddHHmmss", Locale.US).format(this)
    )
