package com.rawlin.notesapp.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.rawlin.notesapp.database.NotesDatabase
import com.rawlin.notesapp.domain.DispatcherProvider
import com.rawlin.notesapp.domain.StandardDispatchers
import com.rawlin.notesapp.utils.Constants.DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideNoteDatabase(app: Application) =
        Room.databaseBuilder(app, NotesDatabase::class.java, DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build()

    @Singleton
    @Provides
    fun provideDispatcherProvider(): DispatcherProvider {
        return StandardDispatchers()
    }
}