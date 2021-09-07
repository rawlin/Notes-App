package com.rawlin.notesapp.ui.notes_lists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rawlin.notesapp.database.NotesDatabase
import com.rawlin.notesapp.domain.DispatcherProvider
import com.rawlin.notesapp.domain.Note
import com.rawlin.notesapp.domain.StandardDispatchers
import com.rawlin.notesapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesListsViewModel @Inject constructor(
    private val database: NotesDatabase,
    private val dispatchers: DispatcherProvider
) : ViewModel() {

    private val _allNotes: MutableStateFlow<Resource<List<Note>>> = MutableStateFlow(
        Resource.Success(
            emptyList()
        )
    )
    val allNotes: StateFlow<Resource<List<Note>>> = _allNotes

    init {
        getAllNotes()
    }

    private fun getAllNotes() {
        viewModelScope.launch {
            database.notesDao().getAllNotes()
                .catch {
                    _allNotes.emit(Resource.Error("Failed to fetch notes"))
                }.collect {
                    _allNotes.emit(Resource.Success(it))
                }
        }

    }
}