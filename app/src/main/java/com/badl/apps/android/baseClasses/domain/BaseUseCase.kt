package com.badl.apps.android.baseClasses.domain

import kotlinx.coroutines.flow.Flow


interface BaseUseCase <T, N> {

    abstract fun execute(params: T): Flow<N>

}