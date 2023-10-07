package com.badl.apps.android.appFeatures.auth.domain


import com.badl.apps.android.appFeatures.addAd.domain.AdsRepository
import com.badl.apps.android.appFeatures.userAccount.data.UserAdItem
import com.badl.apps.android.appFeatures.userAccount.domain.UserAccountRepository
import com.badl.apps.android.appFeatures.userAccount.ui.UserAccountViewModel
import com.badl.apps.android.baseClasses.domain.BaseUseCase
import com.badl.apps.android.network.NetworkResult
import kotlinx.coroutines.flow.Flow

class ChangeLangUseCase constructor(private val repository: UserAccountRepository):

    BaseUseCase<String, NetworkResult<Any>> {

    override fun execute(params: String): Flow<NetworkResult<Any>> {

        return repository.changeLanguage(params)
    }
}