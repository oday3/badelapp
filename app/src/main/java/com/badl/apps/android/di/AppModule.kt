package com.badl.apps.android.di

import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.util.Log
import com.badl.apps.android.network.ApiConstants
import com.badl.apps.android.network.AppEndpoints
import com.badl.apps.android.utils.SharedPrefUtils
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSharedPrefUtils(@ApplicationContext context: Context): SharedPrefUtils {
        return SharedPrefUtils(context)
    }


    @Provides
    @Singleton
    fun provideNotificationManager(@ApplicationContext context: Context): NotificationManager {
        return context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    }


    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        @ApplicationContext context:
        Context, moshi: Moshi,
        sharedPrefUtils: SharedPrefUtils, httpLoggingInterceptor: HttpLoggingInterceptor
    ): Retrofit {

        return Retrofit.Builder()
            .baseUrl(ApiConstants.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(
                OkHttpClient.Builder().addNetworkInterceptor { chain ->
                    val builder = chain.request().newBuilder()

                    builder.addHeader("Accept", "application/json")
                    sharedPrefUtils.appLang?.let {

                        builder.addHeader("Accept-Language", it)
                    }



                    sharedPrefUtils.getCurrentUserData()?.let { userData ->
                        userData.getBearerToken().let {
                            Log.e("user_token", it)
                            builder.addHeader(
                                "Authorization", it
                            )
                        }
                    }


                    val request = builder.build()

                    chain.proceed(request)
                }
                   // .addInterceptor(httpLoggingInterceptor)
                    .readTimeout(4, TimeUnit.MINUTES)
                    .connectTimeout(4, TimeUnit.MINUTES)
                    .writeTimeout(4, TimeUnit.MINUTES)
                    .build()
            ).build()
    }

    @Provides
    @Singleton
    fun provideAppEndpoints(retrofit: Retrofit): AppEndpoints {

        return retrofit.create(AppEndpoints::class.java)
    }


    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {

        val httpLoggingInterceptor =
            HttpLoggingInterceptor { message: String -> Log.d("API_REQUEST_LOG", "log:: = $message") }
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        return httpLoggingInterceptor
    }
}