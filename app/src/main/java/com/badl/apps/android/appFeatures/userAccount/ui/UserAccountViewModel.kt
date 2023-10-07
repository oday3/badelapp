package com.badl.apps.android.appFeatures.userAccount.ui

import androidx.lifecycle.ViewModel
import com.badl.apps.android.appFeatures.appCommon.data.UserData
import com.badl.apps.android.appFeatures.biddingAndBarterSystem.data.AdOrderItem
import com.badl.apps.android.appFeatures.main.data.FavoriteItem
import com.badl.apps.android.appFeatures.userAccount.data.OrderItem
import com.badl.apps.android.appFeatures.userAccount.data.SwapOrdersDataItem
import com.badl.apps.android.appFeatures.userAccount.data.TransactionItem
import com.badl.apps.android.appFeatures.userAccount.data.UserAdItem
import com.badl.apps.android.appFeatures.userAccount.data.UserRateItem
import com.badl.apps.android.appFeatures.userAccount.domain.AcceptRejectOrderUseCase
import com.badl.apps.android.appFeatures.userAccount.domain.DeleteUserAdUseCase
import com.badl.apps.android.appFeatures.userAccount.domain.GetAdOrdersUseCase
import com.badl.apps.android.appFeatures.userAccount.domain.GetSwapOrdersUseCase
import com.badl.apps.android.appFeatures.userAccount.domain.GetUserAdsUseCase
import com.badl.apps.android.appFeatures.userAccount.domain.UserAccountRepository
import com.badl.apps.android.baseClasses.domain.FavAdUseCase
import com.badl.apps.android.network.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class UserAccountViewModel @Inject constructor(
    private val repository: UserAccountRepository,
    private val getUserAdsUseCase: GetUserAdsUseCase,
    private val deleteUserAdUseCase: DeleteUserAdUseCase,
    private val favAdUseCase: FavAdUseCase,
    private val getSwapOrders: GetSwapOrdersUseCase,
    private val getAdOrdersUseCase: GetAdOrdersUseCase,
    private val acceptRejectOrderUseCase: AcceptRejectOrderUseCase,
) : ViewModel() {


    fun getProfile(): Flow<NetworkResult<UserData>> {

        return repository.getProfile()
    }

    fun updateProfile(
        updatedData: Map<String, RequestBody>,
        updatedImage: MultipartBody.Part?
    ): Flow<NetworkResult<UserData>> {

        return repository.updateProfile(updatedData, updatedImage)
    }


    fun changeLanguage(lang: String): Flow<NetworkResult<Any>> {

        return repository.changeLanguage(lang)
    }

    fun getUserOrders(params: HashMap<String, String>): Flow<NetworkResult<List<OrderItem>>> {

        return repository.getUserOrders(params)
    }

    fun getAds(params: HashMap<String, String>): Flow<NetworkResult<List<UserAdItem>>> {

        return getUserAdsUseCase.execute(params)
    }

    fun deleteAd(adId: String): Flow<NetworkResult<Any>> {

        return deleteUserAdUseCase.execute(adId)
    }


    fun getFavorites(): Flow<NetworkResult<List<FavoriteItem>>> {

        return repository.getFavorites()
    }

    fun favoriteProduct(data: HashMap<String, String>): Flow<NetworkResult<Any>> {

        return favAdUseCase.execute(data)
    }

    fun getSwapOrders(adId: String): Flow<NetworkResult<SwapOrdersDataItem>> {

        return getSwapOrders.execute(adId)
    }

    fun getUserRates(params:HashMap<String, String>): Flow<NetworkResult<List<UserRateItem>>> {

        return repository.getUserRates(params)
    }


    fun acceptRejectOrder(params: HashMap<String, String>): Flow<NetworkResult<Any>> {

        return acceptRejectOrderUseCase.execute(params)
    }


    fun getUserSeals(params:HashMap<String, String>): Flow<NetworkResult<List<TransactionItem>>> {

        return repository.getUserSeals(params)
    }
}