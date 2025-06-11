package com.dilekbaykara.tasky.features.agenda.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalTime
@Entity(tableName = "reminders")
data class Reminder(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String,
    val date: LocalDate,
    val time: LocalTime,
    val reminderMinutes: Int = 30,
    val createdAt: Long = System.currentTimeMillis()
)