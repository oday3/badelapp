package com.badl.apps.android.appFeatures.main.ui

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.badl.apps.android.R
import com.badl.apps.android.adapters.AdListAdapter
import com.badl.apps.android.adapters.FilterAdapter
import com.badl.apps.android.appFeatures.appCommon.data.FilterItem
import com.badl.apps.android.appFeatures.biddingAndBarterSystem.ui.ProductDetailsActivity
import com.badl.apps.android.baseClasses.ui.BaseActivity
import com.badl.apps.android.databinding.ActivityAdListBinding
import com.badl.apps.android.databinding.BottomFilterProductsBinding
import com.badl.apps.android.network.ApiConstants
import com.badl.apps.android.utils.AppUtil.collectFlow
import com.badl.apps.android.utils.AppUtil.getIntentExtension
import com.badl.apps.android.utils.Constants
import com.badl.apps.android.utils.NetworkUtils.handelApiResult
import com.badl.apps.android.utils.UiUtils
import com.badl.apps.android.utils.UiUtils.addSearchListener
import com.badl.apps.android.utils.UiUtils.hideKeyboard
import com.badl.apps.android.utils.UiUtils.initializeMainToolBar
import com.badl.apps.android.utils.UiUtils.isTextEmpty
import com.badl.apps.android.utils.UiUtils.setFlowLayoutManager
import com.badl.apps.android.utils.UiUtils.showCustomToast
import com.badl.apps.android.utils.UiUtils.showKeyboard
import com.badl.apps.android.utils.UiUtils.showView
import com.badl.apps.android.utils.ValidationUtils.isUserLogin
import com.badl.apps.android.utils.ValidationUtils.validateEmpty
import com.google.android.material.bottomsheet.BottomSheetDialog

class AdListActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityAdListBinding
    private lateinit var gridLayoutManager: GridLayoutManager
    private lateinit var adAdapter: AdListAdapter
    private val SPAN_COUNT_ONE = 1
    private val SPAN_COUNT_TOW = 2
    private var sectionId = -1
    private var from = ""
    private val favData = HashMap<String, String>()
    private val mHomeViewModel by viewModels<MainViewModel>()
    private val requestParams = HashMap<String, String>()
    private lateinit var filterProductBottomSheet: BottomSheetDialog
    private lateinit var filterProductBottomSheetBinding: BottomFilterProductsBinding
    private lateinit var filterAdapter: FilterAdapter
    private val listOfFilters = arrayListOf<TextView>()
    private var hasMore = false
    private var isLoading = false
    private var mCurrentPage = 1
    private var isFilterSelected = false
    private lateinit var inputMethodManager: InputMethodManager

    private val goToProductDetails =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {


                    finish()
//                val uri =  "https://maps.googleapis.com/maps/api/staticmap?center=${mAddressLat},${mAddressLng}&zoom=14&size=600x300&key=${getString(R.string.google_api_key)}"
//
//                binding.imgMap.setImage(uri)
//
//                Log.e("sajdb", uri)

            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdListBinding.inflate(layoutInflater)
        filterProductBottomSheetBinding = BottomFilterProductsBinding.inflate(layoutInflater)

        setContentView(binding.root)


        if (intent.hasExtra(Constants.TOOLBAR_TITLE)) {

            val toolBarTitle = intent.getStringExtra(Constants.TOOLBAR_TITLE)

            initializeMainToolBar(toolBarTitle)

        }

        if (intent.hasExtra(Constants.SECTION_ID)) {

            sectionId = intent.getIntExtra(Constants.SECTION_ID, -1)

        }

        if (intent.hasExtra(Constants.FROM)) {

            from = intent.getStringExtra(Constants.FROM).toString()

        }

        inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager


        initAdapters()
        initViews()
        initData()
        initListeners()
        initObservers()

        requestParams[ApiConstants.PAR_SECTION_ID] = sectionId.toString()
        requestParams[ApiConstants.PAR_PAGE] = mCurrentPage.toString()

        executeRequest(true, true)

        if (from == Constants.FROM_HOME_SEARCH) {

            binding.edtAdListActivitySearch.showKeyboard(inputMethodManager)
            binding.imgFilter.showView(false)
        }

        if (isUserLogin()) {

            if (sharedPrefUtils.getCurrentUserData()?.type?.toInt() == 2) {
                binding.imgFilter.showView(false)
            }
        }
    }

    override fun initViews() {

        filterProductBottomSheet =
            UiUtils.createBottomSheetDialog(
                this,
                -1,
                filterProductBottomSheetBinding.root,
                true
            )

        binding.recListOfAds.layoutManager = gridLayoutManager
        binding.recListOfAds.adapter = adAdapter

        filterProductBottomSheetBinding.recBottomFilterProductsListOfFilters.setFlowLayoutManager()

        filterProductBottomSheetBinding.recBottomFilterProductsListOfFilters.adapter = filterAdapter

    }

    override fun initData() {

        filterAdapter.setData(
            arrayListOf(
                FilterItem(-1, getString(R.string.all)),
                FilterItem(1, getString(R.string.bid)),
                FilterItem(2, getString(R.string.swap)),
                FilterItem(3, getString(R.string.buy)),
                FilterItem(4, getString(R.string.charity)),
            )
        )


        if (isUserLogin()) {

            if (sharedPrefUtils.getCurrentUserData()?.type?.toInt() == 1) {

                filterAdapter.deleteItem(filterAdapter.itemCount - 1)
            }

        } else {

            filterAdapter.deleteItem(filterAdapter.itemCount - 1)

        }
    }

    override fun initAdapters() {

        gridLayoutManager = GridLayoutManager(this, SPAN_COUNT_ONE)

        adAdapter = AdListAdapter(
            this,
            ::clickAction,
            ::favAction,
            gridLayoutManager, sharedPrefUtils.getCurrentUserData()?.type?.toInt() ?: 1
        )

        filterAdapter = FilterAdapter(this, false, 0)


    }

    private fun clickAction(id: Int) {

        getIntentExtension(ProductDetailsActivity::class.java).run {

            putExtra(Constants.ITEM_ID, id)
            putExtra(Constants.FROM, Constants.FROM_AD_DETAILS)

            goToProductDetails.launch(this)
        }
    }

    override fun initObservers() {

    }

    override fun initListeners() {

        binding.imgViewLayout.setOnClickListener {

            switchLayout()
        }

        binding.imgFilter.setOnClickListener {

            filterProductBottomSheet.show()
        }


        binding.recListOfAds.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {

                    if (gridLayoutManager.itemCount > 0) {
                        if (!isLoading && hasMore && gridLayoutManager.findLastCompletelyVisibleItemPosition() == adAdapter.itemCount - 1) {
                            isLoading = true

                            requestParams[ApiConstants.PAR_PAGE] = (++mCurrentPage).toString()

                            if (isFilterSelected) {

                                requestParams[ApiConstants.PAR_FILTER_TYPE] =
                                    filterAdapter.getSelectedItem()?.id.toString()
                            }

                            executeRequest(false, false)
                        }
                    }
                }
            }
        })


        binding.edtAdListActivitySearch.addSearchListener {

            if (it.isTextEmpty()) {

                showCustomToast(getString(R.string.please_enter_ad_name), Constants.TOAST_INFO)

                return@addSearchListener
            }

            binding.imgSearchAction.setImageResource(R.drawable.ic_close_fill)
            binding.imgSearchAction.tag = "cancel_search"
            executeSearchCommand()
        }

        binding.imgSearchAction.setOnClickListener {

            if (it.tag == "search") {

                if (validateEmpty(binding.edtAdListActivitySearch)) {

                    executeSearchCommand()

                    (it as ImageView).setImageResource(R.drawable.ic_close_fill)
                    it.tag = "cancel_search"
                }

            } else if (it.tag == "cancel_search") {

                binding.edtAdListActivitySearch.setText("")
                mCurrentPage = 1
                requestParams.remove(ApiConstants.PAR_FILTER_TYPE)
                requestParams.remove(ApiConstants.PAR_DATA)
                requestParams[ApiConstants.PAR_PAGE] = mCurrentPage.toString()
                executeRequest(true, false)
                (it as ImageView).setImageResource(R.drawable.ic_search_main_color)
                it.tag = "search"
                filterAdapter.setSelectedItem(0)
            }

        }

        filterProductBottomSheetBinding.btnApplyFilter.setOnClickListener {

            filterAdapter.getSelectedItem().let {

                it?.id?.let {

                    if (it == -1) {

                        requestParams.remove(ApiConstants.PAR_FILTER_TYPE)
                        isFilterSelected = false

                    } else {

                        requestParams[ApiConstants.PAR_FILTER_TYPE] = it.toString()
                        isFilterSelected = true
                    }

                    mCurrentPage = 1
                    requestParams[ApiConstants.PAR_PAGE] = mCurrentPage.toString()


                    executeRequest(true, false)
                    filterProductBottomSheet.dismiss()
                }
            }
        }
    }

    private fun switchLayout() {

        if (gridLayoutManager.spanCount == SPAN_COUNT_ONE) {

            gridLayoutManager.spanCount = SPAN_COUNT_TOW
            binding.imgViewLayout.setImageResource(R.drawable.ic_list_view)

        } else {

            gridLayoutManager.spanCount = SPAN_COUNT_ONE
            binding.imgViewLayout.setImageResource(R.drawable.ic_grid_view)

        }

        adAdapter.notifyItemRangeChanged(0, adAdapter.itemCount)
    }

    private fun showAllProducts(
        params: HashMap<String, String>,
        refreshResult: Boolean = false,
        hideAllView: Boolean = false
    ) {

        collectFlow(mHomeViewModel.showAll(params)) {

            handelApiResult(it,
                onResultSuccess = {

                    it.resultData?.data?.let {


                        Log.e("returned_data", it.toString())

                        if (it.isNotEmpty()) {

                           hideEmptyLayout()
                            adAdapter.setAdapterData(refreshResult, it)
                        } else {

                            showEmptyLayout(hideAllView)
                        }
                    }

                    hasMore = (it.resultData?.pages?.last_page ?: 0) > (it.resultData?.pages?.current_page ?: 0)
                    isLoading = false

                })
        }
    }

    private fun getSectionAds(
        params: HashMap<String, String>,
        refreshResult: Boolean = false,
        hideAllView: Boolean = false
    ) {

        collectFlow(mHomeViewModel.getSectionAds(params)) {

            handelApiResult(it,
                onResultSuccess = {

                    it.resultData?.data?.let {


                        Log.e("returned_data", it.toString())

                        if (it.isNotEmpty()) {


                            hideEmptyLayout()

                            adAdapter.setAdapterData(refreshResult, it)

                        } else {

                            showEmptyLayout(hideAllView)
                        }
                    }

                    hasMore = (it.resultData?.pages?.last_page ?: 0) > (it.resultData?.pages?.current_page ?: 0)
                    isLoading = false
                })
        }
    }


    private fun homeSearch(
        params: HashMap<String, String>,
        refreshResult: Boolean = false,
        hideAllView: Boolean = false
    ) {

        collectFlow(mHomeViewModel.homeSearch(params)) {

            handelApiResult(it,
                onResultSuccess = {

                    it.resultData?.data?.let {


                        Log.e("returned_data", it.toString())

                        if (it.isNotEmpty()) {


                            hideEmptyLayout()

                            adAdapter.setAdapterData(refreshResult, it)

                        } else {

                            showEmptyLayout(hideAllView)
                        }
                    }

                    hasMore = (it.resultData?.pages?.last_page ?: 0) > (it.resultData?.pages?.current_page ?: 0)
                    isLoading = false
                })
        }
    }

    private fun favAction(position: Int) {


        if (isUserLogin(true)) {

            adAdapter.getItem(position).id?.let {

                favData[ApiConstants.PAR_AD_ID] = it.toString()
                favProduct(position, favData)
            }
        }
    }

    private fun favProduct(
        itemPosition: Int, favData: HashMap<String, String>
    ) {

        collectFlow(mHomeViewModel.favoriteProduct(favData)) {

            handelApiResult(it, onLoading = {
                adAdapter.getItem(itemPosition).is_loading = true
                adAdapter.refreshItem(itemPosition)

            },
                onResultSuccess = {

                    it.resultData?.data?.let {

                        adAdapter.getItem(itemPosition).is_favorite =
                            !(adAdapter.getItem(itemPosition).is_favorite ?: false)
                        adAdapter.getItem(itemPosition).is_loading = false
                        adAdapter.refreshItem(itemPosition)
                    }

                }, onResultFail = {
                    adAdapter.getItem(itemPosition).is_loading = false
                    adAdapter.refreshItem(itemPosition)
                })
        }
    }

    private fun showEmptyLayout(hideAllView: Boolean = true) {

        if (hideAllView) {

            binding.conAdListActivityContent.showView(hideAllView)

        } else {

            binding.recListOfAds.showView(false)
        }

        binding.layoutEmpty.constEmptyLayout.showView(true)
        binding.layoutEmpty.tvEmptyLayoutTitle.text = getString(R.string.no_products_avaliable)
        binding.layoutEmpty.imgEmptyLayoutIcon.setImageResource(R.drawable.ic_empty_ads)
        binding.layoutEmpty.btnEmptyLayoutAction.showView(false)
    }


    private fun hideEmptyLayout() {

        binding.conAdListActivityContent.showView(true)
        binding.recListOfAds.showView(true)
        binding.layoutEmpty.constEmptyLayout.showView(false)
    }

    override fun onClick(v: View?) {

        for (tv in listOfFilters) {

            tv.isSelected = tv.id == v?.id

            if (tv.isSelected) {

                when (tv.id) {

                    R.id.tv_filterBidAds -> {

                    }

                    R.id.tv_filterSwapAds -> {

                    }


                    R.id.tv_filterBuyAds -> {

                    }
                }
            }
        }
    }


    fun executeRequest(refreshResult: Boolean = false, hideAllView: Boolean) {

        binding.edtAdListActivitySearch.hideKeyboard(inputMethodManager)

        when (from) {

            Constants.FROM_SHOW_ALL -> {

                showAllProducts(requestParams, refreshResult, hideAllView)
            }

            Constants.FROM_SECTION -> {

                getSectionAds(requestParams, refreshResult, hideAllView)

            }


            Constants.FROM_HOME_SEARCH -> {

                if (binding.edtAdListActivitySearch.text?.isNotEmpty() == true) {

                    requestParams[ApiConstants.PAR_DATA] = binding.edtAdListActivitySearch.text.toString()
                    homeSearch(requestParams, refreshResult, hideAllView)

                } else {

                   // adAdapter.setAdapterData(true, arrayListOf())
                }
            }
        }
    }

    private fun executeSearchCommand() {

        isFilterSelected = false
        mCurrentPage = 1
        requestParams[ApiConstants.PAR_DATA] = binding.edtAdListActivitySearch.text.toString()
        requestParams[ApiConstants.PAR_PAGE] = mCurrentPage.toString()
        requestParams.remove(ApiConstants.PAR_FILTER_TYPE)
        filterAdapter.clearSelectedFilter()
        executeRequest(true, false)
    }
}