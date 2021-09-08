package com.rawlin.notesapp.domain

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "note")
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val title: String,
    val message: String,
    val createdTime: Long? = null,
    val imageUri: String? = null,
    val lastOpened: Long? = null
): Parcelable
