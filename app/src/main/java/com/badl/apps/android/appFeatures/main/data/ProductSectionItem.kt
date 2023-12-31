package com.badl.apps.android.appFeatures.main.data

data class ProductSectionItem(
    val ad_type_id: Int?,
    val ad_type_text: String?,
    val auto_repost_id: Int?,
    val auto_repost_text: String?,
    val bids_count: Int?,
    val description: String?,
    val end_date: String?,
    val highest_bid: Int?,
    val id: Int?,
    val image: String?,
    var is_favorite: Boolean? = false,
    var is_loading: Boolean? = false,
    val is_featured: String?,
    val price: Double?,
    val quantity: Int?,
    val product_status_id: Int?,
    val rate: Double?,
    val rate_count: Int?,
    val remaining_days: String?,
    val status: Int?,
    val title: String?,
    val user_id: String?,
    val count_text: String?,
    val status_text: String?,
    val quantity_note: String?,
    val views: Int?
)