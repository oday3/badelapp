package com.badl.apps.android.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.badl.apps.android.appFeatures.appCommon.data.UserData

class SharedPrefUtils(context: Context) {

    private var preferencesInstance: SharedPreferences = context.getSharedPreferences(
        Constants.APP_SHARED_PREF_FILE_NAME,
        Context.MODE_PRIVATE
    )

    private var editor: SharedPreferences.Editor = preferencesInstance.edit()

    init {
        editor.apply()
        Log.e("ahjsdbao", "SharedPrefUtils created")

    }

    var showIntroFlag: Boolean
        get() = preferencesInstance.getBoolean(Constants.APP_INTRO_FLAG, true)
        set(flag) {
            editor.putBoolean(Constants.APP_INTRO_FLAG, flag)
            editor.apply()
        }

    var currentUserCartId: Int
        get() = preferencesInstance.getInt(Constants.CURRENT_USER_CART_ID, 0)
        set(flag) {
            editor.putInt(Constants.CURRENT_USER_CART_ID, flag)
            editor.apply()
        }

    var currentUserCartCount: Int
        get() = preferencesInstance.getInt(Constants.CURRENT_USER_CART_COUNT, 0)
        set(flag) {
            editor.putInt(Constants.CURRENT_USER_CART_COUNT, flag)
            editor.apply()
        }

    var lastUserLocation: String?
        get() = preferencesInstance.getString(Constants.USER_LOCATION, null)
        set(location) {
            editor.putString(Constants.USER_LOCATION, location)
            editor.apply()
        }

//    var userLocation: String?
//        get() = preferencesInstance.getString(Constants.USER_LOCATION, null)
//        set(location) {
//            editor.putString(Constants.USER_LOCATION, location)
//            editor.apply()
//        }

    var isNotificationEnabled: Boolean
        get() = preferencesInstance.getBoolean(Constants.APP_NOTIFICATIONS_FLAG, true)
        set(flag) {
            editor.putBoolean(Constants.APP_NOTIFICATIONS_FLAG, flag)
            editor.apply()
        }

        var notificationCount: Int
        get() = preferencesInstance.getInt(Constants.APP_NOTIFICATIONS_COUNT, 0)
        set(flag) {
            editor.putInt(Constants.APP_NOTIFICATIONS_COUNT, flag)
            editor.apply()
        }


//    var popUpAdFlag: Boolean
//        get() = preferencesInstance.getBoolean(Constants.MAIN_POPUP_AD_FLAG, true)
//        set(flag) {
//            editor.putBoolean(Constants.MAIN_POPUP_AD_FLAG, flag)
//            editor.apply()
//        }


    var fcmDeviceToken: String?
        get() = preferencesInstance.getString(Constants.DEVICE_FCM_TOKEN, null)
        set(token) {
            editor.putString(Constants.DEVICE_FCM_TOKEN, token)
            editor.apply()
        }

    var appLang: String?
        get() = preferencesInstance.getString(Constants.APP_LANG, "ar")
        set(lang) {
            editor.putString(Constants.APP_LANG, lang)
            editor.apply()
        }


    fun getCurrentUserData(): UserData? {

        val userData = preferencesInstance.getString(Constants.CURRENT_USER_DATA, null)

        return if (userData == null) {

            null

        } else {

            NetworkUtils.fromJsonToObject(UserData::class.java, userData)
        }
    }

    fun setCurrentUserData(userData: UserData?) {

        if (userData != null) {

            editor.putString(Constants.CURRENT_USER_DATA, NetworkUtils.fromObjectToJson(UserData::class.java, userData))

        } else {
            editor.putString(Constants.CURRENT_USER_DATA, null)
        }
        editor.apply()
    }
}