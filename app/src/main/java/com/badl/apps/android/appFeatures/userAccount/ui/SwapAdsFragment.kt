package com.badl.apps.android.appFeatures.userAccount.ui

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.badl.apps.android.R
import com.badl.apps.android.adapters.FilterAdapter
import com.badl.apps.android.adapters.MyAdsAdapter
import com.badl.apps.android.adapters.SwapOffersAdapter
import com.badl.apps.android.appFeatures.biddingAndBarterSystem.ui.BarterOfferActivity
import com.badl.apps.android.appFeatures.userAccount.data.UserAdItem
import com.badl.apps.android.baseClasses.ui.BaseActivity
import com.badl.apps.android.databinding.BottomFilterProductsBinding
import com.badl.apps.android.databinding.DialogSwapOffersBinding
import com.badl.apps.android.databinding.FragmentSwapAdsBinding
import com.badl.apps.android.network.ApiConstants
import com.badl.apps.android.utils.AppUtil.collectFlow
import com.badl.apps.android.utils.AppUtil.getIntentExtension
import com.badl.apps.android.utils.Constants
import com.badl.apps.android.utils.NetworkUtils.handelApiResult
import com.badl.apps.android.utils.UiUtils
import com.badl.apps.android.utils.UiUtils.extensionSetBackground
import com.badl.apps.android.utils.UiUtils.getScreenDensity
import com.badl.apps.android.utils.UiUtils.setFlowLayoutManager
import com.badl.apps.android.utils.UiUtils.showCustomToast
import com.badl.apps.android.utils.UiUtils.showView
import com.google.android.material.bottomsheet.BottomSheetDialog

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SwapAdsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SwapAdsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private  var binding: FragmentSwapAdsBinding? = null
    private lateinit var swapOffersDialog: Dialog
    private lateinit var swapOffersDialogBinding: DialogSwapOffersBinding
    private lateinit var swapOffersAdapter: SwapOffersAdapter
    private lateinit var adsAdapter: MyAdsAdapter
    private val mViewModel by activityViewModels<UserAccountViewModel>()
    private val adParameters = HashMap<String, String>()
    private var currentSelectedId = -1

    private var hasMore = false
    private var isLoading = false
    private var mCurrentPage = 1
    private var filterID = -1

    private lateinit var filterProductBottomSheet: BottomSheetDialog
    private lateinit var filterProductBottomSheetBinding: BottomFilterProductsBinding

    private lateinit var filterAdapter: FilterAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSwapAdsBinding.inflate(inflater, container, false)
        swapOffersDialogBinding = DialogSwapOffersBinding.inflate(layoutInflater)
        filterProductBottomSheetBinding = BottomFilterProductsBinding.inflate(inflater)

        adParameters[ApiConstants.PAR_FILTER_TYPE] = 2.toString()
        adParameters[ApiConstants.PAR_PAGE] = mCurrentPage.toString()


        swapOffersAdapter = SwapOffersAdapter(requireContext(), ::swapAdClickAction)

        swapOffersDialogBinding.recListOfSwapOrders.adapter = swapOffersAdapter

        swapOffersDialog =
            UiUtils.createDialog(
                requireContext(),
                themeResId = R.style.TransparentAlertDialog,
                windowAnimationResId = R.style.SlideDialogAnimation,
                true, swapOffersDialogBinding.root
            )

        swapOffersDialog.extensionSetBackground(
            (getScreenDensity() * 14).toInt(),
            AppCompatResources.getDrawable(requireContext(), R.drawable.bg_dialog_),
        )

        adsAdapter = MyAdsAdapter(requireContext(), 2, ::mainAdClickAction, ::editAction, ::deleteAction, ::checkIsDataEmpty)
        binding?.recListOfSwapOrders?.adapter = adsAdapter

        binding?.swipeLayout?.setOnRefreshListener {
            mCurrentPage = 1
            adParameters.remove(ApiConstants.PAR_STATUS)
            adParameters.remove(ApiConstants.PAR_DATA)
            adParameters[ApiConstants.PAR_PAGE] = mCurrentPage.toString()
            (requireActivity() as MyAdsActivity).resetUi()
            getAds(adParameters) }


        filterProductBottomSheet =
            UiUtils.createBottomSheetDialog(
                requireContext(),
                -1,
                filterProductBottomSheetBinding.root,
                true
            )


        filterAdapter = (requireActivity() as MyAdsActivity).createFilterAdapter()

        filterProductBottomSheetBinding.recBottomFilterProductsListOfFilters.setFlowLayoutManager()

        filterProductBottomSheetBinding.recBottomFilterProductsListOfFilters.adapter = filterAdapter



        return binding?.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.recListOfSwapOrders?.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {

                    if ((binding?.recListOfSwapOrders?.layoutManager as LinearLayoutManager).itemCount > 0) {
                        if (!isLoading && hasMore && (binding?.recListOfSwapOrders?.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition() == adsAdapter.itemCount - 1) {
                            isLoading = true

                            adParameters[ApiConstants.PAR_PAGE] = (++mCurrentPage).toString()

                            if ((requireActivity() as MyAdsActivity).isFilterSelected) {

                                adParameters[ApiConstants.PAR_STATUS] = filterID.toString()
                            }

                            getAds(adParameters, false)
                        }
                    }
                }
            }
        })


        getAds(adParameters)
    }

    private fun deleteAction(position: Int, adItem: UserAdItem, adapter: MyAdsAdapter) {


        adItem.id?.let {

            (requireActivity() as MyAdsActivity).showDeleteAd(position, it, adapter)

        }

    }

    private fun getAds(params: HashMap<String, String>, refreshData: Boolean = true, hideAllLayout: Boolean = false) {

        collectFlow(mViewModel.getAds(params)) {

            binding?.swipeLayout?.isRefreshing = false
            (requireActivity() as BaseActivity).handelApiResult(it,
                onResultSuccess = {

                    it.resultData?.data?.let { adsData ->


                        checkIsDataEmpty(adsData, refreshData)
                    }

                    hasMore = (it.resultData?.pages?.last_page ?: 0) > (it.resultData?.pages?.current_page ?: 0)
                    isLoading = false
                })
        }
    }

    private fun checkIsDataEmpty(adsData: List<UserAdItem>, refreshData: Boolean) {

        if (adsData.isNotEmpty()) {

            hideEmptyLayout()
            adsAdapter.setAdapterData(refreshData, adsData)


        } else {

            showEmptyLayout()
        }
    }

    private fun getSwapOrders(adID: Int) {

        collectFlow(mViewModel.getSwapOrders(adID.toString())) {

            (requireActivity() as BaseActivity).handelApiResult(it,
                onResultSuccess = {

                    currentSelectedId = adID

                    it.resultData?.data?.let { adsData ->


                        adsData.orders?.let {

                            if (it.isNotEmpty()) {

                                swapOffersAdapter.setData(it)
                                swapOffersDialogBinding.yvSwapOffersDialogSwapOffers.text = adsData.orders_count
                                swapOffersDialogBinding.tvSwapOffersDialogDesc.text = adsData.ad_description
                                swapOffersDialog.show()
                            } else {


                                showCustomToast(getString(R.string.there_is_no_swap_orders_currently), Constants.TOAST_INFO)
                            }
                        }

                    }

                })
        }
    }

    private fun mainAdClickAction(position: Int) {


        adsAdapter.getItem(position)?.id?.let {

            if (currentSelectedId != it) {


                getSwapOrders(it)
            } else {

                swapOffersDialog.show()
            }
        }
    }


    private fun swapAdClickAction(position: Int) {

        getIntentExtension(BarterOfferActivity::class.java).run {

            putExtra(Constants.ORDER_ID, swapOffersAdapter.getItem(position).id)
            requireActivity().startActivity(this)
        }

    }



    private fun editAction(position: Int) {

        getIntentExtension(EditAcquisitionActivity::class.java).run {

            putExtra(Constants.AD_ID, adsAdapter.getItem(position)?.id)

            requireActivity().startActivity(this)
        }

    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SwapAdsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SwapAdsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun showEmptyLayout() {

        binding?.consSwapAdsFragContent?.showView(false)
        binding?.layoutEmpty?.constEmptyLayout?.showView(true)
        binding?.layoutEmpty?.tvEmptyLayoutTitle?.text = getString(R.string.there_are_no_ads_to_view)
        binding?.layoutEmpty?.tvEmptyLayoutMsg?.text = getString(R.string.sorry_there_are_no_ads_to_view_currently_you_can_add_a_product_now)
        binding?.layoutEmpty?.imgEmptyLayoutIcon?.setImageResource(R.drawable.ic_empty_ads)
        binding?.layoutEmpty?.btnEmptyLayoutAction?.showView(false)
    }


    private fun hideEmptyLayout() {
        binding?.consSwapAdsFragContent?.showView(true)
        binding?.recListOfSwapOrders?.showView(true)
        binding?.layoutEmpty?.constEmptyLayout?.showView(false)

    }

    fun executeFilterAction (filterID: Int) {


        this.filterID = filterID

        if (filterID == -1) {

            adParameters.remove(ApiConstants.PAR_STATUS)
           // isFilterSelected = false

        } else {

            adParameters[ApiConstants.PAR_STATUS] = filterID.toString()
           // isFilterSelected = true
        }

        mCurrentPage = 1
        adParameters[ApiConstants.PAR_PAGE] = mCurrentPage.toString()


        getAds(adParameters)

    }

    fun executeSearchAction (query: String) {


        mCurrentPage = 1
        adParameters[ApiConstants.PAR_DATA] = query
        adParameters[ApiConstants.PAR_PAGE] = mCurrentPage.toString()
        adParameters.remove(ApiConstants.PAR_STATUS)

        // searchForProduct(params, true)
        getAds(adParameters)

    }

    fun clearSearchAction () {


        mCurrentPage = 1
        adParameters.remove(ApiConstants.PAR_STATUS)
        adParameters.remove(ApiConstants.PAR_DATA)
        adParameters[ApiConstants.PAR_PAGE] = mCurrentPage.toString()

        // searchForProduct(params, true)
        getAds(adParameters)

    }
}