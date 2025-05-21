package com.dilekbaykara.tasky.data.repository

import com.dilekbaykara.tasky.domain.model.AgendaItem

interface AgendaRepository {
    fun getAgendaItems(date: Long): List<AgendaItem>
    suspend fun updateAgendaItems(id: String)
    suspend fun deleteAgendaItems()

}

