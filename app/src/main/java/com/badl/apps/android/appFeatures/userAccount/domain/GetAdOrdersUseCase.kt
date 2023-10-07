package com.badl.apps.android.appFeatures.userAccount.domain


import com.badl.apps.android.appFeatures.addAd.domain.AdsRepository
import com.badl.apps.android.appFeatures.biddingAndBarterSystem.data.AdOrderItem
import com.badl.apps.android.appFeatures.biddingAndBarterSystem.data.AdOrdersDataItem
import com.badl.apps.android.appFeatures.userAccount.data.SwapOrdersDataItem
import com.badl.apps.android.appFeatures.userAccount.data.UserAdItem
import com.badl.apps.android.baseClasses.domain.BaseUseCase
import com.badl.apps.android.network.NetworkResult
import kotlinx.coroutines.flow.Flow

class GetAdOrdersUseCase constructor(private val repository: AdsRepository):

    BaseUseCase<String, NetworkResult<AdOrdersDataItem>> {

    override fun execute(params:String): Flow<NetworkResult<AdOrdersDataItem>> {

        return repository.getAdOrders(params)
    }
}