package com.badl.apps.android.appFeatures.userAccount.domain


import com.badl.apps.android.appFeatures.addAd.domain.AdsRepository
import com.badl.apps.android.appFeatures.userAccount.data.UserAdItem
import com.badl.apps.android.baseClasses.domain.BaseUseCase
import com.badl.apps.android.network.NetworkResult
import kotlinx.coroutines.flow.Flow

class AcceptRejectOrderUseCase constructor(private val repository: AdsRepository):

    BaseUseCase<HashMap<String, String>, NetworkResult<Any>> {

    override fun execute(params:HashMap<String, String>): Flow<NetworkResult<Any>> {

        return repository.acceptRejectOrder(params)
    }
}