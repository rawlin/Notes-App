package com.rawlin.notesapp.database

import androidx.room.*
import com.rawlin.notesapp.domain.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface NotesDao {

    @Insert
    fun insertNote(note: Note): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertAll(vararg notes: Note)

    @Update(onConflict = OnConflictStrategy.ABORT)
    fun updateNote(note: Note): Int

    @Query("SELECT * FROM note ORDER BY CASE WHEN :isNewBottom = 1 THEN createdTime END ASC, CASE WHEN :isNewBottom = 0 THEN createdTime END DESC")
    fun getAllNotes(isNewBottom: Boolean): Flow<List<Note>>

    @Query("SELECT * FROM note ORDER BY createdTime ")
    fun getNotesByCreatedTime(): Flow<List<Note>>

    @Delete
    fun deleteNote(note: Note): Int

    @Query("DELETE FROM note")
    suspend fun deleteAll()
}