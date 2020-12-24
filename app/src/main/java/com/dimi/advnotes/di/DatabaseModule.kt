package com.dimi.advnotes.di

import android.content.Context
import androidx.room.Room
import com.dimi.advnotes.framework.database.NoteDatabase
import com.dimi.advnotes.framework.database.dao.CheckItemDao
import com.dimi.advnotes.framework.database.dao.NoteDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideNoteDatabase(@ApplicationContext context: Context): NoteDatabase =
        Room.databaseBuilder(context, NoteDatabase::class.java, NoteDatabase.DATABASE_NAME)
            .fallbackToDestructiveMigration()
//            .addMigrations(object: Migration(28, 29) {
//                override fun migrate(database: SupportSQLiteDatabase) {
//                    database.execSQL("ALTER TABLE check_item ADD COLUMN 'order_' INTEGER NOT NULL DEFAULT(0)")
//                }
//            })
            .build()

    @Singleton
    @Provides
    fun provideNoteDao(database: NoteDatabase): NoteDao = database.getNoteDao()

    @Singleton
    @Provides
    fun provideCheckItemDaoDao(database: NoteDatabase): CheckItemDao = database.getCheckItemDao()
}