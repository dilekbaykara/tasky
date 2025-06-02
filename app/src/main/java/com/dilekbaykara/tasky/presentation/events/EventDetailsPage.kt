package com.dilekbaykara.tasky.presentation.events

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.OpenDocument
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EventDetailsPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    photoViewModel: PhotoViewModel,
    eventTitle: String = "Meeting",
    eventDescription: String = "Amet minim mollit non deserunt ullamco est sit aliqua dolor do amet sint.",
    fromTime: String = "08:00",
    fromDate: String = "Jul 21, 2022",
    toTime: String = "08:00",
    toDate: String = "Jul 21, 2022",
    reminder: String = "30 minutes before",
    visitors: List<Visitor> = sampleVisitors,
    onDelete: () -> Unit = {},
    onClose: () -> Unit = {}
) {
    var selectedFilter by remember { mutableStateOf("All") }
    var showFromTimePicker by remember { mutableStateOf(false) }
    var showFromDatePicker by remember { mutableStateOf(false) }
    var showToTimePicker by remember { mutableStateOf(false) }
    var showToDatePicker by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    var fromTimeState by remember { mutableStateOf(LocalTime.of(8, 0)) }
    var fromDateState by remember { mutableStateOf(LocalDate.of(2022, 7, 21)) }
    var toTimeState by remember { mutableStateOf(LocalTime.of(8, 0)) }
    var toDateState by remember { mutableStateOf(LocalDate.of(2022, 7, 21)) }
    var selectedPhoto by remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(OpenDocument()) { uri: Uri? ->
        uri?.let {
            try {
                context.contentResolver.takePersistableUriPermission(
                    it,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: SecurityException) {}
            photoViewModel.photos.value = photoViewModel.photos.value + it
        }
    }

    // Remove photo at index lambda
    val removePhotoAt: (Int) -> Unit = { idx ->
        if (idx in photoViewModel.photos.value.indices) {
            photoViewModel.removePhotoAt(idx)
        }
    }

    if (showFromTimePicker) {
        TimePickerDialog(
            context,
            { _, hour, minute -> fromTimeState = LocalTime.of(hour, minute) },
            fromTimeState.hour,
            fromTimeState.minute,
            true
        ).apply { setOnDismissListener { showFromTimePicker = false } }.show()
    }
    if (showFromDatePicker) {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth -> fromDateState = LocalDate.of(year, month + 1, dayOfMonth) },
            fromDateState.year,
            fromDateState.monthValue - 1,
            fromDateState.dayOfMonth
        ).apply { setOnDismissListener { showFromDatePicker = false } }.show()
    }
    if (showToTimePicker) {
        TimePickerDialog(
            context,
            { _, hour, minute -> toTimeState = LocalTime.of(hour, minute) },
            toTimeState.hour,
            toTimeState.minute,
            true
        ).apply { setOnDismissListener { showToTimePicker = false } }.show()
    }
    if (showToDatePicker) {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth -> toDateState = LocalDate.of(year, month + 1, dayOfMonth) },
            toDateState.year,
            toDateState.monthValue - 1,
            toDateState.dayOfMonth
        ).apply { setOnDismissListener { showToDatePicker = false } }.show()
    }
    val maxPhotos = 10
    val columns = 5
    val itemSize = 80.dp
    val itemPadding = 4.dp
    val totalItems = photoViewModel.photos.value.size + if (photoViewModel.photos.value.size < maxPhotos) 1 else 0
    val rows = (totalItems + columns - 1) / columns
    val gridHeight = (itemSize + itemPadding * 2) * rows
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(16.dp)
                    .background(Color(0xFFB6E388), RoundedCornerShape(4.dp))
            )
            Spacer(Modifier.width(8.dp))
            Text("EVENT", style = MaterialTheme.typography.labelMedium)
        }

        Spacer(Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = false, onClick = {})
            Text(
                eventTitle,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        }

        Spacer(Modifier.height(8.dp))

        Text(eventDescription, style = MaterialTheme.typography.bodyMedium)
        Text("Photos", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
            modifier = Modifier
                .height(gridHeight)
                .fillMaxWidth(),
            userScrollEnabled = false
        ) {
            items(photoViewModel.photos.value.size) { idx ->
                val context = LocalContext.current
                Image(
                    painter = rememberAsyncImagePainter(photoViewModel.photos.value[idx]),
                    contentDescription = null,
                    modifier = Modifier
                        .size(itemSize)
                        .padding(itemPadding)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            val bitmap = context.contentResolver.openInputStream(photoViewModel.photos.value[idx])?.use { input ->
                                BitmapFactory.decodeStream(input)
                            }
                            photoViewModel.selectedBitmap.value = bitmap
                            photoViewModel.selectedPhotoIndex.value = idx
                            navController.navigate("photo_detail")
                        },
                    contentScale = ContentScale.Crop
                )
            }
            if (photoViewModel.photos.value.size < maxPhotos) {
                item {
                    AddPhotoBox { launcher.launch(arrayOf("image/*")) }
                }
            }
        }
        if (photoViewModel.photos.value.size >= maxPhotos) {
            Text("Maximum 10 photos allowed", color = Color.Red)
        }
        Spacer(Modifier.height(16.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text("From", style = MaterialTheme.typography.labelSmall)
                Row {
                    Text(fromTimeState.format(DateTimeFormatter.ofPattern("HH:mm")), Modifier.background(Color(0xFFF3F4F6)).padding(8.dp).clickable { showFromTimePicker = true })
                    Spacer(Modifier.width(8.dp))
                    Text(fromDateState.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")), Modifier.background(Color(0xFFF3F4F6)).padding(8.dp).clickable { showFromDatePicker = true })
                }
            }
            Column {
                Text("To", style = MaterialTheme.typography.labelSmall)
                Row {
                    Text(toTimeState.format(DateTimeFormatter.ofPattern("HH:mm")), Modifier.background(Color(0xFFF3F4F6)).padding(8.dp).clickable { showToTimePicker = true })
                    Spacer(Modifier.width(8.dp))
                    Text(toDateState.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")), Modifier.background(Color(0xFFF3F4F6)).padding(8.dp).clickable { showToDatePicker = true })
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Notifications, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text(reminder)
        }

        Spacer(Modifier.height(16.dp))
        Text("Visitors", style = MaterialTheme.typography.titleMedium)
        Row {
            FilterChip("All", selected = selectedFilter == "All") { selectedFilter = "All" }
            Spacer(Modifier.width(8.dp))
            FilterChip("Going", selected = selectedFilter == "Going") { selectedFilter = "Going" }
            Spacer(Modifier.width(8.dp))
            FilterChip("Not going", selected = selectedFilter == "Not going") { selectedFilter = "Not going" }
        }

        Spacer(Modifier.height(8.dp))

        // Going list
        if (selectedFilter == "All" || selectedFilter == "Going") {
            Text("Going", style = MaterialTheme.typography.labelMedium)
            visitors.filter { it.going }.forEach { visitor ->
                VisitorRow(visitor)
            }
            Spacer(Modifier.height(8.dp))
        }

        // Not going list
        if (selectedFilter == "All" || selectedFilter == "Not going") {
            Text("Not going", style = MaterialTheme.typography.labelMedium)
            visitors.filter { !it.going }.forEach { visitor ->
                VisitorRow(visitor)
            }
        }

        Spacer(Modifier.weight(1f))
        Text(
            "DELETE EVENT",
            color = Color.Red,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .clickable { onDelete() }
                .padding(vertical = 16.dp),
            style = MaterialTheme.typography.bodyMedium
        )
    }

    // Navigation to PhotoDetailPage
    if (photoViewModel.selectedPhotoIndex.value != null) {
        // Show PhotoDetailPage as a dialog or overlay if needed
        // For now, navigation is handled via navController
    }
}

@Composable
fun FilterChip(text: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        Modifier
            .background(
                if (selected) Color.Black else Color(0xFFF3F4F6),
                RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text,
            color = if (selected) Color.White else Color.Black,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun VisitorRow(visitor: Visitor) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .background(Color(0xFFF3F4F6), RoundedCornerShape(8.dp)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .size(32.dp)
                .background(Color.Gray, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(visitor.initials, color = Color.White)
        }
        Spacer(Modifier.width(8.dp))
        Text(visitor.name)
        if (visitor.isCreator) {
            Spacer(Modifier.width(8.dp))
            Text("CREATOR", color = Color.Gray, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
fun AddPhotoBox(modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(80.dp)
            .border(
                BorderStroke(1.dp, Color(0xFFB0BEC5)), // light blue-grey border
                shape = RoundedCornerShape(8.dp)
            )
            .background(Color(0xFFF5F7FA), shape = RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "+",
            color = Color(0xFFB0BEC5),
            fontSize = 32.sp
        )
    }
}

@Composable
fun PhotoDetailPage(
    modifier: Modifier = Modifier,
    photoViewModel: PhotoViewModel,
    onBack: () -> Unit,
    onDelete: () -> Unit,
    isOffline: Boolean
) {
    val bitmap = photoViewModel.selectedBitmap.value
    val selectedIndex = photoViewModel.selectedPhotoIndex.value
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, start = 8.dp, end = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
            }
            Text(
                "PHOTO",
                color = Color.White,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            IconButton(
                onClick = {
                    if (!isOffline && selectedIndex != null) {
                        onDelete()
                        onBack()
                    }
                },
                enabled = !isOffline
            ) {
                if (isOffline) {
                    Icon(Icons.Default.Cloud, contentDescription = "Offline", tint = Color.White)
                } else {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.White)
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        // Image
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (bitmap != null) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            } else {
                Text(
                    text = "Failed to load image",
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

data class Visitor(val name: String, val initials: String, val going: Boolean, val isCreator: Boolean)
val sampleVisitors = listOf(
    Visitor("Wade Warren", "EH", true, true),
    Visitor("Wade Warren", "EH", true, false),
    Visitor("Wade Warren", "EH", true, false),
    Visitor("Wade Warren", "EH", false, false),
    Visitor("Wade Warren", "EH", false, false)
)