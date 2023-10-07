package com.badl.apps.android.appFeatures.userAccount.domain


import com.badl.apps.android.appFeatures.addAd.domain.AdsRepository
import com.badl.apps.android.appFeatures.userAccount.data.UserAdItem
import com.badl.apps.android.baseClasses.domain.BaseUseCase
import com.badl.apps.android.network.NetworkResult
import kotlinx.coroutines.flow.Flow

class GetUserAdsUseCase constructor(private val repository: AdsRepository):

    BaseUseCase<HashMap<String, String>, NetworkResult<List<UserAdItem>>> {

    override fun execute(params:HashMap<String, String>): Flow<NetworkResult<List<UserAdItem>>> {

        return repository.getAds(params)
    }
}