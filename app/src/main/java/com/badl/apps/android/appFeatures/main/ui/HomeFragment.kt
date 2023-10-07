package com.badl.apps.android.appFeatures.main.ui

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.badl.apps.android.R
import com.badl.apps.android.adapters.ProductSectionAdapter
import com.badl.apps.android.appFeatures.biddingAndBarterSystem.ui.ProductDetailsActivity
import com.badl.apps.android.appFeatures.main.data.ProductSectionItem
import com.badl.apps.android.appFeatures.main.data.SectionDataItem
import com.badl.apps.android.application.BadelApp
import com.badl.apps.android.baseClasses.ui.BaseActivity
import com.badl.apps.android.databinding.FragmentHomeBinding
import com.badl.apps.android.databinding.LayoutSectionProductsBinding
import com.badl.apps.android.network.ApiConstants
import com.badl.apps.android.utils.AppUtil.collectFlow
import com.badl.apps.android.utils.AppUtil.getIntentExtension
import com.badl.apps.android.utils.AppUtil.navToActivity
import com.badl.apps.android.utils.Constants
import com.badl.apps.android.utils.NetworkUtils
import com.badl.apps.android.utils.NetworkUtils.handelApiResult
import com.badl.apps.android.utils.UiUtils.getScreenDensity
import com.badl.apps.android.utils.UiUtils.setImage
import com.badl.apps.android.utils.UiUtils.setPrice
import com.badl.apps.android.utils.ValidationUtils.isUserLogin

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var binding: FragmentHomeBinding? = null
    private var param1: String? = null
    private var param2: String? = null
    private val mHomeViewModel by activityViewModels<MainViewModel>()
    private var mMostViewedItemId = -1
    private val favData = HashMap<String, String>()
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
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding?.swipeLayout?.setOnRefreshListener { getHomeData(true) }


        return binding?.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding?.constHomeFragMostViewedProduct?.setOnClickListener {

            getIntentExtension(ProductDetailsActivity::class.java).run {

                putExtra(Constants.ITEM_ID, mMostViewedItemId)
                putExtra(Constants.FROM, Constants.FROM_ORDER_DETAILS)
                startActivity(this)
            }

        }


        binding?.edtHomeFragSearch?.setOnClickListener {

            Intent(requireActivity(), AdListActivity::class.java).run {

                putExtra(Constants.FROM, Constants.FROM_HOME_SEARCH)
                putExtra(Constants.TOOLBAR_TITLE, getString(R.string.search))
                startActivity(this)
                requireActivity().overridePendingTransition(0,0)
            }
        }

//
//        binding?.scrollParent?.setOnScrollChangeListener { p0, p1, p2, p3, p4 ->
//            val margins = (binding?.tvBg?.layoutParams as ConstraintLayout.LayoutParams).apply {
//                leftMargin = 0
//                rightMargin = 0
//                topMargin = p2 * -1
//            }
//            binding?.tvBg?.layoutParams = margins
//        }
        getHomeData(true)
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
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

    private fun addProductSection(
        dataList: List<ProductSectionItem>,
        sectionName: String, sectionId: Int, orientation: String
    ) {

        val layoutBinding =
            LayoutSectionProductsBinding.inflate(LayoutInflater.from(requireActivity()))

        if (orientation == "1") {

            val linearLayoutManager =
                LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
            layoutBinding.recSectionLayoutListOfProducts.layoutManager = linearLayoutManager

        } else {

            val gridLayoutManager =
                GridLayoutManager(requireContext(), 2, RecyclerView.VERTICAL, false)
            layoutBinding.recSectionLayoutListOfProducts.layoutManager = gridLayoutManager
        }

        val dataAdapter =
            ProductSectionAdapter(
                requireContext(),
                ::clickAction,
                ::favAction,
                orientation, (requireActivity() as BaseActivity).sharedPrefUtils.getCurrentUserData()?.type?.toInt() ?: 1
            )

        layoutBinding.myAdapter = dataAdapter

        dataAdapter.setData(dataList)


        //layoutBinding.recSectionLayoutListOfProducts.adapter = dataAdapter

        layoutBinding.tvSectionLayoutSectionTitle.text = sectionName

        layoutBinding.tvSectionLayoutShowMore.setOnClickListener {

                getIntentExtension(AdListActivity::class.java).run {

                    putExtra(Constants.TOOLBAR_TITLE, sectionName)
                    putExtra(Constants.SECTION_ID, sectionId)
                      putExtra(Constants.FROM, Constants.FROM_SHOW_ALL)
                    startActivity(this)
                }
            }

        val layoutParams = ConstraintLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        layoutParams.setMargins(
            0,
            (15 * getScreenDensity()).toInt(),
            0,
            0
        )

        binding?.linearDynamicLayout?.childCount?.let {
            binding?.linearDynamicLayout?.addView(
                layoutBinding.root,
                it,
                layoutParams
            )
        }
    }


    override fun onResume() {
        super.onResume()

        if ((requireActivity().applicationContext as BadelApp).refreshMain) {

            getHomeData(true)

            (requireActivity().applicationContext as BadelApp).refreshMain = false
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)

        if (!hidden) {
            if ((requireActivity().applicationContext as BadelApp).refreshMain) {

                getHomeData(true)

                (requireActivity().applicationContext as BadelApp).refreshMain = false
            }
        }
    }
    private fun clickAction(id: Int) {

        getIntentExtension(ProductDetailsActivity::class.java).run {

            putExtra(Constants.ITEM_ID, id)
            putExtra(Constants.FROM, Constants.FROM_AD_DETAILS)

            startActivity(this)
        }
    }

        private fun getHomeData(showLoader: Boolean) {

            collectFlow(mHomeViewModel.getHomeData()) {

                // binding?.swipeLayout?.isRefreshing = false
                // binding?.recSearchResultList?.showView(false)
                binding?.swipeLayout?.isRefreshing = false

                (requireActivity() as BaseActivity).handelApiResult(it, onLoading = {

                    (requireActivity() as BaseActivity).showDialog(showLoader)
                },
                    onResultSuccess = {

                        it.resultData?.data?.let { homeDataItem ->

                            binding?.linearDynamicLayout?.removeAllViews()

                            homeDataItem.home_data?.let {
                                setUiData(it)
                            }

                            homeDataItem.most_viewed?.let {

                                mMostViewedItemId = it.id ?: -1
                                binding?.imgHomeFragProdImg?.setImage(it.image)
                                binding?.tvHomeFragProdName?.text = it.title
                                binding?.tvHomeFragProdPrice?.setPrice(it.price)
                            }
                        }

                    })
            }
        }

    private fun setUiData(data: List<SectionDataItem>) {

        for (section in data) {

            when (section.type_text) {

//                "banner" -> {
//
//                    section.banner_height?.let {
//
//                        addBannerSection(
//                            NetworkUtils.toListByGetClass(
//                                BannerSectionItem::class.java,
//                                section.items
//                            ), it.toInt()
//                        )
//                    }
//                }

                "ad" -> {

                    section.id?.let {

                        addProductSection(
                            dataList = NetworkUtils.toListByGetClass(
                                ProductSectionItem::class.java,
                                section.items
                            ),
                            sectionName = section.title ?: "",
                            sectionId = it,
                            orientation = section.appearance ?: "1"
                        )
                    }
                }
            }
        }
    }

    private fun favAction(
        position: Int,
        adapter: ProductSectionAdapter
    ) {

        if ((requireActivity() as BaseActivity).isUserLogin(true)) {

            adapter.getItem(position).id?.let {

                favData[ApiConstants.PAR_AD_ID] = it.toString()
                favProduct(adapter, position, favData)
            }
        }
    }


    private fun favProduct(
        adapter: ProductSectionAdapter,
        itemPosition: Int, favData: HashMap<String, String>
    ) {

        collectFlow(mHomeViewModel.favoriteProduct(favData)) {

            (requireActivity() as BaseActivity).handelApiResult(it, onLoading = {
                adapter.getItem(itemPosition).is_loading = true
                adapter.refreshItem(itemPosition)

            },
                onResultSuccess = {

                    it.resultData?.data?.let {

                        adapter.getItem(itemPosition).is_favorite = !(adapter.getItem(itemPosition).is_favorite ?: false)
                        adapter.getItem(itemPosition).is_loading = false
                        adapter.refreshItem(itemPosition)
                    }

                }, onResultFail = {
                    adapter.getItem(itemPosition).is_loading = false
                    adapter.refreshItem(itemPosition)
                })
        }
    }

}