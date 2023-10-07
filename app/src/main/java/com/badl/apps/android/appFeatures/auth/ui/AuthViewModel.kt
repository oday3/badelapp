package com.badl.apps.android.appFeatures.auth.ui

import androidx.lifecycle.ViewModel
import com.badl.apps.android.appFeatures.appCommon.data.UserData
import com.badl.apps.android.appFeatures.auth.domain.AuthRepository
import com.badl.apps.android.appFeatures.auth.domain.ChangeLangUseCase
import com.badl.apps.android.appFeatures.userAccount.domain.GetUserAdsUseCase
import com.badl.apps.android.network.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val changLangUseCase: ChangeLangUseCase,
) : ViewModel() {


    fun registerUser(data: Map<String, String>): Flow<NetworkResult<Any>> {

        return repository.registerUser(data)
    }

    fun verifyRegister(data: Map<String, String>): Flow<NetworkResult<UserData>> {

        return repository.verifyRegister(data)
    }


    fun loginUser(data: Map<String, String>): Flow<NetworkResult<UserData>> {

        return repository.loginUser(data)
    }

    fun resendCode(data: Map<String, String>): Flow<NetworkResult<Any>> {

        return repository.resendCode(data)
    }

    fun forgotPassword(data: Map<String, String>): Flow<NetworkResult<Any>> {

        return repository.forgotPassword(data)
    }


    fun verifyResetForgotPassword(data: Map<String, String>): Flow<NetworkResult<Int>> {

        return repository.verifyResetForgotPassword(data)
    }

    fun resetPassword(data: Map<String, String>): Flow<NetworkResult<Any>> {

        return repository.resetPassword(data)
    }

    fun logout(): Flow<NetworkResult<Any>> {

        return repository.logout()
    }

    fun socialLogin(data: Map<String, String>): Flow<NetworkResult<UserData>> {

        return repository.socialLogin(data)
    }

    fun changeLang(params: String): Flow<NetworkResult<Any>> {

        return changLangUseCase.execute(params)
    }
}