package com.badl.apps.android.appFeatures.chat.data

import com.badl.apps.android.appFeatures.appCommon.domain.AppCommonRepository
import com.badl.apps.android.appFeatures.auth.domain.AuthRepository
import com.badl.apps.android.appFeatures.chat.domain.ChatRepository
import com.badl.apps.android.appFeatures.main.domain.MainRepository
import com.badl.apps.android.baseClasses.data.BaseRepository
import com.badl.apps.android.network.AppEndpoints

class ChatRepoImpl(apiClient: AppEndpoints) : ChatRepository, BaseRepository(apiClient) {


    override fun createChat(data: Map<String, String>) =
        requestResultAsFlow({
            executeRequest {
                apiClient.createChat(data)
            }
        })


    override fun getChats(params: HashMap<String, String>) =
        requestResultAsFlow({
            executeRequest {
                apiClient.getChats(params)
            }
        })


    override fun getChatDetails(params: HashMap<String, String>) =
        requestResultAsFlow({
            executeRequest {
                apiClient.getChatDetails(params)
            }
        })

    override fun getAddresses() =
        requestResultAsFlow({
            executeRequest {
                apiClient.getAddresses()
            }
        })


    override fun addAddress(data:Map<String, String>) =
        requestResultAsFlow({
            executeRequest {
                apiClient.addAddress(data)
            }
        })
}