package com.dilekbaykara.tasky.presentation.agenda

import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import com.dilekbaykara.tasky.domain.repository.AgendaRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class AgendaViewModel @Inject constructor(
val agendaRepositoryImpl : AgendaRepositoryImpl
) : ViewModel() {

   private val _agendaItems = MutableStateFlow(getItems())
    val agendaItems = _agendaItems.asStateFlow()

    fun getItems() = agendaRepositoryImpl.getAgendaItems(
            32
        )


}