package com.badl.apps.android.appFeatures.biddingAndBarterSystem.data

data class BiddingHistoryItem(
    val bidders_count: Int?,
    val bids: List<BidItem>?,
    val bids_count: Int?,
    val highest_bid: Int?,
    val ad_status: Int?,
    val ad_title: String?,
)