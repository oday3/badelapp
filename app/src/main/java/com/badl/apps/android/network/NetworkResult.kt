package com.badl.apps.android.network
import okhttp3.ResponseBody

data class NetworkResult<T>(
    val resultStatus: Status,
    val resultData: BaseResponse<T>?,
    val errorBody: ResponseBody?,
    val message: String?
) {

    enum class Status {
        SUCCESS,
        ERROR,
        LOADING,

    }

    companion object {
        fun <T> success(data: BaseResponse<T>): NetworkResult<T> {
            return NetworkResult(Status.SUCCESS, data, null, null)
        }

        fun <T> error(
            errorBody: ResponseBody?,
            message: String?,
            data: BaseResponse<T>? = null
        ): NetworkResult<T> {
            return NetworkResult(Status.ERROR, data, errorBody, message)
        }


        fun <T> loading(data: BaseResponse<T>? = null): NetworkResult<T> {
            return NetworkResult(Status.LOADING, data, null, null)
        }

//        fun <T> init(data: BaseResponse<T>? = null): NetworkResult<T> {
//            return NetworkResult(Status.INIT, data, null, null)
//        }
    }
}
