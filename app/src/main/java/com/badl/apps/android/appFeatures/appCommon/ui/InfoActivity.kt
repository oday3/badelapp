package com.badl.apps.android.appFeatures.appCommon.ui

import android.os.Bundle
import android.text.Html
import androidx.activity.viewModels
import com.badl.apps.android.baseClasses.ui.BaseActivity
import com.badl.apps.android.databinding.ActivityInfoBinding
import com.badl.apps.android.utils.AppUtil.collectFlow
import com.badl.apps.android.utils.Constants
import com.badl.apps.android.utils.NetworkUtils.handelApiResult
import com.badl.apps.android.utils.UiUtils.initializeMainToolBar

class InfoActivity : BaseActivity() {
    private lateinit var binding: ActivityInfoBinding
    private var from = ""
    private var toolbarTitle = ""
    private val mViewModel by viewModels<AppCommonViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.hasExtra(Constants.FROM)) {

            from = intent.getStringExtra(Constants.FROM).toString()
        }

        if (intent.hasExtra(Constants.TOOLBAR_TITLE)) {

            toolbarTitle = intent.getStringExtra(Constants.TOOLBAR_TITLE).toString()
        }

        initializeMainToolBar(toolbarTitle)


        when(from) {

            Constants.FROM_TERMS -> {

                getTerms()
            }

            Constants.FROM_APP_PRIVACY -> {

                getPrivacy()
            }
            Constants.FROM_ABOUT_US -> {

                aboutApp()
            }

        }
    }

    override fun initViews() {
        // nothing to do
    }

    override fun initData() {
        // nothing to do
    }

    override fun initAdapters() {
        // nothing to do
    }

    override fun initObservers() {
        // nothing to do
    }

    override fun initListeners() {
        // nothing to do
    }

    private fun getPrivacy() {

        collectFlow(mViewModel.getPrivacy()) {

            // binding?.swipeLayout?.isRefreshing = false
            // binding?.recSearchResultList?.showView(false)

            handelApiResult(it,
                onResultSuccess = {

                    it.resultData?.data?.let { data ->

                        binding.tvCommonText.text = Html.fromHtml(
                            data.value,
                            Html.FROM_HTML_MODE_COMPACT
                        )

                    }
                })
        }
    }

    private fun getTerms() {

        collectFlow(mViewModel.getTerms()) {

            // binding?.swipeLayout?.isRefreshing = false
            // binding?.recSearchResultList?.showView(false)

            handelApiResult(it,
                onResultSuccess = {

                    it.resultData?.data?.let { data ->

                        binding.tvCommonText.text = Html.fromHtml(
                            data.value,
                            Html.FROM_HTML_MODE_COMPACT
                        )

                    }
                })
        }
    }

    private fun aboutApp() {

        collectFlow(mViewModel.aboutApp()) {

            // binding?.swipeLayout?.isRefreshing = false
            // binding?.recSearchResultList?.showView(false)

            handelApiResult(it,
                onResultSuccess = {

                    it.resultData?.data?.let { data ->

                        binding.tvCommonText.text = Html.fromHtml(
                            data.value,
                            Html.FROM_HTML_MODE_COMPACT
                        )

                    }
                })
        }
    }

}