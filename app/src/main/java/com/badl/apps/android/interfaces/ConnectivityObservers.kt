package com.badl.apps.android.interfaces

import kotlinx.coroutines.flow.Flow

interface ConnectivityObservers {

    fun  observe(): Flow<Status>

    enum class Status {

        available, unavailable, losing, lost
    }
}