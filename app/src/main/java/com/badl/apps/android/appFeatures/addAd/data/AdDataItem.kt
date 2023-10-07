package com.badl.apps.android.appFeatures.addAd.data

import com.badl.apps.android.appFeatures.appCommon.data.DropdownItem
import com.badl.apps.android.appFeatures.main.data.SectionItem

data class AdDataItem(
    val ad_types: List<DropdownItem>?,
    val auto_repost: List<DropdownItem>?,
    val product_statuses: List<DropdownItem>?,
    val sections: List<SectionItem>?
)