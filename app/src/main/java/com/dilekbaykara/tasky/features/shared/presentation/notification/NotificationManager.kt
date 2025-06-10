package com.dilekbaykara.tasky.features.shared.presentation.notification
import android.R
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.dilekbaykara.tasky.MainActivity
import com.dilekbaykara.tasky.features.agenda.data.local.entities.Event
import com.dilekbaykara.tasky.features.agenda.data.local.entities.Reminder
import com.dilekbaykara.tasky.features.agenda.data.local.entities.Task
import com.dilekbaykara.tasky.features.shared.presentation.notification.data.dao.NotificationScheduleDao
import com.dilekbaykara.tasky.features.shared.presentation.notification.data.entity.NotificationSchedule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
@Singleton
class TaskyNotificationManager @Inject constructor(
    private val context: Context,
    private val notificationScheduleDao: NotificationScheduleDao
) {
    companion object {
        private const val CHANNEL_ID = "tasky_reminders"
        private const val CHANNEL_NAME = "Tasky Reminders"
        private const val CHANNEL_DESCRIPTION = "Notifications for agenda item reminders"
        private const val NOTIFICATION_REQUEST_CODE_BASE = 10000
    }
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val notificationManager = NotificationManagerCompat.from(context)
    init {
        createNotificationChannel()
    }
    private fun createNotificationChannel() {
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
    fun scheduleNotificationForEvent(event: Event, userId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val reminderTime = calculateReminderTime(
                LocalDateTime.of(event.fromDate, event.fromTime),
                event.reminderMinutes
            )
            Log.d("NotificationManager", "Scheduling notification for event '${event.title}' at $reminderTime (current: ${LocalDateTime.now()})")
            if (reminderTime.isAfter(LocalDateTime.now())) {
                val notification = NotificationSchedule(
                    id = UUID.randomUUID().toString(),
                    agendaItemId = event.id,
                    agendaItemType = "event",
                    title = event.title.ifEmpty { "Untitled Event" },
                    description = event.description,
                    scheduledTime = reminderTime,
                    userId = userId
                )
                cancelNotificationForAgendaItem(event.id)
                notificationScheduleDao.insertNotification(notification)
                scheduleAlarm(notification)
            }
        }
    }
    fun scheduleNotificationForTask(task: Task, userId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val reminderTime = calculateReminderTime(
                LocalDateTime.of(task.date, task.time),
                task.reminderMinutes
            )
            if (reminderTime.isAfter(LocalDateTime.now())) {
                val notification = NotificationSchedule(
                    id = UUID.randomUUID().toString(),
                    agendaItemId = task.id,
                    agendaItemType = "task",
                    title = task.title.ifEmpty { "Untitled Task" },
                    description = task.description,
                    scheduledTime = reminderTime,
                    userId = userId
                )
                cancelNotificationForAgendaItem(task.id)
                notificationScheduleDao.insertNotification(notification)
                scheduleAlarm(notification)
            }
        }
    }
    fun scheduleNotificationForReminder(reminder: Reminder, userId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val reminderTime = calculateReminderTime(
                LocalDateTime.of(reminder.date, reminder.time),
                reminder.reminderMinutes
            )
            if (reminderTime.isAfter(LocalDateTime.now())) {
                val notification = NotificationSchedule(
                    id = UUID.randomUUID().toString(),
                    agendaItemId = reminder.id,
                    agendaItemType = "reminder",
                    title = reminder.title.ifEmpty { "Untitled Reminder" },
                    description = reminder.description,
                    scheduledTime = reminderTime,
                    userId = userId
                )
                cancelNotificationForAgendaItem(reminder.id)
                notificationScheduleDao.insertNotification(notification)
                scheduleAlarm(notification)
            }
        }
    }
    private fun calculateReminderTime(itemDateTime: LocalDateTime, reminderMinutes: Int): LocalDateTime {
        return itemDateTime.minusMinutes(reminderMinutes.toLong())
    }
    private fun scheduleAlarm(notification: NotificationSchedule) {
        Log.d("NotificationManager", "Setting up alarm for notification '${notification.title}' at ${notification.scheduledTime}")
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("notification_id", notification.id)
            putExtra("agenda_item_id", notification.agendaItemId)
            putExtra("agenda_item_type", notification.agendaItemType)
            putExtra("title", notification.title)
            putExtra("description", notification.description)
            putExtra("user_id", notification.userId)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            notification.agendaItemId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val triggerTime = notification.scheduledTime
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
        } else {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
        }
        Log.d("NotificationManager", "Alarm scheduled successfully for $triggerTime (${Date(triggerTime)})")
    }
    fun cancelNotificationForAgendaItem(agendaItemId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val intent = Intent(context, NotificationReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                agendaItemId.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.cancel(pendingIntent)
            notificationScheduleDao.deleteNotificationByAgendaItemId(agendaItemId)
        }
    }
    fun cancelAllNotificationsForUser(userId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val notifications = notificationScheduleDao.getActiveNotificationsForUser(userId)
            notifications.collect { notificationList ->
                notificationList.forEach { notification ->
                    val intent = Intent(context, NotificationReceiver::class.java)
                    val pendingIntent = PendingIntent.getBroadcast(
                        context,
                        notification.agendaItemId.hashCode(),
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                    alarmManager.cancel(pendingIntent)
                }
                notificationScheduleDao.deleteAllNotificationsForUser(userId)
            }
        }
    }
    fun showNotification(
        notificationId: String,
        agendaItemId: String,
        agendaItemType: String,
        title: String,
        description: String
    ) {
        val deepLinkIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            data = Uri.parse("tasky://agenda/$agendaItemType/$agendaItemId")
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
            notificationManager.notify(notificationId.hashCode(), notification)
            CoroutineScope(Dispatchers.IO).launch {
                notificationScheduleDao.deactivateNotification(notificationId)
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }
    fun rescheduleAllNotifications(userId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val notifications = notificationScheduleDao.getActiveNotificationsForUser(userId)
            notifications.collect { notificationList ->
                notificationList.forEach { notification ->
                    if (notification.scheduledTime.isAfter(LocalDateTime.now())) {
                        scheduleAlarm(notification)
                    }
                }
            }
        }
    }
    fun scheduleTestNotification(userId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val testTime = LocalDateTime.now().plusMinutes(1)
            val notification = NotificationSchedule(
                id = UUID.randomUUID().toString(),
                agendaItemId = "test_item_${System.currentTimeMillis()}",
                agendaItemType = "test",
                title = "Test Notification",
                description = "This is a test notification scheduled for $testTime",
                scheduledTime = testTime,
                userId = userId
            )
            notificationScheduleDao.insertNotification(notification)
            scheduleAlarm(notification)
            Log.d("NotificationManager", "Test notification scheduled for $testTime")
        }
    }
}
