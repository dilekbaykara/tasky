package com.dilekbaykara.tasky.features.agenda.data.local.dao
import androidx.room.*
import com.dilekbaykara.tasky.features.agenda.data.local.entities.Event
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
@Dao
interface EventDao {
    @Query("SELECT * FROM events ORDER BY fromDate ASC, fromTime ASC")
    fun getAllEvents(): Flow<List<Event>>

    @Query("SELECT * FROM events ORDER BY fromDate ASC, fromTime ASC")
    suspend fun getAllEventsSync(): List<Event>

    @Query("SELECT * FROM events WHERE fromDate = :date ORDER BY fromTime ASC")
    fun getEventsByDate(date: LocalDate): Flow<List<Event>>

    @Query("SELECT * FROM events WHERE id = :id")
    suspend fun getEventById(id: String): Event?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: Event)

    @Update
    suspend fun updateEvent(event: Event)

    @Query("DELETE FROM events WHERE id = :eventId")
    suspend fun deleteEvent(eventId: String)

    @Query("DELETE FROM events WHERE id = :id")
    suspend fun deleteEventById(id: String)

    @Query("DELETE FROM events")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(events: List<Event>)
}
