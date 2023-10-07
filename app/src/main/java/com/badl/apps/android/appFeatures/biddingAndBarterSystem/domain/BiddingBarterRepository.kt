package com.badl.apps.android.appFeatures.biddingAndBarterSystem.domain


import com.badl.apps.android.appFeatures.biddingAndBarterSystem.data.BiddingHistoryItem
import com.badl.apps.android.appFeatures.biddingAndBarterSystem.data.SellerInfoItem
import com.badl.apps.android.appFeatures.biddingAndBarterSystem.data.SellerRateItem
import com.badl.apps.android.appFeatures.userAccount.data.AdDetailsItem
import com.badl.apps.android.network.NetworkResult
import kotlinx.coroutines.flow.Flow

interface BiddingBarterRepository {

        fun getSellerAds(userId: Int): Flow<NetworkResult<SellerInfoItem>>

        fun getSellerAdBids(adId: Int): Flow<NetworkResult<BiddingHistoryItem>>

        fun getSellerRates(userId: Int): Flow<NetworkResult<List<SellerRateItem>>>

        fun order(data: HashMap<String, String>): Flow<NetworkResult<Any>>

        fun getAdDetails(id: String): Flow<NetworkResult<AdDetailsItem>>

        fun getOrderDetails(id: String): Flow<NetworkResult<AdDetailsItem>>

}