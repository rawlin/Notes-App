package com.rawlin.notesapp.domain

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.DocumentId
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "note")
data class Note(
    @PrimaryKey
    @DocumentId
    val id: String,
    val title: String,
    val message: String,
    val createdTime: Long? = null,
    val imageUri: String? = null,
): Parcelable
