package com.badl.apps.android.appFeatures.chat.ui

import androidx.lifecycle.ViewModel
import com.badl.apps.android.appFeatures.biddingAndBarterSystem.data.BiddingHistoryItem
import com.badl.apps.android.appFeatures.biddingAndBarterSystem.data.SellerInfoItem
import com.badl.apps.android.appFeatures.biddingAndBarterSystem.data.SellerRateItem
import com.badl.apps.android.appFeatures.biddingAndBarterSystem.domain.BiddingBarterRepository
import com.badl.apps.android.appFeatures.chat.data.AddressItem
import com.badl.apps.android.appFeatures.chat.data.ChatDetailsItem
import com.badl.apps.android.appFeatures.chat.data.ChatsHistoryData
import com.badl.apps.android.appFeatures.chat.domain.ChatRepository
import com.badl.apps.android.appFeatures.userAccount.data.AdDetailsItem
import com.badl.apps.android.appFeatures.userAccount.data.UserAdItem
import com.badl.apps.android.appFeatures.userAccount.domain.AcceptRejectOrderUseCase
import com.badl.apps.android.appFeatures.userAccount.domain.GetUserAdsUseCase
import com.badl.apps.android.baseClasses.domain.FavAdUseCase
import com.badl.apps.android.network.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repository: ChatRepository) : ViewModel() {


    fun getChats(params: HashMap<String, String>): Flow<NetworkResult<ChatsHistoryData>> {

        return repository.getChats(params)
    }

    fun createChat(data: Map<String, String>): Flow<NetworkResult<Any>> {

        return repository.createChat(data)
    }

    fun getChatDetails(params: HashMap<String, String>): Flow<NetworkResult<ChatDetailsItem>> {

        return repository.getChatDetails(params)
    }

    fun addAddress(data: Map<String, String>): Flow<NetworkResult<Any>> {

        return repository.addAddress(data)
    }

    fun getAddresses(): Flow<NetworkResult<List<AddressItem>>> {

        return repository.getAddresses()
    }

}