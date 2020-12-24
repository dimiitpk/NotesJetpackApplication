package com.dimi.advnotes.presentation.common.base

import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.afollestad.materialdialogs.MaterialDialog
import com.dimi.advnotes.R
import com.dimi.advnotes.presentation.common.AreYouSureCallback
import com.dimi.advnotes.presentation.common.DialogCalendarCaptured
import com.dimi.advnotes.presentation.common.DialogColorCaptured
import com.dimi.advnotes.presentation.common.TodoCallback
import com.dimi.advnotes.presentation.common.UIController
import com.dimi.advnotes.presentation.common.extensions.areYouSureDialog
import com.dimi.advnotes.presentation.common.extensions.hideKeyboard
import com.dimi.advnotes.presentation.common.extensions.showColorPickerDialog
import com.dimi.advnotes.presentation.common.extensions.showDatePickerDialog
import com.dimi.advnotes.presentation.common.extensions.showTimePickerDialog
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import java.util.*

abstract class BaseActivity<B : ViewDataBinding>(
    @LayoutRes
    private val layoutId: Int
) : AppCompatActivity(), UIController {

    private var dialogInView: MaterialDialog? = null

    private val currentNavigationFragment: Fragment?
        get() = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
            ?.childFragmentManager
            ?.fragments
            ?.first()

    val navController: NavController
        get() = findNavController(R.id.nav_host_fragment)

    override fun showColorChoseDialog(colorPickerCallback: DialogColorCaptured) {
        hideKeyboard()

        val listOfAvailableColors = resources.getIntArray(R.array.note_background_colors)

        dialogInView = showColorPickerDialog(
            colors = listOfAvailableColors,
            colorPickerCallback = colorPickerCallback,
            onDismiss = {
                dialogInView = null
            }
        )
    }

    override fun showAreYouSureDialog(message: String, areYouSureCallback: AreYouSureCallback) {
        dialogInView = areYouSureDialog(
            message = message,
            areYouSureCallback = areYouSureCallback,
            onDismiss = {
                dialogInView = null
            }
        )
    }

    override fun showToast(message: String, length: Int) {
        Toast.makeText(this, message, length).show()
    }

    override fun showSnackBar(
        message: String,
        length: Int
    ) {
        currentNavigationFragment?.let { it ->
            Snackbar.make(it.requireView(), message, length).show()
        }
    }

    override fun showUndoSnackBar(
        message: Int,
        length: Int,
        onUndoCallback: TodoCallback?,
        onDismissCallback: TodoCallback?
    ) {
        currentNavigationFragment?.let { it ->
            Snackbar.make(it.requireView(), message, length)
                .setAction(R.string.undo) {
                    onUndoCallback?.execute()
                }
                .addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                        if (event != DISMISS_EVENT_ACTION)
                            onDismissCallback?.execute()
                        super.onDismissed(transientBottomBar, event)
                    }
                })
                .show()
        }
    }

    override fun showTimePickerDialog(
        calendarPickerCallback: DialogCalendarCaptured,
        currentTime: Calendar?
    ) {
        dialogInView = showTimePickerDialog(
            currentTime = currentTime,
            calendarPickerCallback = calendarPickerCallback,
            onDismiss = {
                dialogInView = null
            }
        )
    }

    override fun showDatePickerDialog(
        calendarPickerCallback: DialogCalendarCaptured,
        currentDate: Calendar?
    ) {
        dialogInView = showDatePickerDialog(
            currentDate = currentDate,
            calendarPickerCallback = calendarPickerCallback,
            onDismiss = {
                dialogInView = null
            }
        )
    }

    override fun onPause() {
        super.onPause()
        if (dialogInView != null) {
            (dialogInView as MaterialDialog).dismiss()
            dialogInView = null
        }
    }
}