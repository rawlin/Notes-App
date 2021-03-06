package com.rawlin.notesapp.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.rawlin.notesapp.domain.Note

@Database(entities = [Note::class, PinnedNote::class], version = 1)
abstract class NotesDatabase : RoomDatabase() {

    abstract fun notesDao(): NotesDao

    abstract fun pinnedNotesDao(): PinnedNotesDao
}