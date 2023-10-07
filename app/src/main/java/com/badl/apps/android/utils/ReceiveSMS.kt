package com.badl.apps.android.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import androidx.lifecycle.MutableLiveData

class ReceiveSMS: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        if(Telephony.Sms.Intents.SMS_RECEIVED_ACTION == intent?.action) {

            for (item in Telephony.Sms.Intents.getMessagesFromIntent(intent)) {

                item?.let {


                    val data = HashMap<String, String>()

                    data[Constants.RESULT_DATA] = it.messageBody

                    actionNotifier.value = data
                }
            }
        }
    }

    companion object {

        var actionNotifier = MutableLiveData<HashMap<String, String>>()
    }
}