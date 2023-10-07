package com.badl.apps.android.appFeatures.main.domain


import com.badl.apps.android.appFeatures.main.data.FavoriteItem
import com.badl.apps.android.appFeatures.main.data.HomeDataItem
import com.badl.apps.android.appFeatures.main.data.ProductSectionItem
import com.badl.apps.android.appFeatures.main.data.SectionItem
import com.badl.apps.android.appFeatures.userAccount.data.OrderItem
import com.badl.apps.android.network.NetworkResult
import kotlinx.coroutines.flow.Flow

interface MainRepository {

      fun getHomeData(): Flow<NetworkResult<HomeDataItem>>

      fun getSections(): Flow<NetworkResult<List<SectionItem>>>

      fun getFavorites(): Flow<NetworkResult<List<FavoriteItem>>>

      fun showAll(params:HashMap<String, String>): Flow<NetworkResult<List<ProductSectionItem>>>

      fun getSectionAds(params:HashMap<String, String>): Flow<NetworkResult<List<ProductSectionItem>>>

      fun getUserOrders(params:HashMap<String, String>): Flow<NetworkResult<List<OrderItem>>>

      fun homeSearch(params:HashMap<String, String>): Flow<NetworkResult<List<ProductSectionItem>>>

}