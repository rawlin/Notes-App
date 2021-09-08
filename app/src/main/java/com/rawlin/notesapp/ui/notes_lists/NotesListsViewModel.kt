package com.rawlin.notesapp.ui.notes_lists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rawlin.notesapp.database.NotesDatabase
import com.rawlin.notesapp.database.PinnedNote
import com.rawlin.notesapp.domain.DispatcherProvider
import com.rawlin.notesapp.domain.Note
import com.rawlin.notesapp.domain.StandardDispatchers
import com.rawlin.notesapp.repository.RepositoryImpl
import com.rawlin.notesapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesListsViewModel @Inject constructor(
    private val repository: RepositoryImpl,
    private val dispatchers: DispatcherProvider
) : ViewModel() {

    private val _allNotes: MutableStateFlow<Resource<List<Note>>> = MutableStateFlow(
        Resource.Success(
            emptyList()
        )
    )
    val allNotes: StateFlow<Resource<List<Note>>>
        get() = _allNotes

    private val _allPinnedNotes: MutableStateFlow<Resource<List<PinnedNote>>> = MutableStateFlow(
        Resource.Success(
            emptyList()
        )
    )
    val allPinnedNotes: StateFlow<Resource<List<PinnedNote>>>
        get() = _allPinnedNotes

    init {
        getAllNotes()
    }

    private fun getAllNotes() {
        viewModelScope.launch {

            launch {
                repository.getAllNotes()
                    .catch {
                        _allNotes.emit(Resource.Error("Failed to fetch notes"))
                    }.collect {
                        _allNotes.emit(Resource.Success(it))
                    }
            }

            launch {
                repository.getAllPinnedNotes()
                    .catch {
                        _allPinnedNotes.emit(Resource.Error("Failed to fetch pinned notes"))
                    }
                    .collect {
                        _allPinnedNotes.emit(Resource.Success(it))
                    }
            }

        }

    }


}