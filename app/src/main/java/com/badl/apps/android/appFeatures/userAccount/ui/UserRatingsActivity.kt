package com.badl.apps.android.appFeatures.userAccount.ui

import android.os.Bundle
import com.badl.apps.android.R
import com.badl.apps.android.adapters.BiddingPagerAdapter
import com.badl.apps.android.baseClasses.ui.BaseActivity
import com.badl.apps.android.databinding.ActivityUserRatingsBinding
import com.badl.apps.android.utils.UiUtils.initializeMainToolBar
import com.google.android.material.tabs.TabLayoutMediator

class UserRatingsActivity : BaseActivity() {
    private lateinit var binding: ActivityUserRatingsBinding
    private lateinit var tabLayoutMediator: TabLayoutMediator
    private lateinit var pagerAdapter: BiddingPagerAdapter
    private lateinit var ratingsAsSeller: UserRatingFragment
    private lateinit var ratingsAsBuyer: UserRatingFragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserRatingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeMainToolBar(getString(R.string.ratings))
        initViews()
    }

    override fun initViews() {

        binding.tabLayoutUserRatings.let { mTabLayout ->

            binding.viewPager.let { mViewPager ->

                tabLayoutMediator = TabLayoutMediator(
                    mTabLayout,
                    mViewPager
                ) { tab, position ->

                    when (position) {

                        0 -> {

                            tab.text = getString(R.string.as_seller)
                        }

                        1 -> {

                            tab.text = getString(R.string.as_buyer)

                        }
                    }
                }
            }
        }


        ratingsAsSeller = UserRatingFragment()
        ratingsAsSeller.type = 1
        ratingsAsBuyer = UserRatingFragment()
        ratingsAsBuyer.type = 2
        pagerAdapter = BiddingPagerAdapter(arrayListOf(ratingsAsSeller, ratingsAsBuyer), supportFragmentManager, lifecycle)

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