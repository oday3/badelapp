package com.badl.apps.android.appFeatures.chat.data

data class ChatHistoryItem(
    val ad_id: Int?,
    val firebase_key: String?,
    val id: Int?,
    val last_message: String?,
    val last_message_time: String?,
    val owner_id: Int?,
    val owner_image: String?,
    val owner_name: String?,
    val owner_unread_messages: Int?,
    val user_id: Int?,
    val user_image: String?,
    val user_name: String?,
    val user_unread_messages: Int?
)