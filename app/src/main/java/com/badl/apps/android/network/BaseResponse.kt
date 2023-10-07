package com.badl.apps.android.network
import com.badl.apps.android.utils.NetworkUtils
import com.squareup.moshi.Json


data class BaseResponse<T>(
    @Json(name ="code")
    val code: Int,
    @Json(name = "status")
    val status: Boolean,
    @Json(name ="message")
    val message: String,
    @Json(name = "data")
    val data: T?,
    @Json(name ="pages")
    val pages: BaseResponsePagesItem?,
    @Json(name ="errors")
    val errors: List<BaseResponseErrorItem>,
) {


    fun <T> toObjectByGetClass(model: Class<T>): T? {

        return NetworkUtils.toObjectByGetClass(model, data)
        //return moshi.adapter(model).fromJsonValue(data)
    }


    fun <T> toListByGetClass(model: Class<T>): List<T> {

        return NetworkUtils.toListByGetClass(model, data)

    }
}
