package com.dimi.advnotes.presentation.common

import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.view.ActionMode
import com.google.android.material.snackbar.Snackbar
import java.util.*

interface UIController {
    fun showColorChoseDialog(colorPickerCallback: DialogColorCaptured)

    fun showAreYouSureDialog(
        message: String,
        areYouSureCallback: AreYouSureCallback
    )

    fun openDrawer()

    fun closeDrawer()

    fun showToast(message: String, length: Int = Toast.LENGTH_SHORT)

    fun showSnackBar(
        message: String,
        length: Int = Snackbar.LENGTH_SHORT
    )

    fun showUndoSnackBar(
        @StringRes message: Int,
        length: Int = Snackbar.LENGTH_LONG,
        onUndoCallback: TodoCallback?,
        onDismissCallback: TodoCallback?
    )

    fun showTimePickerDialog(
        calendarPickerCallback: DialogCalendarCaptured,
        currentTime: Calendar? = null
    )

    fun showDatePickerDialog(
        calendarPickerCallback: DialogCalendarCaptured,
        currentDate: Calendar? = null
    )

    fun startActionMode(
        listener: ActionModeCallback.Listener
    ): ActionMode?

    fun clearActionMode()
}

interface DialogCalendarCaptured {
    fun onCalendarInstance(calendar: Calendar)
}

interface DialogColorCaptured {
    fun onCapturedColor(color: Int)
}

interface AreYouSureCallback {

    fun proceed()

    fun cancel()
}

interface TodoCallback {

    fun execute()
}