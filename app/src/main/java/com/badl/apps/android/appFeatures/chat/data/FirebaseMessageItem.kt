package com.badl.apps.android.appFeatures.chat.data

data class FirebaseMessageItem(
    var id: String = "",
    var text: String = "",
    var lat: String = "",
    var lng: String = "",
    val senderID: Int = 0,
    val type: String = "",
    val messagePosition: Int = 0,
    val sendTime: String ="",
    var messageRead: Boolean = false,


)