package com.dilekbaykara.tasky.features.shared.presentation.notification.data.entity
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime
@Entity(tableName = "notification_schedules")
data class NotificationSchedule(
    @PrimaryKey
    val id: String,
    val agendaItemId: String,
    val agendaItemType: String,
    val title: String,
    val description: String,
    val scheduledTime: LocalDateTime,
    val isActive: Boolean = true,
    val userId: String,
    val createdAt: LocalDateTime = LocalDateTime.now()
)
