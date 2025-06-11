package com.dilekbaykara.tasky.features.agenda.presentation
import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.dilekbaykara.tasky.BuildConfig
import com.dilekbaykara.tasky.features.agenda.data.local.entities.Event
import com.dilekbaykara.tasky.features.agenda.data.local.entities.Reminder
import com.dilekbaykara.tasky.features.agenda.data.local.entities.Task
import com.dilekbaykara.tasky.features.auth.presentation.register.AgendaDescription
import com.dilekbaykara.tasky.features.auth.presentation.register.AttendeeButton
import com.dilekbaykara.tasky.features.auth.presentation.register.AuthViewModel
import com.dilekbaykara.tasky.features.auth.presentation.register.AvatarDropDown
import com.dilekbaykara.tasky.features.auth.presentation.register.CalendarButton
import com.dilekbaykara.tasky.features.auth.presentation.register.Header
import com.dilekbaykara.tasky.features.auth.presentation.register.MoreDropdownMenu
import com.dilekbaykara.tasky.features.auth.presentation.register.TaskyFab
import com.dilekbaykara.tasky.features.shared.presentation.navigation.ux.TaskyScreen
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoField
import java.time.temporal.ChronoUnit
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AgendaScreen(
    navController: NavController,
    selectedDate: String? = null,
    authViewModel: AuthViewModel? = null,
    onLogout: () -> Unit = {}
) {
    val viewModel: AgendaViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()
    LaunchedEffect(selectedDate) {
        selectedDate?.let { dateString ->
            try {
                val date = LocalDate.parse(dateString)
                viewModel.setSelectedDateFromNavigation(date)
            } catch (e: Exception) {
            }
        }
    }
    var showDeleteEventDialog by remember { mutableStateOf(false) }
    var showDeleteTaskDialog by remember { mutableStateOf(false) }
    var showDeleteReminderDialog by remember { mutableStateOf(false) }
    var itemToDelete by remember { mutableStateOf<Any?>(null) }
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 30.dp),
        color = MaterialTheme.colorScheme.inverseSurface
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
                    .background(color = MaterialTheme.colorScheme.inverseSurface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    MonthHeader(
                        selectedDate = uiState.selectedDate,
                        onDateSelected = { date ->
                            viewModel.selectDate(date)
                        }
                    )
                    Box {
                        Row(
                            modifier = Modifier.padding(5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CalendarButton(
                                selectedDate = uiState.selectedDate,
                                onDateSelected = { date ->
                                    viewModel.selectDate(date)
                                }
                            )
                            AvatarDropDown(
                                modifier = Modifier,
                                authViewModel = authViewModel,
                                onLogout = onLogout
                            )
                            DebugSettingsMenu(viewModel)
                        }
                    }
                }
            }
            Box(modifier = Modifier.fillMaxSize()) {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        DatePickerCarousel(
                            modifier = Modifier.height(40.dp).align(Alignment.CenterHorizontally),
                            selectedDate = uiState.selectedDate,
                            onDateSelected = { date -> viewModel.selectDate(date) }
                        )
                        DateHeader(uiState.selectedDate)
                        AgendaItemList(
                            selectedDate = uiState.selectedDate,
                            events = uiState.events,
                            tasks = uiState.tasks,
                            reminders = uiState.reminders,
                            onEventClick = { event ->
                                navController.navigate("${TaskyScreen.EventDetail.route}/${event.id}")
                            },
                            onTaskClick = { task ->
                                navController.navigate("${TaskyScreen.TaskDetail.route}/${task.id}")
                            },
                            onReminderClick = { reminder ->
                                navController.navigate("${TaskyScreen.ReminderDetail.route}/${reminder.id}")
                            },
                            onEventOpen = { event ->
                                navController.navigate("${TaskyScreen.EventDetail.route}/${event.id}")
                            },
                            onTaskOpen = { task ->
                                navController.navigate("${TaskyScreen.TaskDetail.route}/${task.id}")
                            },
                            onReminderOpen = { reminder ->
                                navController.navigate("${TaskyScreen.ReminderDetail.route}/${reminder.id}")
                            },
                            onEventEdit = { event ->
                                navController.navigate("${TaskyScreen.EventDetail.route}/${event.id}/edit/${uiState.selectedDate}?edit=true")
                            },
                            onTaskEdit = { task ->
                                navController.navigate("${TaskyScreen.TaskDetail.route}/${task.id}/edit/${uiState.selectedDate}?edit=true")
                            },
                            onReminderEdit = { reminder ->
                                navController.navigate("${TaskyScreen.ReminderDetail.route}/${reminder.id}/edit/${uiState.selectedDate}?edit=true")
                            },
                            onEventDelete = { event ->
                                itemToDelete = event
                                showDeleteEventDialog = true
                            },
                            onTaskDelete = { task ->
                                itemToDelete = task
                                showDeleteTaskDialog = true
                            },
                            onReminderDelete = { reminder ->
                                itemToDelete = reminder
                                showDeleteReminderDialog = true
                            },
                            onTaskToggleDone = { task ->
                                viewModel.toggleTaskDone(task.id)
                            }
                        )
                    }
                }
                TaskyFab(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp),
                    onEventClick = {
                        val newEvent = viewModel.createNewEvent()
                        navController.navigate("${TaskyScreen.EventDetail.route}/new/${newEvent.id}/${uiState.selectedDate}")
                    },
                    onTaskClick = {
                        val newTask = viewModel.createNewTask()
                        navController.navigate("${TaskyScreen.TaskDetail.route}/new/${newTask.id}/${uiState.selectedDate}")
                    },
                    onReminderClick = {
                        val newReminder = viewModel.createNewReminder()
                        navController.navigate("${TaskyScreen.ReminderDetail.route}/new/${newReminder.id}/${uiState.selectedDate}")
                    }
                )
            }
        }
    }
    if (showDeleteEventDialog && itemToDelete is Event) {
        DeleteConfirmationDialog(
            title = "Delete Event",
            message = "Are you sure you want to delete \"${(itemToDelete as Event).title.ifEmpty { "Untitled Event" }}\"?",
            onConfirm = {
                viewModel.deleteEvent((itemToDelete as Event).id)
                showDeleteEventDialog = false
                itemToDelete = null
            },
            onDismiss = {
                showDeleteEventDialog = false
                itemToDelete = null
            }
        )
    }
    if (showDeleteTaskDialog && itemToDelete is Task) {
        DeleteConfirmationDialog(
            title = "Delete Task",
            message = "Are you sure you want to delete \"${(itemToDelete as Task).title.ifEmpty { "Untitled Task" }}\"?",
            onConfirm = {
                viewModel.deleteTask((itemToDelete as Task).id)
                showDeleteTaskDialog = false
                itemToDelete = null
            },
            onDismiss = {
                showDeleteTaskDialog = false
                itemToDelete = null
            }
        )
    }
    if (showDeleteReminderDialog && itemToDelete is Reminder) {
        DeleteConfirmationDialog(
            title = "Delete Reminder",
            message = "Are you sure you want to delete \"${(itemToDelete as Reminder).title.ifEmpty { "Untitled Reminder" }}\"?",
            onConfirm = {
                viewModel.deleteReminder((itemToDelete as Reminder).id)
                showDeleteReminderDialog = false
                itemToDelete = null
            },
            onDismiss = {
                showDeleteReminderDialog = false
                itemToDelete = null
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MonthHeader(selectedDate: LocalDate, onDateSelected: (LocalDate) -> Unit) {
    var showDatePicker by remember { mutableStateOf(false) }
    val monthName = selectedDate.format(DateTimeFormatter.ofPattern("MMMM")).uppercase()

    Box(modifier = Modifier.padding(10.dp)) {
        Row(
            modifier = Modifier.clickable { showDatePicker = true },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = monthName,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = Color.White
            )
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Select date",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    }

    // Date Picker Dialog
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDate.toEpochDay() * 24 * 60 * 60 * 1000
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val newSelectedDate = java.time.Instant.ofEpochMilli(millis)
                                .atZone(java.time.ZoneId.systemDefault())
                                .toLocalDate()
                            onDateSelected(newSelectedDate)
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@SuppressLint("NewApi")
@Composable
fun DatePickerCarousel(
    modifier: Modifier = Modifier,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    val listState = rememberLazyListState()
    val today = LocalDate.now()
    val leftBound = today.minusDays(15)
    val rightBound = today.plusDays(15)
    val daysBetween = ChronoUnit.DAYS.between(leftBound, rightBound)
    val allDaysInBounds = List(daysBetween.toInt() + 1) { leftBound.plusDays(it.toLong()) }
    val pairDays = allDaysInBounds.map { date ->
        val dayLetter = date.dayOfWeek.name.first()
        val dayIndex = date.get(ChronoField.DAY_OF_MONTH)
        Triple(dayLetter, dayIndex, date)
    }
    LaunchedEffect(selectedDate) {
        val selectedIndex = allDaysInBounds.indexOfFirst { it == selectedDate }
        if (selectedIndex != -1) {
            listState.animateScrollToItem(selectedIndex)
        }
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp, bottom = 20.dp)
    ) {
        LazyRow(state = listState) {
            items(pairDays.size) { index ->
                val (dayLetter, dayIndex, date) = pairDays[index]
                val isSelected = date == selectedDate
                Box(
                    Modifier
                        .padding(8.dp)
                        .width(34.dp)
                        .background(
                            if (isSelected) {
                                Color(0xFFB6E388)
                            } else {
                                Color.Transparent
                            },
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clickable { onDateSelected(date) }
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = dayLetter.toString(),
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isSelected) Color.White else Color.Black
                            )
                            Text(
                                text = dayIndex.toString(),
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (isSelected) Color.White else Color.Black
                            )
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DateHeader(selectedDate: LocalDate, modifier: Modifier = Modifier) {
    val dateText = when {
        selectedDate == LocalDate.now() -> "Today"
        selectedDate == LocalDate.now().minusDays(1) -> "Yesterday"
        selectedDate == LocalDate.now().plusDays(1) -> "Tomorrow"
        else -> selectedDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
    }
    Box() {
        Header(modifier = Modifier.padding(15.dp), dateText)
    }
}

@SuppressLint("NewApi")
@Composable
fun AgendaItemList(
    selectedDate: LocalDate,
    events: List<Event>,
    tasks: List<Task>,
    reminders: List<Reminder>,
    onEventClick: (Event) -> Unit,
    onTaskClick: (Task) -> Unit,
    onReminderClick: (Reminder) -> Unit,
    onEventOpen: (Event) -> Unit,
    onTaskOpen: (Task) -> Unit,
    onReminderOpen: (Reminder) -> Unit,
    onEventEdit: (Event) -> Unit,
    onTaskEdit: (Task) -> Unit,
    onReminderEdit: (Reminder) -> Unit,
    onEventDelete: (Event) -> Unit,
    onTaskDelete: (Task) -> Unit,
    onReminderDelete: (Reminder) -> Unit,
    onTaskToggleDone: (Task) -> Unit = {}
) {
    val allItems = buildList {
        events.forEach { event ->
            add(AgendaListItemData.EventItem(event))
        }
        tasks.forEach { task ->
            add(AgendaListItemData.TaskItem(task))
        }
        reminders.forEach { reminder ->
            add(AgendaListItemData.ReminderItem(reminder))
        }
    }.sortedBy { item ->
        when (item) {
            is AgendaListItemData.EventItem -> item.event.fromTime
            is AgendaListItemData.TaskItem -> item.task.time
            is AgendaListItemData.ReminderItem -> item.reminder.time
        }
    }
    val isToday = selectedDate == LocalDate.now()
    val currentTime = if (isToday) LocalTime.now() else null
    if (allItems.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Nothing scheduled for today",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Tap the + button to add an event, task, or reminder",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
                if (isToday && currentTime != null) {
                    Spacer(modifier = Modifier.height(20.dp))
                    TimeNeedle(currentTime = currentTime)
                }
            }
        }
    } else {
        LazyColumn {
            items(allItems.size + 1) { index ->
                if (isToday && currentTime != null && index < allItems.size) {
                    val item = allItems[index]
                    val itemTime = when (item) {
                        is AgendaListItemData.EventItem -> item.event.fromTime
                        is AgendaListItemData.TaskItem -> item.task.time
                        is AgendaListItemData.ReminderItem -> item.reminder.time
                    }
                    val shouldShowNeedleBefore = if (index == 0) {
                        currentTime.isBefore(itemTime)
                    } else {
                        val prevItem = allItems[index - 1]
                        val prevTime = when (prevItem) {
                            is AgendaListItemData.EventItem -> prevItem.event.fromTime
                            is AgendaListItemData.TaskItem -> prevItem.task.time
                            is AgendaListItemData.ReminderItem -> prevItem.reminder.time
                        }
                        currentTime.isAfter(prevTime) && currentTime.isBefore(itemTime)
                    }
                    if (shouldShowNeedleBefore) {
                        TimeNeedle(currentTime = currentTime)
                    }
                }
                if (index < allItems.size) {
                    val item = allItems[index]
                    Box(
                        modifier = Modifier
                            .padding(15.dp)
                            .background(Color.Transparent)
                            .wrapContentSize()
                            .clickable {
                                when (item) {
                                    is AgendaListItemData.EventItem -> onEventClick(item.event)
                                    is AgendaListItemData.TaskItem -> onTaskClick(item.task)
                                    is AgendaListItemData.ReminderItem -> onReminderClick(item.reminder)
                                }
                            }
                    ) {
                        AgendaListItem(
                            item = item,
                            onOpenClick = {
                                when (item) {
                                    is AgendaListItemData.EventItem -> onEventOpen(item.event)
                                    is AgendaListItemData.TaskItem -> onTaskOpen(item.task)
                                    is AgendaListItemData.ReminderItem -> onReminderOpen(item.reminder)
                                }
                            },
                            onEditClick = {
                                when (item) {
                                    is AgendaListItemData.EventItem -> onEventEdit(item.event)
                                    is AgendaListItemData.TaskItem -> onTaskEdit(item.task)
                                    is AgendaListItemData.ReminderItem -> onReminderEdit(item.reminder)
                                }
                            },
                            onDeleteClick = {
                                when (item) {
                                    is AgendaListItemData.EventItem -> onEventDelete(item.event)
                                    is AgendaListItemData.TaskItem -> onTaskDelete(item.task)
                                    is AgendaListItemData.ReminderItem -> onReminderDelete(item.reminder)
                                }
                            },
                            onTaskToggleDone = {
                                when (item) {
                                    is AgendaListItemData.TaskItem -> onTaskToggleDone(item.task)
                                    else -> {}
                                }
                            }
                        )
                    }
                } else if (isToday && currentTime != null && allItems.isNotEmpty()) {
                    val lastItem = allItems.lastOrNull()
                    if (lastItem != null) {
                        val lastTime = when (lastItem) {
                            is AgendaListItemData.EventItem -> lastItem.event.fromTime
                            is AgendaListItemData.TaskItem -> lastItem.task.time
                            is AgendaListItemData.ReminderItem -> lastItem.reminder.time
                        }
                        if (currentTime.isAfter(lastTime)) {
                            TimeNeedle(currentTime = currentTime)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TimeNeedle(currentTime: LocalTime) {
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(
                    color = Color.Red,
                    shape = CircleShape
                )
        )
        Box(
            modifier = Modifier
                .height(2.dp)
                .weight(1f)
                .background(Color.Red)
        )
        Text(
            text = currentTime.format(timeFormatter),
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Bold
            ),
            color = Color.Red,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}
sealed class AgendaListItemData {
    data class EventItem(val event: Event) : AgendaListItemData()
    data class TaskItem(val task: Task) : AgendaListItemData()
    data class ReminderItem(val reminder: Reminder) : AgendaListItemData()
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AgendaListItem(
    item: AgendaListItemData,
    onOpenClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onTaskToggleDone: () -> Unit = {}
) {
    Box(
        Modifier
            .background(
                color = getItemColor(item),
                shape = RoundedCornerShape(20.dp)
            )
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .height(150.dp)
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(15.dp)
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                val title = when (item) {
                    is AgendaListItemData.EventItem -> item.event.title.ifEmpty { "Untitled Event" }
                    is AgendaListItemData.TaskItem -> item.task.title.ifEmpty { "Untitled Task" }
                    is AgendaListItemData.ReminderItem -> item.reminder.title.ifEmpty { "Untitled Reminder" }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    if (item is AgendaListItemData.TaskItem) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .background(
                                    color = if (item.task.isDone) MaterialTheme.colorScheme.primary else Color.Transparent,
                                    shape = CircleShape
                                )
                                .border(
                                    width = 2.dp,
                                    color = if (item.task.isDone) MaterialTheme.colorScheme.primary else Color.Gray,
                                    shape = CircleShape
                                )
                                .clickable { onTaskToggleDone() },
                            contentAlignment = Alignment.Center
                        ) {
                            if (item.task.isDone) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Task completed",
                                    tint = Color.White,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    val textDecoration = when (item) {
                        is AgendaListItemData.TaskItem -> if (item.task.isDone) TextDecoration.LineThrough else null
                        else -> null
                    }
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            textDecoration = textDecoration
                        ),
                        modifier = Modifier.padding(top = 10.dp, bottom = 10.dp)
                    )
                }
                MoreDropdownMenu(
                    onOpenClick = onOpenClick,
                    onEditClick = onEditClick,
                    onDeleteClick = onDeleteClick
                )
            }
            val description = when (item) {
                is AgendaListItemData.EventItem -> item.event.description.ifEmpty { "No description" }
                is AgendaListItemData.TaskItem -> item.task.description.ifEmpty { "No description" }
                is AgendaListItemData.ReminderItem -> item.reminder.description.ifEmpty { "No description" }
            }
            AgendaDescription(Modifier.align(Alignment.Start), description)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                if (item is AgendaListItemData.EventItem) {
                    AttendeeButton(
                        Modifier
                            .padding(top = 10.dp)
                            .align(Alignment.CenterVertically),
                        "${item.event.visitors.size} attendees"
                    )
                }
                val timeText = when (item) {
                    is AgendaListItemData.EventItem -> {
                        "${item.event.fromTime.format(DateTimeFormatter.ofPattern("HH:mm"))} - ${item.event.toTime.format(DateTimeFormatter.ofPattern("HH:mm"))}"
                    }
                    is AgendaListItemData.TaskItem -> {
                        item.task.time.format(DateTimeFormatter.ofPattern("HH:mm"))
                    }
                    is AgendaListItemData.ReminderItem -> {
                        item.reminder.time.format(DateTimeFormatter.ofPattern("HH:mm"))
                    }
                }
                AgendaDateTime(
                    Modifier
                        .padding(top = 10.dp)
                        .align(Alignment.CenterVertically),
                    text = timeText
                )
            }
        }
    }
}
fun getItemColor(item: AgendaListItemData): Color {
    return when (item) {
        is AgendaListItemData.TaskItem -> greenColor
        is AgendaListItemData.EventItem -> greyColor
        is AgendaListItemData.ReminderItem -> lightGreenColor
    }
}
val greenColor = Color(red = 39, green = 159, blue = 112, alpha = 200)
val greyColor = Color(red = 242, green = 243, blue = 250, alpha = 200)
val lightGreenColor = Color(red = 202, green = 239, blue = 69, alpha = 200)

@Composable
fun AgendaDateTime(modifier: Modifier = Modifier, text: String) {
    Text(
        text = text,
        modifier = modifier,
        style = MaterialTheme.typography.bodySmall
    )
}

@Composable
fun DeleteConfirmationDialog(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm
            ) {
                Text(
                    "Delete",
                    color = Color.Red
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun DebugSettingsMenu(viewModel: AgendaViewModel) {
    if (!BuildConfig.DEBUG) return
    var expanded by remember { mutableStateOf(false) }
    Box {
        IconButton(
            onClick = { expanded = true }
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Debug Settings",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = {
                    Text(
                        text = "Test Notification (1 min)",
                        color = Color.Black
                    )
                },
                onClick = {
                    expanded = false
                    viewModel.scheduleTestNotification()
                }
            )
            DropdownMenuItem(
                text = {
                    Text(
                        text = "Debug Info",
                        color = Color.Black
                    )
                },
                onClick = {
                    expanded = false
                    Log.d("Agenda", "DEBUG: Current time: ${LocalDateTime.now()}")
                    Log.d("Agenda", "DEBUG: Build type: ${if (BuildConfig.DEBUG) "DEBUG" else "RELEASE"}")
                }
            )
        }
    }
}
