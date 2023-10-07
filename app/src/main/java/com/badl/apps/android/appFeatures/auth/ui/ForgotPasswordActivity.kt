package com.badl.apps.android.appFeatures.auth.ui

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.content.res.AppCompatResources
import com.badl.apps.android.R
import com.badl.apps.android.appFeatures.main.ui.MainActivity
import com.badl.apps.android.baseClasses.ui.BaseActivity
import com.badl.apps.android.databinding.ActivityForgotPasswordBinding
import com.badl.apps.android.databinding.DialogEnterCodeBinding
import com.badl.apps.android.databinding.DialogOfferDoneBinding
import com.badl.apps.android.network.ApiConstants
import com.badl.apps.android.utils.AppUtil.collectFlow
import com.badl.apps.android.utils.AppUtil.getIntentExtension
import com.badl.apps.android.utils.AppUtil.navToActivity
import com.badl.apps.android.utils.Constants
import com.badl.apps.android.utils.GenericKeyEvent
import com.badl.apps.android.utils.GenericTextWatcher
import com.badl.apps.android.utils.NetworkUtils.handelApiResult
import com.badl.apps.android.utils.UiUtils
import com.badl.apps.android.utils.UiUtils.extensionSetBackground
import com.badl.apps.android.utils.UiUtils.getScreenDensity
import com.badl.apps.android.utils.UiUtils.setTransparentStatusBar
import com.badl.apps.android.utils.UiUtils.showCustomToast
import com.badl.apps.android.utils.UiUtils.showView
import com.badl.apps.android.utils.ValidationUtils.validateEmails
import com.badl.apps.android.utils.ValidationUtils.validateEmpty

class ForgotPasswordActivity : BaseActivity(), View.OnTouchListener {
    private lateinit var binding: ActivityForgotPasswordBinding
    private val mViewModel by viewModels<AuthViewModel>()
    private lateinit var codeDialog: Dialog
    private lateinit var codeDialogBinding: DialogEnterCodeBinding

    private lateinit var doneDialog: Dialog
    private lateinit var doneDialogBinding: DialogOfferDoneBinding

    private var code = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        doneDialogBinding = DialogOfferDoneBinding.inflate(layoutInflater)
        codeDialogBinding = DialogEnterCodeBinding.inflate(layoutInflater)

        setContentView(binding.root)

        setTransparentStatusBar()

        initViews()
        initListeners()
    }

    override fun initViews() {

        codeDialogBinding.tvMobileNumber.showView(false)
        codeDialogBinding.tvDescription.showView(false)

        codeDialogBinding.tvTitle.text = getString(R.string.we_have_sent_the_verification_code_to_your_email)

        codeDialog =
            UiUtils.createDialog(
                this,
                themeResId = R.style.TransparentAlertDialog,
                windowAnimationResId = R.style.SlideDialogAnimation,
                true, codeDialogBinding.root
            )

        codeDialog.extensionSetBackground(
            (getScreenDensity() * 14).toInt(),
            AppCompatResources.getDrawable(this, R.drawable.bg_dialog_),
        )


        doneDialog =
            UiUtils.createDialog(
                this,
                themeResId = R.style.TransparentAlertDialog,
                windowAnimationResId = R.style.SlideDialogAnimation,
                true, doneDialogBinding.root
            )

        doneDialog.extensionSetBackground(
            (getScreenDensity() * 14).toInt(),
            AppCompatResources.getDrawable(this, R.drawable.bg_dialog_),
        )

        doneDialogBinding.tvDescription.text =
            getString(R.string.a_link_has_been_sent_to_you_to_set_the_password)
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

        initTextWatcher()
        setImeOptions()
        setClickListener()

        binding.btnResetPassword.setOnClickListener {

            if (validateEmpty(binding.tvinEmail) && validateEmails(binding.tvinEmail)) {

                val data = HashMap<String,String>()
                data[ApiConstants.PAR_EMAIL] = binding.edtEmail.text.toString()

                forgotPassword(data)
            }
        }

        binding.imgBack.setOnClickListener {
            finish()
        }


        codeDialogBinding.btnConfirm.setOnClickListener {


            code = codeDialogBinding.edtCode1.text.toString() +
                    codeDialogBinding.edtCode2.text.toString() +
                    codeDialogBinding.edtCode3.text.toString() +
                    codeDialogBinding.edtCode4.text.toString()

            if (code.length == 4) {

                val parmas = HashMap<String,String>()
                parmas[ApiConstants.PAR_EMAIL] = binding.edtEmail.text.toString()
                parmas[ApiConstants.PAR_CODE] = code
                verifyResetForgotPassword(parmas)
            } else {

                showCustomToast(getString(R.string.please_enter_code),0)
            }
        }


        codeDialogBinding.tvResendCode.setOnClickListener {

            val parmas = HashMap<String,String>()
            parmas[ApiConstants.PAR_EMAIL] = binding.edtEmail.text.toString()
            parmas[ApiConstants.PAR_DEVICE] = "android"
            parmas[ApiConstants.PAR_FCM_TOKEN] = sharedPrefUtils.fcmDeviceToken.toString()
            resendCode(parmas)
        }
    }

    private fun forgotPassword(data: Map<String, String>) {

        collectFlow(mViewModel.forgotPassword(data)) {

            handelApiResult(it, onResultSuccess = {

                it.resultData?.data?.let {

                    codeDialog.show()
                }
            })
        }
    }

    private fun initTextWatcher() {
        codeDialogBinding.apply {
            //GenericTextWatcher here works only for moving to next EditText when a number is entered
            //first parameter is the current EditText and second parameter is next EditText
            edtCode1.addTextChangedListener(
                GenericTextWatcher(
                    edtCode1,
                    edtCode2,
                    edtCode1,
                    edtCode2,
                    edtCode3,
                    edtCode4
                )
            )
            edtCode2.addTextChangedListener(
                GenericTextWatcher(
                    edtCode2,
                    edtCode3,
                    edtCode1,
                    edtCode2,
                    edtCode3,
                    edtCode4
                )
            )
            edtCode3.addTextChangedListener(
                GenericTextWatcher(
                    edtCode3,
                    edtCode4,
                    edtCode1,
                    edtCode2,
                    edtCode3,
                    edtCode4
                )
            )
            edtCode4.addTextChangedListener(
                GenericTextWatcher(
                    edtCode4,
                    null,
                    edtCode1,
                    edtCode2,
                    edtCode3,
                    edtCode4
                )
            )

            //GenericKeyEvent here works for deleting the element and to switch back to previous EditText
            //first parameter is the current EditText and second parameter is previous EditText
            edtCode1.setOnKeyListener(GenericKeyEvent(edtCode1, null, edtCode1))
            edtCode2.setOnKeyListener(GenericKeyEvent(edtCode2, edtCode1, edtCode1))
            edtCode3.setOnKeyListener(GenericKeyEvent(edtCode3, edtCode2, edtCode1))
            edtCode4.setOnKeyListener(GenericKeyEvent(edtCode4, edtCode3, edtCode1))
        }

    }

    private fun setImeOptions() {
        val editorActionListener = TextView.OnEditorActionListener { v, p1, p2 ->
            when (v?.id) {
                R.id.edt_code1 -> {
                    codeDialogBinding.edtCode2.requestFocus()
                }
                R.id.edt_code2 -> {
                    codeDialogBinding.edtCode3.requestFocus()
                }
                R.id.edt_code3 -> {
                    codeDialogBinding.edtCode4.requestFocus()
                }
            }
            true
        }

        codeDialogBinding.edtCode1.setOnEditorActionListener(editorActionListener)
        codeDialogBinding.edtCode2.setOnEditorActionListener(editorActionListener)
        codeDialogBinding.edtCode3.setOnEditorActionListener(editorActionListener)
    }

    private fun setClickListener() {

        codeDialogBinding.edtCode1.setOnTouchListener(this)
        codeDialogBinding.edtCode2.setOnTouchListener(this)
        codeDialogBinding.edtCode3.setOnTouchListener(this)
        codeDialogBinding.edtCode4.setOnTouchListener(this)
    }

    override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {

        if (!TextUtils.isEmpty((p0 as EditText).text.toString())) {

            p0.requestFocus()

            when (p0.id) {

                codeDialogBinding.edtCode1.id -> {

                    codeDialogBinding.edtCode1.setText("")

                    return true
                }


                codeDialogBinding.edtCode2.id -> {

                    codeDialogBinding.edtCode2.setText("")
                    return true
                }


                codeDialogBinding.edtCode3.id -> {

                    codeDialogBinding.edtCode3.setText("")
                    return true
                }

                codeDialogBinding.edtCode4.id -> {

                    codeDialogBinding.edtCode4.setText("")
                    return true
                }
            }
        }

        return false
    }


    private fun verifyResetForgotPassword(data: Map<String, String>) {

        collectFlow(mViewModel.verifyResetForgotPassword(data)) {

            handelApiResult(it, onResultSuccess = {

                it.resultData?.data?.let {

                    codeDialog.dismiss()
                    getIntentExtension(UpdatePassowordActivity::class.java).run {
                        putExtra(Constants.CODE, it.toString())
                        putExtra(Constants.EMAIL, binding.edtEmail.text.toString())
                        startActivity(this)
                    }
                }
            })
        }
    }

    private fun resendCode(data: Map<String, String>) {

        collectFlow(mViewModel.resendCode(data)) {

            handelApiResult(it, onResultSuccess = {

            })
        }
    }
}