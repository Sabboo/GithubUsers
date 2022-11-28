package com.saber.githubusers.ui

import android.view.View
import com.google.android.material.snackbar.Snackbar

fun View.showSnackBarWithAction(
    description: String,
    actionTitle: String,
    action: () -> Any
) {
    Snackbar.make(this, description, Snackbar.LENGTH_LONG)
        .setAction(actionTitle) { action.invoke() }
        .show()
}