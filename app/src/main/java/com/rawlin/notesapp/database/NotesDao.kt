package com.rawlin.notesapp.database

import androidx.room.*
import com.rawlin.notesapp.domain.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface NotesDao {

    @Insert
    fun insertNote(note: Note): Long

    @Update(onConflict = OnConflictStrategy.ABORT)
    fun updateNote(note: Note): Int

    @Query("SELECT * FROM note WHERE 1 IS 1 ORDER BY createdTime DESC")
    fun getAllNotes(): Flow<List<Note>>

    @Query("SELECT * FROM note ORDER BY createdTime ")
    fun getNotesByCreatedTime(): Flow<List<Note>>

    @Delete
    fun deleteNote(note: Note): Int
}