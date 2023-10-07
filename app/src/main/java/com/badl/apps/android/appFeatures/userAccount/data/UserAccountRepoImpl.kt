package com.badl.apps.android.appFeatures.userAccount.data

import com.badl.apps.android.appFeatures.userAccount.domain.UserAccountRepository
import com.badl.apps.android.baseClasses.data.BaseRepository
import com.badl.apps.android.network.AppEndpoints
import okhttp3.MultipartBody
import okhttp3.RequestBody

class UserAccountRepoImpl(apiClient: AppEndpoints) : UserAccountRepository, BaseRepository(apiClient) {


    override fun getProfile() =
        requestResultAsFlow({
            executeRequest {
                apiClient.getProfile()
            }
        })

    override fun updateProfile(updatedData: Map<String, RequestBody>, updatedImage: MultipartBody.Part?) =
        requestResultAsFlow({
            executeRequest {
                apiClient.updateProfile(updatedData, updatedImage)
            }
        })

    override fun changeLanguage(lang: String) =
        requestResultAsFlow({
            executeRequest {
                apiClient.changeLanguage(lang)
            }
        })

    override fun getUserOrders(params:HashMap<String, String>) =
        requestResultAsFlow({
            executeRequest {
                apiClient.getUserOrders(params)
            }
        })


    override fun getFavorites() =
        requestResultAsFlow({
            executeRequest {
                apiClient.getFavorites()
            }
        })

    override fun getUserRates(params:HashMap<String, String>) =
        requestResultAsFlow({
            executeRequest {
                apiClient.getUserRates(params)
            }
        })

    override fun getUserSeals(params:HashMap<String, String>) =
        requestResultAsFlow({
            executeRequest {
                apiClient.getUserSeals(params)
            }
        })
}