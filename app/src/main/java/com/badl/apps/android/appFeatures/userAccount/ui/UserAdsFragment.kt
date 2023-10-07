package com.badl.apps.android.appFeatures.userAccount.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.badl.apps.android.R
import com.badl.apps.android.adapters.FilterAdapter
import com.badl.apps.android.adapters.UserAdsAdapter
import com.badl.apps.android.appFeatures.userAccount.data.UserAdItem
import com.badl.apps.android.appFeatures.addAd.ui.AddAdActivity
import com.badl.apps.android.appFeatures.appCommon.data.FilterItem
import com.badl.apps.android.baseClasses.ui.BaseActivity
import com.badl.apps.android.baseClasses.ui.BaseFragment
import com.badl.apps.android.databinding.BottomFilterProductsBinding
import com.badl.apps.android.databinding.FragmentAdBinding
import com.badl.apps.android.utils.AppUtil.collectFlow
import com.badl.apps.android.utils.AppUtil.getIntentExtension
import com.badl.apps.android.utils.AppUtil.navToActivity
import com.badl.apps.android.utils.Constants
import com.badl.apps.android.utils.NetworkUtils.handelApiResult
import com.badl.apps.android.utils.UiUtils
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.android.material.bottomsheet.BottomSheetDialog

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
/**
 * A simple [Fragment] subclass.
 * Use the [UserAdsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

class UserAdsFragment : BaseFragment() {

    private var binding: FragmentAdBinding? = null
    private lateinit var adsAdapter: UserAdsAdapter
    private lateinit var filterProductBottomSheet: BottomSheetDialog
    private lateinit var filterProductBottomSheetBinding: BottomFilterProductsBinding
    private lateinit var filterAdapter: FilterAdapter
    private val mAdViewModel by activityViewModels<UserAccountViewModel>()

    private var param1: String? = null
    private var param2: String? = null

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

        binding = FragmentAdBinding.inflate(inflater, container, false)
        filterProductBottomSheetBinding = BottomFilterProductsBinding.inflate(layoutInflater)

        adsAdapter = UserAdsAdapter(requireContext(), ::deleteAction, ::editAction)
        binding?.recListOfAds?.adapter = adsAdapter

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


        binding?.imgFilter?.setOnClickListener {

            filterProductBottomSheet.show()
        }

        filterProductBottomSheetBinding.btnApplyFilter.setOnClickListener {

            filterProductBottomSheet.dismiss()
        }

        return binding?.root
    }

    override fun loadData() {
        // nothing to do
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getAds(HashMap())
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AdFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            UserAdsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun editAction(userAdItem: UserAdItem, position: Int) {

        getIntentExtension(EditAcquisitionActivity::class.java).run {

            putExtra(Constants.AD_ID, userAdItem.id)

            requireActivity().startActivity(this)
        }

    }

    private fun getAds(params: HashMap<String, String>) {

        collectFlow(mAdViewModel.getAds(params)) {

            (requireActivity() as BaseActivity).handelApiResult(it,
                onResultSuccess = {

                    it.resultData?.data?.let { adsData ->

                        adsAdapter.setData(adsData)
                    }

                })
        }
    }

    private fun deleteAction(userAdItem: UserAdItem, position: Int) {

        deleteAd(userAdItem.id.toString(), position)
    }


    private fun deleteAd(adId: String, position: Int) {

        collectFlow(mAdViewModel.deleteAd(adId)) {

            // binding?.swipeLayout?.isRefreshing = false
            // binding?.recSearchResultList?.showView(false)

            (requireActivity() as BaseActivity).handelApiResult(it,
                onResultSuccess = {

                    adsAdapter.deleteItem(position)

                })
        }
    }
}