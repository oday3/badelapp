package com.badl.apps.android.appFeatures.addAd.ui

import androidx.lifecycle.ViewModel
import com.badl.apps.android.appFeatures.userAccount.data.AdDetailsItem
import com.badl.apps.android.appFeatures.userAccount.data.UserAdItem
import com.badl.apps.android.appFeatures.addAd.data.AdDataItem
import com.badl.apps.android.appFeatures.addAd.domain.AdsRepository
import com.badl.apps.android.appFeatures.biddingAndBarterSystem.data.AdOrderItem
import com.badl.apps.android.appFeatures.biddingAndBarterSystem.data.AdOrdersDataItem
import com.badl.apps.android.appFeatures.biddingAndBarterSystem.data.SwapOrderDetailsItem
import com.badl.apps.android.network.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class AdsViewModel @Inject constructor(private val repository: AdsRepository)
    : ViewModel() {


    fun getAdData(): Flow<NetworkResult<AdDataItem>> {

        return repository.getAdData()
    }

    fun addAd(updatedData: Map<String, RequestBody>,
                  updatedImage: List<MultipartBody.Part?>?): Flow<NetworkResult<Any>> {

        return repository.addAd(updatedData, updatedImage)
    }

    fun getAds(params:HashMap<String, String>): Flow<NetworkResult<List<UserAdItem>>> {

        return repository.getAds(params)
    }


    fun deleteAd(id: String): Flow<NetworkResult<Any>> {

        return repository.deleteAd(id)
    }

    fun getAdDetails(id: String): Flow<NetworkResult<AdDetailsItem>> {

        return repository.getAdDetails(id)
    }


    fun updateAd(updatedData: Map<String, RequestBody>,
              updatedImage: List<MultipartBody.Part?>?): Flow<NetworkResult<Any>> {

        return repository.updateAd(updatedData, updatedImage)
    }


    fun swapOrderDetails(id: String): Flow<NetworkResult<SwapOrderDetailsItem>> {

        return repository.swapOrderDetails(id)
    }

    fun getAdOrders(id: String): Flow<NetworkResult<AdOrdersDataItem>> {

        return repository.getAdOrders(id)
    }

    fun updateAdStatus(params: HashMap<String, String>): Flow<NetworkResult<AdDetailsItem>> {

        return repository.updateAdStatus(params)
    }

    fun acceptRejectOrder(params: HashMap<String, String>): Flow<NetworkResult<Any>> {

        return repository.acceptRejectOrder(params)
    }
}