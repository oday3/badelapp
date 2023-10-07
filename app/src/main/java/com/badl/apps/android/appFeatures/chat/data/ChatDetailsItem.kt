package com.badl.apps.android.appFeatures.chat.data

import com.badl.apps.android.appFeatures.appCommon.data.FilterItem

data class ChatDetailsItem(
    val ads: List<FilterItem>,
    val chat: ChatHistoryItem
)