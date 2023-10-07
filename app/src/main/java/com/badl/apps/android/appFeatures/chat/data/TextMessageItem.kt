package com.badl.apps.android.appFeatures.chat.data
data class TextMessageItem(
                           val text: String = "",
                           val senderID: Int = 0,
                           val type: String = "",
                           val sendTime: String ="",
                           val messagePosition: Int = -1,
                           val messageRead: Boolean = false)
