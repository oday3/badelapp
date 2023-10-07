package com.badl.apps.android.network

import com.badl.apps.android.appFeatures.userAccount.data.AdDetailsItem
import com.badl.apps.android.appFeatures.userAccount.data.UserAdItem
import com.badl.apps.android.appFeatures.addAd.data.AdDataItem
import com.badl.apps.android.appFeatures.appCommon.data.AppDataItem
import com.badl.apps.android.appFeatures.appCommon.data.NotificationItem
import com.badl.apps.android.appFeatures.appCommon.data.UserData
import com.badl.apps.android.appFeatures.biddingAndBarterSystem.data.AdOrderItem
import com.badl.apps.android.appFeatures.biddingAndBarterSystem.data.AdOrdersDataItem
import com.badl.apps.android.appFeatures.biddingAndBarterSystem.data.BiddingHistoryItem
import com.badl.apps.android.appFeatures.biddingAndBarterSystem.data.SellerInfoItem
import com.badl.apps.android.appFeatures.biddingAndBarterSystem.data.SellerRateItem
import com.badl.apps.android.appFeatures.biddingAndBarterSystem.data.SwapOrderDetailsItem
import com.badl.apps.android.appFeatures.chat.data.AddressItem
import com.badl.apps.android.appFeatures.chat.data.ChatsHistoryData
import com.badl.apps.android.appFeatures.chat.data.ChatDetailsItem
import com.badl.apps.android.appFeatures.main.data.FavoriteItem
import com.badl.apps.android.appFeatures.main.data.HomeDataItem
import com.badl.apps.android.appFeatures.main.data.ProductSectionItem
import com.badl.apps.android.appFeatures.main.data.SectionItem
import com.badl.apps.android.appFeatures.userAccount.data.OrderItem
import com.badl.apps.android.appFeatures.userAccount.data.SwapOrdersDataItem
import com.badl.apps.android.appFeatures.userAccount.data.TransactionItem
import com.badl.apps.android.appFeatures.userAccount.data.UserRateItem
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.PartMap
import retrofit2.http.Query
import retrofit2.http.QueryMap


interface AppEndpoints {

    // auth operations

    @POST(ApiConstants.REQ_REGISTER)
    suspend fun registerUser(@Body data: Map<String, String>):
            Response<BaseResponse<Any>>

    @POST(ApiConstants.REQ_VERIFY_REGISTER)
    suspend fun verifyRegister(@Body data: Map<String, String>):
            Response<BaseResponse<UserData>>

    @PUT(ApiConstants.REQ_RESEND_CODE)
    suspend fun resendCode(@QueryMap params: Map<String, String>):
            Response<BaseResponse<Any>>

    @POST(ApiConstants.REQ_LOGIN)
    suspend fun loginUser(@Body data: Map<String, String>):
            Response<BaseResponse<UserData>>

    @POST(ApiConstants.REQ_FORGOT_PASSWORD)
    suspend fun forgotPassword(@Body data: Map<String, String>):
            Response<BaseResponse<Any>>

    @POST(ApiConstants.REQ_VERIFY_RESET_FORGOT_PASS)
    suspend fun verifyResetForgotPassword(@Body data: Map<String, String>):
            Response<BaseResponse<Int>>

    @POST(ApiConstants.REQ_RESET_PASSWORD)
    suspend fun resetPassword(@Body data: Map<String, String>):
            Response<BaseResponse<Any>>

    @GET(ApiConstants.REQ_LOGOUT)
    suspend fun logout(): Response<BaseResponse<Any>>


    @POST(ApiConstants.REQ_SOCIAL_LOGIN)
    suspend fun socialLogin(@Body data: Map<String, String>):
            Response<BaseResponse<UserData>>


    // home operations

    @GET(ApiConstants.REQ_HOME)
    suspend fun getHomeData(): Response<BaseResponse<HomeDataItem>>


    @GET(ApiConstants.REQ_SECTIONS)
    suspend fun getSections(): Response<BaseResponse<List<SectionItem>>>


    @GET(ApiConstants.REQ_GET_FAVORITES)
    suspend fun getFavorites():
            Response<BaseResponse<List<FavoriteItem>>>

    @GET(ApiConstants.REQ_SHOW_ALL)
    suspend fun showAll(@QueryMap params:HashMap<String, String>):
            Response<BaseResponse<List<ProductSectionItem>>>

    @GET(ApiConstants.REQ_SECTION_ADS)
    suspend fun getSectionAds(@QueryMap params:HashMap<String, String>):
            Response<BaseResponse<List<ProductSectionItem>>>

    @GET(ApiConstants.REQ_ORDERS)
    suspend fun getUserOrders(@QueryMap params:HashMap<String, String>): Response<BaseResponse<List<OrderItem>>>


    @GET(ApiConstants.REQ_HOME_SEARCH)
    suspend fun homeSearch(@QueryMap params:HashMap<String, String>): Response<BaseResponse<List<ProductSectionItem>>>

    // user account operations

    @GET(ApiConstants.REQ_GET_PROFILE)
    suspend fun getProfile(): Response<BaseResponse<UserData>>

    @Multipart
    @JvmSuppressWildcards
    @POST(ApiConstants.REQ_UPDATE_PROFILE)
    suspend fun updateProfile(
        @PartMap updatedData: Map<String, RequestBody>,
        @Part updatedImage: MultipartBody.Part?
    ): Response<BaseResponse<UserData>>


    @PUT(ApiConstants.REQ_CHANGE_LANGUAGE)
    suspend fun changeLanguage(@Query(ApiConstants.PAR_LANG) lang: String): Response<BaseResponse<Any>>

    @GET(ApiConstants.REQ_USER_RATES)
    suspend fun getUserRates(@QueryMap params:HashMap<String, String>): Response<BaseResponse<List<UserRateItem>>>


    @GET(ApiConstants.REQ_MY_SEALS)
    suspend fun getUserSeals(@QueryMap params:HashMap<String, String>): Response<BaseResponse<List<TransactionItem>>>


    // ad operation

    @GET(ApiConstants.REQ_AD_DATA)
    suspend fun getAdData(): Response<BaseResponse<AdDataItem>>


    @POST(ApiConstants.REQ_FAVORITE)
    suspend fun favoriteProduct(@Body data: Map<String, String>):
            Response<BaseResponse<Any>>

    @Multipart
    @JvmSuppressWildcards
    @POST(ApiConstants.REQ_ADD_AD)
    suspend fun addAd(
        @PartMap updatedData: Map<String, RequestBody>,
        @Part updatedImage: List<MultipartBody.Part?>?
    ): Response<BaseResponse<Any>>


    @GET(ApiConstants.REQ_ADDS)
    suspend fun getAds(@QueryMap params:HashMap<String, String>): Response<BaseResponse<List<UserAdItem>>>

    @DELETE(ApiConstants.REQ_DELETE_AD)
    suspend fun deleteAd(@Query(ApiConstants.PAR_AD_ID) id: String): Response<BaseResponse<Any>>

    @GET(ApiConstants.REQ_AD_DETAILS)
    suspend fun getAdDetails(@Query(ApiConstants.PAR_AD_ID) id: String): Response<BaseResponse<AdDetailsItem>>

    @PUT(ApiConstants.REQ_UPDATE_AD_STATUS)
    suspend fun updateAdStatus(@QueryMap params:HashMap<String, String>): Response<BaseResponse<AdDetailsItem>>

    @GET(ApiConstants.REQ_ACCEPT_REJECT_ORDER)
    suspend fun acceptRejectOrder(@QueryMap params:HashMap<String, String>): Response<BaseResponse<Any>>


    @GET(ApiConstants.REQ_ORDER_DETAILS)
    suspend fun getOrderDetails(@Query(ApiConstants.PAR_ORDER_ID) id: String): Response<BaseResponse<AdDetailsItem>>

    @GET(ApiConstants.REQ_EXCHANGE_AD_ORDERS)
    suspend fun getSwapOrdersData(@Query(ApiConstants.PAR_AD_ID) id: String): Response<BaseResponse<SwapOrdersDataItem>>

    @GET(ApiConstants.REQ_EXCHANGE_ORDER_DETAILS)
    suspend fun swaOrderDetails(@Query(ApiConstants.PAR_ORDER_ID) id: String): Response<BaseResponse<SwapOrderDetailsItem>>


    @GET(ApiConstants.REQ_AD_ORDERS)
    suspend fun getAdOrders(@Query(ApiConstants.PAR_AD_ID) id: String): Response<BaseResponse<AdOrdersDataItem>>

    @Multipart
    @JvmSuppressWildcards
    @POST(ApiConstants.REQ_UPDATE_AD)
    suspend fun updateAd(
        @PartMap updatedData: Map<String, RequestBody>,
        @Part updatedImage: List<MultipartBody.Part?>?,
    ): Response<BaseResponse<Any>>


    // common app operation

    @GET(ApiConstants.REQ_TERMS)
    suspend fun getTerms(): Response<BaseResponse<AppDataItem>>

    @GET(ApiConstants.REQ_PRIVACY_POLICY)
    suspend fun getPrivacy(): Response<BaseResponse<AppDataItem>>

    @GET(ApiConstants.REQ_ABOUT_AD)
    suspend fun aboutApp(): Response<BaseResponse<AppDataItem>>

    @GET(ApiConstants.REQ_NOTIFICATIONS)
    suspend fun getNotifications(): Response<BaseResponse<List<NotificationItem>>>

    @DELETE(ApiConstants.REQ_DELETE_NOTIFICATIONS)
    suspend fun deleteNotifications(@Query(ApiConstants.PAR_NOTIFICATION_ID) notificationId: Int): Response<BaseResponse<Any>>

    @POST(ApiConstants.REQ_RATE)
    suspend fun rate(@Body data:Map<String, String>): Response<BaseResponse<Any>>

    // bidding and barter operations


    @GET(ApiConstants.REQ_SELLER_ADS)
    suspend fun getSellerAds(@Query(ApiConstants.PAR_USER_ID) userId: Int): Response<BaseResponse<SellerInfoItem>>

    @GET(ApiConstants.REQ_SELLER_AD_BIDS)
    suspend fun getSellerAdBids(@Query(ApiConstants.PAR_AD_ID) adId: Int): Response<BaseResponse<BiddingHistoryItem>>

    @GET(ApiConstants.REQ_SELLER_RATES)
    suspend fun getSellerRates(@Query(ApiConstants.PAR_USER_ID) userId: Int): Response<BaseResponse<List<SellerRateItem>>>

    @JvmSuppressWildcards
    @POST(ApiConstants.REQ_ORDER)
    suspend fun order(@Body data: Map<String, String>): Response<BaseResponse<Any>>

    // chat operation

    @JvmSuppressWildcards
    @POST(ApiConstants.REQ_CREATE_CHAT)
    suspend fun createChat(@Body data: Map<String, String>): Response<BaseResponse<Any>>


    @GET(ApiConstants.REQ_CHATS)
    suspend fun getChats(@QueryMap params:HashMap<String, String>): Response<BaseResponse<ChatsHistoryData>>


    @GET(ApiConstants.REQ_CHAT_DETAILS)
    suspend fun getChatDetails(@QueryMap params:HashMap<String, String>): Response<BaseResponse<ChatDetailsItem>>


    @GET(ApiConstants.REQ_ADDRESSES)
    suspend fun getAddresses(): Response<BaseResponse<List<AddressItem>>>

    @POST(ApiConstants.REQ_ADD_ADDRESS)
    suspend fun addAddress(@Body data:Map<String, String>): Response<BaseResponse<Any>>

}