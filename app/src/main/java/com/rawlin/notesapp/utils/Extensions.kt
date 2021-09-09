package com.rawlin.notesapp.utils

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.navigation.*
import androidx.navigation.fragment.NavHostFragment.findNavController

fun String.isValidInput(): Boolean {
    return !(this.isEmpty() || this.isBlank())
}

fun NavController.navigateSafely(destination: NavDirections) {
    currentDestination?.getAction(destination.actionId)
        ?.let { navigate(destination) }
}