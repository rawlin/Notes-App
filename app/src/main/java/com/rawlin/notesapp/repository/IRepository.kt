package com.rawlin.notesapp.repository

import com.rawlin.notesapp.database.PinnedNote
import com.rawlin.notesapp.domain.Note
import kotlinx.coroutines.flow.Flow

interface IRepository {

    suspend fun addNote(note: Note): Long

    suspend fun deleteNote(note: Note): Int

    suspend fun updateNote(note: Note): Int

    fun getAllNotes(): Flow<List<Note>>

    fun getAllPinnedNotes(): Flow<List<PinnedNote>>

    suspend fun addPinnedNote(note: PinnedNote): Long

    suspend fun deletePinnedNote(note: PinnedNote): Int

}