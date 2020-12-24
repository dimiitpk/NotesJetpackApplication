package com.dimi.advnotes.presentation.reminder

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.dimi.advnotes.R
import com.dimi.advnotes.databinding.DialogReminderBinding
import com.dimi.advnotes.domain.model.Reminder
import com.dimi.advnotes.domain.model.RepeatInterval
import com.dimi.advnotes.domain.model.RepeatType
import com.dimi.advnotes.presentation.common.DialogCalendarCaptured
import com.dimi.advnotes.presentation.common.base.BaseDialogFragment
import com.dimi.advnotes.presentation.common.extensions.collectWhenStarted
import com.dimi.advnotes.presentation.detail.REMINDER_BUNDLE_KEY
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class ReminderDialogFragment :
    BaseDialogFragment<DialogReminderBinding>(
        R.layout.dialog_reminder
    ) {

    private val viewModel: ReminderViewModel by viewModels()

    private val args by navArgs<ReminderDialogFragmentArgs>()

    // on rotation it reset all alarm settings
    override fun onInitDataBinding() {
        viewBinding.viewModel = viewModel

        viewBinding.repeatingIntervalText.apply {
            setAdapter(
                ArrayAdapter(
                    requireContext(),
                    R.layout.list_item_text,
                    RepeatInterval.printNameValues()
                )
            )
            setOnItemClickListener { _, view, _, _ ->
                if (view is TextView)
                    viewModel.setRepeatingInterval(RepeatInterval.from(view.text.toString()))
            }
        }
        viewBinding.repeatingTypeText.apply {
            setAdapter(
                ArrayAdapter(
                    requireContext(),
                    R.layout.list_item_text,
                    RepeatType.printNameValues()
                )
            )
            setOnItemClickListener { _, view, _, _ ->
                if (view is TextView)
                    viewModel.setRepeatingType(RepeatType.from(view.text.toString()))
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState == null)
            args.reminder?.let { viewModel.setReminder(it) }

        viewBinding.repeatingIntervalText.setText(
            viewModel.reminder.value?.repeatIntervalString() ?: RepeatInterval.DAY.printName, false
        )
        viewBinding.repeatingTypeText.setText(
            viewModel.reminder.value?.repeatTypeString() ?: RepeatType.FOREVER.printName, false
        )

        viewLifecycleOwner.lifecycleScope
            .collectWhenStarted(viewModel.reminderEvent, ::onViewEventChange)
    }

    private fun onViewEventChange(viewEvent: NoteReminderViewEvent) {
        when (viewEvent) {
            is NoteReminderViewEvent.OpenTimePicker ->
                showTimePicker()
            is NoteReminderViewEvent.OpenDatePicker ->
                showDatePicker(viewEvent.repeat)
            is NoteReminderViewEvent.DismissDialog ->
                dismissDialog()
            is NoteReminderViewEvent.ConfirmReminder -> {
                findNavController()
                    .previousBackStackEntry
                    ?.savedStateHandle
                    ?.set<Reminder>(
                        REMINDER_BUNDLE_KEY,
                        viewEvent.reminder
                    )

                dismissDialog()
            }
            is NoteReminderViewEvent.PastTimeError ->
                uiController.showToast(resources.getString(R.string.confirm_reminder_past_time_error), Toast.LENGTH_LONG)
        }
    }

    private fun dismissDialog() {
        findNavController().popBackStack()
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

    private fun showDatePicker(repeat: Boolean) {
        uiController.showDatePickerDialog(
            currentDate = if (repeat) null else viewModel.getReminderCurrentTime(),
            calendarPickerCallback = object : DialogCalendarCaptured {
                override fun onCalendarInstance(calendar: Calendar) {
                    if (repeat)
                        viewModel.setRepeatUntilDate(calendar)
                    else
                        viewModel.setReminderDate(calendar)
                }
            })
    }
}