package com.dilekbaykara.tasky.domain.repository

import com.dilekbaykara.tasky.data.repository.AgendaRepository
import com.dilekbaykara.tasky.domain.model.AgendaItem
import com.dilekbaykara.tasky.domain.model.AgendaItemType
import javax.inject.Inject

class AgendaRepositoryImpl @Inject constructor(): AgendaRepository {
    override fun getAgendaItems(date: Long) : List<AgendaItem> {
        return listOf(
            AgendaItem(
                AgendaItemType.Reminder,
                "Eat Food",
                "Cheese",
                "May",
                9,
                10
            ),
            AgendaItem(
                AgendaItemType.Task,
                "Brush Hair",
                "Use Hair oil",
                "May",
                11,
                12
            ),
            AgendaItem(
                AgendaItemType.Event,
                "Eat container of cookies",
                "Oreo's.. don't forget the milk",
                "May",
                13,
                2
            )
        )
    }

    override suspend fun updateAgendaItems(id: String) {

    }

    override suspend fun deleteAgendaItems() {

    }
}