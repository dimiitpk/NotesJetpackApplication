package com.dimi.advnotes.presentation.common

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import com.dimi.advnotes.domain.model.DEFAULT_REPEAT_NUMBER_TIMES
import com.dimi.advnotes.domain.model.Reminder
import com.dimi.advnotes.domain.model.RepeatType
import com.dimi.advnotes.presentation.AlarmReceiver
import com.dimi.advnotes.presentation.BootReceiver
import com.dimi.advnotes.util.Constants.INVALID_PRIMARY_KEY
import java.util.*

const val REMINDER_NOTE_ID_KEY = "reminder_key_id"

class ReminderManager(val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun setReminder(reminder: Reminder) {
        if (isReminderValid(reminder)) {
            val alarmIntent = getPendingIntent(reminder)
            reminder.repeating?.let { repeating ->
                alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    reminder.timeInMillis!!,
                    repeating.interval.value,
                    alarmIntent
                )
            } ?: alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                reminder.timeInMillis!!,
                alarmIntent
            )

            enableBootReceiver()
        }
    }

    private fun isReminderValid(reminder: Reminder) =
        reminder.noteId != INVALID_PRIMARY_KEY && reminder.timeInMillis != null

    private fun getPendingIntent(reminder: Reminder): PendingIntent {
        return Intent(context, AlarmReceiver::class.java).let { intent ->
            intent.putExtra(REMINDER_NOTE_ID_KEY, reminder.noteId)

            PendingIntent.getBroadcast(
                context,
                reminder.requestCode,
                intent,
                0
            )
        }
    }

    /**
     * When reminder is fired, check reminder for repeating, cancel if needed
     * @return true if reminder need to be updated in db, false otherwise
     */
    fun checkJustFiredReminder(reminder: Reminder): Boolean {
        return if (isReminderValid(reminder)) {
            reminder.repeating?.let { repeating ->
                when (repeating.type) {
                    RepeatType.NO_OF_TIMES -> {
                        if (repeating.noOfTimes >= DEFAULT_REPEAT_NUMBER_TIMES)
                            repeating.noOfTimes--

                        if (repeating.noOfTimes < DEFAULT_REPEAT_NUMBER_TIMES)
                            cancelReminder(reminder)

                        true
                    }
                    RepeatType.UNTIL_DATE -> {
                        repeating.untilCertainDate?.let { untilDate ->
                            val currentDate = Calendar.getInstance()
                            if (currentDate >= untilDate) {
                                cancelReminder(reminder)
                            }
                        } ?: run { cancelReminder(reminder) }
                        true
                    }
                    else -> false
                }
            } ?: run {
                cancelReminder(reminder)
                true
            }
        } else
            false
    }

    /**
     * If pending intent is started cancel it and resetReminder
     * otherwise just reset reminder
     */
    fun cancelReminder(reminder: Reminder) {
        if (isReminderValid(reminder)) {
            getStartedAlarmIntent(reminder)?.let { alarmIntent ->
                alarmManager.cancel(alarmIntent)
                resetReminder(reminder)
            } ?: resetReminder(reminder)
        }
    }

    fun getStartedAlarmIntent(reminder: Reminder): PendingIntent? =
        Intent(context, AlarmReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(
                context, reminder.requestCode, intent,
                PendingIntent.FLAG_NO_CREATE
            )
        }

    private fun resetReminder(reminder: Reminder) {
        reminder.timeInMillis = null
        reminder.repeating = null
    }

    private fun enableBootReceiver() {
        val receiver = ComponentName(context, BootReceiver::class.java)

        context.packageManager.setComponentEnabledSetting(
            receiver,
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )
    }

    fun disableBootReceiver() {
        val receiver = ComponentName(context, BootReceiver::class.java)

        context.packageManager.setComponentEnabledSetting(
            receiver,
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP
        )
    }
}