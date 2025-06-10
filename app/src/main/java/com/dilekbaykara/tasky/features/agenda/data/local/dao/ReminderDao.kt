package com.dilekbaykara.tasky.features.agenda.data.local.dao
import androidx.room.*
import com.dilekbaykara.tasky.features.agenda.data.local.entities.Reminder
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
@Dao
interface ReminderDao {
    @Query("SELECT * FROM reminders ORDER BY date ASC, time ASC")
    fun getAllReminders(): Flow<List<Reminder>>

    @Query("SELECT * FROM reminders ORDER BY date ASC, time ASC")
    suspend fun getAllRemindersSync(): List<Reminder>

    @Query("SELECT * FROM reminders WHERE date = :date ORDER BY time ASC")
    fun getRemindersByDate(date: LocalDate): Flow<List<Reminder>>

    @Query("SELECT * FROM reminders WHERE id = :id")
    suspend fun getReminderById(id: String): Reminder?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: Reminder)

    @Update
    suspend fun updateReminder(reminder: Reminder)

    @Query("DELETE FROM reminders WHERE id = :reminderId")
    suspend fun deleteReminder(reminderId: String)

    @Query("DELETE FROM reminders WHERE id = :id")
    suspend fun deleteReminderById(id: String)

    @Query("DELETE FROM reminders")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(reminders: List<Reminder>)
}
