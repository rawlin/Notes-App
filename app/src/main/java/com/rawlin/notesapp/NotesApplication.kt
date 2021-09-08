package com.rawlin.notesapp

import android.app.Application
import com.evernote.client.android.EvernoteSession
import com.rawlin.notesapp.utils.Constants.CONSUMER_KEY
import com.rawlin.notesapp.utils.Constants.CONSUMER_SECRET
import com.rawlin.notesapp.utils.Constants.EVERNOTE_SERVICE
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class NotesApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        val evernoteSession = EvernoteSession.Builder(this)
            .setEvernoteService(EVERNOTE_SERVICE)
            .build(CONSUMER_KEY, CONSUMER_SECRET)
            .asSingleton()
    }
}