package com.dilekbaykara.tasky.features.shared.presentation.notification
import android.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import com.dilekbaykara.tasky.MainActivity

class NotificationReceiver : BroadcastReceiver() {
    companion object {
        private const val CHANNEL_ID = "tasky_reminders"
        private const val CHANNEL_NAME = "Tasky Reminders"
        private const val CHANNEL_DESCRIPTION = "Notifications for agenda item reminders"
    }
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("NotificationReceiver", "DEBUG: NotificationReceiver triggered!")
        val notificationId = intent.getStringExtra("notification_id") ?: return
        val agendaItemId = intent.getStringExtra("agenda_item_id") ?: return
        val agendaItemType = intent.getStringExtra("agenda_item_type") ?: return
        val title = intent.getStringExtra("title") ?: "Reminder"
        val description = intent.getStringExtra("description") ?: ""
        val userId = intent.getStringExtra("user_id") ?: return
        Log.d("NotificationReceiver", "DEBUG: Showing notification for '$title' - $description")
        createNotificationChannel(context)
        val deepLinkIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            data = "tasky://agenda/$agendaItemType/$agendaItemId".toUri()
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            agendaItemId.hashCode(),
            deepLinkIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(description)
            .setStyle(NotificationCompat.BigTextStyle().bigText(description))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setVibrate(longArrayOf(0, 250, 250, 250))
            .build()
        try {
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(notificationId.hashCode(), notification)
            Log.d("NotificationReceiver", "DEBUG: Notification displayed successfully!")
        } catch (e: SecurityException) {
            Log.d("NotificationReceiver", "DEBUG: Failed to show notification - permission denied: ${e.message}")
            e.printStackTrace()
        }
    }
    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESCRIPTION
                enableVibration(true)
                setShowBadge(true)
            }
            val systemNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            systemNotificationManager.createNotificationChannel(channel)
        }
    }
}
