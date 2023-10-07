package com.badl.apps.android.appFeatures.appCommon.domain


import com.badl.apps.android.appFeatures.appCommon.data.AppDataItem
import com.badl.apps.android.appFeatures.appCommon.data.NotificationItem
import com.badl.apps.android.appFeatures.main.data.FavoriteItem
import com.badl.apps.android.appFeatures.main.data.HomeDataItem
import com.badl.apps.android.appFeatures.main.data.SectionItem
import com.badl.apps.android.network.NetworkResult
import kotlinx.coroutines.flow.Flow
import retrofit2.http.QueryMap

interface AppCommonRepository {

      fun getTerms(): Flow<NetworkResult<AppDataItem>>

      fun getPrivacy(): Flow<NetworkResult<AppDataItem>>

      fun aboutApp(): Flow<NetworkResult<AppDataItem>>

      fun getNotifications(): Flow<NetworkResult<List<NotificationItem>>>

      fun deleteNotifications(notificationId: Int): Flow<NetworkResult<Any>>

      fun rate(data:HashMap<String, String>): Flow<NetworkResult<Any>>


}