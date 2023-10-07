package com.badl.apps.android.application

import android.app.Application
import android.util.Log
import com.badl.apps.android.utils.Constants
import com.badl.apps.android.utils.NetworkUtils
import com.badl.apps.android.utils.SharedPrefUtils
import com.google.firebase.messaging.FirebaseMessaging
//import com.google.firebase.messaging.FirebaseMessaging
import com.squareup.moshi.Moshi
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class BadelApp: Application() {

    @Inject
    lateinit var moshi: Moshi
    @Inject
    lateinit var sharedPrefUtils: SharedPrefUtils

    var navToBidding = false
    var refreshMain = false
    var refreshOrders = false

    override fun onCreate() {
        super.onCreate()
        NetworkUtils.initMoshi(moshi)

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {

                sharedPrefUtils.fcmDeviceToken = (Constants.DEVICE_FCM_TOKEN_FAIL)

                Log.e(
                    "fcm_token_from_app_er",
                    "Fetching FCM registration token failed",
                    task.exception
                )

            } else {
                // Get new FCM registration token
                val token = task.result
                Log.e("fcm_token_from_app_su", token)
                sharedPrefUtils.fcmDeviceToken = (token)
            }
        }
    }
}