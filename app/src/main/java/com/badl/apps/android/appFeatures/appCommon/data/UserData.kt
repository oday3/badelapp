package com.badl.apps.android.appFeatures.appCommon.data

import com.badl.apps.android.utils.Constants

data class UserData(
    val add_time: String?,
    val created_at: String?,
    val deleted_at: String?,
    val email: String?,
    val id: Int?,
    val image: String?,
    val lang: String?,
    val member_since: String?,
    val mobile: String?,
    val name: String?,
    val rate: Double?,
    val rates_count: Int?,
    val social_provider: String?,
    val social_token: String?,
    val status: String?,
    val token: String?,
    val type: String?,
    val updated_at: String?)
{

    fun getBearerToken(): String {
        return Constants.BEARER + token?.replace(Constants.BEARER, "")

    }
}