package com.rawlin.notesapp.ui.notes_details

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rawlin.notesapp.database.PinnedNote
import com.rawlin.notesapp.domain.Note
import com.rawlin.notesapp.repository.RepositoryImpl
import com.rawlin.notesapp.utils.Resource
import com.rawlin.notesapp.utils.isValidInput
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

private const val TAG = "NotesDetailViewModel"

@HiltViewModel
class NotesDetailViewModel @Inject constructor(
    private val repository: RepositoryImpl
) : ViewModel() {

    private val _createNoteState = MutableSharedFlow<Resource<Unit>>()
    val createNoteState: SharedFlow<Resource<Unit>>
        get() = _createNoteState

    private val _updateNoteState = MutableSharedFlow<Resource<Unit>>()
    val updateNoteState: SharedFlow<Resource<Unit>>
        get() = _updateNoteState

    val isSharingEnabled
        get() = repository.isSharingEnabled

    private val _pinnedState: MutableSharedFlow<Resource<Unit>> = MutableSharedFlow()
    val pinnedState: SharedFlow<Resource<Unit>>
        get() = _pinnedState

    private val _deleteNoteStatus = MutableSharedFlow<Resource<Unit>>()
    val deleteNoteStatus: SharedFlow<Resource<Unit>>
        get() = _deleteNoteStatus

    private val _unPinnedState: MutableSharedFlow<Resource<Unit>> = MutableSharedFlow()
    val unPinnedState: SharedFlow<Resource<Unit>>
        get() = _unPinnedState


    fun createNote(title: String, message: String, imageUri: Uri?) = viewModelScope.launch {
        if (!title.isValidInput() || !message.isValidInput()) {
            Log.d(TAG, "createNote: $title $message")
            _createNoteState.emit(Resource.Error("Please enter Title and message to create note"))
            return@launch
        }
        Log.d(TAG, "createNote: $title $message")
        _createNoteState.emit(Resource.Loading())
        try {
            val imageUriString = imageUri?.toString()
            val randomId = UUID.randomUUID().toString()
            val note = Note(
                id = randomId,
                title = title,
                message = message,
                createdTime = System.currentTimeMillis(),
                imageUri = imageUriString
            )
            repository.addNote(note)
            _createNoteState.emit(Resource.Success(Unit))
        } catch (e: Throwable) {
            _createNoteState.emit(Resource.Error("Error in creating note"))
            Log.e(TAG, "Failed to create note: $e")
        }
    }

    fun updateNote(title: String, message: String, id: String, imageUri: Uri?, createdTime: Long?) =
        viewModelScope.launch {
            if (!title.isValidInput() || !message.isValidInput()) {
                Log.d(TAG, "createNote: $title $message")
                _createNoteState.emit(Resource.Error("Please enter Title and message to create note"))
                return@launch
            }
            _updateNoteState.emit(Resource.Loading())
            try {
                val imageUriString = imageUri?.toString()
                val note = Note(
                    id = id,
                    title = title,
                    message = message,
                    imageUri = imageUriString,
                    createdTime = createdTime
                )
                repository.updateNote(note)
                _updateNoteState.emit(Resource.Success(Unit))
            } catch (e: Throwable) {
                _updateNoteState.emit(Resource.Error("Error updating note"))
                Log.e(TAG, "Failed to update note: $e")
            }
        }

    fun updatePinnedNote(
        title: String,
        message: String,
        id: String,
        imageUri: Uri?
    ) =
        viewModelScope.launch {
            if (!title.isValidInput() || !message.isValidInput()) {
                Log.d(TAG, "createNote: $title $message")
                _createNoteState.emit(Resource.Error("Please enter Title and message to create note"))
                return@launch
            }
            _updateNoteState.emit(Resource.Loading())
            try {
                val imageUriString = imageUri?.toString()
                val note = PinnedNote(
                    id = id,
                    title = title,
                    message = message,
                    imageUri = imageUriString,
                    createdTime = System.currentTimeMillis()
                )
                repository.updatePinnedNote(note)
                _updateNoteState.emit(Resource.Success(Unit))
            } catch (e: Throwable) {
                _updateNoteState.emit(Resource.Error("Error updating note"))
                Log.e(TAG, "Failed to update note: $e")
            }
        }

    fun deleteNote(note: Note) = viewModelScope.launch {
        _deleteNoteStatus.emit(Resource.Loading())
        try {
            repository.deleteNote(note)
            _deleteNoteStatus.emit(Resource.Success(Unit))
        } catch (e: Throwable) {
            e.printStackTrace()
            _deleteNoteStatus.emit(Resource.Error(e.message ?: "Could not delete"))
        }
    }

    fun deletePinnedNote(pinnedNote: PinnedNote) = viewModelScope.launch {
        _deleteNoteStatus.emit(Resource.Loading())
        try {
            repository.deletePinnedNote(pinnedNote)
            _deleteNoteStatus.emit(Resource.Success(Unit))
        } catch (e: Throwable) {
            e.printStackTrace()
            _deleteNoteStatus.emit(Resource.Error(e.message ?: "Could not delete"))
        }
    }

    fun pinNote(note: Note) = viewModelScope.launch {
        _pinnedState.emit(Resource.Loading())
        try {
            val pinnedNote = PinnedNote(
                id = note.id,
                title = note.title,
                message = note.message,
                imageUri = note.imageUri,
                createdTime = System.currentTimeMillis()
            )
            val size = repository.getNumberOfPinnedEntries()
            if (size < 4) {
                repository.addPinnedNote(pinnedNote)
                repository.deleteNote(note)
                _pinnedState.emit(Resource.Success(Unit))
                return@launch
            }
            _pinnedState.emit(Resource.Error("Only 4 notes can be pinned"))
        } catch (e: Throwable) {
            e.printStackTrace()
            _pinnedState.emit(Resource.Error(e.message ?: "Could note pin note"))
        }
    }

    fun unPinNote(pinnedNote: PinnedNote) = viewModelScope.launch {
        _unPinnedState.emit(Resource.Loading())
        try {
            val note = Note(
                id = pinnedNote.id,
                title = pinnedNote.title,
                message = pinnedNote.message,
                imageUri = pinnedNote.imageUri,
                createdTime = System.currentTimeMillis()
            )
            repository.addNote(note)
            repository.deletePinnedNote(pinnedNote)
            _unPinnedState.emit(Resource.Success(Unit))
        } catch (e: Throwable) {
            e.printStackTrace()
            _unPinnedState.emit(Resource.Error(e.message ?: "Could not unpin"))
        }
    }

}