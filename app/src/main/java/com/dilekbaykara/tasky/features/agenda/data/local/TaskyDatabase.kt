package com.dilekbaykara.tasky.features.agenda.data.local
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.dilekbaykara.tasky.features.agenda.data.local.converters.Converters
import com.dilekbaykara.tasky.features.agenda.data.local.dao.EventDao
import com.dilekbaykara.tasky.features.agenda.data.local.dao.ReminderDao
import com.dilekbaykara.tasky.features.agenda.data.local.dao.TaskDao
import com.dilekbaykara.tasky.features.agenda.data.local.entities.Event
import com.dilekbaykara.tasky.features.agenda.data.local.entities.Reminder
import com.dilekbaykara.tasky.features.agenda.data.local.entities.Task
import com.dilekbaykara.tasky.features.shared.presentation.notification.data.dao.NotificationScheduleDao
import com.dilekbaykara.tasky.features.shared.presentation.notification.data.entity.NotificationSchedule
@Database(
    entities = [Event::class, Task::class, Reminder::class, NotificationSchedule::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class TaskyDatabase : RoomDatabase() {
    abstract fun eventDao(): EventDao
    abstract fun taskDao(): TaskDao
    abstract fun reminderDao(): ReminderDao
    abstract fun notificationScheduleDao(): NotificationScheduleDao
    companion object {
        @Volatile
        private var INSTANCE: TaskyDatabase? = null
        fun getDatabase(context: Context): TaskyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TaskyDatabase::class.java,
                    "tasky_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
