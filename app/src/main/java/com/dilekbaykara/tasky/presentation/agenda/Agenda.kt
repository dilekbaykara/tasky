package com.dilekbaykara.tasky.presentation.agenda


import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.dilekbaykara.tasky.domain.model.AgendaItem
import com.dilekbaykara.tasky.domain.model.AgendaItemType
import com.dilekbaykara.tasky.presentation.auth.register.AgendaDateTime
import com.dilekbaykara.tasky.presentation.auth.register.AgendaDescription
import com.dilekbaykara.tasky.presentation.auth.register.AttendeeButton
import com.dilekbaykara.tasky.presentation.auth.register.AvatarDropDown
import com.dilekbaykara.tasky.presentation.auth.register.CalendarButton
import com.dilekbaykara.tasky.presentation.auth.register.DatePickers
import com.dilekbaykara.tasky.presentation.auth.register.Header
import com.dilekbaykara.tasky.presentation.auth.register.MoreDropdownMenu
import com.dilekbaykara.tasky.presentation.auth.register.TaskyFab
import java.time.LocalDate
import java.time.temporal.ChronoField
import java.time.temporal.ChronoUnit
import androidx.navigation.NavController
import com.dilekbaykara.tasky.presentation.navigation.ux.TaskyScreen


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AgendaScreen(viewModel: AgendaViewModel, navController: NavController) {
    val agendaItems by viewModel.agendaItems.collectAsState()
    val context = LocalContext.current

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
                        .padding(10.dp), horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    MonthHeader(Modifier)
                    Box {
                        CalendarButton(Modifier.background(Color.White))
                        Row(
                            modifier = Modifier.padding(5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CalendarButton()
                            AvatarDropDown(Modifier)
                        }
                    }
                }

            }
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally)
                ) {

                    DatePickerCarousel(Modifier.height(40.dp))
                    DateHeader()
                    AgendaItemList(
                        agendaItems
                    ) {
                        when (it.type) {
                            is AgendaItemType.Task -> navController.navigate(TaskyScreen.TaskDetail.route)
                            is AgendaItemType.Event -> navController.navigate(TaskyScreen.EventDetail.route)
                            is AgendaItemType.Reminder -> navController.navigate(TaskyScreen.ReminderDetail.route)
                        }
                    }
                    //Align this properly?
                    TaskyFab()
                }

            }

        }
    }

}

@Composable
fun MonthHeader(modifier: Modifier = Modifier){
    Header(modifier = Modifier.padding(10.dp), "MAY")
}


@SuppressLint("NewApi")
@Composable
fun DatePickerCarousel(modifier: Modifier = Modifier){
    val listState = rememberLazyListState()
    val presentDate = listState
    val today = LocalDate.now()
    val leftBound = today.minusDays(15)
    val rightBound = today.plusDays(15)

    val daysBetween = ChronoUnit.DAYS.between(leftBound, rightBound)
    val allDaysInBounds = List(daysBetween.toInt() + 1) { today.plusDays(it.toLong())}

    val pairDays = allDaysInBounds.map {
        val dayLetter = it.dayOfWeek.name.first()
        val dayIndex = it.get(ChronoField.DAY_OF_MONTH)
        Pair(dayLetter, dayIndex)

    }
    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(top = 20.dp, bottom = 20.dp)) {
        Column {
            LazyRow(state = listState) {
                items(Int.MAX_VALUE) { index ->
                    val actualIndex = index % pairDays.size
                    Box(
                        Modifier
                            .padding(8.dp)
                            .width(30.dp)
                            .background(MaterialTheme.colorScheme.surface)
                    ) {
                        Column(verticalArrangement = Arrangement.Center) {
                            DatePickers(modifier.align(Alignment.CenterHorizontally), pairDays[actualIndex])
                        }
                    }
                }
            }
        }
    }

}





@Composable
fun DateHeader(modifier: Modifier = Modifier){
    Box() {
        Header(modifier = Modifier.padding(15.dp), "Today")
    }
}

@SuppressLint("NewApi")
@Composable
fun AgendaItemList(agendaItems : List<AgendaItem>, onClick: (itemType: AgendaItem) -> Unit) {

    LazyColumn {
        items(agendaItems.size) { index ->
            Box(modifier = Modifier
                .padding(15.dp)
                .background(Color.Transparent)
                .wrapContentSize().clickable {
                    onClick(agendaItems[index])
                }
            ) {
                AgendaListItem(agendaItems[index])
            }
        }
    }
}

@Composable
fun AgendaListItem(agendaItem: AgendaItem) {
    Box(
        Modifier
            .background(
                color = getColor(agendaItem),
                shape = RoundedCornerShape(20.dp)
            )
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .height(150.dp)
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(15.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Header(Modifier.padding(top = 10.dp, bottom = 10.dp), "Placeholder")
                MoreDropdownMenu(
                )
            }
            AgendaDescription(Modifier.align(Alignment.Start), "Placeholder")
            Row (Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                AttendeeButton(
                    Modifier
                        .padding(top = 10.dp)
                        .align(Alignment.CenterVertically), "Attendee")
                AgendaDateTime(
                    Modifier
                        .padding(top = 10.dp)
                        .align(Alignment.CenterVertically), text = "Mar 5, 2025")
            }
        }
    }

}


fun getColor(agendaItem: AgendaItem) : Color {
    return when (agendaItem.type) {
        AgendaItemType.Task -> {
            greenColor
        }
        AgendaItemType.Event -> {
            greyColor
        }
        else -> {
            lightGreenColor
        }
    }
}


val greenColor = Color(red = 39, green = 159, blue = 112, alpha = 200)




val greyColor = Color(red = 242, green = 243, blue = 250, alpha = 200)




val lightGreenColor = Color(red = 202, green = 239, blue = 69, alpha = 200)






@Composable
fun AddAgendaItemFab() {




}






