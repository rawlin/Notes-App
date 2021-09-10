package com.rawlin.notesapp.repository

import android.util.Log
import androidx.room.withTransaction
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.rawlin.notesapp.database.NotesDatabase
import com.rawlin.notesapp.database.PinnedNote
import com.rawlin.notesapp.domain.DispatcherProvider
import com.rawlin.notesapp.domain.Note
import com.rawlin.notesapp.preferences.DataStoreManager
import com.rawlin.notesapp.utils.Constants.CREATED_TIME
import com.rawlin.notesapp.utils.Constants.IMAGE_URI
import com.rawlin.notesapp.utils.Constants.MESSAGE
import com.rawlin.notesapp.utils.Constants.NOTES_PATH
import com.rawlin.notesapp.utils.Constants.PINNED_NOTES_PATH
import com.rawlin.notesapp.utils.Constants.TITLE
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.resume


private const val TAG = "RepositoryImpl"

class RepositoryImpl @Inject constructor(
    private val notesDb: NotesDatabase,
    private val dispatcher: DispatcherProvider,
    private val dataStoreManager: DataStoreManager,
) : IRepository {

    private val db = Firebase.firestore


    override suspend fun addNote(note: Note) = withContext(dispatcher.io) {
        addNoteToFirestore(note)
        notesDb.notesDao().insertNote(note)
    }

    override suspend fun deleteNote(note: Note) = withContext(dispatcher.io) {
        deleteNoteInFirestore(note.id)
        notesDb.notesDao().deleteNote(note)
    }

    override suspend fun updateNote(note: Note) = withContext(dispatcher.io) {
        updateNoteInFirestore(note)
        notesDb.notesDao().updateNote(note)
    }

    override suspend fun updatePinnedNote(pinnedNote: PinnedNote): Int =
        withContext(dispatcher.io) {
            updatePinnedNoteInFirestore(pinnedNote)
            notesDb.pinnedNotesDao().updatePinnedNote(pinnedNote)
        }

    override suspend fun getAllNotes(isSortByCreatedTime: Boolean): Flow<List<Note>> =
        withContext(dispatcher.io) {
            notesDb.notesDao().getAllNotes(isSortByCreatedTime)
        }

    override suspend fun getAllPinnedNotes(): Flow<List<PinnedNote>> = withContext(dispatcher.io) {
        notesDb.pinnedNotesDao().getAllPinnedNotes()
    }


    override suspend fun addPinnedNote(note: PinnedNote) = withContext(dispatcher.io) {
        addPinnedNoteToFirestore(note)
        notesDb.pinnedNotesDao().addPinnedNote(note)
    }

    override suspend fun deletePinnedNote(note: PinnedNote): Int = withContext(dispatcher.io) {
        deletePinnedNoteInFirestore(note)
        notesDb.pinnedNotesDao().deletePinnedNote(note)
    }

    override suspend fun getNumberOfPinnedEntries(): Int = withContext(dispatcher.io) {
        notesDb.pinnedNotesDao().getNumberOfEntries()
    }


    override val pinMode: Flow<Boolean>
        get() = dataStoreManager.pinMode

    override val showNewBottom: Flow<Boolean>
        get() = dataStoreManager.showBottom

    override val isSharingEnabled: Flow<Boolean>
        get() = dataStoreManager.sharing

    override suspend fun setPinMode(isSet: Boolean) = dataStoreManager.setPinMode(isSet)

    override suspend fun setShowNewBottom(isSet: Boolean) = dataStoreManager.setShowBottom(isSet)

    override suspend fun setIsSharingEnabled(isSet: Boolean) = dataStoreManager.setSharing(isSet)

    private fun addPinnedNoteToFirestore(pinnedNote: PinnedNote) {
        db.collection(PINNED_NOTES_PATH)
            .document(pinnedNote.id)
            .set(pinnedNote)
            .addOnSuccessListener {
                Log.d(TAG, "Successfully added pinned notes to Firestore")
            }
            .addOnFailureListener {
                Log.e(TAG, "Could not add pinned note to Firestore")
            }
    }

    private fun updatePinnedNoteInFirestore(pinnedNote: PinnedNote) {
        db.collection(PINNED_NOTES_PATH)
            .document(pinnedNote.id)
            .update(
                mapOf(
                    "title" to pinnedNote.title,
                    "message" to pinnedNote.message,
                    "imageUri" to pinnedNote.imageUri
                )
            )
    }

    private fun deletePinnedNoteInFirestore(pinnedNote: PinnedNote) {
        db.collection(PINNED_NOTES_PATH)
            .document(pinnedNote.id)
            .delete()
            .addOnSuccessListener {
                Log.d(TAG, "Successfully deleted pinned note from Firebase")
            }
            .addOnFailureListener {
                Log.e(TAG, "Could note delete pinned note from Firebase", it)
            }
    }

    private fun addNoteToFirestore(note: Note) {
        db.collection(NOTES_PATH)
            .document(note.id)
            .set(note)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "Note added successfully: ")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error adding document", e)
            }
    }

    private fun deleteNoteInFirestore(docId: String) {
        db.collection(NOTES_PATH)
            .document(docId)
            .delete()
            .addOnSuccessListener {
                Log.d(TAG, "Successfully deleted note from Firebase")
            }
            .addOnFailureListener {
                Log.e(TAG, "Could note delete note from Firebase", it)
            }
    }

    private fun updateNoteInFirestore(note: Note) {
        db.collection("notes")
            .document(note.id)
            .update(
                mapOf(
                    TITLE to note.title,
                    MESSAGE to note.message,
                    IMAGE_URI to note.imageUri
                )
            )
    }

    suspend fun triggerCalls() = withContext(dispatcher.io) {
        val notesDeferred = async { getAllNotesFromFirestore() }
        val pinnedNotesDeferred = async { getAllPinnedNotesFromFirestore() }
        val notes = notesDeferred.await()
        val pinnedNote = pinnedNotesDeferred.await()
        notesDb.withTransaction {
            notesDb.notesDao().deleteAll()
            notesDb.notesDao().insertAll(*notes.toTypedArray())
        }
        notesDb.withTransaction {
            notesDb.pinnedNotesDao().deleteAll()
            notesDb.pinnedNotesDao().insertAll(*pinnedNote.toTypedArray())
        }
    }

    private suspend fun getAllNotesFromFirestore() = withContext(dispatcher.io) {
        suspendCancellableCoroutine<List<Note>> { continuation ->
            db.collection(NOTES_PATH)
                .get(Source.SERVER)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        if (task.result == null) {
                            continuation.resume(emptyList())
                        } else {
                            val list = mutableListOf<Note>()
                            for (document in task.result!!) {
                                val id = document.id
                                val title = document.getString(TITLE) ?: ""
                                val message = document.getString(MESSAGE) ?: ""
                                val createdAt =
                                    document.getLong(CREATED_TIME) ?: System.currentTimeMillis()
                                val imageUri = document.getString(IMAGE_URI)
                                val pinnedNote = Note(
                                    id = id,
                                    title = title,
                                    message = message,
                                    createdTime = createdAt,
                                    imageUri = imageUri
                                )
                                list.add(pinnedNote)

                            }
                            continuation.resume(list)
                        }

                    } else {
                        continuation.resume(emptyList())
                        Log.d(TAG, "Error getting all notes from Firestore ", task.exception)
                    }
                }
        }
    }

    private suspend fun getAllPinnedNotesFromFirestore() = withContext(dispatcher.io) {
        suspendCancellableCoroutine<List<PinnedNote>> { continuation ->
            db.collection(PINNED_NOTES_PATH)
                .get(Source.SERVER)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {

                        if (task.result == null) {
                            continuation.resume(emptyList())
                        } else {
                            val list = mutableListOf<PinnedNote>()
                            for (document in task.result!!) {
                                val id = document.id
                                val title = document.getString(TITLE) ?: ""
                                val message = document.getString(MESSAGE) ?: ""
                                val createdAt =
                                    document.getLong(CREATED_TIME) ?: System.currentTimeMillis()
                                val imageUri = document.getString(IMAGE_URI)
                                val pinnedNote = PinnedNote(
                                    id = id,
                                    title = title,
                                    message = message,
                                    createdTime = createdAt,
                                    imageUri = imageUri
                                )
                                list.add(pinnedNote)

                            }
                            continuation.resume(list)
                        }


                    } else {
                        Log.e(TAG, "Error getting pinned notes from Firestore ", task.exception)
                        continuation.resume(emptyList())
                    }
                }
        }


    }


}