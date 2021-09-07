package com.rawlin.notesapp.repository

import com.rawlin.notesapp.domain.Note
import kotlinx.coroutines.flow.Flow

interface IRepository  {

    suspend fun addNote(note: Note): Long

    suspend fun deleteNote(note: Note)

    suspend fun updateNote(note: Note)

    fun getAllNotes(): Flow<List<Note>>

}