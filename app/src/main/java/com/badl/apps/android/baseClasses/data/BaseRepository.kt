package com.badl.apps.android.baseClasses.data

import android.util.Log
import com.badl.apps.android.network.AppEndpoints
import com.badl.apps.android.network.BaseResponse
import com.badl.apps.android.network.NetworkResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.ResponseBody
import retrofit2.Response


open class BaseRepository(val apiClient: AppEndpoints) {


    suspend fun <T> executeRequest(request: suspend () -> Response<BaseResponse<T>>): NetworkResult<T> {


        try {

            val response = request()

            Log.e("RESPONSE_LOG", response.body().toString())
            val body = response.body()
            if (response.isSuccessful) {

                return if (body != null) {

                    Log.e(
                        "RESPONSE_SUCCESS","done")
                    NetworkResult.success(body)

                } else {

                    error("Null Response")
                }

            } else {

                Log.e(
                    "RESPONSE_EER",
                    "== NetworkResult.Status.ERROR// code = ${response.code()} ====== message =${response.message()}" +
                            "====== errorBody = ${response.errorBody()} ======  response = ${response.toString()}"
                )

                return error(body?.message ?: "error while execute")
            }

        } catch (e: Exception) {

            Log.e(
                "RESPONSE_EER_EXCEPTION",
                " Error_BASE_REPO_EXCEPTION ===---------------------------" + e.message)

            print(e.stackTrace)
            return error(e.message ?: e.toString())
        }
    }

    private fun <T> error(message: String): NetworkResult<T> {
        return NetworkResult.error(null, "Network call has failed for a following reason: $message")
    }

    private fun <T> error(errorBody: ResponseBody?, code: Int): NetworkResult<T> {
        return NetworkResult.error(errorBody, "$code")
    }

    fun <T> requestResultAsFlow(
        request: suspend () -> NetworkResult<T>,
        logText: String = ""
    ): Flow<NetworkResult<T>> {

        return flow {

            emit(NetworkResult.loading())

            val requestResult = request()

            if (requestResult.resultStatus == NetworkResult.Status.SUCCESS && requestResult.resultData != null) {

                if (requestResult.resultData.status
                    && (requestResult.resultData.code == 200 || requestResult.resultData.code == 201)
                    && requestResult.resultData.errors.isEmpty()
                ) {

                    Log.e("SUCCESS_FLOW_DATA", requestResult.resultData.toString())
                    emit(NetworkResult.success(requestResult.resultData))

                } else {

                    Log.e(
                        "ERROR_FLOW_STATUS",
                        "first log $logText == NetworkResult.Status.ERROR ${requestResult.message}"
                    )
                    emit(
                        NetworkResult.error(
                            requestResult.errorBody,
                            requestResult.resultData.message,
                            requestResult.resultData
                        )
                    )
                }
            } else if (requestResult.resultStatus == NetworkResult.Status.ERROR) {

                Log.e(
                    "ERROR_FLOW_STATUS",
                    "second log $logText == NetworkResult.Status.ERROR ${requestResult.message}"
                )

                if (requestResult.resultData == null) {


                    Log.e(
                        "ERROR_FLOW_STATUS",
                        "third log $logText == NetworkResult.Status.ERROR ${requestResult.message}"
                    )

                    emit(
                        NetworkResult.error(
                            null,
                            requestResult.message ?: "Network Error"
                        )
                    )

                } else {

                    Log.e(
                        "ERROR_FLOW_STATUS",
                        "fourth log $logText == NetworkResult.Status.ERROR ${requestResult.message}"
                    )

                    emit(
                        NetworkResult.error(
                            requestResult.errorBody,
                            requestResult.resultData.message
                        )
                    )

                }

            }
        }.flowOn(Dispatchers.IO)

    }

//    fun <T> requestResult(
//        request: suspend () -> NetworkResult<T>,
//        logText: String
//    ): LiveData<NetworkResult<T>>{
//
//        return liveData(Dispatchers.IO) {
//
//            emit(NetworkResult.loading())
//
//            val requestResult = request()
//
//            if (requestResult.resultStatus == NetworkResult.Status.SUCCESS && requestResult.resultData != null) {
//
//                if (requestResult.resultData.status
//                    && (requestResult.resultData.code == 200 || requestResult.resultData.code == 201)
//                    && requestResult.resultData.errors.isEmpty()
//                ) {
//
//                    Log.e("SUCCESS_FLOW_DATA", "$logText == NetworkResult.Status.SUCCESS")
//                    emit(NetworkResult.success(requestResult.resultData))
//
//                } else {
//
//                    Log.e(
//                        "ERROR_FLOW_STATUS",
//                        "$logText == NetworkResult.Status.ERROR ${requestResult.message}"
//                    )
//                    emit(
//                        NetworkResult.error(
//                            requestResult.errorBody,
//                            requestResult.resultData.message,
//                            requestResult.resultData
//                        )
//                    )
//                }
//            } else if (requestResult.resultStatus == NetworkResult.Status.ERROR) {
//
//                Log.e(
//                    "ERROR_FLOW",
//                    "$logText == NetworkResult.Status.ERROR ${requestResult.message}"
//                )
//
//                if (requestResult.resultData == null) {
//
//                    // showMessage(requestResult.message)
//                    emit(
//                        NetworkResult.error(
//                            requestResult.errorBody,
//                            requestResult.message ?: "Network Error"
//                        )
//                    )
//
//                } else {
//                    emit(
//                        NetworkResult.error(
//                            requestResult.errorBody,
//                            requestResult.resultData.message
//                        )
//                    )
//
//                }
//
//            }
//        }
//
//    }
//
//
//    fun <T> requestResultAsLiveData(
//        request: suspend () -> NetworkResult<T>,
//        logText: String
//    ): LiveData<NetworkResult<T>> {
//
//        return liveData(Dispatchers.IO) {
//
//            emit(NetworkResult.loading())
//
//            val requestResult = request()
//
//            if (requestResult.resultStatus == NetworkResult.Status.SUCCESS && requestResult.resultData != null) {
//
//                if (requestResult.resultData.status
//                    && (requestResult.resultData.code == 200 || requestResult.resultData.code == 201)
//                    && requestResult.resultData.errors.isEmpty()
//                ) {
//
//                    Log.e("SUCCESS_FLOW_DATA", "$logText == NetworkResult.Status.SUCCESS")
//                    emit(NetworkResult.success(requestResult.resultData))
//
//                } else {
//
//                    Log.e(
//                        "ERROR_FLOW_STATUS",
//                        "$logText == NetworkResult.Status.ERROR ${requestResult.message}"
//                    )
//                    emit(
//                        NetworkResult.error(
//                            requestResult.errorBody,
//                            requestResult.resultData.message,
//                            requestResult.resultData
//                        )
//                    )
//                }
//            } else if (requestResult.resultStatus == NetworkResult.Status.ERROR) {
//
//                Log.e(
//                    "ERROR_FLOW",
//                    "$logText == NetworkResult.Status.ERROR ${requestResult.message}"
//                )
//
//                if (requestResult.resultData == null) {
//
//                    // showMessage(requestResult.message)
//                    emit(
//                        NetworkResult.error(
//                            requestResult.errorBody,
//                            requestResult.message ?: "Network Error"
//                        )
//                    )
//
//                } else {
//                    emit(
//                        NetworkResult.error(
//                            requestResult.errorBody,
//                            requestResult.resultData.message
//                        )
//                    )
//                }
//            }
//        }
//
//    }
}