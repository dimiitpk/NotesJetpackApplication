package com.dimi.advnotes.presentation.common

import android.content.Context
import android.util.AttributeSet
import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.MaterialAutoCompleteTextView

class ExposedDropDownTextView(context: Context, attributeSet: AttributeSet) :
    MaterialAutoCompleteTextView(context, attributeSet) {
    override fun getFreezesText(): Boolean {
        return false
    }
}

@BindingAdapter("textWithoutFilter")
fun ExposedDropDownTextView.textWithoutFilter(string: String?) {
    setText(string, false)
}