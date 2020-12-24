package com.dimi.advnotes.domain.model

import android.os.Parcelable
import com.dimi.advnotes.presentation.common.extensions.toShortDateString
import com.dimi.advnotes.presentation.common.extensions.toShortTimeString
import com.dimi.advnotes.util.Constants.INVALID_PRIMARY_KEY
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class Reminder(
    var noteId: Long = INVALID_PRIMARY_KEY,
    var requestCode: Int = 0,
    var timeInMillis: Long? = null,
    var repeating: Repeating? = null
) : Parcelable {
    override fun toString(): String {
        return StringBuilder().apply {
            timeInMillis?.let {
                append(Date(it).toShortDateString())
                append(", ")
                append(Date(it).toShortTimeString())
            }
        }.toString()
    }
}