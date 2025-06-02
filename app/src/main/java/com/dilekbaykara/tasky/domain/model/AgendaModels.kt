package com.dilekbaykara.tasky.domain.model

data class AgendaItem(
    val type: AgendaItemType,
    val title: String,
    val description: String,
    val month: String,
    val date: Long,
    val time: Long
)

sealed class AgendaItemType {
    data object Reminder : AgendaItemType()
    data object Task : AgendaItemType()
    data object Event : AgendaItemType()
}

