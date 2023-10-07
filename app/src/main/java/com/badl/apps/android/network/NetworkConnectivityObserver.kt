package com.badl.apps.android.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import com.badl.apps.android.interfaces.ConnectivityObservers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

class NetworkConnectivityObserver(private val context: Context): ConnectivityObservers {

    private val mConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager


    override fun observe(): Flow<ConnectivityObservers.Status> {

        return callbackFlow {

            val callBack = object : ConnectivityManager.NetworkCallback() {

                override fun onAvailable(network: Network) {
                    super.onAvailable(network)

                    launch { send(ConnectivityObservers.Status.available) }

                }

                override fun onLosing(network: Network, maxMsToLive: Int) {
                    super.onLosing(network, maxMsToLive)

                    launch { send(ConnectivityObservers.Status.losing) }
                }

                override fun onLost(network: Network) {
                    super.onLost(network)

                    launch { send(ConnectivityObservers.Status.lost) }
                }

                override fun onUnavailable() {
                    super.onUnavailable()

                    launch { send(ConnectivityObservers.Status.unavailable) }
                }
            }

            awaitClose {  }
        }
    }
}