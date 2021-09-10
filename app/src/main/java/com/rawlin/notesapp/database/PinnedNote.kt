package com.rawlin.notesapp.database

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.google.firebase.firestore.DocumentId
import com.rawlin.notesapp.domain.Note
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(
    tableName = "pinned_notes"
)
data class PinnedNote(
    @DocumentId
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val title: String,
    val message: String,
    val createdTime: Long,
    val imageUri: String? = null,
) : Parcelable
