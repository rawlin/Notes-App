package com.rawlin.notesapp.database

import androidx.room.*
import com.rawlin.notesapp.domain.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface PinnedNotesDao {

    @Update(onConflict = OnConflictStrategy.ABORT)
    fun updatePinnedNote(note: PinnedNote): Int

    @Query("SELECT * FROM pinned_notes ORDER BY createdTime ASC")
    fun getAllPinnedNotes(): Flow<List<PinnedNote>>

    @Delete
    fun deletePinnedNote(note: PinnedNote): Int

    @Insert
    fun addPinnedNote(note: PinnedNote): Long

    @Query("SELECT COUNT(*) FROM pinned_notes")
    fun getNumberOfEntries(): Int
}