package com.dilekbaykara.tasky.features.agenda.presentation.reminders
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dilekbaykara.tasky.features.agenda.data.AgendaRepository
import com.dilekbaykara.tasky.features.agenda.data.local.dao.ReminderDao
import com.dilekbaykara.tasky.features.agenda.data.local.entities.Reminder
import com.dilekbaykara.tasky.features.auth.data.AuthService
import com.dilekbaykara.tasky.features.shared.presentation.notification.TaskyNotificationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class ReminderViewModel @Inject constructor(
    private val reminderDao: ReminderDao,
    private val agendaRepository: AgendaRepository,
    private val notificationManager: TaskyNotificationManager,
    private val authService: AuthService
) : ViewModel() {
    private val _currentReminder = MutableStateFlow<Reminder?>(null)
    val currentReminder: StateFlow<Reminder?> = _currentReminder.asStateFlow()
    fun loadReminder(reminderId: String) {
        viewModelScope.launch {
            _currentReminder.value = reminderDao.getReminderById(reminderId)
        }
    }
    fun setNewReminder(reminder: Reminder) {
        _currentReminder.value = reminder
    }
    fun saveReminder(reminder: Reminder) {
        viewModelScope.launch {
            agendaRepository.createReminder(reminder)
            val userId = authService.getCurrentUserId()
            notificationManager.scheduleNotificationForReminder(reminder, userId)
        }
    }
    fun updateReminder(reminder: Reminder) {
        viewModelScope.launch {
            agendaRepository.updateReminder(reminder)
            val userId = authService.getCurrentUserId()
            notificationManager.scheduleNotificationForReminder(reminder, userId)
        }
    }
    fun deleteReminder(reminderId: String) {
        viewModelScope.launch {
            agendaRepository.deleteReminder(reminderId)
            notificationManager.cancelNotificationForAgendaItem(reminderId)
        }
    }
}