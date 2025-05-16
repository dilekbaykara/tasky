package com.dilekbaykara.tasky.presentation.agenda

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.dilekbaykara.tasky.domain.model.AgendaItem
import com.dilekbaykara.tasky.domain.model.AgendaItemType
import com.dilekbaykara.tasky.presentation.auth.register.Header

@Composable
fun AgendaScreen(viewModel: AgendaViewModel) {
    val agendaItems by viewModel.agendaItems.collectAsState()
    Surface(
        modifier = Modifier.fillMaxSize()
            .padding(top = 30.dp),
        color = Color.Black
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().align(Alignment.CenterHorizontally)){
                Row(modifier = Modifier.padding(10.dp)) {
                    MonthHeader(Modifier.padding(30.dp),)
                }
            }
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color.White,
                shape = RoundedCornerShape(24.dp)
            ) {
                DatePickerCarousel()
                DateHeader()
                AgendaItemList(
                    agendaItems
                )
            }
        }
    }
}


@Composable
fun MonthHeader(modifier: Modifier){
    Header(modifier = Modifier, "May")
}

@Composable
fun DatePickerCarousel(){
    Surface() { }
}

@Composable
fun DateHeader(){
    Header(modifier = Modifier,"")
}


@Composable
fun AgendaItemList(agendaItems : List<AgendaItem>) {
    LazyColumn {

        items(agendaItems.size) { index ->
            Box(modifier = Modifier.padding(15.dp)) {

                AgendaListItem(agendaItems[index])

            }


        }
    }

}


val greenColor = Color(red = 39, green = 159, blue = 112, alpha = 1)

val greyColor = Color(red = 242, green = 243, blue = 247, alpha = 1)

val lightGreenColor = Color(red = 202, green = 239, blue = 69, alpha = 1)


@Composable
fun AgendaListItem(agendaItem: AgendaItem) {

    val color = when (agendaItem.type) {
        AgendaItemType.Task -> {
           Color(red = 39, green = 159, blue = 112, alpha = 200)
        }
        AgendaItemType.Event -> {
            Color(red = 242, green = 243, blue = 247, alpha = 200)
        }
        else -> {
            Color(red = 202, green = 239, blue = 69, alpha = 200)
        }
    }
    Box(
        Modifier.background(color = color)
            .fillMaxWidth().padding(25.dp).height(50.dp)
            .border(width = 0.dp, color = Color.Transparent, shape = RoundedCornerShape(10.dp))
    ) {

    }
}

@Composable
fun AddAgendaItemFab() {

}
