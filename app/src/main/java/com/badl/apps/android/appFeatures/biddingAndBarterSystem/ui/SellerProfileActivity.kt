package com.badl.apps.android.appFeatures.biddingAndBarterSystem.ui

import android.os.Bundle
import androidx.activity.viewModels
import com.badl.apps.android.R
import com.badl.apps.android.adapters.SellerActiveAdsAdapter
import com.badl.apps.android.appFeatures.biddingAndBarterSystem.data.SellerAdItem
import com.badl.apps.android.baseClasses.ui.BaseActivity
import com.badl.apps.android.databinding.ActivitySellerProfileBinding
import com.badl.apps.android.network.ApiConstants
import com.badl.apps.android.utils.AppUtil.collectFlow
import com.badl.apps.android.utils.AppUtil.getIntentExtension
import com.badl.apps.android.utils.AppUtil.navToActivity
import com.badl.apps.android.utils.Constants
import com.badl.apps.android.utils.NetworkUtils.handelApiResult
import com.badl.apps.android.utils.UiUtils.addIndicatorsTabs
import com.badl.apps.android.utils.UiUtils.getScreenDensity
import com.badl.apps.android.utils.UiUtils.initializeMainToolBar
import com.badl.apps.android.utils.UiUtils.setImage
import com.badl.apps.android.utils.UiUtils.setTabMargins
import com.badl.apps.android.utils.UiUtils.showView
import com.badl.apps.android.utils.ValidationUtils.isUserLogin

class SellerProfileActivity : BaseActivity() {
    private lateinit var binding: ActivitySellerProfileBinding
    private lateinit var sellerActiveAdsAdapter: SellerActiveAdsAdapter
    private val mViewModel by viewModels<BiddingBarterViewModel>()
    private var mUserId = -1
    private val favData = HashMap<String, String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySellerProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeMainToolBar(getString(R.string.seller_profile))

        initAdapters()
        initViews()
        initListeners()

        if (intent.hasExtra(Constants.USER_ID)) {

            mUserId = intent.getIntExtra(Constants.USER_ID, -1)

        }

        getSellerAds(mUserId)

    }


    override fun initViews() {

        binding.recListOfAds.adapter = sellerActiveAdsAdapter

    }

    override fun initData() {
        // nothing to do
    }

    override fun initAdapters() {

        sellerActiveAdsAdapter = SellerActiveAdsAdapter(this, ::favAction)
        binding.myAdapter = sellerActiveAdsAdapter
    }

    override fun initObservers() {
        // nothing to do
    }

    override fun initListeners() {

        binding.tvSellerRate.setOnClickListener {

            getIntentExtension(SellerRatingActivity::class.java).run {
                putExtra(Constants.USER_ID, mUserId)

                startActivity(this)
            }
        }
    }

    private fun getSellerAds(userId: Int) {

        collectFlow(mViewModel.getSellerAds(userId)) {

            handelApiResult(it,
                onResultSuccess = {

                    it.resultData?.data?.let { userData ->

                        binding.imgSellerImg.setImage(userData.user_details?.user_image)
                        binding.tvSellerName.text = userData.user_details?.user_name
                        binding.tvMemberSince.text = userData.user_details?.member_since

                        binding.tvSellerRate.text = getString(
                            R.string.holder_ad_ratings,
                            userData.user_details?.rates_count.toString(), userData.user_details?.rate.toString()
                        )


                        sellerActiveAdsAdapter.setData(userData.ads as ArrayList)
                    }
                })
        }
    }


    private fun favAction(
        position: Int,
        item: SellerAdItem
    ) {


        if (isUserLogin(true)) {

            favData[ApiConstants.PAR_AD_ID] = item.id.toString()
            favProduct(position, favData)
        }
    }


    private fun favProduct(
        itemPosition: Int, favData: HashMap<String, String>
    ) {

        collectFlow(mViewModel.favoriteAd(favData)) {

            handelApiResult(it, onLoading = {
                sellerActiveAdsAdapter.getItem(itemPosition).is_loading = true
                sellerActiveAdsAdapter.refreshItem(itemPosition)

            },
                onResultSuccess = {

                    it.resultData?.data?.let {

                        sellerActiveAdsAdapter.getItem(itemPosition).is_favorite = !(sellerActiveAdsAdapter.getItem(itemPosition).is_favorite ?: false)
                        sellerActiveAdsAdapter.getItem(itemPosition).is_loading = false
                        sellerActiveAdsAdapter.refreshItem(itemPosition)
                    }

                }, onResultFail = {
                    sellerActiveAdsAdapter.getItem(itemPosition).is_loading = false
                    sellerActiveAdsAdapter.refreshItem(itemPosition)
                })
        }
    }
}