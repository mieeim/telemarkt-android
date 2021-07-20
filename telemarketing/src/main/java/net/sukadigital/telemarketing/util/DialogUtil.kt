package net.sukadigital.telemarketing.util

import android.content.Context
import android.view.View
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar

fun snackbarColor(msg: String, parent_view: View, context: Context, color: Int) {
    val snackbar = Snackbar.make(parent_view, msg, Snackbar.LENGTH_SHORT)
    snackbar.view.setBackgroundColor(
        ContextCompat.getColor(
            context,
            color
        )
    )
    snackbar.show()
}