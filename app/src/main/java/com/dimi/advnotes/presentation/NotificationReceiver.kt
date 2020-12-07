package com.dimi.advnotes.presentation

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.dimi.advnotes.presentation.common.NOTIFICATION_ID_KEY

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        val id =
            intent?.getIntExtra(NOTIFICATION_ID_KEY, 0) ?: 0
        if (id != 0) {
            context?.let {
                val notificationManager = it.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.cancel(id)
            }
        }
    }
}