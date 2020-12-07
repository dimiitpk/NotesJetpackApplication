package com.dimi.advnotes.presentation.common.extensions

import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.fragment.FragmentNavigator

fun NavController.safeNavigate(
    destination: NavDirections,
    extras: FragmentNavigator.Extras? = null
) {
    currentDestination?.getAction(destination.actionId)
        ?.let {
            if (extras != null)
                navigate(destination, extras)
            else
                navigate(destination)
        }
}