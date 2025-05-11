package com.dilekbaykara.tasky.presentation.agenda

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dilekbaykara.tasky.domain.model.AgendaItem
import com.dilekbaykara.tasky.presentation.auth.register.Header

@Composable
fun AgendaScreen(viewModel: AgendaViewModel, agendaItem: AgendaItem){
    val agendaItems by viewModel.agendaItems.collectAsState()
    Column {
        DatePickerCarousel()
        DateHeader()
        AgendaItemList(
            agendaItems
        )
    }
}

@Composable
fun DatePickerCarousel(){
    Surface() { }
}

@Composable
fun DateHeader(){
    Header()
}


@Composable
fun AgendaItemList(agendaItems : List<AgendaItem>){
    LazyColumn{

        items(agendaItems) { item ->
            Text(text = item, modifier = Modifier.padding(16.dp))
        }
        }
    }



@Composable
fun AgendaItem(){

}

@Composable
fun AddAgendaItemFab(){

}


