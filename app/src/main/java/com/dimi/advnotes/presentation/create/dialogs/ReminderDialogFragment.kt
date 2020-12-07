package com.dimi.advnotes.presentation.create.dialogs

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.dimi.advnotes.R
import com.dimi.advnotes.databinding.ReminderSettingsBinding
import com.dimi.advnotes.presentation.common.DialogCalendarCaptured
import com.dimi.advnotes.presentation.common.base.BaseDialogFragment
import com.dimi.advnotes.presentation.common.extensions.observe
import com.dimi.advnotes.presentation.create.NoteDetailViewModel
import java.util.*

class ReminderDialogFragment :
    BaseDialogFragment<ReminderSettingsBinding>(R.layout.reminder_settings) {

    private val viewModel: NoteDetailViewModel by navGraphViewModels(R.id.detail_nav) {
        defaultViewModelProviderFactory
    }

    override fun onInitDataBinding() {
        viewBinding.viewModel = viewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observe(viewModel.reminderEvent, ::onViewEventChange)
    }

    private fun onViewEventChange(viewData: NoteReminderViewEvent) {
        when (viewData) {
            is NoteReminderViewEvent.OpenTimePicker -> {
                showTimePicker()
            }
            is NoteReminderViewEvent.OpenDatePicker -> {
                showDatePicker()
            }
            is NoteReminderViewEvent.DismissDialog -> {
                dismissDialog()
            }
            else -> { }
        }
    }

    private fun dismissDialog() {
        findNavController().popBackStack()
        viewModel.setDefaultView()
    }

    private fun showTimePicker() {
        uiController.showTimePickerDialog(
            currentTime = viewModel.getReminderCurrentTime(),
            calendarPickerCallback = object : DialogCalendarCaptured {
                override fun onCalendarInstance(calendar: Calendar) {
                    viewModel.setReminderTime(calendar)
                }
            })
    }

    private fun showDatePicker() {
        uiController.showDatePickerDialog(
            currentDate = viewModel.getReminderCurrentTime(),
            calendarPickerCallback = object : DialogCalendarCaptured {
                override fun onCalendarInstance(calendar: Calendar) {
                    viewModel.setReminderDate(calendar)
                }
            })
    }
}