package com.dilekbaykara.tasky.features.agenda.presentation.events
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dilekbaykara.tasky.features.agenda.data.AgendaRepository
import com.dilekbaykara.tasky.features.agenda.data.local.dao.EventDao
import com.dilekbaykara.tasky.features.agenda.data.local.entities.Event
import com.dilekbaykara.tasky.features.auth.data.AuthService
import com.dilekbaykara.tasky.features.shared.data.remote.AttendeeResponse
import com.dilekbaykara.tasky.features.shared.data.remote.TaskyApi
import com.dilekbaykara.tasky.features.shared.presentation.notification.TaskyNotificationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class EventViewModel @Inject constructor(
    private val eventDao: EventDao,
    private val agendaRepository: AgendaRepository,
    private val notificationManager: TaskyNotificationManager,
    private val authService: AuthService,
    private val taskyApi: TaskyApi
) : ViewModel() {
    private val _currentEvent = MutableStateFlow<Event?>(null)
    val currentEvent: StateFlow<Event?> = _currentEvent.asStateFlow()
    fun loadEvent(eventId: String) {
        viewModelScope.launch {
            _currentEvent.value = eventDao.getEventById(eventId)
        }
    }
    fun setNewEvent(event: Event) {
        _currentEvent.value = event
    }
    fun saveEvent(event: Event) {
        viewModelScope.launch {
            agendaRepository.createEvent(event)
            val userId = authService.getCurrentUserId()
            notificationManager.scheduleNotificationForEvent(event, userId)
        }
    }
    fun updateEvent(event: Event) {
        viewModelScope.launch {
            agendaRepository.updateEvent(event)
            val userId = authService.getCurrentUserId()
            notificationManager.scheduleNotificationForEvent(event, userId)
        }
    }
    fun deleteEvent(eventId: String) {
        viewModelScope.launch {
            agendaRepository.deleteEvent(eventId)
            notificationManager.cancelNotificationForAgendaItem(eventId)
        }
    }

    suspend fun checkAttendee(email: String): AttendeeResponse? {
        return try {
            val response = taskyApi.getAttendee(email)
            if (response.isSuccessful) {
                val body = response.body()
                if (body?.doesUserExist == true) body.attendee else null
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
    fun getCurrentUserId(): String = authService.getCurrentUserId()
    fun getFullName(): String? = authService.getFullName()
}
