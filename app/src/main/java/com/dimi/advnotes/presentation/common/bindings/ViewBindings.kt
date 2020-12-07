package com.dimi.advnotes.presentation.common.bindings

import android.graphics.Paint
import android.view.View
import android.view.View.*
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.dimi.advnotes.R
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable

/**
 * Simplification to check and setup view as visible.
 */
@set:BindingAdapter("visible")
var View.visible
    get() = visibility == VISIBLE
    set(value) {
        visibility = if (value) VISIBLE else GONE
    }

/**
 * Simplification to check and setup view as gone.
 */
@set:BindingAdapter("gone")
var View.gone
    get() = visibility == GONE
    set(value) {
        visibility = if (value) GONE else VISIBLE
    }

/**
 * Simplification to check and setup view as invisible.
 */
@set:BindingAdapter("invisible")
var View.invisible
    get() = visibility == INVISIBLE
    set(value) {
        visibility = if (value) INVISIBLE else VISIBLE
    }

@BindingAdapter("strikeThrough")
fun TextView.strikeThrough(strikeThrough: Boolean) {
    paintFlags = if (strikeThrough) {
        paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
    } else {
        paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
    }
}

@BindingAdapter("roundedCorners")
fun MaterialToolbar.roundedCorners(roundedCorners: Boolean) {
    if (roundedCorners) {
        val radius = resources.getDimension(R.dimen.toolbar_corner_radius)
        val background = this.background as MaterialShapeDrawable
        background.fillColor = null
        background.strokeColor = resources.getColorStateList(R.color.colorStrokeDefault, null)
        background.strokeWidth = resources.getDimension(R.dimen.default_stroke)
        background.shapeAppearanceModel =
            background.shapeAppearanceModel
                .toBuilder()
                .setAllCorners(CornerFamily.ROUNDED, radius)
                .build()
    }
}

@BindingAdapter("pickBackgroundColor")
fun View.colorFromArray(index: Int) {
    val listOfAvailableColors = resources.getIntArray(R.array.note_background_colors)
    val color = listOfAvailableColors[index]

    setBackgroundColor(color)
}
