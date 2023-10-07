package com.badl.apps.android.appFeatures.userAccount.data

import com.badl.apps.android.appFeatures.biddingAndBarterSystem.data.AdOrderItem

data class AdOrdersDataItem(
    val ad_description: String?,
    val orders: List<AdOrderItem>?,
    val orders_count: String?
)