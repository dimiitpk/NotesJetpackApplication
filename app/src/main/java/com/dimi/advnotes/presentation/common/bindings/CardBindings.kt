package com.dimi.advnotes.presentation.common.bindings

import androidx.annotation.Dimension
import androidx.databinding.BindingAdapter
import com.dimi.advnotes.R
import com.google.android.material.card.MaterialCardView


@BindingAdapter("strokeWidth")
fun MaterialCardView.setStrokeWidth(
    @Dimension width: Float
) {
    strokeWidth = width.toInt()
}

@BindingAdapter("pickCardBackgroundColor")
fun MaterialCardView.pickCardBackgroundColor(index: Int) {
    val listOfAvailableColors = resources.getIntArray(R.array.note_background_colors)
    val color = listOfAvailableColors[index]

    setCardBackgroundColor(color)
}


@BindingAdapter("state_checked")
fun MaterialCardView.setStateChecked(checked: Boolean) {
    isChecked = checked
}
