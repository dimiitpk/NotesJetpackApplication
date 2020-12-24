package com.dimi.advnotes.di

import android.content.Context
import com.dimi.advnotes.AppScopeCoroutineExceptionHandler
import com.dimi.advnotes.data.database.CacheDataSource
import com.dimi.advnotes.data.database.CacheDataSourceImpl
import com.dimi.advnotes.data.interactors.ClearReminderUseCase
import com.dimi.advnotes.data.interactors.DeleteNoteUseCase
import com.dimi.advnotes.data.interactors.DeleteNotesUseCase
import com.dimi.advnotes.data.interactors.FetchAllNotesUseCase
import com.dimi.advnotes.data.interactors.FetchSingleNote
import com.dimi.advnotes.data.interactors.InsertAllNotesUseCase
import com.dimi.advnotes.data.interactors.InsertOrUpdateNoteUseCase
import com.dimi.advnotes.data.interactors.NoteUseCase
import com.dimi.advnotes.data.interactors.ObserveNotesUseCase
import com.dimi.advnotes.data.interactors.ObserveRemindersUseCase
import com.dimi.advnotes.data.interactors.UpdateNotesUseCase
import com.dimi.advnotes.di.qualifiers.DataSource
import com.dimi.advnotes.di.qualifiers.FrameworkSource
import com.dimi.advnotes.framework.database.DatabaseRepository
import com.dimi.advnotes.framework.database.NoteDatabase
import com.dimi.advnotes.framework.database.mapper.CacheMapper
import com.dimi.advnotes.presentation.common.ReminderManager
import com.dimi.advnotes.presentation.common.UserPreferencesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideNoteUseCase(
        @DataSource cacheSource: CacheDataSource
    ): NoteUseCase {
        return NoteUseCase(
            insertOrUpdateNote = InsertOrUpdateNoteUseCase(cacheSource),
            observeNotes = ObserveNotesUseCase(cacheSource),
            deleteNotes = DeleteNotesUseCase(cacheSource),
            updateNotes = UpdateNotesUseCase(cacheSource),
            insertAllNotes = InsertAllNotesUseCase(cacheSource),
            deleteNote = DeleteNoteUseCase(cacheSource),
            fetchSingleNote = FetchSingleNote(cacheSource),
            clearReminder = ClearReminderUseCase(cacheSource),
            observeReminders = ObserveRemindersUseCase(cacheSource),
            fetchAllNotes = FetchAllNotesUseCase(cacheSource)
        )
    }

    @Singleton
    @FrameworkSource
    @Provides
    fun provideDatabaseRepository(
        database: NoteDatabase,
        cacheMapper: CacheMapper
    ): CacheDataSource = DatabaseRepository(database, cacheMapper)

    @Singleton
    @DataSource
    @Provides
    fun provideCacheDataSource(
        @FrameworkSource cacheSource: CacheDataSource
    ): CacheDataSource = CacheDataSourceImpl(cacheSource)

    @Singleton
    @Provides
    fun provideReminderManager(
        @ApplicationContext context: Context
    ) = ReminderManager(context)

    @Singleton
    @Provides
    fun provideUserPreferencesRepository(
        @ApplicationContext context: Context
    ) = UserPreferencesRepository(context)

    @Singleton
    @Provides
    fun provideAppCoroutineScope(
        appScopeCoroutineExceptionHandler: AppScopeCoroutineExceptionHandler
    ): CoroutineScope =
        CoroutineScope(
            SupervisorJob() +
                    appScopeCoroutineExceptionHandler +
                    Dispatchers.Main
        )
}