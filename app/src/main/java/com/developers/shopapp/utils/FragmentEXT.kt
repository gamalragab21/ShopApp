package com.developers.shopapp.utils

import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar


fun Fragment.snackbar(message: String) {
    Snackbar.make(
        requireView(),
        message,
        Snackbar.LENGTH_LONG
    ).show()

}





