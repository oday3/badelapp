package com.badl.apps.android.appFeatures.chat.data

data class ChatsHistoryData(
    val chat_ad_types: List<FilterChatItem>?,
    val chats: List<ChatHistoryItem>?,
    val chats_count: Int?
)