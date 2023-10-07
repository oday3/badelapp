package com.badl.apps.android.appFeatures.appCommon.data

data class NotificationItem(
    val id: Int?,
    val image: String?,
    val is_seen: Int?,
    val message: String?,
    val notification_id: String?,
    val reference_id: String?,
    val send_date: String?,
    val title: String?,
    val type: String?,
    val user_id: String?,
    val firebase_key: String?
)