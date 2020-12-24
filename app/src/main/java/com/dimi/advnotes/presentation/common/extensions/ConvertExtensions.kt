package com.dimi.advnotes.presentation.common.extensions

import android.text.format.DateUtils
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

internal fun Int.toHex() = "#${Integer.toHexString(this)}"

fun Date?.toSimpleString(): String {
    return if (this != null)
        if (DateUtils.isToday(this.time)) {
            this.toShortTimeString()
        } else this.toMonthAndDay()
    else ""
}

fun Date?.toShortTimeString(): String {
    val outputFormat: DateFormat = SimpleDateFormat.getTimeInstance(DateFormat.SHORT)
    return if (this != null) outputFormat.format(this) else ""
}

fun Date?.toShortDateString(): String {
    val outputFormat: DateFormat = SimpleDateFormat.getDateInstance(DateFormat.SHORT)
    return if (this != null) outputFormat.format(this) else ""
}

fun Date?.toMonthAndDay(): String {
    val outputFormat: DateFormat = SimpleDateFormat("dd MMM", Locale.US)
    return if (this != null) outputFormat.format(this) else ""
}

fun Date?.toMediumDateString(): String {
    val outputFormat: DateFormat =
        SimpleDateFormat.getDateInstance(DateFormat.MEDIUM)
    return if (this != null) outputFormat.format(this) else ""
}

fun Date.generateUniqueId() =
    Integer.parseInt(
        SimpleDateFormat("ddHHmmss", Locale.US).format(this)
    )

fun <T> List<T>.swap(a: Int, b: Int): List<T> = this
    .toMutableList()
    .also {
        it[a] = this[b]
        it[b] = this[a]
    }
