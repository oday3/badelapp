package com.badl.apps.android.appFeatures.biddingAndBarterSystem.data

data class AdOrderItem(
    val ad_type: Int?,
    val ad_type_text: String?,
    val id: Int?,
    val owner_id: Int?,
    val owner_name: String?,
    val price: Int?,
    val quantity: String?,
    val order_date: String?,
    val user_name: String?,
    val user_id: Int?,
    val ad_id: Int?,
    val user_image: String?,
    val has_chat: Boolean?,
    val firebase_key: String?,
)