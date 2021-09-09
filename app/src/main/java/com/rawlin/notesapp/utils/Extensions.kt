package com.rawlin.notesapp.utils

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.navigation.*
import androidx.navigation.fragment.NavHostFragment.findNavController

fun String.isValidInput(): Boolean {
    return !(this.isEmpty() || this.isBlank())
}

//fun NavController.navigateSafely(
//    @IdRes resId: Int,
//    args: Bundle? = null,
//    navOptions: NavOptions? = null,
//    navExtras: Navigator.Extras? = null
//) {
//    val action = currentDestination?.getAction(resId) ?: graph.getAction(resId)
//    if(action != null && currentDestination?.id != action.destinationId) {
//        navigate(resId, args, navOptions, navExtras)
//    }
//}
fun NavController.navigateSafely(destination: NavDirections) {
    currentDestination?.getAction(destination.actionId)
        ?.let { navigate(destination) }
}