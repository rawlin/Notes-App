package com.rawlin.notesapp.repository

import com.rawlin.notesapp.database.PinnedNote
import com.rawlin.notesapp.domain.Note
import kotlinx.coroutines.flow.Flow

interface IRepository {
    val pinMode: Flow<Boolean>

    val showNewBottom: Flow<Boolean>

    val isSharingEnabled: Flow<Boolean>

    suspend fun setPinMode(isSet: Boolean)

    suspend fun setShowNewBottom(isSet: Boolean)

    suspend fun setIsSharingEnabled(isSet: Boolean)

    suspend fun addNote(note: Note): Long

    suspend fun deleteNote(note: Note): Int

    suspend fun updateNote(note: Note): Int

    suspend fun updatePinnedNote(pinnedNote: PinnedNote): Int

    suspend fun getAllNotes(isSortByCreatedTime: Boolean): Flow<List<Note>>

    suspend fun getAllPinnedNotes(): Flow<List<PinnedNote>>

    suspend fun addPinnedNote(note: PinnedNote): Long

    suspend fun deletePinnedNote(note: PinnedNote): Int

    suspend fun getNumberOfPinnedEntries(): Int


}