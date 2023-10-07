package com.badl.apps.android.appFeatures.userAccount.data

data class SwapOrderItem(
    val description: String?,
    val id: Int?,
    val image: String?,
    val title: String?,
    var is_favorite: Boolean? = false,
)