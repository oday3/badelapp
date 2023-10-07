package com.badl.apps.android.appFeatures.biddingAndBarterSystem.ui

import androidx.lifecycle.ViewModel
import com.badl.apps.android.appFeatures.biddingAndBarterSystem.data.BiddingHistoryItem
import com.badl.apps.android.appFeatures.biddingAndBarterSystem.data.SellerInfoItem
import com.badl.apps.android.appFeatures.biddingAndBarterSystem.data.SellerRateItem
import com.badl.apps.android.appFeatures.biddingAndBarterSystem.domain.BiddingBarterRepository
import com.badl.apps.android.appFeatures.userAccount.data.AdDetailsItem
import com.badl.apps.android.appFeatures.userAccount.data.UserAdItem
import com.badl.apps.android.appFeatures.userAccount.domain.AcceptRejectOrderUseCase
import com.badl.apps.android.appFeatures.userAccount.domain.GetUserAdsUseCase
import com.badl.apps.android.baseClasses.domain.FavAdUseCase
import com.badl.apps.android.network.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class BiddingBarterViewModel @Inject constructor(
    private val repository: BiddingBarterRepository,
    private val favAdUseCase: FavAdUseCase,
    private val getUserAdsUseCase: GetUserAdsUseCase,
    private val acceptRejectOrderUseCase: AcceptRejectOrderUseCase,

    ) : ViewModel() {


    fun getSellerAds(userId: Int): Flow<NetworkResult<SellerInfoItem>> {

        return repository.getSellerAds(userId)
    }

    fun getSellerAdBids(adId: Int): Flow<NetworkResult<BiddingHistoryItem>> {

        return repository.getSellerAdBids(adId)
    }


    fun getSellerRates(userId: Int): Flow<NetworkResult<List<SellerRateItem>>> {

        return repository.getSellerRates(userId)
    }

    fun order(data: HashMap<String, String>): Flow<NetworkResult<Any>> {

        return repository.order(data)
    }


    fun getAdDetails(id: String): Flow<NetworkResult<AdDetailsItem>> {

        return repository.getAdDetails(id)
    }

    fun getOrderDetails(id: String): Flow<NetworkResult<AdDetailsItem>> {

        return repository.getOrderDetails(id)
    }

    fun favoriteAd(data: HashMap<String, String>): Flow<NetworkResult<Any>> {

        return favAdUseCase.execute(data)
    }

    fun getAds(params: HashMap<String, String>): Flow<NetworkResult<List<UserAdItem>>> {

        return getUserAdsUseCase.execute(params)
    }

    fun acceptRejectOrder(params: HashMap<String, String>): Flow<NetworkResult<Any>> {

        return acceptRejectOrderUseCase.execute(params)
    }
}