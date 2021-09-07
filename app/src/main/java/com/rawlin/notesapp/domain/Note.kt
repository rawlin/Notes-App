package com.rawlin.notesapp.domain

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "note")
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val title: String,
    val message: String,
    val createdTime: Long,
    val imageUri: String? = null,
    val lastOpened: Long? = null
)
