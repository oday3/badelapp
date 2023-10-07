package com.badl.apps.android.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity

object  HelperUtils {

    fun AppCompatActivity.dialPhone(mobileNumber: String) {

        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:${mobileNumber}")
        startActivity(intent)
    }


    fun Intent.dialPhone(mobileNumber: String, context: Context) {

        action = Intent.ACTION_DIAL
        data = Uri.parse("tel:${mobileNumber}")
        context.startActivity(this)
    }
}