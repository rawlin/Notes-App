package com.rawlin.notesapp

import android.app.Application
import com.rawlin.notesapp.repository.RepositoryImpl
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class NotesApplication: Application() {

    @Inject
    lateinit var repo: RepositoryImpl
    override fun onCreate() {
        super.onCreate()
        GlobalScope.launch {
            try {
                repo.triggerCalls()
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }
}