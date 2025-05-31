package com.dilekbaykara.tasky.presentation.events

import android.app.TimePickerDialog
import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dilekbaykara.tasky.presentation.tasks.DeleteTaskSheet
import kotlinx.coroutines.launch
import java.util.Calendar
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun EventDetailsPage(
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

data class Visitor(val name: String, val initials: String, val going: Boolean, val isCreator: Boolean)
val sampleVisitors = listOf(
    Visitor("Wade Warren", "EH", true, true),
    Visitor("Wade Warren", "EH", true, false),
    Visitor("Wade Warren", "EH", true, false),
    Visitor("Wade Warren", "EH", false, false),
    Visitor("Wade Warren", "EH", false, false)
)