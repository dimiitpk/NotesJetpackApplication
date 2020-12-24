package com.dimi.advnotes.presentation.common.extensions

import androidx.appcompat.app.AppCompatActivity
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.afollestad.materialdialogs.color.colorChooser
import com.afollestad.materialdialogs.datetime.datePicker
import com.afollestad.materialdialogs.datetime.timePicker
import com.dimi.advnotes.R
import com.dimi.advnotes.presentation.common.AreYouSureCallback
import com.dimi.advnotes.presentation.common.DialogCalendarCaptured
import com.dimi.advnotes.presentation.common.DialogColorCaptured
import java.util.*

fun AppCompatActivity.showColorPickerDialog(
    colors: IntArray,
    colorPickerCallback: DialogColorCaptured,
    onDismiss: () -> Unit
) = MaterialDialog(this, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
    title(R.string.note_color_choose)
    colorChooser(colors) { _, color ->
        val index = colors.indexOf(color)
        colorPickerCallback.onCapturedColor(index)
    }
    positiveButton(R.string.select)
    onDismiss {
        onDismiss()
    }
    negativeButton(R.string.cancel)
}

fun AppCompatActivity.showTimePickerDialog(
    calendarPickerCallback: DialogCalendarCaptured,
    onDismiss: () -> Unit,
    currentTime: Calendar? = null
) = MaterialDialog(this).show {
    timePicker(
        currentTime = currentTime
    ) { _, datetime ->
        calendarPickerCallback.onCalendarInstance(datetime)
    }
    positiveButton(R.string.select)
    onDismiss {
        onDismiss()
    }
    negativeButton(R.string.cancel)
}

fun AppCompatActivity.showDatePickerDialog(
    calendarPickerCallback: DialogCalendarCaptured,
    onDismiss: () -> Unit,
    currentDate: Calendar? = null
) = MaterialDialog(this).show {
    datePicker(
        currentDate = currentDate,
        requireFutureDate = true
    ) { _, datetime ->
        calendarPickerCallback.onCalendarInstance(datetime)
    }
    positiveButton(R.string.select)
    onDismiss {
        onDismiss()
    }
    negativeButton(R.string.cancel)
}

fun AppCompatActivity.areYouSureDialog(
    message: String,
    areYouSureCallback: AreYouSureCallback,
    onDismiss: () -> Unit
) = MaterialDialog(this)
    .show {
        title(R.string.are_you_sure)
        message(text = message)
        negativeButton(R.string.cancel) {
            areYouSureCallback.cancel()
            dismiss()
        }
        positiveButton(R.string.yes) {
            areYouSureCallback.proceed()
            dismiss()
        }
        onDismiss {
            onDismiss()
        }
        cancelable(false)
    }