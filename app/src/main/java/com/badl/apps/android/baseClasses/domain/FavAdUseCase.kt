package com.badl.apps.android.baseClasses.domain


import com.badl.apps.android.appFeatures.addAd.domain.AdsRepository
import com.badl.apps.android.network.NetworkResult
import kotlinx.coroutines.flow.Flow

class FavAdUseCase constructor(private val repository: AdsRepository):

    BaseUseCase<HashMap<String, String>, NetworkResult<Any>> {

    override fun execute(params: HashMap<String, String>): Flow<NetworkResult<Any>> {

        return repository.favoriteProduct(params)
    }
}