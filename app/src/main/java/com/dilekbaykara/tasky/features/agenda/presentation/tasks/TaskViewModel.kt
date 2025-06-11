package com.dilekbaykara.tasky.features.agenda.presentation.tasks
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dilekbaykara.tasky.features.agenda.data.AgendaRepository
import com.dilekbaykara.tasky.features.agenda.data.local.dao.TaskDao
import com.dilekbaykara.tasky.features.agenda.data.local.entities.Task
import com.dilekbaykara.tasky.features.auth.data.AuthService
import com.dilekbaykara.tasky.features.shared.presentation.notification.TaskyNotificationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class TaskViewModel @Inject constructor(
    private val taskDao: TaskDao,
    private val agendaRepository: AgendaRepository,
    private val notificationManager: TaskyNotificationManager,
    private val authService: AuthService
) : ViewModel() {
    private val _currentTask = MutableStateFlow<Task?>(null)
    val currentTask: StateFlow<Task?> = _currentTask.asStateFlow()
    fun setNewTask(task: Task) {
        _currentTask.value = task
    }
    fun loadTask(taskId: String) {
        viewModelScope.launch {
            _currentTask.value = taskDao.getTaskById(taskId)
        }
    }
    fun saveTask(task: Task) {
        viewModelScope.launch {
            val result = agendaRepository.createTask(task)
            val userId = authService.getCurrentUserId()
            notificationManager.scheduleNotificationForTask(task, userId)
        }
    }
    fun updateTask(task: Task) {
        viewModelScope.launch {
            val result = agendaRepository.updateTask(task)
            val userId = authService.getCurrentUserId()
            notificationManager.scheduleNotificationForTask(task, userId)
        }
    }
    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            agendaRepository.deleteTask(taskId)
            notificationManager.cancelNotificationForAgendaItem(taskId)
        }
    }
    fun toggleTaskDone(taskId: String) {
        viewModelScope.launch {
            val task = taskDao.getTaskById(taskId)
            task?.let {
                val updatedTask = it.copy(isDone = !it.isDone)
                agendaRepository.updateTask(updatedTask)
                _currentTask.value = updatedTask
            }
        }
    }
}
