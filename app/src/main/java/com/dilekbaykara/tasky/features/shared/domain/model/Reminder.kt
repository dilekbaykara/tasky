package com.dilekbaykara.tasky.features.shared.domain.model
import java.time.LocalDateTime
data class Reminder(
    val id: String,
    val title: String,
    val description: String,
    val time: LocalDateTime,
    val remindAt: LocalDateTime
)
