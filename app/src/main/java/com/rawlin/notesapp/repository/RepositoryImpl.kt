package com.rawlin.notesapp.repository

import com.rawlin.notesapp.database.NotesDatabase
import com.rawlin.notesapp.domain.DispatcherProvider
import com.rawlin.notesapp.domain.Note
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
    private val notesDb: NotesDatabase,
    private val dispatcher: DispatcherProvider
) : IRepository {

    override suspend fun addNote(note: Note) = withContext(dispatcher.io) {
        notesDb.notesDao().insertNote(note)
    }

    override suspend fun deleteNote(note: Note) {
        notesDb.notesDao().deleteNote(note)
    }

    override suspend fun updateNote(note: Note) {
        notesDb.notesDao().updateNote(note)
    }

    override fun getAllNotes(): Flow<List<Note>> = notesDb.notesDao().getAllNotes()
}