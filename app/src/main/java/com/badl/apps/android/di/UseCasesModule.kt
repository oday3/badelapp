package com.badl.apps.android.di

import com.badl.apps.android.appFeatures.addAd.domain.AdsRepository
import com.badl.apps.android.appFeatures.auth.domain.ChangeLangUseCase
import com.badl.apps.android.appFeatures.userAccount.domain.AcceptRejectOrderUseCase
import com.badl.apps.android.appFeatures.userAccount.domain.DeleteUserAdUseCase
import com.badl.apps.android.appFeatures.userAccount.domain.GetAdOrdersUseCase
import com.badl.apps.android.appFeatures.userAccount.domain.GetSwapOrdersUseCase
import com.badl.apps.android.appFeatures.userAccount.domain.GetUserAdsUseCase
import com.badl.apps.android.appFeatures.userAccount.domain.UserAccountRepository
import com.badl.apps.android.baseClasses.domain.FavAdUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object UseCasesModule {


    @Provides
    @ViewModelScoped
    fun provideGetUserAdUseCase(repository: AdsRepository): GetUserAdsUseCase {
        return GetUserAdsUseCase(repository)
    }

    @Provides
    @ViewModelScoped
    fun provideDeleteUserAdUseCase(repository: AdsRepository): DeleteUserAdUseCase {
        return DeleteUserAdUseCase(repository)
    }

    @Provides
    @ViewModelScoped
    fun provideFavAdUseCase(repository: AdsRepository): FavAdUseCase {
        return FavAdUseCase(repository)
    }

    @Provides
    @ViewModelScoped
    fun provideGetSwapOrderUseCase(repository: AdsRepository): GetSwapOrdersUseCase {
        return GetSwapOrdersUseCase(repository)
    }

    @Provides
    @ViewModelScoped
    fun provideAcceptRejectOrderUseCase(repository: AdsRepository): AcceptRejectOrderUseCase {
        return AcceptRejectOrderUseCase(repository)
    }


    @Provides
    @ViewModelScoped
    fun provideGetAdOrdersUseCase(repository: AdsRepository): GetAdOrdersUseCase {
        return GetAdOrdersUseCase(repository)
    }

    @Provides
    @ViewModelScoped
    fun provideChangeLangUseCase(repository: UserAccountRepository): ChangeLangUseCase {
        return ChangeLangUseCase(repository)
    }
}