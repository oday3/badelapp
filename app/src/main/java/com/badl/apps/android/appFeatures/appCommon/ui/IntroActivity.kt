package com.badl.apps.android.appFeatures.appCommon.ui

import android.os.Bundle
import com.badl.apps.android.appFeatures.auth.ui.LoginActivity
import com.badl.apps.android.appFeatures.auth.ui.RegisterActivity
import com.badl.apps.android.appFeatures.main.ui.MainActivity
import com.badl.apps.android.baseClasses.ui.BaseActivity
import com.badl.apps.android.databinding.ActivityIntroBinding
import com.badl.apps.android.utils.AppUtil.navToActivity
import com.badl.apps.android.utils.UiUtils.setCompactBottomInset
import com.badl.apps.android.utils.UiUtils.setTransparentStatusBar

class IntroActivity : BaseActivity() {
    private lateinit var binding: ActivityIntroBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setTransparentStatusBar(true)
        initViews()
        initListeners()
    }

    override fun initViews() {

        binding.linearAuthActions.setCompactBottomInset()
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

        binding.btnLogin.setOnClickListener {

            navToActivity(LoginActivity::class.java)
           // finish()
        }

        binding.btnRegister.setOnClickListener {

            navToActivity(RegisterActivity::class.java)
            //finish()
        }


        binding.tvSkip.setOnClickListener {

            navToActivity(MainActivity::class.java)
        }

    }
}