package com.rawlin.notesapp.utils

import androidx.navigation.NavController
import androidx.navigation.NavDirections

fun String.isValidInput(): Boolean {
    return !(this.isEmpty() || this.isBlank())
}

fun NavController.navigateSafely(destination: NavDirections) {
    currentDestination?.getAction(destination.actionId)
        ?.let { navigate(destination) }
}