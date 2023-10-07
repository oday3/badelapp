package com.badl.apps.android.appFeatures.biddingAndBarterSystem.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import com.badl.apps.android.R
import com.badl.apps.android.adapters.PurchaseOrdersAdapter
import com.badl.apps.android.appFeatures.addAd.ui.AdsViewModel
import com.badl.apps.android.appFeatures.biddingAndBarterSystem.data.AdOrderItem
import com.badl.apps.android.appFeatures.chat.ui.ChatActivity
import com.badl.apps.android.baseClasses.ui.BaseActivity
import com.badl.apps.android.databinding.ActivityAdOrdersBinding
import com.badl.apps.android.firebaseMessage.FCMMessagingService
import com.badl.apps.android.network.ApiConstants
import com.badl.apps.android.utils.AppUtil.collectFlow
import com.badl.apps.android.utils.AppUtil.getIntentExtension
import com.badl.apps.android.utils.Constants
import com.badl.apps.android.utils.NetworkUtils.handelApiResult
import com.badl.apps.android.utils.UiUtils.initializeMainToolBar
import com.badl.apps.android.utils.UiUtils.showCustomToast

class AdOrdersActivity : BaseActivity() {
    private lateinit var binding: ActivityAdOrdersBinding
    private lateinit var ordersAdapter: PurchaseOrdersAdapter
    private var from = -1
    private val mViewModel by viewModels<AdsViewModel>()

    private var adId = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdOrdersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeMainToolBar(getString(R.string.order_proposal), showNotificationIcon = true)


        if (intent.hasExtra(Constants.FROM)) {

            from = intent.getIntExtra(Constants.FROM, -1)

        }

        if (intent.hasExtra(Constants.AD_ID)) {

            adId = intent.getIntExtra(Constants.AD_ID, -1)

        }

        initAdapters()
        initViews()
        initListeners()
        initObservers()

        getAdOrders(adId.toString())
    }

    override fun initViews() {

        binding.recListOfAdOrders.adapter = ordersAdapter
    }

    override fun initData() {
        // nothing to do
    }

    override fun initAdapters() {

        ordersAdapter = PurchaseOrdersAdapter(this, from, ::acceptRejectAction, ::chatAction)
    }

    override fun initObservers() {

        FCMMessagingService.actionNotifier.observe(this) {

            Log.e("aysfd", it[Constants.FROM].toString())

            if (it[Constants.FROM] == Constants.NOTIFICATION_TYPE_ORDER) {

                getAdOrders(adId.toString())

                FCMMessagingService.actionNotifier.value = HashMap<String, String>()
            }
        }
    }

    override fun initListeners() {
        // nothing to do
    }

    private fun getAdOrders(adId: String) {

        collectFlow(mViewModel.getAdOrders(adId)) {

            handelApiResult(it,
                onResultSuccess = {

                    it.resultData?.data?.let { data ->

//                        if (from == 1) {
//
//                            binding.tvPurchaseOrdersCount.text = getString(R.string.holder_charitable_orders, data.orders.size.toString())
//
//                        } else {
//
//                            binding.tvPurchaseOrdersCount.text = getString(R.string.holder_purchase_orders, data.orders.size.toString())
//
//                        }

                        binding.tvAdOrdersCount.text = data.ad_description
                        ordersAdapter.setData(data.orders as ArrayList)
                    }
                })
        }
    }


    private fun acceptRejectAction(position: Int, type: Int) {

        val params = HashMap<String, String>()

        ordersAdapter.getItem(position).let {

            params[ApiConstants.PAR_ORDER_ID] = it.id.toString()
            params[ApiConstants.PAR_TYPE] = type.toString()

            acceptRejectOrder(params, position, type)
        }

    }


    private fun chatAction(item: AdOrderItem) {


        val intent = getIntentExtension(ChatActivity::class.java).run {

            putExtra(Constants.OWNER_IMAGE, item.user_image)
            putExtra(Constants.AD_ID, item.ad_id)
            putExtra(Constants.OWNER_ID, sharedPrefUtils.getCurrentUserData()?.id)
            putExtra(Constants.SEND_USER_ID, item.user_id)

        }

        if (item.has_chat == true) {

            intent.putExtra(Constants.FIREBASE_KEY, item.firebase_key.toString())
            intent.putExtra(Constants.FROM, Constants.FROM_CHAT)
            startActivity(intent)

        } else {


            intent.putExtra(Constants.FROM, Constants.FROM_AD_DETAILS)
            startActivity(intent)

        }
    }

    private fun acceptRejectOrder(params: HashMap<String, String>, position: Int, type: Int) {

        collectFlow(mViewModel.acceptRejectOrder(params)) {

            handelApiResult(it,
                onResultSuccess = {

                    it.resultData?.message?.let {

                        showCustomToast(it, Constants.TOAST_DONE)

                        if (type == 1) {

                            finish()
                        } else {

                            ordersAdapter.deleteItem(position)
                        }
                    }
                })
        }
    }

    companion object {

        var isActivityActive = is_activity_active

    }

}