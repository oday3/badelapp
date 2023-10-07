package com.badl.apps.android.appFeatures.appCommon.ui

import android.os.Bundle
import androidx.activity.viewModels
import com.badl.apps.android.R
import com.badl.apps.android.adapters.NotificationsAdapter
import com.badl.apps.android.baseClasses.ui.BaseActivity
import com.badl.apps.android.databinding.ActivityNotificationsBinding
import com.badl.apps.android.utils.AppUtil.collectFlow
import com.badl.apps.android.utils.NetworkUtils.handelApiResult
import com.badl.apps.android.utils.UiUtils.initializeMainToolBar
import com.badl.apps.android.utils.UiUtils.showView

class NotificationsActivity : BaseActivity() {

    private lateinit var binding: ActivityNotificationsBinding
    private lateinit var notificationsAdapter: NotificationsAdapter
    private val mViewModel by viewModels<AppCommonViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        initializeMainToolBar(getString(R.string.notifications))
        initAdapters()
        initViews()
        initListeners()


        getNotifications()

    }

    override fun initViews() {

        binding.recListOfNotification.adapter = notificationsAdapter
    }

    override fun initData() {
        // nothing to do
    }

    override fun initAdapters() {

        notificationsAdapter = NotificationsAdapter(this)
    }

    override fun initObservers() {
        // nothing to do
    }

    override fun initListeners() {
       // nothing to do
    }

    private fun getNotifications() {

        collectFlow(mViewModel.getNotifications()) {

            handelApiResult(it,
                onResultSuccess = {

                    it.resultData?.data?.let { returnedData ->

                        if (returnedData.isNotEmpty()) {

                            binding.consNotificationsActivityContent.showView(true)
                            notificationsAdapter.setData(returnedData)

                        } else {

                            showEmptyLayout()
                        }

                    }

                })
        }
    }


    private fun showEmptyLayout() {

        binding.consNotificationsActivityContent.showView(false)
        binding.layoutEmpty.constEmptyLayout.showView(true)
        binding.layoutEmpty.tvEmptyLayoutTitle.text = getString(R.string.no_notices)
        binding.layoutEmpty.tvEmptyLayoutMsg.text =
            getString(R.string.you_haven_t_received_any_notifications_yet)
        binding.layoutEmpty.imgEmptyLayoutIcon.setImageResource(R.drawable.ic_empty_notifications)
        binding.layoutEmpty.btnEmptyLayoutAction.showView(false)
    }
}