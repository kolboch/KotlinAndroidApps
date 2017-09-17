package com.example.kb.tictactoe

import android.support.annotation.ColorRes
import android.support.annotation.StringRes
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View

/**
 * Created by Karlo on 2017-09-17.
 */

inline fun View.snack(@StringRes message: Int, length: Int = Snackbar.LENGTH_LONG, f: Snackbar.() -> Unit) {
    val snack = Snackbar.make(this, message, length)
    snack.f()
    snack.show()
}

fun Snackbar.action(@StringRes action: Int, @ColorRes color: Int? = null, listener: (View) -> Unit) {
    setAction(action, listener)
    if (color == null) {
        Log.v("SNACKBAR", "NULL")
    } else {
        Log.v("SNACKBAR", "NOT NULL")
    }
    color?.let { setActionTextColor(ContextCompat.getColor(context, color)) }
}