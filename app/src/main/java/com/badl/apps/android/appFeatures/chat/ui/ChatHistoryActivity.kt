package com.badl.apps.android.appFeatures.chat.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.badl.apps.android.R
import com.badl.apps.android.adapters.ChatsHistoryAdapter
import com.badl.apps.android.adapters.FilterChatHistoryAdapter
import com.badl.apps.android.appFeatures.biddingAndBarterSystem.ui.BiddingBarterViewModel
import com.badl.apps.android.appFeatures.chat.data.FilterChatItem
import com.badl.apps.android.baseClasses.ui.BaseActivity
import com.badl.apps.android.databinding.ActivityChatHistoryBinding
import com.badl.apps.android.network.ApiConstants
import com.badl.apps.android.utils.AppUtil.collectFlow
import com.badl.apps.android.utils.Constants
import com.badl.apps.android.utils.NetworkUtils.handelApiResult
import com.badl.apps.android.utils.UiUtils.showCustomToast

class ChatHistoryActivity : BaseActivity() {

    private lateinit var binding: ActivityChatHistoryBinding
    private lateinit var chatFilterAdapter: FilterChatHistoryAdapter
    private lateinit var chatHistoryAdapter: ChatsHistoryAdapter
    private val mViewModel by viewModels<ChatViewModel>()
    private val params = HashMap<String, String>()
    private var firstLoad = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatHistoryBinding.inflate(layoutInflater)

        setContentView(binding.root)

        initAdapters()
        initViews()
        initObservers()

        getChats(params, true)
    }

    override fun initViews() {

        binding.recChatsFilter.adapter = chatFilterAdapter
        binding.recListOfChats.adapter = chatHistoryAdapter
    }

    override fun initData() {
        // nothing to do
    }

    override fun initAdapters() {

        chatHistoryAdapter = ChatsHistoryAdapter(this)

        chatFilterAdapter = FilterChatHistoryAdapter(this, false, 0)

//        chatFilterAdapter.setData(arrayListOf(
//            FilterChatItem(1,getString(R.string.bidding_list), 6),
//            FilterChatItem(2, getString(R.string.swap_list), 2),
//            FilterChatItem(3, getString(R.string.buy_list), 10),
//
//        ))
    }

    override fun initObservers() {

        chatFilterAdapter.itemSelected.observe(this) {

            it.id?.let {

                params[ApiConstants.PAR_TYPE] = it.toString()
                getChats(params)
            }
        }
    }

    override fun initListeners() {
        // nothing to do
    }


    private fun getChats(params: HashMap<String, String>, refreshFilter: Boolean = false) {

        collectFlow(mViewModel.getChats(params)) {

            handelApiResult(it,
                onResultSuccess = {

                    it.resultData?.data?.let { data ->

                        if (refreshFilter) {

                            chatFilterAdapter.setData(data.chat_ad_types as ArrayList)

                        }

                        chatHistoryAdapter.setData(data.chats as ArrayList)

                        binding.tvAllChatsLabel.text = getString(R.string.holder_all_chats, data.chats.size.toString())

                        if (firstLoad) {

                            binding.tvClientsNum.text = getString(R.string.holder_client_num, data.chats_count.toString())
                        }

                        firstLoad = false
                    }
                })
        }
    }
}