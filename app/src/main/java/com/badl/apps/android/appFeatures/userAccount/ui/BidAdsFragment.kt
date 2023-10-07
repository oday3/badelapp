package com.badl.apps.android.appFeatures.userAccount.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.badl.apps.android.R
import com.badl.apps.android.adapters.MyAdsAdapter
import com.badl.apps.android.appFeatures.biddingAndBarterSystem.ui.BiddingHistoryActivity
import com.badl.apps.android.appFeatures.userAccount.data.UserAdItem
import com.badl.apps.android.baseClasses.ui.BaseActivity
import com.badl.apps.android.databinding.FragmentBidAdsBinding
import com.badl.apps.android.network.ApiConstants
import com.badl.apps.android.utils.AppUtil.collectFlow
import com.badl.apps.android.utils.AppUtil.getIntentExtension
import com.badl.apps.android.utils.AppUtil.navToActivity
import com.badl.apps.android.utils.Constants
import com.badl.apps.android.utils.NetworkUtils.handelApiResult
import com.badl.apps.android.utils.UiUtils.showView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [BidAdsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BidAdsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var binding: FragmentBidAdsBinding? = null
    private lateinit var adsAdapter: MyAdsAdapter
    private val mAdViewModel by activityViewModels<UserAccountViewModel>()
    private val adParameters = HashMap<String, String>()

    private var hasMore = false
    private var isLoading = false
    private var mCurrentPage = 1
    private var filterID = -1

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
        binding = FragmentBidAdsBinding.inflate(inflater, container, false)

        adsAdapter = MyAdsAdapter(requireContext(), 1, ::clickAction, ::editAction, ::deleteAction, ::checkIsDataEmpty)
        binding?.recListOfBidAds?.adapter = adsAdapter
        adParameters[ApiConstants.PAR_FILTER_TYPE] = 1.toString()
        adParameters[ApiConstants.PAR_PAGE] = mCurrentPage.toString()

        binding?.swipeLayout?.setOnRefreshListener {

            mCurrentPage = 1
            adParameters.remove(ApiConstants.PAR_STATUS)
            adParameters.remove(ApiConstants.PAR_DATA)
            adParameters[ApiConstants.PAR_PAGE] = mCurrentPage.toString()
            (requireActivity() as MyAdsActivity).resetUi()
            getAds(adParameters) }

        return binding?.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding?.recListOfBidAds?.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {

                    if ((binding?.recListOfBidAds?.layoutManager as LinearLayoutManager).itemCount > 0) {
                        if (!isLoading && hasMore && (binding?.recListOfBidAds?.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition() == adsAdapter.itemCount - 1) {
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



    private fun clickAction(position: Int) {

        getIntentExtension(BiddingHistoryActivity::class.java).run {

            putExtra(Constants.FROM, Constants.FROM_AD_DETAILS)
            putExtra(Constants.AD_ID, adsAdapter.getItem(position)?.id)

            startActivity(this)
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

    private fun editAction(position: Int) {

        getIntentExtension(EditAcquisitionActivity::class.java).run {

            putExtra(Constants.AD_ID, adsAdapter.getItem(position)?.id)

            requireActivity().startActivity(this)
        }

    }

    private fun deleteAction(position: Int, adItem: UserAdItem, adapter: MyAdsAdapter) {


        adItem.id?.let {

            (requireActivity() as MyAdsActivity).showDeleteAd(position, it, adapter)

        }

    }


    private fun getAds(params: HashMap<String, String>, refreshData: Boolean = true, hideAllLayout: Boolean = false) {

        collectFlow(mAdViewModel.getAds(params)) {

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

    private fun deleteAd(adID: Int) {

        collectFlow(mAdViewModel.deleteAd(adID.toString())) {

            (requireActivity() as BaseActivity).handelApiResult(it,
                onResultSuccess = {

                    it.resultData?.data?.let {

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
         * @return A new instance of fragment BidAdsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            BidAdsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


    private fun showEmptyLayout() {

        binding?.consBidAdsFragContent?.showView(false)
        binding?.layoutEmpty?.constEmptyLayout?.showView(true)
        binding?.layoutEmpty?.tvEmptyLayoutTitle?.text = getString(R.string.there_are_no_ads_to_view)
        binding?.layoutEmpty?.tvEmptyLayoutMsg?.text = getString(R.string.sorry_there_are_no_ads_to_view_currently_you_can_add_a_product_now)
        binding?.layoutEmpty?.imgEmptyLayoutIcon?.setImageResource(R.drawable.ic_empty_ads)
        binding?.layoutEmpty?.btnEmptyLayoutAction?.showView(false)
    }

    private fun hideEmptyLayout() {
        binding?.consBidAdsFragContent?.showView(true)
        binding?.recListOfBidAds?.showView(true)
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