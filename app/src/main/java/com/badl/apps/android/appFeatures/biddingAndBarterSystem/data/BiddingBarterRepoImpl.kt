package com.badl.apps.android.appFeatures.biddingAndBarterSystem.data

import com.badl.apps.android.appFeatures.auth.domain.AuthRepository
import com.badl.apps.android.appFeatures.biddingAndBarterSystem.domain.BiddingBarterRepository
import com.badl.apps.android.appFeatures.main.domain.MainRepository
import com.badl.apps.android.baseClasses.data.BaseRepository
import com.badl.apps.android.network.AppEndpoints

class BiddingBarterRepoImpl(apiClient: AppEndpoints) : BiddingBarterRepository, BaseRepository(apiClient) {



    override fun getSellerAds(userId: Int) =
        requestResultAsFlow({
            executeRequest {
                apiClient.getSellerAds(userId)
            }
        })

    override fun getSellerAdBids(adId: Int) =
        requestResultAsFlow({
            executeRequest {
                apiClient.getSellerAdBids(adId)
            }
        })

    override fun getSellerRates(userId: Int) =
        requestResultAsFlow({
            executeRequest {
                apiClient.getSellerRates(userId)
            }
        })

    override fun order(data: HashMap<String, String>) =
        requestResultAsFlow({
            executeRequest {
                apiClient.order(data)
            }
        })

    override fun getAdDetails(id: String) =
        requestResultAsFlow({
            executeRequest {
                apiClient.getAdDetails(id)
            }
        })

    override fun getOrderDetails(id: String) =
        requestResultAsFlow({
            executeRequest {
                apiClient.getOrderDetails(id)
            }
        })



}