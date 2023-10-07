package com.badl.apps.android.appFeatures.biddingAndBarterSystem.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import com.badl.apps.android.R
import com.badl.apps.android.adapters.BiddingHistoryAdapter
import com.badl.apps.android.appFeatures.biddingAndBarterSystem.data.BidItem
import com.badl.apps.android.appFeatures.chat.ui.ChatActivity
import com.badl.apps.android.appFeatures.userAccount.ui.UserAccountViewModel
import com.badl.apps.android.baseClasses.ui.BaseActivity
import com.badl.apps.android.databinding.ActivityBiddingHistoryBinding
import com.badl.apps.android.network.ApiConstants
import com.badl.apps.android.utils.AppUtil.collectFlow
import com.badl.apps.android.utils.AppUtil.getIntentExtension
import com.badl.apps.android.utils.Constants
import com.badl.apps.android.utils.NetworkUtils.handelApiResult
import com.badl.apps.android.utils.UiUtils.initializeMainToolBar
import com.badl.apps.android.utils.UiUtils.setImage
import com.badl.apps.android.utils.UiUtils.showCustomToast
import com.badl.apps.android.utils.UiUtils.showView
import com.google.android.gms.common.api.Api

class BiddingHistoryActivity : BaseActivity() {
    private lateinit var binding: ActivityBiddingHistoryBinding
    private lateinit var biddingHistoryAdapter: BiddingHistoryAdapter
    private val mViewModel by viewModels<BiddingBarterViewModel>()
    private var from = ""
    private var adId= -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBiddingHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeMainToolBar(getString(R.string.bidding))

        if (intent.hasExtra(Constants.FROM)) {

            from = intent.getStringExtra(Constants.FROM).toString()

        }


        if (intent.hasExtra(Constants.AD_ID)) {

            adId = intent.getIntExtra(Constants.AD_ID, -1)

        }


        initAdapters()
        initViews()
        initListeners()

        getSellerAdBids(adId)
    }

    override fun initViews() {

        binding.recListOfBidding.adapter = biddingHistoryAdapter

        binding.btnAcceptHighestBid.showView(from == Constants.FROM_AD_DETAILS)

    }

    override fun initData() {
        // nothing to do
    }

    override fun initAdapters() {

        biddingHistoryAdapter = BiddingHistoryAdapter(this, from, ::clickAction)
    }

    override fun initObservers() {
        // nothing to do
    }

    override fun initListeners() {

        binding.btnAcceptHighestBid.setOnClickListener {

            if (biddingHistoryAdapter.itemCount > 0) {

                val params = HashMap<String, String>()
                params[ApiConstants.PAR_ORDER_ID] = biddingHistoryAdapter.getItem(0).id.toString()
                params[ApiConstants.PAR_TYPE] = 1.toString()
                acceptRejectOrder(params)

            } else {

                showCustomToast(getString(R.string.nobody_has_made_a_bid_yet), Constants.TOAST_INFO)
            }
        }
    }

    private fun getSellerAdBids(adId: Int) {

        collectFlow(mViewModel.getSellerAdBids(adId)) {

            handelApiResult(it,
                onResultSuccess = {

                    it.resultData?.data?.let { data ->

                        biddingHistoryAdapter.setData(data.bids as ArrayList)
                        binding.tvBiddingHistoryBiddingCount.text = data.bids_count.toString()
                        binding.tvBiddingHistoryBiddersCount.text = data.bidders_count.toString()
                        binding.tvBiddingHistoryHighestBid.text = data.highest_bid.toString()
                        binding.tvProductName.text = data.ad_title.toString()
                    }
                })
        }
    }

    private fun acceptRejectOrder(params: HashMap<String, String>) {

        collectFlow(mViewModel.acceptRejectOrder(params)) {

            handelApiResult(it,
                onResultSuccess = {

                    it.resultData?.message?.let { data ->

                        showCustomToast(message = data, Constants.TOAST_DONE)
                        finish()
                    }
                })
        }
    }

    private fun clickAction(item: BidItem) {


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

        }
        else {

            intent.putExtra(Constants.FROM, Constants.FROM_AD_DETAILS)
            startActivity(intent)

        }
    }
}