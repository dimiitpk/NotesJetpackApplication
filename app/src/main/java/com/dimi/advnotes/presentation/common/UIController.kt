package com.dimi.advnotes.presentation.common

import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import java.util.*
import kotlin.reflect.KClass

interface UIController {
    fun showColorChoseDialog(colorPickerCallback: DialogColorCaptured)

    fun showAreYouSureDialog(
        message: String,
        areYouSureCallback: AreYouSureCallback
    )

    fun hideSoftKeyboard()

    fun showSoftKeyboard(view: View)

    fun showToast(message: String, length: Int = Toast.LENGTH_SHORT)

    fun showSnackBar(message: String, length: Int = Snackbar.LENGTH_SHORT)

    fun showUndoSnackBar(
        view: View,
        @StringRes message: Int,
        length: Int = Snackbar.LENGTH_LONG,
        snackbarUndoCallback: SnackbarUndoCallback?,
        onDismissCallback: TodoCallback?
    )

    fun showTimePickerDialog(calendarPickerCallback: DialogCalendarCaptured, currentTime: Calendar? = null)

    fun showDatePickerDialog(calendarPickerCallback: DialogCalendarCaptured, currentDate: Calendar? = null)
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

interface SnackbarUndoCallback {

    fun undo()
}