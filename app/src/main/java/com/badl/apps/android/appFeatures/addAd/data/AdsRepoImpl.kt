package com.badl.apps.android.appFeatures.addAd.data

import com.badl.apps.android.appFeatures.addAd.domain.AdsRepository
import com.badl.apps.android.baseClasses.data.BaseRepository
import com.badl.apps.android.network.AppEndpoints
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AdsRepoImpl(apiClient: AppEndpoints) : AdsRepository, BaseRepository(apiClient) {

    override fun getAdData() =
        requestResultAsFlow({
            executeRequest {
                apiClient.getAdData()
            }
        })

    override fun addAd(updatedData: Map<String, RequestBody>, updatedImage: List<MultipartBody.Part?>?) =
        requestResultAsFlow({
            executeRequest {
                apiClient.addAd(updatedData, updatedImage)
            }
        })


    override fun getAds(params:HashMap<String, String>) =
        requestResultAsFlow({
            executeRequest {
                apiClient.getAds(params)
            }
        })

    override fun deleteAd(id: String) =
        requestResultAsFlow({
            executeRequest {
                apiClient.deleteAd(id)
            }
        })

    override fun getAdDetails(id: String) =
        requestResultAsFlow({
            executeRequest {
                apiClient.getAdDetails(id)
            }
        })

    override fun updateAd(updatedData: Map<String, RequestBody>, updatedImage: List<MultipartBody.Part?>?) =
        requestResultAsFlow({
            executeRequest {
                apiClient.updateAd(updatedData, updatedImage)
            }
        })

    override fun favoriteProduct(data: HashMap<String, String>) =
        requestResultAsFlow({
            executeRequest {
                apiClient.favoriteProduct(data)
            }
        })

    override fun getSwapOrdersData(id: String) =
        requestResultAsFlow({
            executeRequest {
                apiClient.getSwapOrdersData(id)
            }
        })


    override fun swapOrderDetails(id: String) =
        requestResultAsFlow({
            executeRequest {
                apiClient.swaOrderDetails(id)
            }
        })

    override fun getAdOrders(id: String) =
        requestResultAsFlow({
            executeRequest {
                apiClient.getAdOrders(id)
            }
        })

    override fun updateAdStatus(params: HashMap<String, String>) =
        requestResultAsFlow({
            executeRequest {
                apiClient.updateAdStatus(params)
            }
        })

    override fun acceptRejectOrder(params: HashMap<String, String>) =
        requestResultAsFlow({
            executeRequest {
                apiClient.acceptRejectOrder(params)
            }
        })

}