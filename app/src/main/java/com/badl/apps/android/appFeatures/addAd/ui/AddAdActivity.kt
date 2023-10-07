package com.badl.apps.android.appFeatures.addAd.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.badl.apps.android.R
import com.badl.apps.android.appFeatures.addAd.data.AdDataItem
import com.badl.apps.android.baseClasses.ui.BaseActivity
import com.badl.apps.android.databinding.ActivityAddAdBinding
import com.badl.apps.android.utils.AppUtil.collectFlow
import com.badl.apps.android.utils.NetworkUtils.handelApiResult
import com.badl.apps.android.utils.UiUtils.initializeMainToolBar
import com.badl.apps.android.utils.UiUtils.showView

class AddAdActivity : BaseActivity() {
    private lateinit var binding: ActivityAddAdBinding
    private lateinit var addAdProducFragment: AddAdProducFragment
    private lateinit var addMainAdDetailsFragment: AddMainAdDetailsFragment
    private lateinit var mSupportFragmentManager: FragmentManager
    private lateinit var mAdDataItem: AdDataItem
    private val _mAdDataItem = MutableLiveData<AdDataItem>()
    public val adData: LiveData<AdDataItem> = _mAdDataItem
    var mAdType = 0
    var mAdName = ""
    var mAdProductStatus = 0
    var mAdPrice = ""
    var mProductQuantity = ""
    var mAdDuration = 0
    var mAdAutoRepublishTimes = 0

    private val mViewModel by viewModels<AdsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddAdBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeMainToolBar(getString(R.string.new_ad), backAction = {

            if (addMainAdDetailsFragment.isHidden) {

                replaceFragment(addMainAdDetailsFragment, "homeFrag", true)


            } else {

                finish()

            }
        })

        initViews()

        getAdData()

    }

    override fun initViews() {

        addAdProducFragment = AddAdProducFragment()
        addMainAdDetailsFragment = AddMainAdDetailsFragment()

        replaceFragment(addMainAdDetailsFragment, "homeFrag", true)
        binding.toolbarAddAdActivity.imgMainToolbarBack.showView(true)

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
        // nothing to do
    }

    private fun replaceFragment(fragment: Fragment, tag: String, firstLoad: Boolean) {

        val fragmentManager = supportFragmentManager

        for (fragment1 in fragmentManager.fragments) {
            fragmentManager.beginTransaction().hide(fragment1).commit()
        }

        if (!fragment.isAdded) {
            fragmentManager.beginTransaction().add(R.id.container, fragment, tag).commit()
        } else {
            fragmentManager.beginTransaction().show(fragment).commit()
        }
    }


    fun navToProductAdDetails() {

        replaceFragment(addAdProducFragment, "homeFrag", true)

    }

    private fun getAdData() {

        collectFlow(mViewModel.getAdData()) {

            // binding?.swipeLayout?.isRefreshing = false
            // binding?.recSearchResultList?.showView(false)

            handelApiResult(it,
                onResultSuccess = {

                    it.resultData?.data?.let { data ->

                        _mAdDataItem.value = data
                    }
                })
        }
    }

    fun setFirstStepData(
        adType: Int,
        adName: String,
        adProductStatus: Int,
        adPrice: String,
        productQuantity: String = "",
        adDuration: Int,
        adAutoRepublish: Int
    ) {

        this.mAdType = adType
        this.mAdName = adName
        this.mAdProductStatus = adProductStatus
        this.mAdPrice = adPrice
        this.mProductQuantity = productQuantity
        this.mAdDuration = adDuration
        this.mAdAutoRepublishTimes = adAutoRepublish

    }
}