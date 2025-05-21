package com.dilekbaykara.tasky.presentation.agenda

import androidx.lifecycle.ViewModel
import com.dilekbaykara.tasky.domain.repository.AgendaRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class AgendaViewModel @Inject constructor(
val agendaRepositoryImpl : AgendaRepositoryImpl
) : ViewModel() {

   private val _agendaItems = MutableStateFlow(getItems())
    val agendaItems = _agendaItems.asStateFlow()

    fun getItems() = agendaRepositoryImpl.getAgendaItems(
            32
        )


}