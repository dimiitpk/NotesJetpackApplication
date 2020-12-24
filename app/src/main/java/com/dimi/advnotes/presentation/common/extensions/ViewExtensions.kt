package com.dimi.advnotes.presentation.common.extensions

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.View
import com.dimi.advnotes.R

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.fadeIn(listener: Animator.AnimatorListener? = null) {
    val animationDuration = resources.getInteger(R.integer.note_motion_duration_small)
    apply {
        visible()
        alpha = 0f
        animate()
            .alpha(1f)
            .setDuration(animationDuration.toLong())
            .setListener(listener)
    }
}

fun View.fadeOut(listener: Animator.AnimatorListener? = null) {
    val animationDuration = resources.getInteger(R.integer.note_motion_duration_small)
    apply {
        animate()
            .alpha(0f)
            .setDuration(animationDuration.toLong())
            .setListener(
                listener ?: object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        invisible()
                    }
                })
    }
}