package com.dilekbaykara.tasky.features.shared.domain.model
import com.dilekbaykara.tasky.features.agenda.data.local.entities.Event as EventEntity
import com.dilekbaykara.tasky.features.agenda.data.local.entities.Reminder as ReminderEntity
import com.dilekbaykara.tasky.features.agenda.data.local.entities.Task as TaskEntity
fun Event.toEntity(): EventEntity {
    return EventEntity(
        id = id,
        title = title,
        description = description,
        fromDate = from.toLocalDate(),
        fromTime = from.toLocalTime(),
        toDate = to.toLocalDate(),
        toTime = to.toLocalTime(),
        reminderMinutes = 30, photos = photos,
        visitors = attendees
    )
}
fun Task.toEntity(): TaskEntity {
    return TaskEntity(
        id = id,
        title = title,
        description = description,
        date = time.toLocalDate(),
        time = time.toLocalTime(),
        reminderMinutes = 30,
        isDone = isDone
    )
}
fun Reminder.toEntity(): ReminderEntity {
    return ReminderEntity(
        id = id,
        title = title,
        description = description,
        date = time.toLocalDate(),
        time = time.toLocalTime(),
        reminderMinutes = 30
    )
}
fun EventEntity.toDomainModel(): Event {
    return Event(
        id = id,
        title = title,
        description = description,
        from = fromDate.atTime(fromTime),
        to = toDate.atTime(toTime),
        remindAt = fromDate.atTime(fromTime).minusMinutes(reminderMinutes.toLong()),
        host = "user", isUserEventCreator = true, attendees = visitors,
        photos = photos
    )
}
fun TaskEntity.toDomainModel(): Task {
    return Task(
        id = id,
        title = title,
        description = description,
        time = date.atTime(time),
        remindAt = date.atTime(time).minusMinutes(reminderMinutes.toLong()),
        isDone = isDone
    )
}
fun ReminderEntity.toDomainModel(): Reminder {
    return Reminder(
        id = id,
        title = title,
        description = description,
        time = date.atTime(time),
        remindAt = date.atTime(time).minusMinutes(reminderMinutes.toLong())
    )
}
