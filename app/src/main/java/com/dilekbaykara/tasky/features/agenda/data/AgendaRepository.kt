package com.dilekbaykara.tasky.features.agenda.data
import com.dilekbaykara.tasky.features.agenda.data.local.entities.Event
import com.dilekbaykara.tasky.features.agenda.data.local.entities.Reminder
import com.dilekbaykara.tasky.features.agenda.data.local.entities.Task
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
interface AgendaRepository {
    fun getAgendaForDate(date: LocalDate): Flow<Triple<List<Event>, List<Task>, List<Reminder>>>
    suspend fun syncAgenda(): Result<Unit>
    suspend fun clearAllLocalData()
    suspend fun createEvent(event: Event): Result<Unit>
    suspend fun updateEvent(event: Event): Result<Unit>
    suspend fun deleteEvent(eventId: String): Result<Unit>
    suspend fun createTask(task: Task): Result<Unit>
    suspend fun updateTask(task: Task): Result<Unit>
    suspend fun deleteTask(taskId: String): Result<Unit>
    suspend fun createReminder(reminder: Reminder): Result<Unit>
    suspend fun updateReminder(reminder: Reminder): Result<Unit>
    suspend fun deleteReminder(reminderId: String): Result<Unit>
    fun isOnline(): Boolean
}
