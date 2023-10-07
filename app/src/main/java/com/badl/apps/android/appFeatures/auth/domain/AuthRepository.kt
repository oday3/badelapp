package com.badl.apps.android.appFeatures.auth.domain


import com.badl.apps.android.appFeatures.appCommon.data.UserData
import com.badl.apps.android.network.NetworkResult
import kotlinx.coroutines.flow.Flow

interface AuthRepository {



      fun registerUser(data: Map<String, String>): Flow<NetworkResult<Any>>

      fun verifyRegister(data: Map<String, String>): Flow<NetworkResult<UserData>>

      fun resendCode(data: Map<String, String>): Flow<NetworkResult<Any>>

      fun loginUser(data: Map<String, String>): Flow<NetworkResult<UserData>>

      fun forgotPassword(data: Map<String, String>): Flow<NetworkResult<Any>>

      fun verifyResetForgotPassword(data: Map<String, String>): Flow<NetworkResult<Int>>

      fun resetPassword(data: Map<String, String>): Flow<NetworkResult<Any>>

      fun logout(): Flow<NetworkResult<Any>>

      fun socialLogin(data: Map<String, String>): Flow<NetworkResult<UserData>>


}