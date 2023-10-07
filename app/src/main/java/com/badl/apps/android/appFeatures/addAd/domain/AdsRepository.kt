package com.badl.apps.android.appFeatures.addAd.domain


import com.badl.apps.android.appFeatures.userAccount.data.AdDetailsItem
import com.badl.apps.android.appFeatures.userAccount.data.UserAdItem
import com.badl.apps.android.appFeatures.addAd.data.AdDataItem
import com.badl.apps.android.appFeatures.biddingAndBarterSystem.data.AdOrderItem
import com.badl.apps.android.appFeatures.biddingAndBarterSystem.data.AdOrdersDataItem
import com.badl.apps.android.appFeatures.biddingAndBarterSystem.data.SwapOrderDetailsItem
import com.badl.apps.android.appFeatures.userAccount.data.SwapOrdersDataItem
import com.badl.apps.android.network.NetworkResult
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface AdsRepository {

      fun getAdData(): Flow<NetworkResult<AdDataItem>>

      fun addAd(updatedData: Map<String, RequestBody>, updatedImage: List<MultipartBody.Part?>?): Flow<NetworkResult<Any>>

      fun getAds(params:HashMap<String, String>): Flow<NetworkResult<List<UserAdItem>>>

      fun deleteAd(id: String): Flow<NetworkResult<Any>>

      fun getAdDetails(id: String): Flow<NetworkResult<AdDetailsItem>>

      fun updateAd(updatedData: Map<String, RequestBody>, updatedImage: List<MultipartBody.Part?>?): Flow<NetworkResult<Any>>

      fun favoriteProduct(data: HashMap<String, String>): Flow<NetworkResult<Any>>

      fun getSwapOrdersData(id: String): Flow<NetworkResult<SwapOrdersDataItem>>

      fun swapOrderDetails(id: String): Flow<NetworkResult<SwapOrderDetailsItem>>

      fun getAdOrders(id: String): Flow<NetworkResult<AdOrdersDataItem>>

      fun updateAdStatus(params: HashMap<String, String>): Flow<NetworkResult<AdDetailsItem>>

      fun acceptRejectOrder(params: HashMap<String, String>): Flow<NetworkResult<Any>>

}