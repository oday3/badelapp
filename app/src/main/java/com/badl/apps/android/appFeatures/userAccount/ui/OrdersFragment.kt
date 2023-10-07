package com.badl.apps.android.appFeatures.userAccount.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.badl.apps.android.R
import com.badl.apps.android.adapters.FilterAdapter
import com.badl.apps.android.adapters.UserOrdersAdapter
import com.badl.apps.android.appFeatures.addAd.ui.AddAdActivity
import com.badl.apps.android.appFeatures.appCommon.data.FilterItem
import com.badl.apps.android.baseClasses.ui.BaseActivity
import com.badl.apps.android.databinding.BottomFilterProductsBinding
import com.badl.apps.android.databinding.FragmentOrdersBinding
import com.badl.apps.android.utils.AppUtil.collectFlow
import com.badl.apps.android.utils.AppUtil.navToActivity
import com.badl.apps.android.utils.NetworkUtils.handelApiResult
import com.badl.apps.android.utils.UiUtils
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.android.material.bottomsheet.BottomSheetDialog

/**
 * A simple [Fragment] subclass.
 * Use the [OrdersFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class OrdersFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var binding: FragmentOrdersBinding? = null
    private lateinit var ordersAdapter: UserOrdersAdapter
    private lateinit var filterProductBottomSheet: BottomSheetDialog
    private lateinit var filterProductBottomSheetBinding: BottomFilterProductsBinding
    private lateinit var filterAdapter: FilterAdapter
    private val mViewModel by activityViewModels<UserAccountViewModel>()
    private var params = HashMap<String, String>()
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

        binding = FragmentOrdersBinding.inflate(inflater, container, false)

        filterProductBottomSheetBinding = BottomFilterProductsBinding.inflate(layoutInflater)

        ordersAdapter = UserOrdersAdapter(requireContext())
        binding?.recListOfOrders?.adapter = ordersAdapter

        binding?.fabAddAd?.setOnClickListener {

            navToActivity(AddAdActivity::class.java)
        }

        filterAdapter = FilterAdapter(requireContext(), false, 0)

        filterAdapter.setData(
            arrayListOf(
                FilterItem(4, getString(R.string.bidding_products)),
                FilterItem(1, getString(R.string.swap_products)),
                FilterItem(2, getString(R.string.products_to_buy)),
                FilterItem(3, getString(R.string.oldest_to_newest))
            )
        )

        filterProductBottomSheet =
            UiUtils.createBottomSheetDialog(
                requireContext(),
                -1,
                filterProductBottomSheetBinding.root,
                true
            )

        val layoutManager = FlexboxLayoutManager(requireContext())
        layoutManager.flexDirection = FlexDirection.ROW
        layoutManager.justifyContent = JustifyContent.CENTER
        filterProductBottomSheetBinding.recBottomFilterProductsListOfFilters.layoutManager =
            layoutManager
        filterProductBottomSheetBinding.recBottomFilterProductsListOfFilters.adapter = filterAdapter


        return binding?.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getOrders(params)
    }


    private fun getOrders(params: HashMap<String, String>) {

        collectFlow(mViewModel.getUserOrders(params)) {

            (requireActivity() as BaseActivity).handelApiResult(it,
                onResultSuccess = {

                    it.resultData?.data?.let { orderData ->

                        ordersAdapter.setData(orderData)
                    }

                })
        }
    }



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment OrdersFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            OrdersFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}