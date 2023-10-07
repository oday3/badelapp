package com.badl.apps.android.appFeatures.biddingAndBarterSystem.ui

import android.os.Bundle
import androidx.activity.viewModels
import com.badl.apps.android.R
import com.badl.apps.android.adapters.SellerRatingsAdapter
import com.badl.apps.android.baseClasses.ui.BaseActivity
import com.badl.apps.android.databinding.ActivitySellerRatingBinding
import com.badl.apps.android.utils.AppUtil.collectFlow
import com.badl.apps.android.utils.Constants
import com.badl.apps.android.utils.NetworkUtils.handelApiResult
import com.badl.apps.android.utils.UiUtils.initializeMainToolBar
import com.badl.apps.android.utils.UiUtils.setImage

class SellerRatingActivity : BaseActivity() {
    private lateinit var binding: ActivitySellerRatingBinding
    private lateinit var ratingsAdapter: SellerRatingsAdapter
    private var mUserId = -1
    private val mViewModel by viewModels<BiddingBarterViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySellerRatingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeMainToolBar(getString(R.string.seller_ratings))

        initAdapters()
        initViews()

        if (intent.hasExtra(Constants.USER_ID)) {

            mUserId = intent.getIntExtra(Constants.USER_ID, -1)

        }

        getSellerRatings(mUserId)
    }

    private fun getSellerRatings(mUserId: Int) {


        collectFlow(mViewModel.getSellerRates(mUserId)) {

            handelApiResult(it,
                onResultSuccess = {

                    it.resultData?.data?.let { userRatings ->

                        ratingsAdapter.setData(userRatings)

                    }
                })
        }

    }

    override fun initViews() {

        binding.recListOfRatings.adapter = ratingsAdapter
    }

    override fun initData() {
        // nothing to do
    }

    override fun initAdapters() {

        ratingsAdapter = SellerRatingsAdapter(this)
    }

    override fun initObservers() {
        // nothing to do
    }

    override fun initListeners() {
        // nothing to do
    }
}