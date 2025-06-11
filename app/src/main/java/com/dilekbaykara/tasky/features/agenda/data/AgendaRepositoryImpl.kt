package com.dilekbaykara.tasky.features.agenda.data
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.util.Log
import com.dilekbaykara.tasky.features.agenda.data.local.dao.EventDao
import com.dilekbaykara.tasky.features.agenda.data.local.dao.ReminderDao
import com.dilekbaykara.tasky.features.agenda.data.local.dao.TaskDao
import com.dilekbaykara.tasky.features.agenda.data.local.entities.Event
import com.dilekbaykara.tasky.features.agenda.data.local.entities.Reminder
import com.dilekbaykara.tasky.features.agenda.data.local.entities.Task
import com.dilekbaykara.tasky.features.auth.data.AuthService
import com.dilekbaykara.tasky.features.shared.data.remote.CreateEventRequest
import com.dilekbaykara.tasky.features.shared.data.remote.CreateReminderRequest
import com.dilekbaykara.tasky.features.shared.data.remote.CreateTaskRequest
import com.dilekbaykara.tasky.features.shared.data.remote.EventResponse
import com.dilekbaykara.tasky.features.shared.data.remote.ReminderResponse
import com.dilekbaykara.tasky.features.shared.data.remote.TaskResponse
import com.dilekbaykara.tasky.features.shared.data.remote.TaskyApi
import com.dilekbaykara.tasky.features.shared.data.remote.UpdateEventRequest
import com.dilekbaykara.tasky.features.shared.data.remote.UpdateReminderRequest
import com.dilekbaykara.tasky.features.shared.data.remote.UpdateTaskRequest
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton
@Singleton
class AgendaRepositoryImpl @Inject constructor(
    private val eventDao: EventDao,
    private val taskDao: TaskDao,
    private val reminderDao: ReminderDao,
    private val api: TaskyApi,
    private val authService: AuthService,
    private val context: Context
) : AgendaRepository {
    private val gson = Gson()
    override fun getAgendaForDate(date: LocalDate): Flow<Triple<List<Event>, List<Task>, List<Reminder>>> {
        return combine(
            eventDao.getEventsByDate(date),
            taskDao.getTasksByDate(date),
            reminderDao.getRemindersByDate(date)
        ) { events, tasks, reminders ->
            Triple(events, tasks, reminders)
        }
    }
    override suspend fun syncAgenda(): Result<Unit> {
        return try {
            val token = authService.getToken()
            if (token == null || !isOnline()) {
                return Result.failure(Exception("Not authenticated or offline"))
            }
            Log.d("AgendaRepository", "Starting agenda sync with token: ${token.take(20)}...")
            try {
                val allLocalEvents = eventDao.getAllEventsSync()
                val allLocalTasks = taskDao.getAllTasksSync()
                val allLocalReminders = reminderDao.getAllRemindersSync()
                Log.d("AgendaRepository", "Found local items to upload - events: ${allLocalEvents.size}, tasks: ${allLocalTasks.size}, reminders: ${allLocalReminders.size}")
                allLocalEvents.forEach { event ->
                    Log.d("AgendaRepository", "Local event found: ${event.title} (${event.id}) - date: ${event.fromDate}")
                }
                allLocalTasks.forEach { task ->
                    Log.d("AgendaRepository", "Local task found: ${task.title} (${task.id}) - date: ${task.date}")
                }
                allLocalReminders.forEach { reminder ->
                    Log.d("AgendaRepository", "Local reminder found: ${reminder.title} (${reminder.id}) - date: ${reminder.date}")
                }
                var eventsUploaded = 0
                allLocalEvents.forEach { event ->
                    try {
                        val request = event.toCreateRequest()
                        val jsonString = gson.toJson(request)
                        val requestBody = jsonString.toRequestBody("application/json".toMediaType())

                        // Convert photo URIs to multipart parts
                        val photoParts = convertPhotosToMultipartParts(event.photos)
                        Log.d("AgendaRepository", "Converting ${event.photos.size} photos to ${photoParts.size} multipart parts")

                        val response = api.createEvent(requestBody, photoParts)
                        if (response.isSuccessful) {
                            eventsUploaded++
                            Log.d("AgendaRepository", "Successfully uploaded event: ${event.title}")
                        } else {
                            Log.d("AgendaRepository", "Failed to upload event: ${event.title}, code: ${response.code()}")
                        }
                    } catch (e: Exception) {
                        Log.d("AgendaRepository", "Exception uploading event: ${event.title}, error: ${e.message}")
                    }
                }
                var tasksUploaded = 0
                allLocalTasks.forEach { task ->
                    try {
                        val request = task.toCreateRequest()
                        val response = api.createTask(request)
                        if (response.isSuccessful) {
                            tasksUploaded++
                            Log.d("AgendaRepository", "Successfully uploaded task: ${task.title}")
                        } else {
                            Log.d("AgendaRepository", "Failed to upload task: ${task.title}, code: ${response.code()}")
                        }
                    } catch (e: Exception) {
                        Log.d("AgendaRepository", "Exception uploading task: ${task.title}, error: ${e.message}")
                    }
                }
                var remindersUploaded = 0
                allLocalReminders.forEach { reminder ->
                    try {
                        val request = reminder.toCreateRequest()
                        val response = api.createReminder(request)
                        if (response.isSuccessful) {
                            remindersUploaded++
                            Log.d("AgendaRepository", "Successfully uploaded reminder: ${reminder.title}")
                        } else {
                            Log.d("AgendaRepository", "Failed to upload reminder: ${reminder.title}, code: ${response.code()}")
                        }
                    } catch (e: Exception) {
                        Log.d("AgendaRepository", "Exception uploading reminder: ${reminder.title}, error: ${e.message}")
                    }
                }
                Log.d("AgendaRepository", "Upload summary - events: $eventsUploaded/${allLocalEvents.size}, tasks: $tasksUploaded/${allLocalTasks.size}, reminders: $remindersUploaded/${allLocalReminders.size}")
            } catch (e: Exception) {
                Log.d("AgendaRepository", "Error during upload phase: ${e.message}")
            }
            val response = api.getFullAgenda()
            if (response.isSuccessful) {
                val agendaData = response.body()
                if (agendaData != null) {
                    agendaData.events.forEach { eventResponse ->
                        val event = eventResponse.toEntity()
                        val existingEvent = eventDao.getEventById(event.id)
                        if (existingEvent == null) {
                            eventDao.insertEvent(event)
                        }
                    }
                    agendaData.tasks.forEach { taskResponse ->
                        val task = taskResponse.toEntity()
                        val existingTask = taskDao.getTaskById(task.id)
                        if (existingTask == null) {
                            taskDao.insertTask(task)
                        }
                    }
                    agendaData.reminders.forEach { reminderResponse ->
                        val reminder = reminderResponse.toEntity()
                        val existingReminder = reminderDao.getReminderById(reminder.id)
                        if (existingReminder == null) {
                            reminderDao.insertReminder(reminder)
                        }
                    }
                }
                Result.success(Unit)
            } else {
                Result.failure(Exception("Sync failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    override suspend fun clearAllLocalData() {
        eventDao.deleteAll()
        taskDao.deleteAll()
        reminderDao.deleteAll()
    }
    override suspend fun createEvent(event: Event): Result<Unit> {
        return try {
            Log.d("AgendaRepository", "Creating event locally: ${event.title} (${event.id}) for date: ${event.fromDate}")
            eventDao.insertEvent(event)
            Log.d("AgendaRepository", "Event saved to local database: ${event.title}")
            val token = authService.getToken()
            if (token != null && isOnline()) {
                try {
                    val request = event.toCreateRequest()
                    val jsonString = gson.toJson(request)
                    val requestBody = jsonString.toRequestBody("application/json".toMediaType())

                    // Convert photo URIs to multipart parts
                    val photoParts = convertPhotosToMultipartParts(event.photos)
                    Log.d("AgendaRepository", "Converting ${event.photos.size} photos to ${photoParts.size} multipart parts")

                    val response = api.createEvent(requestBody, photoParts)
                    if (response.isSuccessful) {
                        Log.d("AgendaRepository", "Event uploaded to server successfully: ${event.title}")
                        response.body()?.let { eventResponse ->
                            val updatedEvent = event.copy(photos = eventResponse.photos.map { it.url })
                            eventDao.updateEvent(updatedEvent)
                        }
                    } else {
                        Log.d("AgendaRepository", "Event upload failed: ${event.title}, code: ${response.code()}")
                    }
                } catch (e: Exception) {
                    Log.d("AgendaRepository", "Event upload exception: ${event.title}, error: ${e.message}")
                }
            } else {
                Log.d("AgendaRepository", "Event not uploaded - offline or no token: ${event.title}")
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.d("AgendaRepository", "Error creating event: ${event.title}, error: ${e.message}")
            Result.failure(e)
        }
    }
    override suspend fun updateEvent(event: Event): Result<Unit> {
        return try {
            eventDao.updateEvent(event)
            val token = authService.getToken()
            if (token != null && isOnline()) {
                try {
                    val localPhotoUris = event.photos.filter { it.startsWith("content://") || it.startsWith("file://") }
                    val remotePhotoUrls = event.photos.filter { it.startsWith("http") }

                    val deletedPhotoKeys: List<String> = emptyList()

                    val request = event.toUpdateRequest().copy(
                        deletedPhotoKeys = deletedPhotoKeys
                    )
                    val jsonString = gson.toJson(request)
                    val requestBody = jsonString.toRequestBody("application/json".toMediaType())

                    val photoParts = convertPhotosToMultipartParts(localPhotoUris)
                    Log.d("AgendaRepository", "Updating event with ${localPhotoUris.size} new photos, ${remotePhotoUrls.size} existing, ${deletedPhotoKeys.size} deleted")

                    val response = api.updateEvent(requestBody, photoParts)
                    if (response.isSuccessful) {
                        Log.d("AgendaRepository", "Event updated on server successfully: ${event.title}")
                        response.body()?.let { eventResponse ->
                            val updatedEvent = event.copy(photos = eventResponse.photos.map { it.url })
                            eventDao.updateEvent(updatedEvent)
                        }
                    } else {
                        Log.d("AgendaRepository", "Event update failed: ${event.title}, code: ${response.code()}")
                    }
                } catch (e: Exception) {
                    Log.d("AgendaRepository", "Event update exception: ${event.title}, error: ${e.message}")
                }
            } else {
                Log.d("AgendaRepository", "Event not updated on server - offline or no token: ${event.title}")
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.d("AgendaRepository", "Error updating event: ${event.title}, error: ${e.message}")
            Result.failure(e)
        }
    }
    override suspend fun deleteEvent(eventId: String): Result<Unit> {
        return try {
            eventDao.deleteEventById(eventId)
            val token = authService.getToken()
            if (token != null && isOnline()) {
                try {
                    api.deleteEvent(eventId)
                } catch (e: Exception) {
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    override suspend fun createTask(task: Task): Result<Unit> {
        return try {
            Log.d("AgendaRepository", "Creating task locally: ${task.title} (${task.id}) for date: ${task.date}")
            taskDao.insertTask(task)
            Log.d("AgendaRepository", "Task saved to local database: ${task.title}")
            val token = authService.getToken()
            if (token != null && isOnline()) {
                try {
                    val request = task.toCreateRequest()
                    val response = api.createTask(request)
                    if (response.isSuccessful) {
                        Log.d("AgendaRepository", "Task uploaded to server successfully: ${task.title}")
                    } else {
                        Log.d("AgendaRepository", "Task upload failed: ${task.title}, code: ${response.code()}")
                    }
                } catch (e: Exception) {
                    Log.d("AgendaRepository", "Task upload exception: ${task.title}, error: ${e.message}")
                }
            } else {
                Log.d("AgendaRepository", "Task not uploaded - offline or no token: ${task.title}")
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.d("AgendaRepository", "Error creating task: ${task.title}, error: ${e.message}")
            Result.failure(e)
        }
    }
    override suspend fun updateTask(task: Task): Result<Unit> {
        return try {
            taskDao.updateTask(task)
            val token = authService.getToken()
            if (token != null && isOnline()) {
                try {
                    val request = task.toUpdateRequest()
                    api.updateTask(request)
                } catch (e: Exception) {
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    override suspend fun deleteTask(taskId: String): Result<Unit> {
        return try {
            taskDao.deleteTaskById(taskId)
            val token = authService.getToken()
            if (token != null && isOnline()) {
                try {
                    api.deleteTask(taskId)
                } catch (e: Exception) {
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    override suspend fun createReminder(reminder: Reminder): Result<Unit> {
        return try {
            reminderDao.insertReminder(reminder)
            val token = authService.getToken()
            if (token != null && isOnline()) {
                try {
                    val request = reminder.toCreateRequest()
                    api.createReminder(request)
                } catch (e: Exception) {
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    override suspend fun updateReminder(reminder: Reminder): Result<Unit> {
        return try {
            reminderDao.updateReminder(reminder)
            val token = authService.getToken()
            if (token != null && isOnline()) {
                try {
                    val request = reminder.toUpdateRequest()
                    api.updateReminder(request)
                } catch (e: Exception) {
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    override suspend fun deleteReminder(reminderId: String): Result<Unit> {
        return try {
            reminderDao.deleteReminderById(reminderId)
            val token = authService.getToken()
            if (token != null && isOnline()) {
                try {
                    api.deleteReminder(reminderId)
                } catch (e: Exception) {
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    override fun isOnline(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private fun convertPhotosToMultipartParts(photoUris: List<String>): List<MultipartBody.Part> {
        val photoParts = mutableListOf<MultipartBody.Part>()
        photoUris.forEachIndexed { index, uriString ->
            try {
                if (uriString.startsWith("content://") || uriString.startsWith("file://")) {
                    val uri = Uri.parse(uriString)
                    val compressedImageData = compressImage(uri)
                    if (compressedImageData != null) {
                        val requestBody = compressedImageData.toRequestBody("image/jpeg".toMediaTypeOrNull())
                        val part = MultipartBody.Part.createFormData("photo$index", "photo$index.jpg", requestBody)
                        photoParts.add(part)
                        Log.d("AgendaRepository", "Created multipart for photo $index, size: ${compressedImageData.size} bytes")
                    }
                } else {
                    Log.d("AgendaRepository", "Skipping non-local photo URI: $uriString")
                }
            } catch (e: Exception) {
                Log.d("AgendaRepository", "Error converting photo $index to multipart: ${e.message}")
            }
        }
        return photoParts
    }

    private fun compressImage(uri: Uri): ByteArray? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            val maxWidth = 1024
            val maxHeight = 1024
            val ratio = minOf(maxWidth.toFloat() / bitmap.width, maxHeight.toFloat() / bitmap.height)
            val newWidth = (bitmap.width * ratio).toInt()
            val newHeight = (bitmap.height * ratio).toInt()
            val resizedBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
            val outputStream = ByteArrayOutputStream()
            var quality = 85
            do {
                outputStream.reset()
                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
                quality -= 5
            } while (outputStream.size() > 1024 * 1024 && quality > 10)
            outputStream.toByteArray()
        } catch (e: Exception) {
            Log.d("AgendaRepository", "Compression error: ${e.message}")
            null
        }
    }
}
private fun EventResponse.toEntity(): Event {
    return Event(
        id = id,
        title = title,
        description = description,
        fromDate = LocalDateTime.ofEpochSecond(
            from / 1000,
            0,
            ZoneId.systemDefault().rules.getOffset(
                Instant.now()
            )
        ).toLocalDate(),
        fromTime = LocalDateTime.ofEpochSecond(
            from / 1000,
            0,
            ZoneId.systemDefault().rules.getOffset(
                Instant.now()
            )
        ).toLocalTime(),
        toDate = LocalDateTime.ofEpochSecond(
            to / 1000,
            0,
            ZoneId.systemDefault().rules.getOffset(
                Instant.now()
            )
        ).toLocalDate(),
        toTime = LocalDateTime.ofEpochSecond(
            to / 1000,
            0,
            ZoneId.systemDefault().rules.getOffset(
                Instant.now()
            )
        ).toLocalTime(),
        reminderMinutes = ((from - remindAt) / 60000).toInt(),
        visitors = attendees.map { it.email },
        photos = photos.map { it.url }
    )
}
private fun TaskResponse.toEntity(): Task {
    return Task(
        id = id,
        title = title,
        description = description,
        date = LocalDateTime.ofEpochSecond(
            time / 1000,
            0,
            ZoneId.systemDefault().rules.getOffset(
                Instant.now()
            )
        ).toLocalDate(),
        time = LocalDateTime.ofEpochSecond(
            time / 1000,
            0,
            ZoneId.systemDefault().rules.getOffset(
                Instant.now()
            )
        ).toLocalTime(),
        reminderMinutes = ((time - remindAt) / 60000).toInt(),
        isDone = isDone
    )
}
private fun ReminderResponse.toEntity(): Reminder {
    return Reminder(
        id = id,
        title = title,
        description = description,
        date = LocalDateTime.ofEpochSecond(
            time / 1000,
            0,
            ZoneId.systemDefault().rules.getOffset(
                Instant.now()
            )
        ).toLocalDate(),
        time = LocalDateTime.ofEpochSecond(
            time / 1000,
            0,
            ZoneId.systemDefault().rules.getOffset(
                Instant.now()
            )
        ).toLocalTime(),
        reminderMinutes = ((time - remindAt) / 60000).toInt()
    )
}
private fun Event.toCreateRequest(): CreateEventRequest {
    val fromDateTime = LocalDateTime.of(fromDate, fromTime)
    val toDateTime = LocalDateTime.of(toDate, toTime)
    val reminderDateTime = fromDateTime.minusMinutes(reminderMinutes.toLong())
    return CreateEventRequest(
        id = id,
        title = title,
        description = description,
        from = fromDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
        to = toDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
        remindAt = reminderDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
        attendeeIds = emptyList()
    )
}
private fun Event.toUpdateRequest(): UpdateEventRequest {
    val fromDateTime = LocalDateTime.of(fromDate, fromTime)
    val toDateTime = LocalDateTime.of(toDate, toTime)
    val reminderDateTime = fromDateTime.minusMinutes(reminderMinutes.toLong())
    return UpdateEventRequest(
        id = id,
        title = title,
        description = description,
        from = fromDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
        to = toDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
        remindAt = reminderDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
        attendeeIds = emptyList(),
        deletedPhotoKeys = emptyList(),
        isGoing = true
    )
}
private fun Task.toCreateRequest(): CreateTaskRequest {
    val taskDateTime = LocalDateTime.of(date, time)
    val reminderDateTime = taskDateTime.minusMinutes(reminderMinutes.toLong())
    return CreateTaskRequest(
        id = id,
        title = title,
        description = description,
        time = taskDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
        remindAt = reminderDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
        isDone = isDone
    )
}
private fun Task.toUpdateRequest(): UpdateTaskRequest {
    val taskDateTime = LocalDateTime.of(date, time)
    val reminderDateTime = taskDateTime.minusMinutes(reminderMinutes.toLong())
    return UpdateTaskRequest(
        id = id,
        title = title,
        description = description,
        time = taskDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
        remindAt = reminderDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
        isDone = isDone
    )
}
private fun Reminder.toCreateRequest(): CreateReminderRequest {
    val reminderDateTime = LocalDateTime.of(date, time)
    val remindAtDateTime = reminderDateTime.minusMinutes(reminderMinutes.toLong())
    return CreateReminderRequest(
        id = id,
        title = title,
        description = description,
        time = reminderDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
        remindAt = remindAtDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    )
}
private fun Reminder.toUpdateRequest(): UpdateReminderRequest {
    val reminderDateTime = LocalDateTime.of(date, time)
    val remindAtDateTime = reminderDateTime.minusMinutes(reminderMinutes.toLong())
    return UpdateReminderRequest(
        id = id,
        title = title,
        description = description,
        time = reminderDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
        remindAt = remindAtDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    )
}
