package com.dimi.advnotes.presentation

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.dimi.advnotes.data.interactors.NoteUseCase
import com.dimi.advnotes.presentation.common.ReminderManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject
    lateinit var notesUseCases: NoteUseCase

    @Inject
    lateinit var reminderManager: ReminderManager

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            GlobalScope.launch(Dispatchers.IO) {
                val notes = notesUseCases.fetchAllNotesUseCase()
                val allRemindersValue = notes.sumOf {
                    it.reminder.timeInMillis ?: 0
                }
                if (allRemindersValue != 0L) {
                    for (note in notes) {
                        reminderManager.setReminder(note.reminder)
                    }
                } else {
                    reminderManager.disableBootReceiver()
                }
            }
        }
    }
}