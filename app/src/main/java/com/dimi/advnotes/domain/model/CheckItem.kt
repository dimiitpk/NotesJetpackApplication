package com.dimi.advnotes.domain.model

import android.os.Parcelable
import com.dimi.advnotes.util.Constants
import kotlinx.parcelize.Parcelize

@Parcelize
data class CheckItem(
    var id: Long = Constants.DEFAULT_PRIMARY_KEY,
    var text: String = "",
    var checked: Boolean = false,
    var lastUpdated: Long = 0L,
    var focus: Boolean = false
) : Parcelable