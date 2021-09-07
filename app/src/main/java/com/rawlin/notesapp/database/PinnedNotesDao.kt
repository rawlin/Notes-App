package com.rawlin.notesapp.database

import androidx.room.*
import com.rawlin.notesapp.domain.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface PinnedNotesDao {

    @Update(onConflict = OnConflictStrategy.ABORT)
    fun updatePinnedNote(note: Note): Int

    @Query("SELECT * FROM pinned_notes")
    fun getAllPinnedNotes(): Flow<List<PinnedNote>>

    @Delete
    fun deletePinnedNote(note: PinnedNote): Int
}