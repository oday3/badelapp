package com.badl.apps.android.appFeatures.userAccount.data

data class UserAdItem(
    val ad_type_id: Int?,
    val ad_type_text: String?,
    val auto_repost_id: Int?,
    val auto_repost_text: String?,
    val description: String?,
    val end_date: String?,
    val id: Int?,
    val image: String?,
    val is_favorite: Boolean?,
    val is_featured: String?,
    val price: Int?,
    val product_status_id: Int?,
    val rate: Double?,
    val rate_count: Int?,
    val remaining_days: String?,
    val status: Int?,
    val title: String?,
    val user_id: String?,
    val views: Int?,
    val highest_bid: Int?,
    val bids_count: Int?,
    val count_text: String,
    val status_text: String?,
    var isSelected: Boolean = false

)