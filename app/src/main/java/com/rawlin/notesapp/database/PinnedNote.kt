package com.rawlin.notesapp.database

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.rawlin.notesapp.domain.Note
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(
    tableName = "pinned_notes",
    foreignKeys = [ForeignKey(
        entity = Note::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("id"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class PinnedNote(
    @PrimaryKey
    val id: Int? = null,
    val title: String,
    val message: String,
    val createdTime: Long,
    val imageUri: String? = null,
    val lastOpened: Long? = null
): Parcelable
