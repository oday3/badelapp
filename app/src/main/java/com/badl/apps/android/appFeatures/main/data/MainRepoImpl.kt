package com.badl.apps.android.appFeatures.main.data

import com.badl.apps.android.appFeatures.auth.domain.AuthRepository
import com.badl.apps.android.appFeatures.main.domain.MainRepository
import com.badl.apps.android.baseClasses.data.BaseRepository
import com.badl.apps.android.network.AppEndpoints

class MainRepoImpl(apiClient: AppEndpoints) : MainRepository, BaseRepository(apiClient) {


    override fun getHomeData() =
        requestResultAsFlow({
            executeRequest {
                apiClient.getHomeData()
            }
        })

    override fun getSections() =
        requestResultAsFlow({
            executeRequest {
                apiClient.getSections()
            }
        })

    override fun getFavorites() =
        requestResultAsFlow({
            executeRequest {
                apiClient.getFavorites()
            }
        })


    override fun showAll(params:HashMap<String, String>) =
        requestResultAsFlow({
            executeRequest {
                apiClient.showAll(params)
            }
        })

    override fun getSectionAds(params:HashMap<String, String>) =
        requestResultAsFlow({
            executeRequest {
                apiClient.getSectionAds(params)
            }
        })


    override fun getUserOrders(params:HashMap<String, String>) =
        requestResultAsFlow({
            executeRequest {
                apiClient.getUserOrders(params)
            }
        })


    override fun homeSearch(params:HashMap<String, String>) =
        requestResultAsFlow({
            executeRequest {
                apiClient.homeSearch(params)
            }
        })

}