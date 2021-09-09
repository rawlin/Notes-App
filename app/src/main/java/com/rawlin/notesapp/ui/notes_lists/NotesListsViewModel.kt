package com.rawlin.notesapp.ui.notes_lists

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rawlin.notesapp.database.PinnedNote
import com.rawlin.notesapp.domain.Note
import com.rawlin.notesapp.repository.RepositoryImpl
import com.rawlin.notesapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "NotesListsViewModel"

@HiltViewModel
class NotesListsViewModel @Inject constructor(
    private val repository: RepositoryImpl,
) : ViewModel() {

    private var showNewBottom = true
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

    private val _pinMode = MutableStateFlow(false)
    val pinMode: StateFlow<Boolean>
        get() = _pinMode

    init {
        getPrefs()
        getAllNotes()
    }

    private fun getPrefs() {
        viewModelScope.launch {

            launch {
                repository.pinMode.collect {
                    _pinMode.emit(it)
                    Log.d(TAG, "PinMode:$it ")
                }
            }

            launch {
                repository.showNewBottom.collect {
                    showNewBottom = it
                }
            }

        }
    }

    fun getAllNotes() {
        viewModelScope.launch {

            launch {
                repository.getAllNotes(showNewBottom)
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