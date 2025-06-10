package com.dilekbaykara.tasky.features.agenda.presentation.events

import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.dilekbaykara.tasky.features.agenda.data.local.entities.Event
import com.dilekbaykara.tasky.features.shared.presentation.navigation.ux.TaskyScreen
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailsPage(
    navController: NavController,
    photoViewModel: PhotoViewModel = hiltViewModel(),
    eventId: String? = null,
    isNewEvent: Boolean = false,
    selectedDate: String? = null,
    isEditMode: Boolean = false,
    attendeeManager: AttendeeManager = hiltViewModel(),
    eventViewModel: EventViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val isNew = isNewEvent || eventId == null

    var event by remember {
        mutableStateOf(
            if (isNew) {
                Event(
                    id = java.util.UUID.randomUUID().toString(),
                    title = "",
                    description = "",
                    fromDate = selectedDate?.let { LocalDate.parse(it) } ?: LocalDate.now(),
                    fromTime = LocalTime.of(8, 0),
                    toDate = selectedDate?.let { LocalDate.parse(it) } ?: LocalDate.now(),
                    toTime = LocalTime.of(9, 0),
                    reminderMinutes = 30,
                    visitors = emptyList(),
                    photos = emptyList()
                )
            } else {
                null
            }
        )
    }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var isFromTime by remember { mutableStateOf(true) }
    var showReminderDialog by remember { mutableStateOf(false) }
    var showAddVisitorDialog by remember { mutableStateOf(false) }
    var showAddAttendeeSheet by remember { mutableStateOf(false) }
    var visitorFilter by remember { mutableStateOf("All") }
    var newVisitorEmail by remember { mutableStateOf("") }

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var fromDate by remember { mutableStateOf(selectedDate?.let { LocalDate.parse(it) } ?: LocalDate.now()) }
    var fromTime by remember { mutableStateOf(LocalTime.of(8, 0)) }
    var toDate by remember { mutableStateOf(selectedDate?.let { LocalDate.parse(it) } ?: LocalDate.now()) }
    var toTime by remember { mutableStateOf(LocalTime.of(9, 0)) }
    var reminderMinutes by remember { mutableStateOf(30) }
    var attendees by remember { mutableStateOf(listOf<AttendeeManager.AttendeeData>()) }

    val currentEvent by eventViewModel.currentEvent.collectAsState()

    val deleteEventSheetState = androidx.compose.material3.rememberModalBottomSheetState()

    val isOnline by photoViewModel.isOnline.collectAsState()

    navController.currentBackStackEntry?.savedStateHandle?.getStateFlow<String?>("edited_title", null)?.collectAsState()?.value?.let {
        title = it
        navController.currentBackStackEntry?.savedStateHandle?.remove<String>("edited_title")
    }

    navController.currentBackStackEntry?.savedStateHandle?.getStateFlow<String?>("edited_description", null)?.collectAsState()?.value?.let {
        description = it
        navController.currentBackStackEntry?.savedStateHandle?.remove<String>("edited_description")
    }

    var isEditMode by remember { mutableStateOf(isNew || isEditMode) }

    LaunchedEffect(eventId) {
        if (!isNew && eventId != null) {
            eventViewModel.loadEvent(eventId)
        }
    }

    LaunchedEffect(currentEvent) {
        currentEvent?.let { e ->
            event = e
            title = e.title
            description = e.description
            fromDate = e.fromDate
            fromTime = e.fromTime
            toDate = e.toDate
            toTime = e.toTime
            reminderMinutes = e.reminderMinutes
            attendees = e.visitors.map { email ->
                AttendeeManager.AttendeeData(
                    email = email,
                    fullName = email.substringBefore("@").replaceFirstChar { it.uppercase() },
                    userId = "user_${email.hashCode()}",
                    isGoing = true
                )
            }
            photoViewModel.setPhotosFromEvent(e.photos)
        }
    }

    val saveEvent = {
        val updatedEvent = event?.copy(
            title = title,
            description = description,
            fromDate = fromDate,
            fromTime = fromTime,
            toDate = toDate,
            toTime = toTime,
            reminderMinutes = reminderMinutes,
            visitors = attendees.map { it.email },
            photos = photoViewModel.getPhotoUrls()
        ) ?: Event(
            id = "",
            title = title,
            description = description,
            fromDate = fromDate,
            fromTime = fromTime,
            toDate = toDate,
            toTime = toTime,
            reminderMinutes = reminderMinutes,
            visitors = attendees.map { it.email },
            photos = photoViewModel.getPhotoUrls()
        )

        if (isNew) {
            eventViewModel.saveEvent(updatedEvent)
        } else {
            eventViewModel.updateEvent(updatedEvent)
        }
        navController.popBackStack()
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            photoViewModel.processAndUploadPhotos(listOf(it))
        }
    }

    // Get current user info for creator
    val currentUserId = eventViewModel.getCurrentUserId()
    val currentUserFullName = eventViewModel.getFullName() ?: "You"

    // Ensure creator is always present as first attendee
    val creatorAttendee = AttendeeManager.AttendeeData(
        email = "creator@tasky.com",
        fullName = currentUserFullName,
        userId = currentUserId,
        isCreator = true,
        isGoing = true
    )
    val attendeesWithCreator = remember(attendees, creatorAttendee) {
        val withoutCreator = attendees.filter { !it.isCreator && it.userId != creatorAttendee.userId }
        listOf(creatorAttendee) + withoutCreator
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
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
                    }
                )
                Text("EDIT EVENT", style = MaterialTheme.typography.titleMedium)
                Text(
                    "Save",
                    color = Color(0xFF4CAF50),
                    modifier = Modifier.clickable {
                        saveEvent()
                    }
                )
            } else {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Close",
                    modifier = Modifier.clickable { navController.popBackStack() }
                )
                Text(
                    text = fromDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
                    style = MaterialTheme.typography.titleMedium
                )
                IconButton(onClick = { isEditMode = true }) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color(0xFF4CAF50))
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .background(Color(0xFF4CAF50), CircleShape)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "EVENT",
                        color = Color.Black,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (isEditMode) {
                                        val titleToEdit = title.ifEmpty { "Meeting" }
                                        val safeTitle = titleToEdit.replace("/", "_").replace("?", "_").replace("#", "_")
                                        navController.navigate("event_edit_title/$safeTitle")
                                    }
                                }
                        ) {
                            Icon(
                                Icons.Default.RadioButtonUnchecked,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                title.ifEmpty { "Meeting" },
                                color = Color.Black,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )
                            if (isEditMode) {
                                Icon(
                                    Icons.Default.KeyboardArrowRight,
                                    contentDescription = "Edit title",
                                    tint = Color.Gray,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (isEditMode) {
                                        val descriptionToEdit = description.ifEmpty { "Enter description" }
                                        val safeDescription = descriptionToEdit.replace("/", "_").replace("?", "_").replace("#", "_")
                                        navController.navigate("event_edit_description/$safeDescription")
                                    }
                                }
                        ) {
                            Text(
                                description.ifEmpty { "Amet minim mollit non deserunt ullamco est sit aliqua dolor do amet sint." },
                                color = Color.Black,
                                fontSize = 16.sp,
                                modifier = Modifier.weight(1f)
                            )
                            if (isEditMode) {
                                Icon(
                                    Icons.Default.KeyboardArrowRight,
                                    contentDescription = "Edit description",
                                    tint = Color.Gray,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        val photos by photoViewModel.photos.collectAsState()
                        if (photos.isNotEmpty() || isEditMode) {
                            Text(
                                "Photos",
                                color = Color.Black,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            if (!isOnline) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CloudOff, contentDescription = "Offline", tint = Color.Red)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("You must be online to add or remove photos", color = Color.Red, fontSize = 14.sp)
                                }
                            }
                            if (photos.size == 10) {
                                Text(
                                    "You have 10 photos. You can't add anymore.",
                                    color = Color.Red,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            if (photos.isEmpty() && isEditMode && isOnline) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(60.dp)
                                        .background(Color(0xFFF3F4F6), RoundedCornerShape(8.dp))
                                        .clickable { imagePickerLauncher.launch("image/*") },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            Icons.Default.Add,
                                            contentDescription = "Add Photo",
                                            tint = Color.Gray,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            "ADD PHOTOS",
                                            color = Color.Gray,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            } else if (photos.isNotEmpty() || !isEditMode) {
                                LazyVerticalGrid(
                                    columns = GridCells.Fixed(5),
                                    modifier = Modifier.height(
                                        if (photos.size > 5) 170.dp else 80.dp
                                    ),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(photos) { photoUrl ->
                                        Box(
                                            modifier = Modifier
                                                .size(60.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .border(
                                                    width = 1.dp,
                                                    color = Color.Gray.copy(alpha = 0.3f),
                                                    shape = RoundedCornerShape(8.dp)
                                                )
                                                .clickable {
                                                    val encodedUrl = java.net.URLEncoder.encode(photoUrl, "UTF-8")
                                                    navController.navigate("${TaskyScreen.PhotoDetail.route}/$encodedUrl")
                                                }
                                        ) {
                                            AsyncImage(
                                                model = photoUrl,
                                                contentDescription = null,
                                                modifier = Modifier.fillMaxSize(),
                                                contentScale = ContentScale.Crop
                                            )
                                            if (isEditMode && isOnline) {
                                                IconButton(
                                                    onClick = { photoViewModel.removePhoto(photoUrl) },
                                                    modifier = Modifier.align(Alignment.TopEnd)
                                                ) {
                                                    Icon(Icons.Default.Delete, contentDescription = "Delete Photo", tint = Color.Red)
                                                }
                                            }
                                        }
                                    }
                                    if (isEditMode && isOnline && photos.size < 10) {
                                        item {
                                            Box(
                                                modifier = Modifier
                                                    .size(60.dp)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(Color(0xFFF3F4F6))
                                                    .border(
                                                        width = 1.dp,
                                                        color = Color.Gray.copy(alpha = 0.5f),
                                                        shape = RoundedCornerShape(8.dp)
                                                    )
                                                    .clickable { imagePickerLauncher.launch("image/*") },
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    Icons.Default.Add,
                                                    contentDescription = "Add Photo",
                                                    tint = Color.Gray,
                                                    modifier = Modifier.size(24.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("From", color = Color.Black, fontSize = 14.sp)
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextButton(
                                    onClick = {
                                        if (isEditMode) {
                                            isFromTime = true
                                            showTimePicker = true
                                        }
                                    }
                                ) {
                                    Text(
                                        fromTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                                        color = Color.Black
                                    )
                                    if (isEditMode) {
                                        Icon(
                                            Icons.Default.ArrowDropDown,
                                            contentDescription = null,
                                            tint = Color.Black
                                        )
                                    }
                                }
                                TextButton(
                                    onClick = {
                                        if (isEditMode) {
                                            showDatePicker = true
                                        }
                                    }
                                ) {
                                    Text(
                                        fromDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
                                        color = Color.Black
                                    )
                                    if (isEditMode) {
                                        Icon(
                                            Icons.Default.ArrowDropDown,
                                            contentDescription = null,
                                            tint = Color.Black
                                        )
                                    }
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("To", color = Color.Black, fontSize = 14.sp)
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextButton(
                                    onClick = {
                                        if (isEditMode) {
                                            isFromTime = false
                                            showTimePicker = true
                                        }
                                    }
                                ) {
                                    Text(
                                        toTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                                        color = Color.Black
                                    )
                                    if (isEditMode) {
                                        Icon(
                                            Icons.Default.ArrowDropDown,
                                            contentDescription = null,
                                            tint = Color.Black
                                        )
                                    }
                                }
                                TextButton(
                                    onClick = {
                                        if (isEditMode) {
                                            showDatePicker = true
                                        }
                                    }
                                ) {
                                    Text(
                                        toDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
                                        color = Color.Black
                                    )
                                    if (isEditMode) {
                                        Icon(
                                            Icons.Default.ArrowDropDown,
                                            contentDescription = null,
                                            tint = Color.Black
                                        )
                                    }
                                }
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { if (isEditMode) showReminderDialog = true },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Notifications,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    when (reminderMinutes) {
                                        10 -> "10 minutes before"
                                        30 -> "30 minutes before"
                                        60 -> "1 hour before"
                                        6 * 60 -> "6 hours before"
                                        24 * 60 -> "1 day before"
                                        else -> "$reminderMinutes minutes before"
                                    },
                                    color = Color.Black,
                                    fontSize = 14.sp
                                )
                            }
                            if (isEditMode) {
                                Icon(
                                    Icons.Default.ArrowDropDown,
                                    contentDescription = null,
                                    tint = Color.Black
                                )
                            }
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Visitors",
                                color = Color.Black,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            if (isEditMode) {
                                IconButton(onClick = { showAddAttendeeSheet = true }) {
                                    Icon(
                                        Icons.Default.Add,
                                        contentDescription = "Add Visitor",
                                        tint = Color.Black
                                    )
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("All", "Going", "Not going").forEach { filter ->
                                FilterChip(
                                    onClick = { visitorFilter = filter },
                                    label = { Text(filter) },
                                    selected = visitorFilter == filter,
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Color(0xFF4CAF50),
                                        selectedLabelColor = Color.White,
                                        containerColor = Color.Transparent,
                                        labelColor = Color.Black
                                    )
                                )
                            }
                        }

                        val filteredAttendees = when (visitorFilter) {
                            "Going" -> attendeesWithCreator.filter { it.isGoing }
                            "Not going" -> attendeesWithCreator.filter { !it.isGoing }
                            else -> attendeesWithCreator
                        }

                        filteredAttendees.forEach { attendee ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .then(if (attendee.isCreator) Modifier.border(2.dp, Color.LightGray, RoundedCornerShape(8.dp)) else Modifier)
                                    .padding(vertical = 4.dp, horizontal = 0.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(Color.Gray, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        attendee.fullName.split(" ").joinToString("") { it.take(1).uppercase() }.take(2),
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    attendee.fullName,
                                    color = Color.Black,
                                    fontSize = 16.sp,
                                    modifier = Modifier.weight(1f)
                                )
                                if (attendee.isCreator) {
                                    Text(
                                        "CREATOR",
                                        color = Color.Gray,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(start = 8.dp)
                                    )
                                }
                                if (isEditMode && !attendee.isCreator) {
                                    IconButton(
                                        onClick = {
                                            attendees = attendees.filter { it.email != attendee.email }
                                        }
                                    ) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = "Remove",
                                            tint = Color.Red
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (isEditMode && !isNew) {
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showDeleteDialog = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Delete event",
                            color = Color.Red,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }

    // Date Picker Dialog
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val selectedDate = java.time.Instant.ofEpochMilli(millis)
                                .atZone(java.time.ZoneId.systemDefault())
                                .toLocalDate()
                            fromDate = selectedDate
                            toDate = selectedDate
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

    // Time Picker Dialog
    if (showTimePicker) {
        val timePickerState = rememberTimePickerState()
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title = { Text(if (isFromTime) "Select Start Time" else "Select End Time") },
            text = {
                TimePicker(state = timePickerState)
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val selectedTime = LocalTime.of(timePickerState.hour, timePickerState.minute)
                        if (isFromTime) {
                            fromTime = selectedTime
                        } else {
                            toTime = selectedTime
                        }
                        showTimePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showDeleteDialog) {
        androidx.compose.material3.ModalBottomSheet(
            onDismissRequest = { showDeleteDialog = false },
            sheetState = deleteEventSheetState
        ) {
            DeleteEvent(
                onConfirm = {
                    showDeleteDialog = false
                    event?.let { eventViewModel.deleteEvent(it.id) }
                    navController.popBackStack()
                },
                onCancel = { showDeleteDialog = false }
            )
        }
    }

    if (showReminderDialog) {
        AlertDialog(
            onDismissRequest = { showReminderDialog = false },
            title = { Text("Reminder") },
            text = {
                Column {
                    listOf(10, 30, 60, 6 * 60, 24 * 60).forEach { minutes ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    reminderMinutes = minutes
                                    showReminderDialog = false
                                }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = reminderMinutes == minutes,
                                onClick = {
                                    reminderMinutes = minutes
                                    showReminderDialog = false
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                when (minutes) {
                                    10 -> "10 minutes before"
                                    30 -> "30 minutes before"
                                    60 -> "1 hour before"
                                    6 * 60 -> "6 hours before"
                                    24 * 60 -> "1 day before"
                                    else -> "$minutes minutes before"
                                }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showReminderDialog = false }) {
                    Text("OK")
                }
            }
        )
    }

    if (showAddAttendeeSheet) {
        AddAttendeeBottomSheet(
            onDismiss = { showAddAttendeeSheet = false },
            onAddAttendee = { newAttendee ->
                attendees = attendees + newAttendee
                showAddAttendeeSheet = false
            },
            existingAttendeeEmails = attendeesWithCreator.map { it.email }
        )
    }
}
