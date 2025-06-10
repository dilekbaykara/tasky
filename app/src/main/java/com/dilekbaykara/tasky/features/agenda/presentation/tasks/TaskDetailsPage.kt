package com.dilekbaykara.tasky.features.agenda.presentation.tasks
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.dilekbaykara.tasky.features.agenda.data.local.entities.Task
import com.dilekbaykara.tasky.features.agenda.presentation.AgendaViewModel
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.UUID
@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TaskDetailsPage(
    navController: NavController,
    taskId: String? = null,
    isNewTask: Boolean = false,
    selectedDate: String? = null,
    isEditMode: Boolean = false
) {
    val taskViewModel: TaskViewModel = hiltViewModel()
    val currentTask by taskViewModel.currentTask.collectAsState()
    val agendaViewModel: AgendaViewModel = hiltViewModel()
    LaunchedEffect(taskId, isNewTask) {
        if (isNewTask) {
            val targetDate = selectedDate?.let { LocalDate.parse(it) } ?: LocalDate.now()
            val newTask = Task(
                id = UUID.randomUUID().toString(),
                title = "",
                description = "",
                date = targetDate,
                time = LocalTime.now(),
                reminderMinutes = 30,
                isDone = false
            )
            taskViewModel.setNewTask(newTask)
        } else if (taskId != null) {
            taskViewModel.loadTask(taskId)
        }
    }
    val task = currentTask ?: return
    var showTimePicker by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showDeleteTaskSheet by remember { mutableStateOf(false) }
    var title by remember(task) { mutableStateOf(task.title) }
    var description by remember(task) { mutableStateOf(task.description) }
    var tempTitle by remember(title) { mutableStateOf(title) }
    var tempDescription by remember(description) { mutableStateOf(description) }
    var editingTitle by remember { mutableStateOf(false) }
    var editingDescription by remember { mutableStateOf(false) }
    var timeState by remember(task) { mutableStateOf(task.time) }
    var dateState by remember(task) { mutableStateOf(task.date) }
    var reminderMinutes by remember(task) { mutableStateOf(task.reminderMinutes) }
    var isDone by remember(task) { mutableStateOf(task.isDone) }
    var isEditMode by remember { mutableStateOf(isNewTask || isEditMode) }
    val context = LocalContext.current
    val saveTask = {
        val updatedTask = task.copy(
            title = title,
            description = description,
            date = dateState,
            time = timeState,
            reminderMinutes = reminderMinutes,
            isDone = isDone
        )
        if (isNewTask) {
            taskViewModel.saveTask(updatedTask)
        } else {
            taskViewModel.updateTask(updatedTask)
        }
        navController.popBackStack()
    }
    val deleteTask = {
        taskViewModel.deleteTask(task.id)
        navController.popBackStack()
    }
    var reminderOption by remember {
        mutableStateOf(
            when (reminderMinutes) {
                10 -> "10 minutes before"
                30 -> "30 minutes before"
                60 -> "1 hour before"
                1440 -> "1 day before"
                else -> "30 minutes before"
            }
        )
    }
    val timeOptions = listOf(
        "10 minutes before",
        "30 minutes before",
        "1 hour before",
        "6 hours before",
        "1 day before"
    )
    if (showTimePicker) {
        TimePickerDialog(
            context,
            { _, hour, minute -> timeState = LocalTime.of(hour, minute) },
            timeState.hour,
            timeState.minute,
            true
        ).apply { setOnDismissListener { showTimePicker = false } }.show()
    }
    if (showDatePicker) {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth -> dateState = LocalDate.of(year, month + 1, dayOfMonth) },
            dateState.year,
            dateState.monthValue - 1,
            dateState.dayOfMonth
        ).apply { setOnDismissListener { showDatePicker = false } }.show()
    }
    if (showDeleteTaskSheet) {
        ModalBottomSheet(
            onDismissRequest = { showDeleteTaskSheet = false },
            sheetState = rememberModalBottomSheetState()
        ) {
            DeleteTaskSheet(
                onConfirm = {
                    showDeleteTaskSheet = false
                    deleteTask()
                },
                onCancel = {
                    showDeleteTaskSheet = false
                }
            )
        }
    }
    if (editingTitle) {
        val titleFocusRequester = remember { FocusRequester() }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Cancel",
                    color = Color.Black,
                    modifier = Modifier.clickable {
                        tempTitle = title
                        editingTitle = false
                    }
                )
                Text("EDIT TITLE", style = MaterialTheme.typography.titleMedium)
                Text(
                    "Save",
                    color = Color(0xFF4CAF50),
                    modifier = Modifier.clickable {
                        title = tempTitle
                        editingTitle = false
                    }
                )
            }
            OutlinedTextField(
                value = tempTitle,
                onValueChange = { tempTitle = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .focusRequester(titleFocusRequester),
                textStyle = MaterialTheme.typography.headlineMedium,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                )
            )
            LaunchedEffect(editingTitle) {
                if (editingTitle) {
                    delay(100)
                    titleFocusRequester.requestFocus()
                }
            }
        }
        return
    }
    if (editingDescription) {
        val descriptionFocusRequester = remember { FocusRequester() }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Cancel",
                    color = Color.Black,
                    modifier = Modifier.clickable {
                        tempDescription = description
                        editingDescription = false
                    }
                )
                Text("EDIT DESCRIPTION", style = MaterialTheme.typography.titleMedium)
                Text(
                    "Save",
                    color = Color(0xFF4CAF50),
                    modifier = Modifier.clickable {
                        description = tempDescription
                        editingDescription = false
                    }
                )
            }
            OutlinedTextField(
                value = tempDescription,
                onValueChange = { tempDescription = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(16.dp)
                    .focusRequester(descriptionFocusRequester),
                minLines = 5,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                )
            )
            LaunchedEffect(editingDescription) {
                if (editingDescription) {
                    delay(100)
                    descriptionFocusRequester.requestFocus()
                }
            }
        }
        return
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isEditMode) {
                Text(
                    "Cancel",
                    color = Color.Black,
                    modifier = Modifier.clickable {
                        isEditMode = false
                        tempTitle = title
                        tempDescription = description
                    }
                )
                Text("EDIT TASK", style = MaterialTheme.typography.titleMedium)
                Text(
                    "Save",
                    color = Color(0xFF4CAF50),
                    modifier = Modifier.clickable {
                        title = tempTitle
                        description = tempDescription
                        isEditMode = false
                        saveTask()
                    }
                )
            } else {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Close",
                    modifier = Modifier.clickable { navController.popBackStack() }
                )
                Text(
                    text = dateState.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
                    style = MaterialTheme.typography.titleMedium
                )
                IconButton(onClick = {
                    tempTitle = title
                    tempDescription = description
                    isEditMode = true
                }) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                }
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(Color.Green, CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("TASK", style = MaterialTheme.typography.labelMedium)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(
                            color = if (isDone) MaterialTheme.colorScheme.primary else Color.Transparent,
                            shape = CircleShape
                        )
                        .border(
                            width = 2.dp,
                            color = if (isDone) MaterialTheme.colorScheme.primary else Color.Gray,
                            shape = CircleShape
                        )
                        .clickable {
                            isDone = isDone.not()
                            val updatedTask = task.copy(
                                title = title,
                                description = description,
                                date = dateState,
                                time = timeState,
                                reminderMinutes = reminderMinutes,
                                isDone = isDone
                            )
                            taskViewModel.updateTask(updatedTask)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (isDone) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Task completed",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = if (isEditMode) tempTitle else title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        textDecoration = if (isDone) TextDecoration.LineThrough else null
                    ),
                    modifier = Modifier.weight(1f).clickable {
                        if (isEditMode) {
                            editingTitle = true
                        }
                    }
                )
                if (isEditMode) {
                    Icon(Icons.Default.KeyboardArrowRight, contentDescription = null)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.weight(1f).clickable {
                        if (isEditMode) {
                            editingDescription = true
                        }
                    }
                ) {
                    val displayDescription = if (isEditMode) tempDescription else description
                    displayDescription.split("\n").forEach {
                        Text(it, style = MaterialTheme.typography.bodyMedium)
                    }
                }
                if (isEditMode) {
                    Icon(Icons.Default.KeyboardArrowRight, contentDescription = null)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color(0xFFF3F4F6), RoundedCornerShape(8.dp))
                        .clickable { if (isEditMode) showTimePicker = true }
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = timeState.format(DateTimeFormatter.ofPattern("HH:mm")))
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color(0xFFF3F4F6), RoundedCornerShape(8.dp))
                        .clickable { if (isEditMode) showDatePicker = true }
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = dateState.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            var expanded by remember { mutableStateOf(false) }
            Box {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { if (isEditMode) expanded = true }
                ) {
                    Icon(Icons.Default.Notifications, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(reminderOption, modifier = Modifier.weight(1f))
                    if (isEditMode) {
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    }
                }
                if (isEditMode) {
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        timeOptions.forEach {
                            DropdownMenuItem(
                                text = { Text(it) },
                                onClick = {
                                    reminderOption = it
                                    reminderMinutes = when (it) {
                                        "10 minutes before" -> 10
                                        "30 minutes before" -> 30
                                        "1 hour before" -> 60
                                        "1 day before" -> 1440
                                        else -> 30
                                    }
                                    expanded = false
                                },
                                trailingIcon = {
                                    if (it == reminderOption) Icon(Icons.Default.Check, contentDescription = null)
                                }
                            )
                        }
                    }
                }
            }
            if (!isEditMode) {
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = "DELETE TASK",
                    color = Color.Red,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .clickable { showDeleteTaskSheet = true },
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
