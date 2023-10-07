package com.badl.apps.android.appFeatures.cart.ui

import android.os.Bundle
import com.badl.apps.android.R
import com.badl.apps.android.adapters.CartAdapter
import com.badl.apps.android.baseClasses.ui.BaseActivity
import com.badl.apps.android.databinding.ActivityCartBinding
import com.badl.apps.android.utils.UiUtils.initializeMainToolBar

class CartActivity : BaseActivity() {
    private lateinit var binding: ActivityCartBinding
    private lateinit var cartAdapter: CartAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeMainToolBar(getString(R.string.cart), showNotificationIcon = true)

        initAdapters()
        initViews()
    }

    override fun initViews() {

        binding.recListOfProducts.adapter = cartAdapter
    }

    override fun initData() {
        // nothing to do
    }

    override fun initAdapters() {

        cartAdapter = CartAdapter(this)
        cartAdapter.setData(arrayListOf(1,1))
    }

    override fun initObservers() {
        // nothing to do
    }

    override fun initListeners() {
        // nothing to do
    }
}