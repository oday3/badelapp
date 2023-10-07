package com.badl.apps.android.appFeatures.auth.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import com.badl.apps.android.appFeatures.appCommon.ui.IntroActivity
import com.badl.apps.android.baseClasses.ui.BaseActivity
import com.badl.apps.android.databinding.ActivityUpdatePassowordBinding
import com.badl.apps.android.network.ApiConstants
import com.badl.apps.android.utils.AppUtil.collectFlow
import com.badl.apps.android.utils.AppUtil.getIntentExtension
import com.badl.apps.android.utils.Constants
import com.badl.apps.android.utils.NetworkUtils.handelApiResult
import com.badl.apps.android.utils.UiUtils.setTransparentStatusBar
import com.badl.apps.android.utils.ValidationUtils.validateEmpty
import com.badl.apps.android.utils.ValidationUtils.validatePassword
import com.badl.apps.android.utils.ValidationUtils.validatePasswordMatches

class UpdatePassowordActivity : BaseActivity() {
    private lateinit var binding: ActivityUpdatePassowordBinding
    private val mViewModel by viewModels<AuthViewModel>()
    private var mCode = ""
    private var mEmail = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdatePassowordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setTransparentStatusBar()

        if (intent.hasExtra(Constants.CODE)) {

            mCode = intent.getStringExtra(Constants.CODE).toString()
        }

        if (intent.hasExtra(Constants.EMAIL)) {

            mEmail = intent.getStringExtra(Constants.EMAIL).toString()
        }
        initListeners()
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

        binding.imgBack.setOnClickListener {
            finish()
        }

        binding.btnUpdatePassword.setOnClickListener {

            if (validateEmpty(binding.tvinPassword, binding.tvinConfirmPassword)) {

                if (validatePassword(binding.tvinPassword, binding.tvinConfirmPassword)) {

                    if (validatePasswordMatches(binding.tvinPassword, binding.tvinConfirmPassword)) {

                        val data = HashMap<String,String>()
                        data[ApiConstants.PAR_EMAIL] = mEmail
                        data[ApiConstants.PAR_CODE] = mCode
                        data[ApiConstants.PAR_PASSWORD] = binding.edtPassword.text.toString()
                        data[ApiConstants.PAR_CONFIRM_PASSWORD] = binding.edtConfirmPassword.text.toString()
                        resetPassword(data)
                    }
                }
            }
        }

    }


    private fun resetPassword(data: Map<String, String>) {

        collectFlow(mViewModel.resetPassword(data)) {

            handelApiResult(it, onResultSuccess = {

                it.resultData?.data?.let {

                    val intent = Intent(this, LoginActivity::class.java)
                    intent.addFlags(
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
                                or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    )
                    startActivity(intent)
                    finishAffinity()
                }
            })
        }
    }

}