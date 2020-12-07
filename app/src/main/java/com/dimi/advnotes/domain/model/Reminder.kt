package com.dimi.advnotes.domain.model

import android.os.Parcelable
import com.dimi.advnotes.util.Constants.INVALID_PRIMARY_KEY
import kotlinx.parcelize.Parcelize

@Parcelize
data class Reminder(
    val noteId: Long = INVALID_PRIMARY_KEY,
    var requestCode: Int = 0,
    var timeInMillis: Long? = null,
    var repeating: Long? = null
) : Parcelable