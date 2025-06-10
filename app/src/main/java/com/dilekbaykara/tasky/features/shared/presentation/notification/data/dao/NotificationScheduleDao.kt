package com.dilekbaykara.tasky.features.shared.presentation.notification.data.dao
import androidx.room.*
import com.dilekbaykara.tasky.features.shared.presentation.notification.data.entity.NotificationSchedule
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
@Dao
interface NotificationScheduleDao {
    @Query("SELECT * FROM notification_schedules WHERE userId = :userId AND isActive = 1")
    fun getActiveNotificationsForUser(userId: String): Flow<List<NotificationSchedule>>

    @Query("SELECT * FROM notification_schedules WHERE userId = :userId AND scheduledTime <= :currentTime AND isActive = 1")
    suspend fun getDueNotifications(userId: String, currentTime: LocalDateTime): List<NotificationSchedule>

    @Query("SELECT * FROM notification_schedules WHERE agendaItemId = :agendaItemId")
    suspend fun getNotificationByAgendaItemId(agendaItemId: String): NotificationSchedule?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationSchedule)

    @Update
    suspend fun updateNotification(notification: NotificationSchedule)

    @Query("DELETE FROM notification_schedules WHERE agendaItemId = :agendaItemId")
    suspend fun deleteNotificationByAgendaItemId(agendaItemId: String)

    @Query("DELETE FROM notification_schedules WHERE userId = :userId")
    suspend fun deleteAllNotificationsForUser(userId: String)

    @Query("UPDATE notification_schedules SET isActive = 0 WHERE id = :notificationId")
    suspend fun deactivateNotification(notificationId: String)

    @Query("SELECT * FROM notification_schedules WHERE scheduledTime >= :fromTime AND scheduledTime <= :toTime AND isActive = 1")
    suspend fun getNotificationsInTimeRange(fromTime: LocalDateTime, toTime: LocalDateTime): List<NotificationSchedule>
}
