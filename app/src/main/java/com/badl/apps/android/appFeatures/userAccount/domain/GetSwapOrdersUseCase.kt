package com.badl.apps.android.appFeatures.userAccount.domain


import com.badl.apps.android.appFeatures.addAd.domain.AdsRepository
import com.badl.apps.android.appFeatures.userAccount.data.SwapOrdersDataItem
import com.badl.apps.android.appFeatures.userAccount.data.UserAdItem
import com.badl.apps.android.baseClasses.domain.BaseUseCase
import com.badl.apps.android.network.NetworkResult
import kotlinx.coroutines.flow.Flow

class GetSwapOrdersUseCase constructor(private val repository: AdsRepository):

    BaseUseCase<String, NetworkResult<SwapOrdersDataItem>> {

    override fun execute(params:String): Flow<NetworkResult<SwapOrdersDataItem>> {

        return repository.getSwapOrdersData(params)
    }
}