package com.badl.apps.android.appFeatures.main.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.badl.apps.android.R
import com.badl.apps.android.adapters.FilterAdapter
import com.badl.apps.android.adapters.OrdersAdapter
import com.badl.apps.android.appFeatures.appCommon.data.FilterItem
import com.badl.apps.android.appFeatures.userAccount.data.OrderItem
import com.badl.apps.android.application.BadelApp
import com.badl.apps.android.baseClasses.ui.BaseActivity
import com.badl.apps.android.databinding.BottomFilterProductsBinding
import com.badl.apps.android.databinding.FragmentBidOrdersBinding
import com.badl.apps.android.databinding.FragmentBuyOrdersBinding
import com.badl.apps.android.network.ApiConstants
import com.badl.apps.android.utils.AppUtil.collectFlow
import com.badl.apps.android.utils.Constants
import com.badl.apps.android.utils.NetworkUtils.handelApiResult
import com.badl.apps.android.utils.UiUtils
import com.badl.apps.android.utils.UiUtils.addSearchListener
import com.badl.apps.android.utils.UiUtils.isTextEmpty
import com.badl.apps.android.utils.UiUtils.refreshData
import com.badl.apps.android.utils.UiUtils.setFlowLayoutManager
import com.badl.apps.android.utils.UiUtils.showCustomToast
import com.badl.apps.android.utils.UiUtils.showView
import com.badl.apps.android.utils.ValidationUtils.validateEmpty
import com.google.android.material.bottomsheet.BottomSheetDialog

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [BuyOrdersFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BuyOrdersFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var binding: FragmentBuyOrdersBinding? = null
    private lateinit var ordersAdapter: OrdersAdapter
    private val orderData = HashMap<String, String>()
    private val mHomeViewModel by activityViewModels<MainViewModel>()

    private var hasMore = false
    private var isLoading = false
    private var mCurrentPage = 1
    private var isFilterSelected = false


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

        binding = FragmentBuyOrdersBinding.inflate(inflater, container, false)
        filterProductBottomSheetBinding = BottomFilterProductsBinding.inflate(inflater)

        ordersAdapter = OrdersAdapter(requireActivity(), ::rateOrder)
        binding?.recListOfBuyOrders?.adapter = ordersAdapter

        filterAdapter = FilterAdapter(requireContext(), false, 0)

        filterAdapter.setData(
            arrayListOf(
                FilterItem(-1, getString(R.string.all)),
                FilterItem(0, getString(R.string.pending_orders)),
                FilterItem(1, getString(R.string.accepted_orders)),
                FilterItem(2, getString(R.string.rejected_order)),
            )
        )


        orderData[ApiConstants.PAR_FILTER_TYPE] = 3.toString()

        binding?.swipeLayout?.setOnRefreshListener {

            resetRequestParamsAndUi()
            getOrders(orderData)
        }


        filterProductBottomSheet =
            UiUtils.createBottomSheetDialog(
                requireContext(),
                -1,
                filterProductBottomSheetBinding.root,
                true
            )


        filterProductBottomSheetBinding.recBottomFilterProductsListOfFilters.setFlowLayoutManager()

        filterProductBottomSheetBinding.recBottomFilterProductsListOfFilters.adapter = filterAdapter


        binding?.imgFilter?.setOnClickListener {

            filterProductBottomSheet.show()
        }

        return binding?.root
    }


    private fun rateOrder(item: OrderItem) {

        item.ad_id?.toInt()?.let {

            (requireActivity() as MainActivity).showRateDialog(it)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding?.recListOfBuyOrders?.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {

                    if ((binding?.recListOfBuyOrders?.layoutManager as LinearLayoutManager).itemCount > 0) {
                        if (!isLoading && hasMore && (binding?.recListOfBuyOrders?.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition() == ordersAdapter.itemCount - 1) {
                            isLoading = true

                            orderData[ApiConstants.PAR_PAGE] = (++mCurrentPage).toString()

                            if (isFilterSelected) {

                                orderData[ApiConstants.PAR_STATUS] =
                                    filterAdapter.getSelectedItem()?.id.toString()
                            }

                            getOrders(orderData, false)
                        }
                    }
                }
            }
        })


        binding?.edtBuyOrdersFragmentSearch?.addSearchListener {

            if (it.isTextEmpty()) {

                showCustomToast(getString(R.string.please_enter_ad_name), Constants.TOAST_INFO)

                return@addSearchListener
            }

            binding?.imgSearchAction?.setImageResource(R.drawable.ic_close_fill)
            binding?.imgSearchAction?.tag = "cancel_search"
            executeSearchCommand()
        }

        binding?.imgSearchAction?.setOnClickListener {

            if (it.tag == "search") {

                if (validateEmpty(binding?.edtBuyOrdersFragmentSearch!!)) {

                    executeSearchCommand()

                    (it as ImageView).setImageResource(R.drawable.ic_close_fill)
                    it.tag = "cancel_search"
                }

            } else if (it.tag == "cancel_search") {

                binding?.edtBuyOrdersFragmentSearch?.setText("")
                mCurrentPage = 1
                orderData.remove(ApiConstants.PAR_STATUS)
                orderData.remove(ApiConstants.PAR_DATA)
                orderData[ApiConstants.PAR_PAGE] = mCurrentPage.toString()

                getOrders(orderData)
                (it as ImageView).setImageResource(R.drawable.ic_search_main_color)
                it.tag = "search"
                filterAdapter.setSelectedItem(0)
            }
        }

        filterProductBottomSheetBinding.btnApplyFilter.setOnClickListener {

            filterAdapter.getSelectedItem().let {

                it?.id?.let {

                    if (it == -1) {

                        orderData.remove(ApiConstants.PAR_STATUS)
                        isFilterSelected = false

                    } else {

                        orderData[ApiConstants.PAR_STATUS] = it.toString()
                        isFilterSelected = true
                    }

                    mCurrentPage = 1
                    orderData[ApiConstants.PAR_PAGE] = mCurrentPage.toString()


                    getOrders(orderData)
                    filterProductBottomSheet.dismiss()
                }
            }
        }

        getOrders(orderData, true)
    }

    override fun onResume() {
        super.onResume()



        if ((requireActivity().applicationContext as BadelApp).refreshOrders && !isHidden) {

            getOrders(orderData)
            // (requireActivity().applicationContext as BadelApp).refreshOrders = false
        }

        //getOrders(type)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment BuyOrdersFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            BuyOrdersFragment().apply {
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

    private fun getOrders(params: HashMap<String, String>, refreshData: Boolean = true, hideAllLayout: Boolean = false) {

        collectFlow(mHomeViewModel.getUserOrders(params)) {

            binding?.swipeLayout?.isRefreshing = false

            (requireActivity() as BaseActivity).handelApiResult(it,

                onResultSuccess = {
                    it.resultData?.data?.let {

                        if (it.isNotEmpty()) {

                            hideEmptyLayout()
                            ordersAdapter.setAdapterData(refreshData, it)

                        } else {

                           // ordersAdapter.setAdapterData(true, arrayListOf())
                            showEmptyLayout()
                        }
                    }
                })
        }

    }

    fun loadData() {
        this.refreshData { getOrders(orderData) }
    }

    private fun showEmptyLayout(hideAllLayout: Boolean = false) {

        binding?.layoutEmpty?.root?.showView(true)
        binding?.recListOfBuyOrders?.showView(false)
        binding?.layoutEmpty?.constEmptyLayout?.showView(true)
        binding?.layoutEmpty?.tvEmptyLayoutTitle?.text = getString(R.string.no_orders_currently)
        binding?.layoutEmpty?.tvEmptyLayoutMsg?.text = getString(R.string.browse_products_and_order)
        binding?.layoutEmpty?.imgEmptyLayoutIcon?.setImageResource(R.drawable.ic_empty_ads)
        binding?.layoutEmpty?.btnEmptyLayoutAction?.showView(false)
    }

    private fun hideEmptyLayout() {

        binding?.layoutEmpty?.root?.showView(false)
        binding?.recListOfBuyOrders?.showView(true)
    }

    fun executeSearchCommand() {


        isFilterSelected = false
        mCurrentPage = 1
        orderData[ApiConstants.PAR_DATA] = binding?.edtBuyOrdersFragmentSearch?.text.toString()
        orderData[ApiConstants.PAR_PAGE] = mCurrentPage.toString()
        orderData.remove(ApiConstants.PAR_STATUS)
        filterAdapter.clearSelectedFilter()
        // searchForProduct(params, true)
        getOrders(orderData)
    }


    fun resetRequestParamsAndUi() {


        filterAdapter.clearSelectedFilter()
        isFilterSelected = false
        mCurrentPage = 1
        orderData[ApiConstants.PAR_PAGE] = mCurrentPage.toString()
        orderData.remove(ApiConstants.PAR_STATUS)
        orderData.remove(ApiConstants.PAR_DATA)
        binding?.edtBuyOrdersFragmentSearch?.setText("")
        binding?.imgSearchAction?.setImageResource(R.drawable.ic_search_main_color)
        binding?.imgSearchAction?.tag = "search"
    }
}