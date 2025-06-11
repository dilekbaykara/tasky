package com.dilekbaykara.tasky.features.shared.domain.model
import java.time.LocalDateTime
data class Task(
    val id: String,
    val title: String,
    val description: String,
    val time: LocalDateTime,
    val remindAt: LocalDateTime,
    val isDone: Boolean
)
