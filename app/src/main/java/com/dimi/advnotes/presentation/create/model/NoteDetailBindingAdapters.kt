package com.dimi.advnotes.presentation.create.model

import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData
import com.dimi.advnotes.R

object NoteDetailBindingAdapters {

    @BindingAdapter("pinned")
    @JvmStatic
    fun ImageView.filledOrStrokePin(pinned: Boolean) {
        if (pinned)
            setImageResource(R.drawable.ic_push_pin_filled)
        else
            setImageResource(R.drawable.ic_push_pin_with_stroke)
    }

    @BindingAdapter("reminderSet")
    @JvmStatic
    fun ImageView.alarmOnOrOff(reminderSet: Boolean) {
        if (reminderSet)
            setImageResource(R.drawable.ic_alarm_on)
        else
            setImageResource(R.drawable.ic_alarm_add)
    }

    @BindingAdapter("focusability")
    @JvmStatic
    fun View.focusable(liveData: MutableLiveData<Boolean>) {
        val boolean = liveData.value!!
        if (isFocusable != boolean)
            isFocusable = boolean
        if (isFocusableInTouchMode != boolean)
            isFocusableInTouchMode = boolean
    }
}