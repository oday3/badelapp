package com.badl.apps.android.appFeatures.chat.domain


import com.badl.apps.android.appFeatures.appCommon.data.AppDataItem
import com.badl.apps.android.appFeatures.appCommon.data.NotificationItem
import com.badl.apps.android.appFeatures.chat.data.AddressItem
import com.badl.apps.android.appFeatures.chat.data.ChatDetailsItem
import com.badl.apps.android.appFeatures.chat.data.ChatsHistoryData
import com.badl.apps.android.appFeatures.main.data.FavoriteItem
import com.badl.apps.android.appFeatures.main.data.HomeDataItem
import com.badl.apps.android.appFeatures.main.data.SectionItem
import com.badl.apps.android.network.NetworkResult
import kotlinx.coroutines.flow.Flow
import retrofit2.http.QueryMap

interface ChatRepository {

      fun createChat(data: Map<String, String>): Flow<NetworkResult<Any>>

      fun getChats(params: HashMap<String, String>): Flow<NetworkResult<ChatsHistoryData>>

      fun getChatDetails(params:HashMap<String, String>): Flow<NetworkResult<ChatDetailsItem>>

      fun getAddresses(): Flow<NetworkResult<List<AddressItem>>>

      fun addAddress(data:Map<String, String>): Flow<NetworkResult<Any>>




}