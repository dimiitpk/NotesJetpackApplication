package com.dimi.advnotes.presentation.reminder

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimi.advnotes.domain.model.DEFAULT_REPEAT_NUMBER_TIMES
import com.dimi.advnotes.domain.model.Reminder
import com.dimi.advnotes.domain.model.RepeatInterval
import com.dimi.advnotes.domain.model.RepeatType
import com.dimi.advnotes.presentation.common.extensions.mutation
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.util.*

class ReminderViewModel @ViewModelInject constructor(
    private val reminderMapper: UiReminderMapper,
) : ViewModel() {

    private val _reminderEvent = MutableSharedFlow<NoteReminderViewEvent>()
    val reminderEvent = _reminderEvent.asSharedFlow()

    private val _reminder = MutableLiveData<UIReminderEntity>()
    val reminder: LiveData<UIReminderEntity>
        get() = _reminder

    val repeatNoOfTimes: MutableLiveData<String> =
        MutableLiveData(DEFAULT_REPEAT_NUMBER_TIMES.toString())

    // visibility for delete reminder button
    private val _isExistingReminder = MutableLiveData(false)
    val isExistingReminder: LiveData<Boolean> = _isExistingReminder

    private val observer = Observer<String?> { string ->
        _reminder.mutation {
            if (string.isNotBlank())
                it?.repeating?.noOfTimes = string.toInt()
        }
    }

    init {
        repeatNoOfTimes.observeForever(observer)
    }

    override fun onCleared() {
        super.onCleared()
        if (repeatNoOfTimes.hasActiveObservers())
            repeatNoOfTimes.removeObserver(observer)
    }

    fun setReminder(reminder: Reminder) {
        _reminder.postValue(reminderMapper.mapToEntity(reminder))
        reminder.repeating?.noOfTimes?.let {
            repeatNoOfTimes.postValue(it.toString())
        }
        if (reminder.timeInMillis != null)
            _isExistingReminder.postValue(true)
    }


    fun openTimePicker() {
        viewModelScope.launch {
            _reminderEvent.emit(NoteReminderViewEvent.OpenTimePicker)
        }
    }

    fun openDatePicker(repeat: Boolean = false) {
        viewModelScope.launch {
            _reminderEvent.emit(NoteReminderViewEvent.OpenDatePicker(repeat))
        }
    }

    fun dismissDialog() {
        viewModelScope.launch {
            _reminderEvent.emit(NoteReminderViewEvent.DismissDialog)
        }
    }

    fun toggleRepeating() {
        _reminder.mutation {
            it?.toggleRepeating()
        }
    }

    fun confirmReminder() {
        viewModelScope.launch {
            reminder.value?.let {
                if (isCalendarInPastTime(it.calendar))
                    _reminderEvent.emit(NoteReminderViewEvent.PastTimeError)
                else
                    _reminderEvent.emit(
                        NoteReminderViewEvent.ConfirmReminder(
                            reminder = reminderMapper.mapFromEntity(it)
                        )
                    )
            }
        }
    }

    private fun isCalendarInPastTime(calendar: Calendar) = calendar.before(Calendar.getInstance())

    fun deleteReminder() {
        viewModelScope.launch {
            _reminderEvent.emit(
                NoteReminderViewEvent.ConfirmReminder(
                    reminder = Reminder(timeInMillis = null, repeating = null)
                )
            )
        }
    }

    fun resetReminder() {
        _reminder.mutation {
            it?.reset()
        }
    }

    fun getReminderCurrentTime() = _reminder.value?.calendar

    fun setReminderTime(calendar: Calendar) {
        _reminder.mutation {
            it?.setTime(calendar)
        }
    }

    fun setReminderDate(calendar: Calendar) {
        _reminder.mutation {
            it?.setDate(calendar)
        }
    }

    fun setRepeatUntilDate(calendar: Calendar) {
        _reminder.mutation {
            it?.repeating?.untilCertainDate = calendar
        }
    }

    fun resetRepeatSettings() {
        repeatNoOfTimes.postValue(DEFAULT_REPEAT_NUMBER_TIMES.toString())
        _reminder.mutation {
            it?.resetRepeatSettings()
        }
    }

    fun setRepeatingInterval(repeating: RepeatInterval) {
        _reminder.mutation {
            it?.repeating?.interval = repeating
        }
    }

    fun setRepeatingType(repeating: RepeatType) {
        _reminder.mutation {
            it?.setRepeatingType(repeating)
        }
    }
}