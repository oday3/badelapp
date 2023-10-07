package com.badl.apps.android.appFeatures.biddingAndBarterSystem.ui

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.activity.viewModels
import com.badl.apps.android.R
import com.badl.apps.android.appFeatures.addAd.ui.AdsViewModel
import com.badl.apps.android.appFeatures.biddingAndBarterSystem.data.SwapOrderDetailsItem
import com.badl.apps.android.appFeatures.chat.ui.ChatActivity
import com.badl.apps.android.baseClasses.ui.BaseActivity
import com.badl.apps.android.databinding.ActivityBarterOfferBinding
import com.badl.apps.android.network.ApiConstants
import com.badl.apps.android.utils.AppUtil.collectFlow
import com.badl.apps.android.utils.AppUtil.getIntentExtension
import com.badl.apps.android.utils.Constants
import com.badl.apps.android.utils.NetworkUtils.handelApiResult
import com.badl.apps.android.utils.UiUtils.initializeMainToolBar
import com.badl.apps.android.utils.UiUtils.setImage
import com.badl.apps.android.utils.UiUtils.showCustomToast
import com.badl.apps.android.utils.UiUtils.showView


class BarterOfferActivity : BaseActivity() {
    private lateinit var binding: ActivityBarterOfferBinding
    private var orderId = -1
    private val mViewModel by viewModels<AdsViewModel>()
    private val params = HashMap<String, String>()
    private var from = ""
    private lateinit var orderData: SwapOrderDetailsItem
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBarterOfferBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeMainToolBar(
            getString(R.string.order_proposal),
            showNotificationIcon = true,
            showChatIcon = true,
            chatAction = {

                if (orderData.has_chat == true) {



                    getIntentExtension(ChatActivity::class.java).run {

                        putExtra(Constants.FIREBASE_KEY, orderData.firebase_key.toString())
                        putExtra(Constants.FROM, Constants.FROM_CHAT)
                        putExtra(Constants.OWNER_IMAGE, orderData.user_image)
                        putExtra(Constants.AD_ID, orderData.ad_id)
                        putExtra(Constants.OWNER_ID, sharedPrefUtils.getCurrentUserData()?.id)
                        putExtra(Constants.SEND_USER_ID, orderData.user_id)
                        startActivity(this)
                    }
                }
                else {


                    getIntentExtension(ChatActivity::class.java).run {

                        putExtra(Constants.AD_ID, orderData.ad_id)
                        putExtra(Constants.OWNER_ID, sharedPrefUtils.getCurrentUserData()?.id)
                        putExtra(Constants.SEND_USER_ID, orderData.user_id)
                        putExtra(Constants.OWNER_IMAGE, orderData.user_image)
                        putExtra(Constants.FROM, Constants.FROM_AD_DETAILS)
                        startActivity(this)
                    }
                }
            })

        if (intent.hasExtra(Constants.ORDER_ID)) {

            orderId = intent.getIntExtra(Constants.ORDER_ID, -1)
        }

        if (intent.hasExtra(Constants.FROM)) {

            from = intent.getStringExtra(Constants.FROM).toString()
        }

        initViews()
        initListeners()
        swapOrderDetails(orderId.toString())

    }

    override fun initViews() {

        binding.btnAccept.showView(from != Constants.FROM_TRANSACTION_LIST)
        binding.btnReject.showView(from != Constants.FROM_TRANSACTION_LIST)


    }

    override fun initData() {
        // nothing to do
    }

    override fun initAdapters() {
        // nothing to do
    }

    override fun initObservers() {
        // nothing to do
    }

    override fun initListeners() {

        binding.btnAccept.setOnClickListener {

            params[ApiConstants.PAR_ORDER_ID] = orderId.toString()
            params[ApiConstants.PAR_TYPE] = 1.toString()

            acceptRejectOrder(params)
        }

        binding.btnReject.setOnClickListener {

            params[ApiConstants.PAR_ORDER_ID] = orderId.toString()
            params[ApiConstants.PAR_TYPE] = 2.toString()

            acceptRejectOrder(params)
        }
    }

    private fun swapOrderDetails(
        orderId: String
    ) {

        collectFlow(mViewModel.swapOrderDetails(orderId)) {

            // binding?.swipeLayout?.isRefreshing = false
            // binding?.recSearchResultList?.showView(false)

            handelApiResult(it,
                onResultSuccess = {

                    it.resultData?.data?.let { orderData ->

                        this.orderData = orderData

                        Log.e("aerfa", orderData.firebase_key.toString())
                        binding.tvAdditionalPrice.showView(!TextUtils.isEmpty(orderData.additional_price))

                        binding.tvAdditionalPrice.text = orderData.additional_price

                        binding.imgOfferProductImg.setImage(orderData.ad?.image)
                        binding.tvProductOfferName.text = orderData.ad?.title
                        binding.tvTvProductOfferDesc.text = orderData.ad?.description

                        binding.imgProductToSwapImg.setImage(orderData.exchange_ad?.image)
                        binding.tvProductToSwapName.text = orderData.exchange_ad?.title
                        binding.tvProductToSwapDesc.text = orderData.exchange_ad?.description
                    }
                })
        }
    }

    private fun acceptRejectOrder(
        params: HashMap<String, String>
    ) {

        collectFlow(mViewModel.acceptRejectOrder(params)) {

            // binding?.swipeLayout?.isRefreshing = false
            // binding?.recSearchResultList?.showView(false)

            handelApiResult(it,
                onResultSuccess = {

                    it.resultData?.message?.let {


                        showCustomToast(it, Constants.TOAST_DONE)
                        finish()
                    }
                })
        }
    }


//    private fun favProduct(
//        adapter: ProductSectionAdapter,
//        itemPosition: Int, favData: HashMap<String, String>
//    ) {
//
//        collectFlow(mViewModel.favoriteProduct(favData)) {
//
//            (requireActivity() as BaseActivity).handelApiResult(it, onLoading = {
//                adapter.getItem(itemPosition).is_loading = true
//                adapter.refreshItem(itemPosition)
//
//            },
//                onResultSuccess = {
//
//                    it.resultData?.data?.let {
//
//                        adapter.getItem(itemPosition).is_favorite = !(adapter.getItem(itemPosition).is_favorite ?: false)
//                        adapter.getItem(itemPosition).is_loading = false
//                        adapter.refreshItem(itemPosition)
//                    }
//
//                }, onResultFail = {
//                    adapter.getItem(itemPosition).is_loading = false
//                    adapter.refreshItem(itemPosition)
//                })
//        }
//    }

//    operator fun invoke () {
//
//    }
}