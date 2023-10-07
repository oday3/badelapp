package com.badl.apps.android.appFeatures.userAccount.domain


import com.badl.apps.android.appFeatures.appCommon.data.UserData
import com.badl.apps.android.appFeatures.main.data.FavoriteItem
import com.badl.apps.android.appFeatures.userAccount.data.OrderItem
import com.badl.apps.android.appFeatures.userAccount.data.TransactionItem
import com.badl.apps.android.appFeatures.userAccount.data.UserRateItem
import com.badl.apps.android.network.NetworkResult
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface UserAccountRepository {

      fun getProfile(): Flow<NetworkResult<UserData>>


      fun updateProfile(updatedData: Map<String, RequestBody>, updatedImage: MultipartBody.Part?): Flow<NetworkResult<UserData>>

      fun changeLanguage(lang: String): Flow<NetworkResult<Any>>

      fun getUserOrders(params:HashMap<String, String>): Flow<NetworkResult<List<OrderItem>>>

      fun getFavorites(): Flow<NetworkResult<List<FavoriteItem>>>

      fun getUserRates(params:HashMap<String, String>): Flow<NetworkResult<List<UserRateItem>>>

      fun getUserSeals(params:HashMap<String, String>): Flow<NetworkResult<List<TransactionItem>>>


}