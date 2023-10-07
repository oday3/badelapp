package com.badl.apps.android.appFeatures.appCommon.ui

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.badl.apps.android.appFeatures.auth.ui.LoginActivity
import com.badl.apps.android.appFeatures.main.ui.MainActivity
import com.badl.apps.android.baseClasses.ui.BaseActivity
import com.badl.apps.android.databinding.ActivitySplashBinding
import com.badl.apps.android.utils.AppUtil.navToActivity
import com.badl.apps.android.utils.UiUtils.setTransparentStatusBar
import com.badl.apps.android.utils.ValidationUtils.isPermissionGranted
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : BaseActivity() {
    private lateinit var binding: ActivitySplashBinding
    private val requestPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->

            //(requireActivity() as BaseActivity).showDialog(false)
            if (isGranted) { // permission is granted

            }
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setTransparentStatusBar()


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            if (!isPermissionGranted(Manifest.permission.POST_NOTIFICATIONS)) {

                requestPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
            }

        }

        lifecycleScope.launch {

            //binding.prog.showView(true)
             delay(1500)

            if(sharedPrefUtils.getCurrentUserData() != null) {

                navToActivity(MainActivity::class.java,false)

            } else {

                navToActivity(IntroActivity::class.java,false)

            }


            finish()
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
}