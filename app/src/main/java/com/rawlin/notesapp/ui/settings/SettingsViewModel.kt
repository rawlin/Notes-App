package com.rawlin.notesapp.ui.settings

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.viewModelScope
import com.rawlin.notesapp.preferences.DataStoreManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "SettingsViewModel"

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    private val _pinMode = MutableStateFlow(false)
    val pinMode: StateFlow<Boolean>
        get() = _pinMode
    private val _showNewBottom = MutableStateFlow(false)
    val showNewBottom: StateFlow<Boolean>
        get() = _showNewBottom
    private val _sharingMode = MutableStateFlow(false)
    val sharingMode: StateFlow<Boolean>
        get() = _sharingMode
    private val _sortByCreatedTime = MutableStateFlow(false)
    val sortByCreatedTime: StateFlow<Boolean>
        get() = _sortByCreatedTime



    init {
        viewModelScope.launch {


            dataStoreManager.apply {

                launch {
                    pinMode.collect {
                        _pinMode.emit(it)
                        Log.d(TAG, "PinMode:$it ")
                    }
                }

                launch {
                    sharing.collect {
                        _sharingMode.emit(it)
                        Log.d(TAG, "Sharing Mode: $it")
                    }
                }

                launch {
                    showBottom.collect {
                        _showNewBottom.emit(it)
                        Log.d(TAG, "Show New at Bottom: $it")
                    }
                }

                launch {
                    sortTimeCreated.collect {
                        _sortByCreatedTime.emit(it)
                        Log.d(TAG, "Sort by Time Created: $it")
                    }
                }
            }

        }
    }

    fun setPinMode(mode: Boolean) = viewModelScope.launch {
        dataStoreManager.setPinMode(mode)
    }
    fun setShowNewBottom(mode: Boolean) = viewModelScope.launch {
        dataStoreManager.setShowBottom(mode)
    }
    fun setSharingMode(mode: Boolean) = viewModelScope.launch {
        dataStoreManager.setSharing(mode)
    }
    fun setSortByCreatedTime(mode: Boolean) = viewModelScope.launch {
        dataStoreManager.setSortByTimeCreated(mode)
    }

}