package com.dilekbaykara.tasky.core.di
import android.content.Context
import com.dilekbaykara.tasky.features.agenda.data.AgendaRepository
import com.dilekbaykara.tasky.features.agenda.data.AgendaRepositoryImpl
import com.dilekbaykara.tasky.features.agenda.data.local.TaskyDatabase
import com.dilekbaykara.tasky.features.agenda.data.local.dao.EventDao
import com.dilekbaykara.tasky.features.agenda.data.local.dao.ReminderDao
import com.dilekbaykara.tasky.features.agenda.data.local.dao.TaskDao
import com.dilekbaykara.tasky.features.auth.data.AuthService
import com.dilekbaykara.tasky.features.shared.data.remote.TaskyApi
import com.dilekbaykara.tasky.features.shared.presentation.notification.data.dao.NotificationScheduleDao
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideContext(@ApplicationContext context: Context): Context {
        return context
    }

    @Provides
    @Singleton
    fun provideTaskyDatabase(@ApplicationContext context: Context): TaskyDatabase {
        return TaskyDatabase.Companion.getDatabase(context)
    }

    @Provides
    fun provideEventDao(database: TaskyDatabase): EventDao {
        return database.eventDao()
    }

    @Provides
    fun provideTaskDao(database: TaskyDatabase): TaskDao {
        return database.taskDao()
    }

    @Provides
    fun provideReminderDao(database: TaskyDatabase): ReminderDao {
        return database.reminderDao()
    }

    @Provides
    fun provideNotificationScheduleDao(database: TaskyDatabase): NotificationScheduleDao {
        return database.notificationScheduleDao()
    }

    @Provides
    @Singleton
    fun provideAgendaRepositoryImpl(
        eventDao: EventDao,
        taskDao: TaskDao,
        reminderDao: ReminderDao,
        api: TaskyApi,
        authService: AuthService,
        @ApplicationContext context: Context
    ): AgendaRepositoryImpl {
        return AgendaRepositoryImpl(eventDao, taskDao, reminderDao, api, authService, context)
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindAgendaRepository(
        agendaRepositoryImpl: AgendaRepositoryImpl
    ): AgendaRepository
}
