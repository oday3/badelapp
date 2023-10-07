package com.badl.apps.android.firebaseMessage

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.core.app.TaskStackBuilder
import androidx.lifecycle.MutableLiveData
import com.badl.apps.android.appFeatures.appCommon.ui.SplashActivity
import com.badl.apps.android.appFeatures.biddingAndBarterSystem.ui.AdOrdersActivity
import com.badl.apps.android.appFeatures.biddingAndBarterSystem.ui.ProductDetailsActivity
import com.badl.apps.android.appFeatures.chat.ui.ChatActivity
import com.badl.apps.android.appFeatures.userAccount.ui.MyAdsActivity
import com.badl.apps.android.utils.Constants
import com.badl.apps.android.utils.NotificationsUtils
import com.badl.apps.android.utils.SharedPrefUtils
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class   FCMMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var sharedPrefUtils: SharedPrefUtils

    @Inject
    lateinit var mNotificationManager: NotificationManager

    override fun onNewToken(s: String) {
        super.onNewToken(s)

        sharedPrefUtils.fcmDeviceToken = s

        Log.e("device_fcm_onNewToken", s)

    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)


           // Log.e("data__n_out", remoteMessage.notification.toString())
           // Log.e("data__out", remoteMessage.data.toString())


       // if (remoteMessage.data.isEmpty()) {

//            NotificationsUtils.sendNotification(
//                mNotificationManager,
//                remoteMessage.notification?.title.toString(),
//                remoteMessage.notification?.body.toString(),
//                applicationContext,
//                "general"
//            )
      //  }

        if (sharedPrefUtils.getCurrentUserData() != null) {


           // Log.e("data__n_in", remoteMessage.notification.toString())

            val notificationType = remoteMessage.data["type"].toString()
            val title = remoteMessage.data["title"].toString()
            val body = remoteMessage.data["body"].toString()
            val refID = remoteMessage.data["reference_id"].toString()
            val ownerID = remoteMessage.data["owner_id"]?.toInt()
            val adID = remoteMessage.data["ad_id"]?.toInt()
            val adType = remoteMessage.data["ad_type"]?.toInt()
            val userID = remoteMessage.data["user_id"]?.toInt()
            val ownerImg = remoteMessage.data["icon"]?.toString()
            val refTitle = remoteMessage.data["reference_title"].toString()
            val isActive = remoteMessage.data["is_active"].toString()
            Log.e("data__in", remoteMessage.data.toString())



            when (notificationType) {

                Constants.NOTIFICATION_TYPE_ADMIN -> {

                    val resultIntent = Intent(this, SplashActivity::class.java)

                    TaskStackBuilder.create(applicationContext).run {
                        // Add the intent, which inflates the back stack
                        addNextIntentWithParentStack(resultIntent)
                        // Get the PendingIntent containing the entire back stack
                        getPendingIntent(
                            0,
                            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                        )
                    }?.let {

                        NotificationsUtils.sendNotification(
                            mNotificationManager,
                            title,
                            body,
                            applicationContext,
                            pendingIntent = it,
                            "general"
                        )
                    }
                }

                Constants.NOTIFICATION_TYPE_NEW_AD -> {

                    val resultIntent = Intent(this, ProductDetailsActivity::class.java)

                    resultIntent.putExtra(Constants.ITEM_ID, refID.toInt())
                    resultIntent.putExtra(Constants.FROM, Constants.FROM_AD_DETAILS)
                    TaskStackBuilder.create(applicationContext).run {
                        // Add the intent, which inflates the back stack
                        addNextIntentWithParentStack(resultIntent)
                        // Get the PendingIntent containing the entire back stack
                        getPendingIntent(
                            1,
                            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                        )
                    }?.let {

                        NotificationsUtils.sendNotification(
                            mNotificationManager,
                            title,
                            body,
                            applicationContext,
                            pendingIntent = it,
                            "products"
                        )
                    }
                }

                Constants.NOTIFICATION_TYPE_ORDER -> {

                    if (AdOrdersActivity.isActivityActive ) {

                        Log.e("aysfd", " AdOrdersActivity active")

                        val hashMap = HashMap<String, String>()
                        hashMap[Constants.FROM] = Constants.NOTIFICATION_TYPE_ORDER
                        //hashMap[Constants.AD_ID] = adID.toString()

                        actionNotifier.postValue(hashMap)


                        NotificationsUtils.sendNotification(
                            mNotificationManager,
                            title,
                            body,
                            applicationContext,
                            "orders"
                        )
                    } else {

                        Log.e("aysfd", " AdOrdersActivity  not active")

                        when(adType) {

                            3 -> {

                                val resultIntent = Intent(this, AdOrdersActivity::class.java)

                                resultIntent.putExtra(Constants.AD_ID, adID?.toInt())
                                resultIntent.putExtra(Constants.FROM, Constants.FROM_BUY_ORDERS)

                                TaskStackBuilder.create(applicationContext).run {
                                    // Add the intent, which inflates the back stack
                                    addNextIntentWithParentStack(resultIntent)
                                    // Get the PendingIntent containing the entire back stack
                                    getPendingIntent(
                                        0,
                                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                                    )
                                }?.
                                let {

                                    NotificationsUtils.sendNotification(
                                        mNotificationManager,
                                        title,
                                        body,
                                        applicationContext,
                                        pendingIntent = it,
                                        "orders"
                                    )
                                }
                            }

                            4 -> {

                                val resultIntent = Intent(this, AdOrdersActivity::class.java)

                                resultIntent.putExtra(Constants.AD_ID, adID?.toInt())
                                resultIntent.putExtra(Constants.FROM, Constants.FROM_CHARITY_ORDERS)

                                TaskStackBuilder.create(applicationContext).run {
                                    // Add the intent, which inflates the back stack
                                    addNextIntentWithParentStack(resultIntent)
                                    // Get the PendingIntent containing the entire back stack
                                    getPendingIntent(
                                        0,
                                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                                    )
                                }?.
                                let {

                                    NotificationsUtils.sendNotification(
                                        mNotificationManager,
                                        title,
                                        body,
                                        applicationContext,
                                        pendingIntent = it,
                                        "orders"
                                    )
                                }
                            }

                            else -> {

                                val resultIntent = Intent(this, MyAdsActivity::class.java)


                                TaskStackBuilder.create(applicationContext).run {
                                    // Add the intent, which inflates the back stack
                                    addNextIntentWithParentStack(resultIntent)
                                    // Get the PendingIntent containing the entire back stack
                                    getPendingIntent(
                                        0,
                                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                                    )
                                }?.
                                let {

                                    NotificationsUtils.sendNotification(
                                        mNotificationManager,
                                        title,
                                        body,
                                        applicationContext,
                                        pendingIntent = it,
                                        "orders"
                                    )
                                }
                            }
                        }

                    }
                }

                Constants.NOTIFICATION_NEW_MESSAGE -> {

                    if (!ChatActivity.isActivityActive) {


                        val resultIntent = Intent(this, ChatActivity::class.java)

                        resultIntent.putExtra(Constants.AD_ID, adID)
                        resultIntent.putExtra(Constants.OWNER_ID, ownerID)
                        resultIntent.putExtra(Constants.SEND_USER_ID, userID)
                        resultIntent.putExtra(Constants.OWNER_IMAGE,ownerImg)
                        resultIntent.putExtra(Constants.FROM, Constants.FROM_AD_DETAILS)

                        TaskStackBuilder.create(applicationContext).run {
                            // Add the intent, which inflates the back stack
                            addNextIntentWithParentStack(resultIntent)
                            // Get the PendingIntent containing the entire back stack
                            getPendingIntent(
                                0,
                                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                            )
                        }?.let {

                            NotificationsUtils.sendNotification(
                                mNotificationManager,
                                title,
                                body,
                                applicationContext,
                                pendingIntent = it,
                                "orders"
                            )
                        }
                    }
                }

                else -> {


                        val resultIntent = Intent(this, SplashActivity::class.java)

                        TaskStackBuilder.create(applicationContext).run {
                            // Add the intent, which inflates the back stack
                            addNextIntentWithParentStack(resultIntent)
                            // Get the PendingIntent containing the entire back stack
                            getPendingIntent(
                                0,
                                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                            )
                        }?.let {

                            NotificationsUtils.sendNotification(
                                mNotificationManager,
                                title,
                                body,
                                applicationContext,
                                pendingIntent = it,
                                "general"
                            )
                        }
                    }
            }
        } else {

            Log.e("data__", "no user")

        }

    }


    companion object {

        var actionNotifier = MutableLiveData<HashMap<String, String>>()
    }



}