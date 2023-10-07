package com.badl.apps.android.appFeatures.userAccount.domain


import com.badl.apps.android.appFeatures.addAd.domain.AdsRepository
import com.badl.apps.android.baseClasses.domain.BaseUseCase
import com.badl.apps.android.network.NetworkResult
import kotlinx.coroutines.flow.Flow

class DeleteUserAdUseCase constructor(private val repository: AdsRepository):

    BaseUseCase<String, NetworkResult<Any>> {

    override fun execute(params: String): Flow<NetworkResult<Any>> {

        return repository.deleteAd(params)
    }
}