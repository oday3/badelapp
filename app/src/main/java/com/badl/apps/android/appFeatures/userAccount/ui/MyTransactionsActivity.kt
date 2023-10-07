package com.badl.apps.android.appFeatures.userAccount.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.content.res.AppCompatResources
import androidx.viewpager2.widget.ViewPager2
import com.badl.apps.android.R
import com.badl.apps.android.adapters.BiddingPagerAdapter
import com.badl.apps.android.adapters.FilterAdapter
import com.badl.apps.android.appFeatures.addAd.ui.AddAdActivity
import com.badl.apps.android.appFeatures.appCommon.data.FilterItem
import com.badl.apps.android.baseClasses.ui.BaseActivity
import com.badl.apps.android.databinding.ActivityMyTransactionsBinding
import com.badl.apps.android.databinding.BottomFilterProductsBinding
import com.badl.apps.android.utils.AppUtil.navToActivity
import com.badl.apps.android.utils.Constants
import com.badl.apps.android.utils.UiUtils
import com.badl.apps.android.utils.UiUtils.addSearchListener
import com.badl.apps.android.utils.UiUtils.extensionSetBackground
import com.badl.apps.android.utils.UiUtils.getScreenDensity
import com.badl.apps.android.utils.UiUtils.initializeMainToolBar
import com.badl.apps.android.utils.UiUtils.isTextEmpty
import com.badl.apps.android.utils.UiUtils.setFlowLayoutManager
import com.badl.apps.android.utils.UiUtils.showCustomToast
import com.badl.apps.android.utils.UiUtils.showView
import com.badl.apps.android.utils.ValidationUtils.isUserLogin
import com.badl.apps.android.utils.ValidationUtils.validateEmpty
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayoutMediator

class MyTransactionsActivity : BaseActivity() {

    private lateinit var binding: ActivityMyTransactionsBinding
    private lateinit var tabLayoutMediator: TabLayoutMediator
    private lateinit var pagerAdapter: BiddingPagerAdapter
    private lateinit var swapTransactionsFragment: SwapTransactionsFrag
    private lateinit var bidTransactionsFragment: BidTransactionsFrag
    private lateinit var buyTransactionsFragment: BuyTransactionsFrag
    private lateinit var charityTransactionsFragment: CharityTransactionsFrag
    private lateinit var filterAdapter: FilterAdapter

    private lateinit var filterProductBottomSheet: BottomSheetDialog
    private lateinit var filterProductBottomSheetBinding: BottomFilterProductsBinding

    private lateinit var pagerCallBack: ViewPager2.OnPageChangeCallback
    private var currentPagerPosition = 0
    var isFilterSelected = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyTransactionsBinding.inflate(layoutInflater)
        filterProductBottomSheetBinding = BottomFilterProductsBinding.inflate(layoutInflater)

        setContentView(binding.root)

        initializeMainToolBar(getString(R.string.my_transactions))

        initAdapters()
        initViews()
        initListeners()
    }

    override fun initViews() {

        binding.tabLayoutMyTransactionsActivity.let { mTabLayout ->

            binding.viewPager.let { mViewPager ->

                tabLayoutMediator = TabLayoutMediator(
                    mTabLayout,
                    mViewPager
                ) { tab, position ->

                    when (position) {

                        0 -> {

                            tab.text = getString(R.string.swap)
                        }

                        1 -> {

                            tab.text = getString(R.string.bid)
                        }

                        2 -> {

                            tab.text = getString(R.string.buy)
                        }

                        3 -> {

                            tab.text = getString(R.string.charity)
                        }


                    }
                }
            }
        }

        swapTransactionsFragment = SwapTransactionsFrag()
        bidTransactionsFragment = BidTransactionsFrag()
        buyTransactionsFragment = BuyTransactionsFrag()
        charityTransactionsFragment = CharityTransactionsFrag()

        pagerAdapter = BiddingPagerAdapter(arrayListOf(swapTransactionsFragment,
            bidTransactionsFragment, buyTransactionsFragment, charityTransactionsFragment), supportFragmentManager, lifecycle)

        binding.viewPager.adapter = pagerAdapter

        tabLayoutMediator.attach()


        filterProductBottomSheet =
            UiUtils.createBottomSheetDialog(
                this,
                -1,
                filterProductBottomSheetBinding.root,
                true
            )

        filterProductBottomSheetBinding.recBottomFilterProductsListOfFilters.setFlowLayoutManager()

        filterProductBottomSheetBinding.recBottomFilterProductsListOfFilters.adapter = filterAdapter


    }

    override fun initData() {
        // nothing to do
    }

    override fun initAdapters() {

        filterAdapter = createFilterAdapter()

    }

    override fun initObservers() {
        // nothing to do
    }

    override fun initListeners() {


        binding.imgFilter.setOnClickListener {

            filterProductBottomSheet.show()
        }

        pagerCallBack = object: ViewPager2.OnPageChangeCallback() {

            override fun onPageSelected(position: Int) {

                currentPagerPosition = position
                filterAdapter.clearSelectedFilter()
                isFilterSelected = false
                binding.edtMyTransactionsActivitySearch.setText("")
                binding.imgSearchAction.setImageResource(R.drawable.ic_search_main_color)
                binding.imgSearchAction.tag = "search"

                when(position) {

                    0 -> {

                    }

                    1 -> {

                    }

                    2 -> {

                    }

                    3 -> {

                    }
                }
            }
        }
        binding.viewPager.registerOnPageChangeCallback(pagerCallBack)

        filterProductBottomSheetBinding.btnApplyFilter.setOnClickListener {

            filterAdapter.getSelectedItem().let {

                it?.id?.let {

                    isFilterSelected = it != -1

                    when(currentPagerPosition) {

                        0 -> {

                            swapTransactionsFragment.executeFilterAction(it)

                        }

                        1 -> {


                            bidTransactionsFragment.executeFilterAction(it)
                        }

                        2 -> {

                            buyTransactionsFragment.executeFilterAction(it)
                        }

                        3 -> {

                            charityTransactionsFragment.executeFilterAction(it)
                        }
                    }

                    filterProductBottomSheet.dismiss()
                }
            }
        }

        binding.edtMyTransactionsActivitySearch.addSearchListener {

            if (it.isTextEmpty()) {

                showCustomToast(getString(R.string.please_enter_ad_name), Constants.TOAST_INFO)

                return@addSearchListener
            }

            binding.imgSearchAction.setImageResource(R.drawable.ic_close_fill)
            binding.imgSearchAction.tag = "cancel_search"
            executeSearchCommand(binding.edtMyTransactionsActivitySearch.text.toString())
        }

        binding.imgSearchAction.setOnClickListener {

            if (it.tag == "search") {

                if (validateEmpty(binding.edtMyTransactionsActivitySearch)) {

                    executeSearchCommand(binding.edtMyTransactionsActivitySearch.text.toString())

                    (it as ImageView).setImageResource(R.drawable.ic_close_fill)
                    it.tag = "cancel_search"
                }

            } else if (it.tag == "cancel_search") {

                binding.edtMyTransactionsActivitySearch.setText("")
                (it as ImageView).setImageResource(R.drawable.ic_search_main_color)
                it.tag = "search"
                clearSearchCommand()
            }
        }

    }

    fun resetUi() {


        filterAdapter.clearSelectedFilter()
        isFilterSelected = false
        binding.edtMyTransactionsActivitySearch.setText("")
        binding.imgSearchAction.setImageResource(R.drawable.ic_search_main_color)
        binding.imgSearchAction.tag = "search"
    }

    fun createFilterAdapter(): FilterAdapter {

        val filterAdapter = FilterAdapter(this, false, 0)

        filterAdapter.setData(
            arrayListOf(
               // FilterItem(-1, getString(R.string.all)),
                FilterItem(-1, getString(R.string.from_newest_to_oldest)),
                FilterItem(1, getString(R.string.from_oldest_to_newest)),
            )
        )

        return filterAdapter
    }


    private fun executeSearchCommand(query: String) {


        when(currentPagerPosition) {

            0 -> {

                swapTransactionsFragment.executeSearchAction(query)

            }

            1 -> {


                bidTransactionsFragment.executeSearchAction(query)
            }

            2 -> {

                buyTransactionsFragment.executeSearchAction(query)
            }

            3 -> {

                charityTransactionsFragment.executeSearchAction(query)
            }
        }


        isFilterSelected = false
        filterAdapter.clearSelectedFilter()

    }

    private fun clearSearchCommand() {


        when(currentPagerPosition) {

            0 -> {

                swapTransactionsFragment.clearSearchAction()

            }

            1 -> {


                bidTransactionsFragment.clearSearchAction()
            }

            2 -> {

                buyTransactionsFragment.clearSearchAction()
            }

            3 -> {

                charityTransactionsFragment.clearSearchAction()
            }
        }


        isFilterSelected = false
        filterAdapter.clearSelectedFilter()
    }
}