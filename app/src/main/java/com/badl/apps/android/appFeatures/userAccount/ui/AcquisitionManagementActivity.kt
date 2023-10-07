package com.badl.apps.android.appFeatures.userAccount.ui

import android.os.Bundle
import com.badl.apps.android.R
import com.badl.apps.android.adapters.BiddingPagerAdapter
import com.badl.apps.android.baseClasses.ui.BaseActivity
import com.badl.apps.android.databinding.ActivityAcquisitionManagmentBinding
import com.badl.apps.android.utils.UiUtils.initializeMainToolBar
import com.google.android.material.tabs.TabLayoutMediator

class AcquisitionManagementActivity : BaseActivity() {
    private lateinit var binding: ActivityAcquisitionManagmentBinding
    private lateinit var tabLayoutMediator: TabLayoutMediator
    private lateinit var pagerAdapter: BiddingPagerAdapter
    private lateinit var adsFragment: UserAdsFragment
    private lateinit var ordersFragment: OrdersFragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAcquisitionManagmentBinding.inflate(layoutInflater)
        setContentView(binding.root)

            initializeMainToolBar(getString(R.string.acquisition_management))

        initViews()
    }

    override fun initViews() {

        binding.tabLayoutAcquisitionManagement.let { mTabLayout ->

            binding.viewPager.let { mViewPager ->

                tabLayoutMediator = TabLayoutMediator(
                    mTabLayout,
                    mViewPager
                ) { tab, position ->

                    when (position) {

                        0 -> {

                            tab.text = getString(R.string.ads)
                        }

                        1 -> {

                            tab.text = getString(R.string.orders)
                        }
                    }
                }
            }
        }


        adsFragment = UserAdsFragment()
        ordersFragment = OrdersFragment()
        pagerAdapter = BiddingPagerAdapter(arrayListOf(adsFragment, ordersFragment), supportFragmentManager, lifecycle)

        binding.viewPager.adapter = pagerAdapter

        tabLayoutMediator.attach()
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
}