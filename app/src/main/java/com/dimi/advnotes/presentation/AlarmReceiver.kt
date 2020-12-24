package com.dimi.advnotes.presentation

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.dimi.advnotes.data.interactors.NoteUseCase
import com.dimi.advnotes.presentation.common.NotificationHelper
import com.dimi.advnotes.presentation.common.REMINDER_NOTE_ID_KEY
import com.dimi.advnotes.presentation.common.ReminderManager
import com.dimi.advnotes.util.Constants.INVALID_PRIMARY_KEY
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var noteUseCase: NoteUseCase

    @Inject
    lateinit var reminderManager: ReminderManager

    override fun onReceive(context: Context?, intent: Intent?) {

        val id =
            intent?.getLongExtra(REMINDER_NOTE_ID_KEY, INVALID_PRIMARY_KEY) ?: INVALID_PRIMARY_KEY
        if (id != INVALID_PRIMARY_KEY) {
            GlobalScope.launch(Dispatchers.IO) {
                val note = noteUseCase.fetchSingleNote(id)

                context?.let {
                    val notificationHelper = NotificationHelper(it)
                    notificationHelper.sendNotification(
                        note
                    )
                }

                reminderManager.checkJustFiredReminder(note.reminder).let { shouldUpdate ->
                    if (shouldUpdate)
                        noteUseCase.updateNotes(listOf(note))
                }
            }
        }
    }
}