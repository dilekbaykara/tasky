package com.dilekbaykara.tasky.features.agenda.presentation
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dilekbaykara.tasky.features.agenda.data.AgendaRepository
import com.dilekbaykara.tasky.features.agenda.data.local.entities.Event
import com.dilekbaykara.tasky.features.agenda.data.local.entities.Reminder
import com.dilekbaykara.tasky.features.agenda.data.local.entities.Task
import com.dilekbaykara.tasky.features.auth.data.AuthService
import com.dilekbaykara.tasky.features.shared.presentation.notification.TaskyNotificationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID
import javax.inject.Inject
data class AgendaUiState(
    val selectedDate: LocalDate = LocalDate.now(),
    val events: List<Event> = emptyList(),
    val tasks: List<Task> = emptyList(),
    val reminders: List<Reminder> = emptyList(),
    val isLoading: Boolean = false,
    val isOnline: Boolean = true,
    val isSyncing: Boolean = false
)
private const val SELECTED_DATE_KEY = "selected_date"

@HiltViewModel
class AgendaViewModel @Inject constructor(
    private val agendaRepository: AgendaRepository,
    private val notificationManager: TaskyNotificationManager,
    private val authService: AuthService,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _uiState = MutableStateFlow(AgendaUiState())
    val uiState: StateFlow<AgendaUiState> = _uiState.asStateFlow()
    private var _isDateToggled = MutableStateFlow(false)
    var isDateToggled = _isDateToggled.asStateFlow()
    private var loadDataJob: Job? = null
    init {
        val savedDate = savedStateHandle.get<String>(SELECTED_DATE_KEY)
        if (savedDate != null) {
            try {
                val date = LocalDate.parse(savedDate)
                _uiState.value = _uiState.value.copy(selectedDate = date)
            } catch (e: Exception) {
            }
        }
        viewModelScope.launch {
            authService.isLoggedIn.collect { isLoggedIn ->
                if (isLoggedIn) {
                    syncAgendaIfOnline()
                } else {
                    agendaRepository.clearAllLocalData()
                    _uiState.value = _uiState.value.copy(
                        events = emptyList(),
                        tasks = emptyList(),
                        reminders = emptyList()
                    )
                }
            }
        }
        loadItemsForSelectedDate()
        if (authService.isAuthenticated()) {
            syncAgendaIfOnline()
        }
    }
    fun selectDate(date: LocalDate) {
        _uiState.value = _uiState.value.copy(selectedDate = date)
        savedStateHandle[SELECTED_DATE_KEY] = date.toString()
        loadItemsForSelectedDate()
    }
    fun setSelectedDateFromNavigation(date: LocalDate) {
        _uiState.value = _uiState.value.copy(selectedDate = date)
        savedStateHandle[SELECTED_DATE_KEY] = date.toString()
        loadItemsForSelectedDate()
    }
    private fun loadItemsForSelectedDate() {
        loadDataJob?.cancel()
        loadDataJob = viewModelScope.launch {
            val selectedDate = _uiState.value.selectedDate
            agendaRepository.getAgendaForDate(selectedDate).collect { (events, tasks, reminders) ->
                _uiState.value = _uiState.value.copy(
                    selectedDate = selectedDate,
                    events = events,
                    tasks = tasks,
                    reminders = reminders,
                    isLoading = false,
                    isOnline = agendaRepository.isOnline()
                )
            }
        }
    }
    private fun refreshCurrentDate() {
        loadItemsForSelectedDate()
    }
    fun syncAgendaIfOnline() {
        if (!agendaRepository.isOnline()) {
            _uiState.value = _uiState.value.copy(isOnline = false)
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSyncing = true)
            try {
                val result = agendaRepository.syncAgenda()
                if (result.isSuccess) {
                    loadItemsForSelectedDate()
                }
            } catch (e: Exception) {
            } finally {
                _uiState.value = _uiState.value.copy(
                    isSyncing = false,
                    isOnline = agendaRepository.isOnline()
                )
            }
        }
    }
    fun refreshAfterChange() {
        loadItemsForSelectedDate()
        if (agendaRepository.isOnline() && authService.isAuthenticated()) {
            syncAgendaIfOnline()
        }
    }
    fun deleteEvent(eventId: String) {
        viewModelScope.launch {
            agendaRepository.deleteEvent(eventId)
            notificationManager.cancelNotificationForAgendaItem(eventId)
            refreshCurrentDate()
        }
    }
    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            agendaRepository.deleteTask(taskId)
            notificationManager.cancelNotificationForAgendaItem(taskId)
            refreshCurrentDate()
        }
    }
    fun deleteReminder(reminderId: String) {
        viewModelScope.launch {
            agendaRepository.deleteReminder(reminderId)
            notificationManager.cancelNotificationForAgendaItem(reminderId)
            refreshCurrentDate()
        }
    }
    fun deleteEventById(id: String) {
        deleteEvent(id)
    }
    fun deleteTaskById(id: String) {
        deleteTask(id)
    }
    fun deleteReminderById(id: String) {
        deleteReminder(id)
    }
    fun toggleTaskDone(taskId: String) {
        viewModelScope.launch {
            val currentTasks = _uiState.value.tasks
            val task = currentTasks.find { it.id == taskId }
            task?.let {
                val updatedTask = it.copy(isDone = !it.isDone)
                agendaRepository.updateTask(updatedTask)
                val userId = authService.getCurrentUserId()
                notificationManager.scheduleNotificationForTask(updatedTask, userId)
                loadItemsForSelectedDate()
            }
        }
    }
    fun createNewEvent(): Event {
        val selectedDate = _uiState.value.selectedDate
        return Event(
            id = UUID.randomUUID().toString(),
            title = "",
            description = "",
            fromDate = selectedDate,
            fromTime = LocalTime.now(),
            toDate = selectedDate,
            toTime = LocalTime.now().plusHours(1),
            reminderMinutes = 30,
            visitors = emptyList(),
            photos = emptyList()
        )
    }
    fun createNewTask(): Task {
        return Task(
            id = UUID.randomUUID().toString(),
            title = "",
            description = "",
            date = _uiState.value.selectedDate,
            time = LocalTime.now(),
            reminderMinutes = 30,
            isDone = false
        )
    }
    fun createNewReminder(): Reminder {
        return Reminder(
            id = UUID.randomUUID().toString(),
            title = "",
            description = "",
            date = _uiState.value.selectedDate,
            time = LocalTime.now(),
            reminderMinutes = 30
        )
    }
    fun scheduleTestNotification() {
        val userId = "default_user_id"
        notificationManager.scheduleTestNotification(userId)
    }
}
