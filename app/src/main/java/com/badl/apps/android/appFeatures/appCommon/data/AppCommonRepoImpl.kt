package com.badl.apps.android.appFeatures.appCommon.data

import com.badl.apps.android.appFeatures.appCommon.domain.AppCommonRepository
import com.badl.apps.android.appFeatures.auth.domain.AuthRepository
import com.badl.apps.android.appFeatures.main.domain.MainRepository
import com.badl.apps.android.baseClasses.data.BaseRepository
import com.badl.apps.android.network.AppEndpoints

class AppCommonRepoImpl(apiClient: AppEndpoints) : AppCommonRepository, BaseRepository(apiClient) {


    override fun getTerms() =
        requestResultAsFlow({
            executeRequest {
                apiClient.getTerms()
            }
        })


    override fun getPrivacy() =
        requestResultAsFlow({
            executeRequest {
                apiClient.getPrivacy()
            }
        })

    override fun aboutApp() =
        requestResultAsFlow({
            executeRequest {
                apiClient.aboutApp()
            }
        })

    override fun getNotifications() =
        requestResultAsFlow({
            executeRequest {
                apiClient.getNotifications()
            }
        })


    override fun deleteNotifications(notificationId: Int) =
        requestResultAsFlow({
            executeRequest {
                apiClient.deleteNotifications(notificationId)
            }
        })


    override fun rate(data:HashMap<String, String>) =
        requestResultAsFlow({
            executeRequest {
                apiClient.rate(data)
            }
        })
}