package com.badl.apps.android.di


import com.badl.apps.android.appFeatures.addAd.data.AdsRepoImpl
import com.badl.apps.android.appFeatures.addAd.domain.AdsRepository
import com.badl.apps.android.appFeatures.appCommon.data.AppCommonRepoImpl
import com.badl.apps.android.appFeatures.appCommon.domain.AppCommonRepository
import com.badl.apps.android.appFeatures.auth.data.AuthRepoImpl
import com.badl.apps.android.appFeatures.auth.domain.AuthRepository
import com.badl.apps.android.appFeatures.biddingAndBarterSystem.data.BiddingBarterRepoImpl
import com.badl.apps.android.appFeatures.biddingAndBarterSystem.domain.BiddingBarterRepository
import com.badl.apps.android.appFeatures.chat.data.ChatRepoImpl
import com.badl.apps.android.appFeatures.chat.domain.ChatRepository
import com.badl.apps.android.appFeatures.main.data.MainRepoImpl
import com.badl.apps.android.appFeatures.main.domain.MainRepository
import com.badl.apps.android.appFeatures.userAccount.data.UserAccountRepoImpl
import com.badl.apps.android.appFeatures.userAccount.domain.UserAccountRepository
import com.badl.apps.android.network.AppEndpoints
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object RepositoriesModule {

//    @Provides
//    @ViewModelScoped
//    fun provideAppCommonRepository(apiClient: AppEndpoints): AppCommonRepository {
//        return AppCommonRepoImpl(apiClient)
//    }

    @Provides
    @ViewModelScoped
    fun provideAuthRepository(apiClient: AppEndpoints): AuthRepository {

        return AuthRepoImpl(apiClient)
    }

    @Provides
    @ViewModelScoped
    fun provideMainRepository(apiClient: AppEndpoints): MainRepository {

        return MainRepoImpl(apiClient)
    }

    @Provides
    @ViewModelScoped
    fun provideUserAccountRepository(apiClient: AppEndpoints): UserAccountRepository {

        return UserAccountRepoImpl(apiClient)
    }

    @Provides
    @ViewModelScoped
    fun provideAdsRepository(apiClient: AppEndpoints): AdsRepository {

        return AdsRepoImpl(apiClient)
    }

    @Provides
    @ViewModelScoped
    fun provideAppCommonRepository(apiClient: AppEndpoints): AppCommonRepository {

        return AppCommonRepoImpl(apiClient)
    }

    @Provides
    @ViewModelScoped
    fun provideBiddingBarterRepository(apiClient: AppEndpoints): BiddingBarterRepository {

        return BiddingBarterRepoImpl(apiClient)
    }

    @Provides
    @ViewModelScoped
    fun provideChatRepository(apiClient: AppEndpoints): ChatRepository {

        return ChatRepoImpl(apiClient)
    }

}