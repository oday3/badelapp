package com.badl.apps.android.appFeatures.userAccount.ui

import android.os.Bundle
import androidx.activity.viewModels
import com.badl.apps.android.R
import com.badl.apps.android.adapters.FavoritesAdapter
import com.badl.apps.android.application.BadelApp
import com.badl.apps.android.baseClasses.ui.BaseActivity
import com.badl.apps.android.databinding.ActivityFollowingListBinding
import com.badl.apps.android.network.ApiConstants
import com.badl.apps.android.utils.AppUtil.collectFlow
import com.badl.apps.android.utils.NetworkUtils.handelApiResult
import com.badl.apps.android.utils.UiUtils.initializeMainToolBar

class FollowingListActivity : BaseActivity() {
    private lateinit var binding: ActivityFollowingListBinding
    private lateinit var adapter: FavoritesAdapter
    private val favData = HashMap<String, String>()
    private val mViewModel by viewModels<UserAccountViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFollowingListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeMainToolBar(getString(R.string.following_list))

        initAdapters()
        initViews()
        getFavorites()
    }

    override fun initViews() {

        binding.recListOfFavorites.adapter = adapter
    }

    override fun initData() {
        // nothing to do
    }

    override fun initAdapters() {

        adapter = FavoritesAdapter(this, ::favAction)
        binding.myAdapter = adapter
    }

    override fun initObservers() {
        // nothing to do
    }

    override fun initListeners() {
        // nothing to do
    }


    private fun favAction(
        position: Int,
    ) {

        adapter.getItem(position).id?.let {

            favData[ApiConstants.PAR_AD_ID] = it.toString()
            favProduct(position, favData)
        }

    }


    private fun getFavorites() {

        collectFlow(mViewModel.getFavorites()) {

            // binding?.swipeLayout?.isRefreshing = false
            // binding?.recSearchResultList?.showView(false)
            handelApiResult(it,
                onResultSuccess = {

                    it.resultData?.data?.let { favoritesData ->

                        adapter.setData(favoritesData)
                    }

                })
        }
    }


    private fun favProduct(
        itemPosition: Int, favData: HashMap<String, String>
    ) {

        collectFlow(mViewModel.favoriteProduct(favData)) {

            (applicationContext as BadelApp).refreshMain = true
            handelApiResult(it, onLoading = {
                adapter.getItem(itemPosition).is_loading = true
                adapter.refreshItem(itemPosition)

            },
                onResultSuccess = {

                    it.resultData?.data?.let {

                        adapter.getItem(itemPosition).is_favorite = !(adapter.getItem(itemPosition).is_favorite ?: false)
                        adapter.getItem(itemPosition).is_loading = false
                        adapter.deleteItem(itemPosition)
                    }

                }, onResultFail = {
                    adapter.getItem(itemPosition).is_loading = false
                    adapter.refreshItem(itemPosition)

                })
        }
    }
}