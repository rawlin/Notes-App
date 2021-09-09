package com.rawlin.notesapp.ui.notes_details

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

    private val _pinnedState: MutableStateFlow<Resource<Unit>> = MutableStateFlow(Resource.Loading())
    val pinnedState: StateFlow<Resource<Unit>>
        get() = _pinnedState

    private val _deleteNoteStatus = MutableSharedFlow<Resource<Unit>>()
    val deleteNoteStatus: SharedFlow<Resource<Unit>>
        get() = _deleteNoteStatus


    fun createNote(title: String, message: String, imageUri: String?) = viewModelScope.launch {
        if (!title.isValidInput() || !message.isValidInput()) {
            Log.d(TAG, "createNote: $title $message")
            _createNoteState.emit(Resource.Error("Please enter Title and message to create note"))
            return@launch
        }
        Log.d(TAG, "createNote: $title $message")

        _createNoteState.emit(Resource.Loading())
        try {
            val note = Note(
                title = title,
                message = message,
                createdTime = System.currentTimeMillis(),
                imageUri = imageUri
            )
            repository.addNote(note)
            _createNoteState.emit(Resource.Success(Unit))
        } catch (e: Throwable) {
            _createNoteState.emit(Resource.Error("Error in creating note"))
            Log.e(TAG, "Failed to create note: $e")
        }
    }

    fun updateNote(title: String, message: String, id: Int, imageUri: String?) = viewModelScope.launch {
        if (!title.isValidInput() || !message.isValidInput()) {
            Log.d(TAG, "createNote: $title $message")
            _createNoteState.emit(Resource.Error("Please enter Title and message to create note"))
            return@launch
        }
        _updateNoteState.emit(Resource.Loading())
        try {
            val note = Note(
                id = id,
                title = title,
                message = message,
                imageUri = imageUri
            )
            repository.updateNote(note)
            _updateNoteState.emit(Resource.Success(Unit))
        } catch (e: Throwable) {
            _updateNoteState.emit(Resource.Error("Error updating note"))
            Log.e(TAG, "Failed to update note: $e")
        }
    }

    fun updatePinnedNote(title: String, message: String, id: Int, imageUri: String?) = viewModelScope.launch {
        if (!title.isValidInput() || !message.isValidInput()) {
            Log.d(TAG, "createNote: $title $message")
            _createNoteState.emit(Resource.Error("Please enter Title and message to create note"))
            return@launch
        }
        _updateNoteState.emit(Resource.Loading())
        try {
            val note = Note(
                id = id,
                title = title,
                message = message,
                imageUri = imageUri
            )
            repository.updateNote(note)
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
                createdTime = note.createdTime ?: System.currentTimeMillis()
            )
            repository.addPinnedNote(pinnedNote)
            repository.deleteNote(note)
            _pinnedState.emit(Resource.Success(Unit))
        } catch (e: Throwable) {
            e.printStackTrace()
            _pinnedState.emit(Resource.Error(e.message ?: "Could note pin note"))
        }
    }

}