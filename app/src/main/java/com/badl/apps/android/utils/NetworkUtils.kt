package com.badl.apps.android.utils

import android.text.TextUtils
import androidx.fragment.app.Fragment
import com.badl.apps.android.baseClasses.data.BaseDataSource
import com.badl.apps.android.baseClasses.ui.BaseActivity
import com.badl.apps.android.network.NetworkResult
import com.badl.apps.android.utils.UiUtils.showCustomToast
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

object NetworkUtils: BaseDataSource {

    lateinit var mMoshi: Moshi

    fun initMoshi(moshi: Moshi) {

        mMoshi = moshi
    }

    fun <T> fromJsonToObject(model: Class<T>, data: String): T? {

        return mMoshi.adapter(model).fromJson(data)
    }

    fun <T> fromObjectToJson(model: Class<T>, data: T): String {

        return mMoshi.adapter(model).toJson(data)
    }

    fun <T> toObjectByGetClass(model: Class<T>, data: Any?): T? {

        return mMoshi.adapter(model).fromJsonValue(data)
    }


    fun <T> toListByGetClass(model: Class<T>, data: Any?): List<T> {

        val listType = Types.newParameterizedType(List::class.java, model)

        val adapter: JsonAdapter<List<T>> = mMoshi.adapter(listType)

        return adapter.fromJsonValue(data) as List<T>
    }

    fun <T> toListByGetClass(model: Class<T>, data: String): List<T> {

        val listType = Types.newParameterizedType(List::class.java, model)

        val adapter: JsonAdapter<List<T>> = mMoshi.adapter(listType)

        return adapter.fromJson(data) as List<T>
    }



    fun createPartFromString(value: String): RequestBody {

        return value.toRequestBody(MultipartBody.FORM)
    }

    fun createFilePart(partName: String, file: File): MultipartBody.Part {

        val xy = file.asRequestBody(contentType = "multipart/form-data".toMediaTypeOrNull())
        // val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file)
        return MultipartBody.Part.createFormData(partName, file.name, xy)
    }

    fun createImagePart(partName: String, file: File): MultipartBody.Part? {

        val xy = file.asRequestBody(contentType = "image/*".toMediaTypeOrNull())

        return MultipartBody.Part.createFormData(
            partName, file.name, xy
        )
    }


    fun <T> BaseActivity.handelApiResult(result: NetworkResult<T>,onLoading: () -> Unit = {
        showDialog(true)
    },
    onResultSuccess: () -> Unit, onResultFail: () -> Unit = {}) {


        when (result.resultStatus) {

            NetworkResult.Status.LOADING -> {

                onLoading()
            }

            NetworkResult.Status.SUCCESS -> {

                onResultSuccess()

                showDialog(false)
            }

            NetworkResult.Status.ERROR -> {

                showDialog(false)

                if (TextUtils.isEmpty(result.resultData?.message)) {

                    showCustomToast(result.message, Constants.TOAST_ERROR)

                } else {

                    showCustomToast(result.resultData?.message, Constants.TOAST_ERROR)

                }

                onResultFail()
            }
        }
    }



    fun <T> Fragment.handelApiResult(result: NetworkResult<T>,onLoading: () -> Unit = {

        (requireActivity() as BaseActivity).showDialog(true)
    },
    onResultSuccess: () -> Unit, onResultFail: () -> Unit = {}) {


        when (result.resultStatus) {

            NetworkResult.Status.LOADING -> {

                (requireActivity() as BaseActivity).showDialog(true)

                onLoading()
            }

            NetworkResult.Status.SUCCESS -> {

                onResultSuccess()

                (requireActivity() as BaseActivity).showDialog(false)
            }

            NetworkResult.Status.ERROR -> {

                (requireActivity() as BaseActivity).showDialog(false)

                if (TextUtils.isEmpty(result.resultData?.message)) {

                    (requireActivity() as BaseActivity).showCustomToast(result.message, Constants.TOAST_ERROR)

                } else {

                    (requireActivity() as BaseActivity).showCustomToast(result.resultData?.message, Constants.TOAST_ERROR)

                }

                onResultFail()
            }
        }
    }


}