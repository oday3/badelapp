package com.badl.apps.android.appFeatures.appCommon.ui

import androidx.lifecycle.ViewModel
import com.badl.apps.android.appFeatures.appCommon.data.AppDataItem
import com.badl.apps.android.appFeatures.appCommon.data.NotificationItem
import com.badl.apps.android.appFeatures.appCommon.data.UserData
import com.badl.apps.android.appFeatures.appCommon.domain.AppCommonRepository
import com.badl.apps.android.appFeatures.auth.domain.AuthRepository
import com.badl.apps.android.appFeatures.auth.domain.ChangeLangUseCase
import com.badl.apps.android.appFeatures.main.data.FavoriteItem
import com.badl.apps.android.appFeatures.main.data.HomeDataItem
import com.badl.apps.android.appFeatures.main.data.SectionItem
import com.badl.apps.android.appFeatures.main.domain.MainRepository
import com.badl.apps.android.network.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class AppCommonViewModel @Inject constructor(private val repository: AppCommonRepository,
                                             private val changLangUseCase: ChangeLangUseCase,)
    : ViewModel() {


    fun getTerms(): Flow<NetworkResult<AppDataItem>> {

        return repository.getTerms()
    }

    fun getPrivacy(): Flow<NetworkResult<AppDataItem>> {

        return repository.getPrivacy()
    }

    fun aboutApp(): Flow<NetworkResult<AppDataItem>> {

        return repository.aboutApp()
    }

    fun getNotifications(): Flow<NetworkResult<List<NotificationItem>>> {

        return repository.getNotifications()
    }

    fun deleteNotifications(notificationId: Int): Flow<NetworkResult<Any>> {

        return repository.deleteNotifications(notificationId)
    }


    fun rate(data :HashMap<String, String>): Flow<NetworkResult<Any>> {

        return repository.rate(data)
    }

    fun changeLang(params: String): Flow<NetworkResult<Any>> {

        return changLangUseCase.execute(params)
    }

}