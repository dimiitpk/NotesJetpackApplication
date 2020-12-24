package com.dimi.advnotes.presentation.common

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.navigation.NavDeepLinkBuilder
import com.dimi.advnotes.R
import com.dimi.advnotes.domain.model.Note
import com.dimi.advnotes.presentation.NotificationReceiver
import com.dimi.advnotes.presentation.detail.DetailFragmentArgs

const val CHANNEL_ID = "reminder_channel_id"
const val CHANNEL_NAME = "reminder_channel_name"

const val NOTIFICATION_ID_KEY = "notification_id_key"

const val NOTIFICATION_ACTION_DONE = "Done"

class NotificationHelper(val context: Context) : ContextWrapper(context) {

    private val notificationManager =
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun sendNotification(
        note: Note
    ) {
        val title = note.title
        val text = if (note.checkItems.isNotEmpty()) note.checkItems[0].text else note.body
        val id = note.reminder.requestCode

        val actionIntent = Intent(context, NotificationReceiver::class.java).let { intent ->
            intent.putExtra(NOTIFICATION_ID_KEY, id)
            PendingIntent.getBroadcast(
                context,
                id,
                intent,
                0
            )
        }

        val navArgs = DetailFragmentArgs(note.id).toBundle()
        val contentIntent = generateContentIntent(navArgs)

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(text)
            .setContentIntent(contentIntent)
            .setSmallIcon(R.drawable.ic_add_24dp)
            .setAutoCancel(true)
            .addAction(R.mipmap.ic_launcher, NOTIFICATION_ACTION_DONE, actionIntent)
            .build()

        notificationManager.notify(id, notification)
    }

    private fun generateContentIntent(args: Bundle) = NavDeepLinkBuilder(context)
        .setGraph(R.navigation.main_nav)
        .setDestination(R.id.detail_fragment)
        .setArguments(args)
        .createPendingIntent()
}