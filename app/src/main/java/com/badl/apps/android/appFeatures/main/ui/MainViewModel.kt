package com.badl.apps.android.appFeatures.main.ui

import android.net.wifi.aware.SubscribeConfig
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.badl.apps.android.appFeatures.main.data.FavoriteItem
import com.badl.apps.android.appFeatures.main.data.HomeDataItem
import com.badl.apps.android.appFeatures.main.data.ProductSectionItem
import com.badl.apps.android.appFeatures.main.data.SectionItem
import com.badl.apps.android.appFeatures.main.domain.MainRepository
import com.badl.apps.android.appFeatures.userAccount.data.OrderItem
import com.badl.apps.android.baseClasses.domain.FavAdUseCase
import com.badl.apps.android.network.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.shareIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: MainRepository,
private val favAdUseCase: FavAdUseCase)
    : ViewModel() {


    fun getHomeData(): Flow<NetworkResult<HomeDataItem>> {

        return repository.getHomeData()
    }

    fun getSections(): Flow<NetworkResult<List<SectionItem>>> {

        return repository.getSections()
    }


    fun getFavorites(): Flow<NetworkResult<List<FavoriteItem>>> {

        return repository.getFavorites()
    }


    fun favoriteProduct(data: HashMap<String, String>): Flow<NetworkResult<Any>> {

        return favAdUseCase.execute(data)
    }


    fun showAll(params:HashMap<String, String>): Flow<NetworkResult<List<ProductSectionItem>>> {

        return repository.showAll(params)
    }

    fun getSectionAds(params:HashMap<String, String>): Flow<NetworkResult<List<ProductSectionItem>>> {


       // repository.getSectionAds(params).shareIn(viewModelScope, SharingStarted.WhileSubscribed(), 0)
        return repository.getSectionAds(params)
    }


    fun getUserOrders(params: HashMap<String, String>): Flow<NetworkResult<List<OrderItem>>> {

        return repository.getUserOrders(params)
    }

    fun homeSearch(params: HashMap<String, String>): Flow<NetworkResult<List<ProductSectionItem>>> {

        return repository.homeSearch(params)
    }

}