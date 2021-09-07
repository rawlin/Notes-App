package com.rawlin.notesapp.utils

fun String.isValidInput(): Boolean {
    return !(this.isEmpty() || this.isBlank())
}