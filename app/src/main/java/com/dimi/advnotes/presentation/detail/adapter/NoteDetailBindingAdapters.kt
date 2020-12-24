package com.dimi.advnotes.presentation.detail.adapter

import androidx.databinding.BindingAdapter
import com.dimi.advnotes.R
import com.dimi.advnotes.presentation.common.extensions.setIconByCondition
import com.google.android.material.appbar.MaterialToolbar

object NoteDetailBindingAdapters {

    @BindingAdapter("reminderSet")
    @JvmStatic
    fun MaterialToolbar.alarmOnOrOff(reminderSet: Boolean) {
        setIconByCondition(
            R.id.action_reminder,
            reminderSet,
            R.drawable.ic_alarm_on_24dp,
            R.drawable.ic_alarm_add_24dp
        )
    }

    @BindingAdapter("pinned")
    @JvmStatic
    fun MaterialToolbar.pinnedIcon(pinned: Boolean) {
        setIconByCondition(
            R.id.action_pin_unpin,
            pinned,
            R.drawable.ic_push_pin_filled_24dp,
            R.drawable.ic_push_pin_stroke_24dp
        )
    }

    @BindingAdapter("archived")
    @JvmStatic
    fun MaterialToolbar.archivedIcon(archived: Boolean) {
        setIconByCondition(
            R.id.action_archive,
            archived,
            R.drawable.ic_unarchive_24dp,
            R.drawable.ic_archive_24dp
        )
    }
}