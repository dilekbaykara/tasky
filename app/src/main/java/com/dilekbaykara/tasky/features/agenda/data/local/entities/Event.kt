package com.dilekbaykara.tasky.features.agenda.data.local.entities
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalTime
@Entity(tableName = "events")
data class Event(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String,
    val fromDate: LocalDate,
    val fromTime: LocalTime,
    val toDate: LocalDate,
    val toTime: LocalTime,
    val reminderMinutes: Int = 30,
    val photos: List<String> = emptyList(),
    val visitors: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)
