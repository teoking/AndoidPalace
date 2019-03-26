package com.teok.android.common

import android.content.Context
import android.os.Looper
import android.widget.Toast

fun showShortToast(context: Context, text: String) {
    if (Looper.myLooper() == Looper.getMainLooper()) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    } else {
        // TODO using a global UI handler
    }
}