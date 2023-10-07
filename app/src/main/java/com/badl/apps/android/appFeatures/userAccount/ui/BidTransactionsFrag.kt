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
import com.badl.apps.android.adapters.TransactionsAdapter
import com.badl.apps.android.appFeatures.chat.ui.ChatActivity
import com.badl.apps.android.appFeatures.userAccount.data.TransactionItem
import com.badl.apps.android.baseClasses.ui.BaseActivity
import com.badl.apps.android.databinding.FragmentBidTransactionsBinding
import com.badl.apps.android.databinding.FragmentSwapTransactionsBinding
import com.badl.apps.android.network.ApiConstants
import com.badl.apps.android.utils.AppUtil.collectFlow
import com.badl.apps.android.utils.AppUtil.getIntentExtension
import com.badl.apps.android.utils.Constants
import com.badl.apps.android.utils.NetworkUtils.handelApiResult
import com.badl.apps.android.utils.UiUtils.showView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [BidTransactionsFrag.newInstance] factory method to
 * create an instance of this fragment.
 */
class BidTransactionsFrag : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var binding: FragmentBidTransactionsBinding? = null
    private lateinit var transactionsAdapter: TransactionsAdapter
    private val mAdViewModel by activityViewModels<UserAccountViewModel>()
    private val requestParameters = HashMap<String, String>()


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

        binding = FragmentBidTransactionsBinding.inflate(inflater, container, false)

        transactionsAdapter = TransactionsAdapter(requireContext(), ::chatAction, null)
        binding?.recListOfBidTransactions?.adapter = transactionsAdapter
        requestParameters[ApiConstants.PAR_FILTER_TYPE] = 1.toString()
        requestParameters[ApiConstants.PAR_PAGE] = mCurrentPage.toString()

        binding?.swipeLayout?.setOnRefreshListener {

            mCurrentPage = 1
            requestParameters[ApiConstants.PAR_PAGE] = mCurrentPage.toString()
            requestParameters.remove(ApiConstants.PAR_FILTER_ORDER)
            requestParameters.remove(ApiConstants.PAR_DATA)

            (requireActivity() as MyTransactionsActivity).resetUi()
            getUserSeals(requestParameters) }

        return binding?.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding?.recListOfBidTransactions?.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {

                    if ((binding?.recListOfBidTransactions?.layoutManager as LinearLayoutManager).itemCount > 0) {
                        if (!isLoading && hasMore && (binding?.recListOfBidTransactions?.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition() == transactionsAdapter.itemCount - 1) {
                            isLoading = true

                            requestParameters[ApiConstants.PAR_PAGE] = (++mCurrentPage).toString()

                            if ((requireActivity() as MyTransactionsActivity).isFilterSelected) {

                                requestParameters[ApiConstants.PAR_STATUS] = filterID.toString()
                            }

                            getUserSeals(requestParameters, false)
                        }
                    }
                }
            }
        })


        getUserSeals(requestParameters)
    }

    private fun chatAction(item: TransactionItem) {


        getIntentExtension(ChatActivity::class.java).run {

            putExtra(Constants.SEND_USER_ID, item.user_id)
            putExtra(Constants.OWNER_ID,(requireActivity() as BaseActivity).sharedPrefUtils.getCurrentUserData()?.id)
            putExtra(Constants.AD_ID, item.ad_id)
            putExtra(Constants.OWNER_IMAGE, item.user_image)

            if (item.has_chat == true) {


                putExtra(Constants.FIREBASE_KEY, item.firebase_key)
                putExtra(Constants.FROM, Constants.FROM_CHAT)
                startActivity(this)


            } else {


                putExtra(Constants.FROM, Constants.FROM_AD_DETAILS)
                startActivity(this)
            }
        }
    }


    private fun getUserSeals(params: HashMap<String, String>, refreshData: Boolean = true) {

        collectFlow(mAdViewModel.getUserSeals(params)) {

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


    private fun checkIsDataEmpty(adsData: List<TransactionItem>, refreshData: Boolean) {

        if (adsData.isNotEmpty()) {

            hideEmptyLayout()
            transactionsAdapter.setAdapterData(refreshData, adsData)


        } else {

            showEmptyLayout()
        }
    }

    private fun showEmptyLayout() {

        binding?.consSwapTransactionsFragContent?.showView(false)
        binding?.layoutEmpty?.constEmptyLayout?.showView(true)
        binding?.layoutEmpty?.tvEmptyLayoutTitle?.text =
            getString(R.string.there_are_no_transactions)
        binding?.layoutEmpty?.imgEmptyLayoutIcon?.setImageResource(R.drawable.ic_empty_ads)
        binding?.layoutEmpty?.btnEmptyLayoutAction?.showView(false)
    }

    private fun hideEmptyLayout() {
        binding?.consSwapTransactionsFragContent?.showView(true)
        binding?.recListOfBidTransactions?.showView(true)
        binding?.layoutEmpty?.constEmptyLayout?.showView(false)

    }

    fun executeFilterAction (filterID: Int) {

        this.filterID = filterID

        if (filterID == -1) {

            requestParameters.remove(ApiConstants.PAR_FILTER_ORDER)
            // isFilterSelected = false

        } else {

            requestParameters[ApiConstants.PAR_FILTER_ORDER] = filterID.toString()
            // isFilterSelected = true
        }

        mCurrentPage = 1
        requestParameters[ApiConstants.PAR_PAGE] = mCurrentPage.toString()


        getUserSeals(requestParameters)

    }

    fun executeSearchAction (query: String) {


        mCurrentPage = 1
        requestParameters[ApiConstants.PAR_DATA] = query
        requestParameters[ApiConstants.PAR_PAGE] = mCurrentPage.toString()
        requestParameters.remove(ApiConstants.PAR_FILTER_ORDER)

        // searchForProduct(params, true)
        getUserSeals(requestParameters)

    }

    fun clearSearchAction () {

        mCurrentPage = 1
        requestParameters.remove(ApiConstants.PAR_FILTER_ORDER)
        requestParameters.remove(ApiConstants.PAR_DATA)
        requestParameters[ApiConstants.PAR_PAGE] = mCurrentPage.toString()

        getUserSeals(requestParameters)

    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment BidTransactionsFrag.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            BidTransactionsFrag().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}