package com.rawlin.notesapp.utils

import com.evernote.client.android.EvernoteSession

object Constants {
    const val DATABASE_NAME = "notes_database"
    const val PREFERENCE_NAME = "settings"

    const val CONSUMER_KEY = "rawlin"
    const val CONSUMER_SECRET = "5a5605cd814041da"
    val EVERNOTE_SERVICE = EvernoteSession.EvernoteService.SANDBOX
}