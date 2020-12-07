package com.dimi.advnotes.presentation

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.afollestad.materialdialogs.MaterialDialog
import com.dimi.advnotes.AppScopeCoroutineExceptionHandler
import com.dimi.advnotes.AppScopeExceptionUIHandler
import com.dimi.advnotes.R
import com.dimi.advnotes.databinding.ActivityMainBinding
import com.dimi.advnotes.presentation.common.AreYouSureCallback
import com.dimi.advnotes.presentation.common.DialogCalendarCaptured
import com.dimi.advnotes.presentation.common.DialogColorCaptured
import com.dimi.advnotes.presentation.common.SnackbarUndoCallback
import com.dimi.advnotes.presentation.common.TodoCallback
import com.dimi.advnotes.presentation.common.UIController
import com.dimi.advnotes.presentation.common.extensions.areYouSureDialog
import com.dimi.advnotes.presentation.common.extensions.showColorPickerDialog
import com.dimi.advnotes.presentation.common.extensions.showDatePickerDialog
import com.dimi.advnotes.presentation.common.extensions.showTimePickerDialog
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), UIController, AppScopeExceptionUIHandler {

    @Inject
    lateinit var appScopeExceptionHandler: AppScopeCoroutineExceptionHandler

    private var dialogInView: MaterialDialog? = null

    private val currentNavigationFragment: Fragment?
        get() = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
            ?.childFragmentManager
            ?.fragments
            ?.first()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        appScopeExceptionHandler.appScopeExceptionUIHandler = this
    }

    override fun showColorChoseDialog(colorPickerCallback: DialogColorCaptured) {
        hideSoftKeyboard()



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

    override fun hideSoftKeyboard() {
        if (currentFocus != null) {
            val inputMethodManager = getSystemService(
                Context.INPUT_METHOD_SERVICE
            ) as InputMethodManager
            inputMethodManager
                .hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
    }

    override fun showSoftKeyboard(view: View) {
        val inputMethodManager = getSystemService(
            Context.INPUT_METHOD_SERVICE
        ) as InputMethodManager
        inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    override fun showToast(message: String, length: Int) {
        Toast.makeText(this, message, length).show()
    }

    override fun showSnackBar(message: String, length: Int) {
        currentNavigationFragment?.let { Snackbar.make(it.requireView(), message, length).show() }
    }

    override fun showUndoSnackBar(
        view: View,
        message: Int,
        length: Int,
        snackbarUndoCallback: SnackbarUndoCallback?,
        onDismissCallback: TodoCallback?
    ) {
        //setSupportActionBar()
        Snackbar.make(view, message, length)
            .setAction(R.string.undo) {
                snackbarUndoCallback?.undo()
            }
            .addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
                override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                    if (event != DISMISS_EVENT_ACTION)
                        onDismissCallback?.execute()
                }
            })
            .show()
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

    override fun onDestroy() {
        super.onDestroy()
        if (dialogInView != null) {
            (dialogInView as MaterialDialog).dismiss()
            dialogInView = null
        }
        appScopeExceptionHandler.appScopeExceptionUIHandler = null
    }

    override fun handleException(exception: Throwable) {
        exception.message?.let { showSnackBar(it) }
    }
}