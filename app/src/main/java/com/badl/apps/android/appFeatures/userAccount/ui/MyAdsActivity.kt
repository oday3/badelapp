package com.badl.apps.android.appFeatures.userAccount.ui

import android.app.Dialog
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import com.badl.apps.android.R
import com.badl.apps.android.adapters.BiddingPagerAdapter
import com.badl.apps.android.adapters.FilterAdapter
import com.badl.apps.android.adapters.MyAdsAdapter
import com.badl.apps.android.appFeatures.addAd.ui.AddAdActivity
import com.badl.apps.android.appFeatures.addAd.ui.AdsViewModel
import com.badl.apps.android.appFeatures.appCommon.data.FilterItem
import com.badl.apps.android.baseClasses.ui.BaseActivity
import com.badl.apps.android.databinding.ActivityMyAdsBinding
import com.badl.apps.android.databinding.BottomFilterProductsBinding
import com.badl.apps.android.databinding.DialogAlertConfirmationBinding
import com.badl.apps.android.databinding.DialogEnableGpsBinding
import com.badl.apps.android.network.ApiConstants
import com.badl.apps.android.utils.AppUtil.collectFlow
import com.badl.apps.android.utils.AppUtil.navToActivity
import com.badl.apps.android.utils.Constants
import com.badl.apps.android.utils.NetworkUtils.handelApiResult
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
import java.text.FieldPosition

class MyAdsActivity : BaseActivity() {
    private var currentSelectedId: Int = -1
    private var currentSelectedPosition: Int = -1
    private lateinit var selectedAdAdapter: MyAdsAdapter
    private lateinit var binding: ActivityMyAdsBinding
    private lateinit var tabLayoutMediator: TabLayoutMediator
    private lateinit var pagerAdapter: BiddingPagerAdapter
    private lateinit var swapAdsFragment: SwapAdsFragment
    private lateinit var bidAdsFragment: BidAdsFragment
    private lateinit var buyAdsFragment: BuyAdsFragment
    private lateinit var charityAdsFragment: CharityAdsFragment
    private lateinit var filterAdapter: FilterAdapter

    private lateinit var filterProductBottomSheet: BottomSheetDialog
    private lateinit var filterProductBottomSheetBinding: BottomFilterProductsBinding

    private lateinit var deleteAdDialog: Dialog
    private lateinit var deleteAdDialogBinding: DialogAlertConfirmationBinding

    private lateinit var pagerCallBack: ViewPager2.OnPageChangeCallback
    private var currentPagerPosition = 0
     var isFilterSelected = false


    private val mViewModel by viewModels<AdsViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyAdsBinding.inflate(layoutInflater)
        filterProductBottomSheetBinding = BottomFilterProductsBinding.inflate(layoutInflater)
        deleteAdDialogBinding = DialogAlertConfirmationBinding.inflate(layoutInflater)

        setContentView(binding.root)
        initializeMainToolBar(getString(R.string.my_ads))

        initAdapters()
        initViews()
        initListeners()
    }

    override fun initViews() {

        binding.tabLayoutMyAdsActivity.let { mTabLayout ->

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

        swapAdsFragment = SwapAdsFragment()
        bidAdsFragment = BidAdsFragment()
        buyAdsFragment = BuyAdsFragment()
        charityAdsFragment = CharityAdsFragment()

        pagerAdapter = BiddingPagerAdapter(arrayListOf(swapAdsFragment, bidAdsFragment, buyAdsFragment, charityAdsFragment), supportFragmentManager, lifecycle)

        binding.viewPager.adapter = pagerAdapter

        tabLayoutMediator.attach()

        if(isUserLogin()) {

            binding.fabAddAd.showView(sharedPrefUtils.getCurrentUserData()?.type?.toInt() == 1)
        }

        filterProductBottomSheet =
            UiUtils.createBottomSheetDialog(
                this,
                -1,
                filterProductBottomSheetBinding.root,
                true
            )

        filterProductBottomSheetBinding.recBottomFilterProductsListOfFilters.setFlowLayoutManager()

        filterProductBottomSheetBinding.recBottomFilterProductsListOfFilters.adapter = filterAdapter


        deleteAdDialog =
            UiUtils.createDialog(
                this,
                themeResId =
                R.style.TransparentAlertDialog,
                windowAnimationResId =
                R.style.SlideDialogAnimation,
                true, deleteAdDialogBinding.root
            )

        deleteAdDialog.extensionSetBackground(
            (getScreenDensity() * 24).toInt(),
            AppCompatResources.getDrawable(this,
                R.drawable.bg_dialog_),

            )

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

        binding.fabAddAd.setOnClickListener {

            navToActivity(AddAdActivity::class.java)
        }

        binding.imgFilter.setOnClickListener {

            filterProductBottomSheet.show()
        }

        pagerCallBack = object: ViewPager2.OnPageChangeCallback() {

            override fun onPageSelected(position: Int) {

                currentPagerPosition = position
                filterAdapter.clearSelectedFilter()
                isFilterSelected = false
                binding.edtMyAdsActivitySearch.setText("")
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

                            swapAdsFragment.executeFilterAction(it)

                        }

                        1 -> {


                            bidAdsFragment.executeFilterAction(it)
                        }

                        2 -> {

                            buyAdsFragment.executeFilterAction(it)
                        }

                        3 -> {

                            charityAdsFragment.executeFilterAction(it)
                        }
                    }

                    filterProductBottomSheet.dismiss()
                }
            }
        }

        binding.edtMyAdsActivitySearch.addSearchListener {

            if (it.isTextEmpty()) {

                showCustomToast(getString(R.string.please_enter_ad_name), Constants.TOAST_INFO)

                return@addSearchListener
            }

            binding.imgSearchAction.setImageResource(R.drawable.ic_close_fill)
            binding.imgSearchAction.tag = "cancel_search"
            executeSearchCommand(binding.edtMyAdsActivitySearch.text.toString())
        }

        binding.imgSearchAction.setOnClickListener {

            if (it.tag == "search") {

                if (validateEmpty(binding.edtMyAdsActivitySearch)) {

                    executeSearchCommand(binding.edtMyAdsActivitySearch.text.toString())

                    (it as ImageView).setImageResource(R.drawable.ic_close_fill)
                    it.tag = "cancel_search"
                }

            } else if (it.tag == "cancel_search") {

                binding.edtMyAdsActivitySearch.setText("")
                (it as ImageView).setImageResource(R.drawable.ic_search_main_color)
                it.tag = "search"
                clearSearchCommand()
            }
        }

        deleteAdDialogBinding.tvNo.setOnClickListener {

            deleteAdDialog.dismiss()
        }

        deleteAdDialogBinding.tvYes.setOnClickListener {


            deleteAd(currentSelectedId, currentSelectedPosition, selectedAdAdapter)
            deleteAdDialog.dismiss()
        }
    }


    fun createFilterAdapter(): FilterAdapter {

       val filterAdapter = FilterAdapter(this, false, 0)

        filterAdapter.setData(
            arrayListOf(
                FilterItem(-1, getString(R.string.all)),
                FilterItem(0, getString(R.string.not_active)),
                FilterItem(1, getString(R.string.active)),
            )
        )

        return filterAdapter
    }


    private fun executeSearchCommand(query: String) {


        when(currentPagerPosition) {

            0 -> {

                swapAdsFragment.executeSearchAction(query)

            }

            1 -> {


                bidAdsFragment.executeSearchAction(query)
            }

            2 -> {

                buyAdsFragment.executeSearchAction(query)
            }

            3 -> {

                charityAdsFragment.executeSearchAction(query)
            }
        }


        isFilterSelected = false
        filterAdapter.clearSelectedFilter()

    }

    private fun clearSearchCommand() {


        when(currentPagerPosition) {

            0 -> {

                swapAdsFragment.clearSearchAction()

            }

            1 -> {


                bidAdsFragment.clearSearchAction()
            }

            2 -> {

                buyAdsFragment.clearSearchAction()
            }

            3 -> {

                charityAdsFragment.clearSearchAction()
            }
        }


        isFilterSelected = false
        filterAdapter.clearSelectedFilter()
    }

    fun showDeleteAd(position: Int, adId: Int, adapter: MyAdsAdapter) {


        deleteAdDialogBinding.tvTitle.text = getString(R.string.do_you_want_delete_this_ad)
        currentSelectedPosition = position
        currentSelectedId = adId
        selectedAdAdapter = adapter
        deleteAdDialog.show()
    }


    private fun deleteAd(adID: Int, position: Int, adapter: MyAdsAdapter) {

        collectFlow(mViewModel.deleteAd(adID.toString())) {

            handelApiResult(it,
                onResultSuccess = {


                    it.resultData?.data?.let { adsData ->

                      //  if (this::selectedAdAdapter.isInitialized) {

                            if (position != -1) {

                                adapter.deleteItem(position)
                            }
                      // }
                    }
                })
        }
    }


    fun resetUi() {

        filterAdapter.clearSelectedFilter()
        isFilterSelected = false
        binding.edtMyAdsActivitySearch.setText("")
        binding.imgSearchAction.setImageResource(R.drawable.ic_search_main_color)
        binding.imgSearchAction.tag = "search"
    }
}