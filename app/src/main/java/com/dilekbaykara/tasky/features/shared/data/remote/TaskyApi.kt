package com.dilekbaykara.tasky.features.shared.data.remote
import com.dilekbaykara.tasky.features.auth.domain.AccessTokenResponse
import com.dilekbaykara.tasky.features.auth.domain.LoginRequest
import com.dilekbaykara.tasky.features.auth.domain.LoginResponse
import com.dilekbaykara.tasky.features.auth.domain.RefreshTokenRequest
import com.dilekbaykara.tasky.features.auth.domain.RegisterRequest
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Query
interface TaskyApi {
    @POST("register")
    suspend fun register(@Body request: RegisterRequest): Response<Unit>

    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("accessToken")
    suspend fun refreshAccessToken(@Body request: RefreshTokenRequest): Response<AccessTokenResponse>

    @GET("authenticate")
    suspend fun authenticate(): Response<Unit>

    @GET("logout")
    suspend fun logout(): Response<Unit>

    @GET("agenda")
    suspend fun getAgenda(
        @Query("timezone") timezone: String,
        @Query("time") time: Long
    ): Response<AgendaResponse>

    @GET("fullAgenda")
    suspend fun getFullAgenda(): Response<FullAgendaResponse>

    @Multipart
    @POST("event")
    suspend fun createEvent(
        @Part("create_event_request") eventRequestJson: okhttp3.RequestBody,
        @Part photos: List<MultipartBody.Part> = emptyList()
    ): Response<EventResponse>

    @Multipart
    @PUT("event")
    suspend fun updateEvent(
        @Part("update_event_request") eventRequestJson: okhttp3.RequestBody,
        @Part photos: List<MultipartBody.Part> = emptyList()
    ): Response<EventResponse>

    @GET("event")
    suspend fun getEvent(
        @Query("eventId") eventId: String
    ): Response<EventResponse>

    @DELETE("event")
    suspend fun deleteEvent(
        @Query("eventId") eventId: String
    ): Response<Unit>

    @POST("task")
    suspend fun createTask(
        @Body task: CreateTaskRequest
    ): Response<TaskResponse>

    @PUT("task")
    suspend fun updateTask(
        @Body task: UpdateTaskRequest
    ): Response<TaskResponse>

    @GET("task")
    suspend fun getTask(
        @Query("taskId") taskId: String
    ): Response<TaskResponse>

    @DELETE("task")
    suspend fun deleteTask(
        @Query("taskId") taskId: String
    ): Response<Unit>

    @POST("reminder")
    suspend fun createReminder(
        @Body reminder: CreateReminderRequest
    ): Response<ReminderResponse>

    @PUT("reminder")
    suspend fun updateReminder(
        @Body reminder: UpdateReminderRequest
    ): Response<ReminderResponse>

    @GET("reminder")
    suspend fun getReminder(
        @Query("reminderId") reminderId: String
    ): Response<ReminderResponse>

    @DELETE("reminder")
    suspend fun deleteReminder(
        @Query("reminderId") reminderId: String
    ): Response<Unit>

    @Multipart
    @POST("photo")
    suspend fun uploadPhoto(
        @Part photo: MultipartBody.Part
    ): Response<PhotoResponse>

    @DELETE("photo")
    suspend fun deletePhoto(
        @Query("key") photoKey: String
    ): Response<Unit>

    @GET("attendee")
    suspend fun getAttendee(
        @Query("email") email: String
    ): Response<CheckAttendeeResponse>

    @DELETE("attendee")
    suspend fun deleteAttendee(
        @Query("eventId") eventId: String
    ): Response<Unit>

    @POST("syncAgenda")
    suspend fun syncAgenda(
        @Body request: SyncAgendaRequest
    ): Response<Unit>
}
data class AgendaResponse(
    val events: List<EventResponse>,
    val tasks: List<TaskResponse>,
    val reminders: List<ReminderResponse>
)
data class FullAgendaResponse(
    val events: List<EventResponse>,
    val tasks: List<TaskResponse>,
    val reminders: List<ReminderResponse>
)
data class EventResponse(
    val id: String,
    val title: String,
    val description: String,
    val from: Long,
    val to: Long,
    val remindAt: Long,
    val host: String,
    val isUserEventCreator: Boolean,
    val attendees: List<AttendeeResponse>,
    val photos: List<PhotoResponse>
)
data class TaskResponse(
    val id: String,
    val title: String,
    val description: String,
    val time: Long,
    val remindAt: Long,
    val isDone: Boolean
)
data class ReminderResponse(
    val id: String,
    val title: String,
    val description: String,
    val time: Long,
    val remindAt: Long
)
data class AttendeeResponse(
    val email: String,
    val fullName: String,
    val userId: String,
    val eventId: String,
    val isGoing: Boolean,
    val remindAt: Long
)
data class PhotoResponse(
    val key: String,
    val url: String
)
data class CreateEventRequest(
    val id: String,
    val title: String,
    val description: String,
    val from: Long,
    val to: Long,
    val remindAt: Long,
    val attendeeIds: List<String>
)
data class UpdateEventRequest(
    val id: String,
    val title: String,
    val description: String,
    val from: Long,
    val to: Long,
    val remindAt: Long,
    val attendeeIds: List<String>,
    val deletedPhotoKeys: List<String>,
    val isGoing: Boolean
)
data class CreateTaskRequest(
    val id: String,
    val title: String,
    val description: String,
    val time: Long,
    val remindAt: Long,
    val isDone: Boolean
)
data class UpdateTaskRequest(
    val id: String,
    val title: String,
    val description: String,
    val time: Long,
    val remindAt: Long,
    val isDone: Boolean
)
data class CreateReminderRequest(
    val id: String,
    val title: String,
    val description: String,
    val time: Long,
    val remindAt: Long
)
data class UpdateReminderRequest(
    val id: String,
    val title: String,
    val description: String,
    val time: Long,
    val remindAt: Long
)
data class SyncAgendaRequest(
    val deletedEventIds: List<String>,
    val deletedTaskIds: List<String>,
    val deletedReminderIds: List<String>
)
data class CheckAttendeeResponse(
    val doesUserExist: Boolean,
    val attendee: AttendeeResponse?
)
