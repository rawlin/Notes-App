package com.rawlin.notesapp.ui.notes_details

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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


    fun createNote(title: String, message: String) = viewModelScope.launch {
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
                createdTime = System.currentTimeMillis()
            )
            repository.addNote(note)
            _createNoteState.emit(Resource.Success(Unit))
        } catch (e: Throwable) {
            _createNoteState.emit(Resource.Error("Error in creating note"))
            Log.e(TAG, "Failed to create note: $e")
        }
    }

    fun updateNote(title: String, message: String, id: Int) = viewModelScope.launch {
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
                message = message
            )
            repository.updateNote(note)
            _updateNoteState.emit(Resource.Success(Unit))
        } catch (e: Throwable) {
            _updateNoteState.emit(Resource.Error("Error updating note"))
            Log.e(TAG, "Failed to update note: $e")
        }
    }

    fun updatePinnedNote(title: String, message: String, id: Int) = viewModelScope.launch {
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
                message = message
            )
            repository.updateNote(note)
            _updateNoteState.emit(Resource.Success(Unit))
        } catch (e: Throwable) {
            _updateNoteState.emit(Resource.Error("Error updating note"))
            Log.e(TAG, "Failed to update note: $e")
        }
    }

    fun deleteNote() {
        //TODO
    }

    fun pinNote() {
        //TODO
    }

}