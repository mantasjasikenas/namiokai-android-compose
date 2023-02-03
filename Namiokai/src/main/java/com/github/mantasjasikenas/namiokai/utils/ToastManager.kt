package com.github.mantasjasikenas.namiokai.utils

import android.content.Context
import android.widget.Toast
import javax.inject.Inject


class ToastManager @Inject constructor(private val context: Context) {

    fun show(message: String, toastLength: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(context, message, toastLength).show()
    }
}
