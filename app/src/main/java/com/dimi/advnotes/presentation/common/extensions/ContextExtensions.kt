package com.dimi.advnotes.presentation.common.extensions

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.content.res.use
import com.dimi.advnotes.R
import com.google.android.material.appbar.MaterialToolbar

@ColorInt
@SuppressLint("Recycle")
fun Context.themeColor(
    @AttrRes themeAttrId: Int
): Int {
    return obtainStyledAttributes(
        intArrayOf(themeAttrId)
    ).use {
        it.getColor(0, Color.MAGENTA)
    }
}

fun Context.themeDrawable(
    @DrawableRes resId: Int
): Drawable? {
    return ResourcesCompat.getDrawable(
        resources,
        resId,
        theme
    )
}

fun MaterialToolbar.setIconByCondition(
    @IdRes menuItemId: Int,
    condition: Boolean,
    @DrawableRes iconTrue: Int,
    @DrawableRes iconFalse: Int
) {
    menu.findItem(menuItemId)?.let { menuItem ->
        menuItem.icon = if (condition)
            context.themeDrawable(iconTrue)
        else
            context.themeDrawable(iconFalse)
    }
}

fun Menu?.setIconByCondition(
    context: Context,
    @IdRes menuItemId: Int,
    condition: Boolean,
    @DrawableRes iconTrue: Int,
    @DrawableRes iconFalse: Int,
) {
    this?.findItem(menuItemId)?.let { menuItem ->
        menuItem.icon = if (condition)
            ContextCompat.getDrawable(context, iconTrue)
        else
            ContextCompat.getDrawable(context, iconFalse)
    }
}