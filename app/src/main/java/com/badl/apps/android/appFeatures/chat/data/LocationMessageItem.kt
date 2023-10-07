package com.badl.apps.android.appFeatures.chat.data
data class LocationMessageItem(
                           val text: String = "",
                           val senderID: Int = 0,
                           val type: String = "",
                           val sendTime: String ="",
                           val lat: String ="",
                           val lng: String ="",
                           val messagePosition: Int = -1,
                           val messageRead: Boolean = false)
