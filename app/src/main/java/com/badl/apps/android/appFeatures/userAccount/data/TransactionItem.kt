package com.badl.apps.android.appFeatures.userAccount.data

import com.badl.apps.android.appFeatures.biddingAndBarterSystem.data.ExchangeAd

data class TransactionItem(
    val ad_id: Int?,
    val ad_type: Int?,
    val ad_type_text: String?,
    val exchange_ad: ExchangeAd?,
    val firebase_key: String?,
    val has_chat: Boolean?,
    val id: Int?,
    val image: String?,
    val order_date: String?,
    val price: Int?,
    val quantity: String?,
    val title: String?,
    val user_id: Int?,
    val user_image: String?,
    val user_name: String?
)