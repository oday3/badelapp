package com.badl.apps.android.appFeatures.biddingAndBarterSystem.data

data class SwapOrderDetailsItem(
    val ad: Ad?,
    val additional_price: String?,
    val exchange_ad: ExchangeAd?,
    val order_id: Int?,
    val has_chat: Boolean?,
    val firebase_key: String?,
    val user_image: String?,
    val user_name: String?,
    val user_id: Int?,
    val ad_id: Int?,
)