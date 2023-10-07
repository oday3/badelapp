package com.badl.apps.android.appFeatures.auth.data

import com.badl.apps.android.appFeatures.auth.domain.AuthRepository
import com.badl.apps.android.baseClasses.data.BaseRepository
import com.badl.apps.android.network.AppEndpoints

class AuthRepoImpl(apiClient: AppEndpoints) : AuthRepository, BaseRepository(apiClient) {


    override fun registerUser(data: Map<String, String>) =
        requestResultAsFlow({
            executeRequest {
                apiClient.registerUser(data)
            }
        })

    override fun verifyRegister(data: Map<String, String>) =
        requestResultAsFlow({
            executeRequest {
                apiClient.verifyRegister(data)
            }
        })

    override fun resendCode(data: Map<String, String>) =
        requestResultAsFlow({
            executeRequest {
                apiClient.resendCode(data)
            }
        })

    override fun loginUser(data: Map<String, String>) =
        requestResultAsFlow({
            executeRequest {
                apiClient.loginUser(data)
            }
        })

    override fun forgotPassword(data: Map<String, String>) =
        requestResultAsFlow({
            executeRequest {
                apiClient.forgotPassword(data)
            }
        })


    override fun verifyResetForgotPassword(data: Map<String, String>) =
        requestResultAsFlow({
            executeRequest {
                apiClient.verifyResetForgotPassword(data)
            }
        })


    override fun resetPassword(data: Map<String, String>) =
        requestResultAsFlow({
            executeRequest {
                apiClient.resetPassword(data)
            }
        })


    override fun logout() =
        requestResultAsFlow({
            executeRequest {
                apiClient.logout()
            }
        })


    override fun socialLogin(data: Map<String, String>) =
        requestResultAsFlow({
            executeRequest {
                apiClient.socialLogin(data)
            }
        })
}