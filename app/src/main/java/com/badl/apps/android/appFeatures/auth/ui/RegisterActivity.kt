package com.badl.apps.android.appFeatures.auth.ui

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.EditText
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.content.res.AppCompatResources
import com.badl.apps.android.R
import com.badl.apps.android.adapters.LangAdapter
import com.badl.apps.android.appFeatures.appCommon.data.LanguageItem
import com.badl.apps.android.appFeatures.main.ui.MainActivity
import com.badl.apps.android.baseClasses.ui.BaseActivity
import com.badl.apps.android.databinding.ActivityRegisterBinding
import com.badl.apps.android.databinding.DialogEnterCodeBinding
import com.badl.apps.android.network.ApiConstants
import com.badl.apps.android.utils.AppUtil.collectFlow
import com.badl.apps.android.utils.AppUtil.navToActivity
import com.badl.apps.android.utils.GenericKeyEvent
import com.badl.apps.android.utils.GenericTextWatcher
import com.badl.apps.android.utils.LocaleLanguageUtils
import com.badl.apps.android.utils.NetworkUtils.handelApiResult
import com.badl.apps.android.utils.UiUtils
import com.badl.apps.android.utils.UiUtils.extensionSetBackground
import com.badl.apps.android.utils.UiUtils.getScreenDensity
import com.badl.apps.android.utils.UiUtils.showCustomToast
import com.badl.apps.android.utils.ValidationUtils.validateEmails
import com.badl.apps.android.utils.ValidationUtils.validateEmpty
import com.badl.apps.android.utils.ValidationUtils.validateMobileIntro
import com.badl.apps.android.utils.ValidationUtils.validatePassword
import com.badl.apps.android.utils.ValidationUtils.validatePasswordMatches
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import org.json.JSONException

class RegisterActivity : BaseActivity(), View.OnTouchListener {
    private lateinit var binding: ActivityRegisterBinding
    private val mRegisterData = HashMap<String, String>()
    private val mViewModel by viewModels<AuthViewModel>()
    private var code = ""

    private lateinit var codeDialog: Dialog
    private lateinit var codeDialogBinding: DialogEnterCodeBinding

    private lateinit var mGoogleSignInOptions: GoogleSignInOptions
    private lateinit var mGoogleSignInClient: GoogleSignInClient

    private lateinit var mFacebookCallbackManager: CallbackManager
    private lateinit var mFacebookLoginManager: LoginManager

    private lateinit var languageList: MutableList<LanguageItem>
    private var firstLoad = true

    private var someActivityResultLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            ActivityResultCallback<ActivityResult> { result ->
                Log.e("login_result_result_code", result.resultCode.toString())

                if (result.resultCode == RESULT_OK) {

                    result?.data?.let {

                        val task = GoogleSignIn.getSignedInAccountFromIntent(it)

                        task.addOnCompleteListener { task ->
                            try {
                                val googleSignInAccount = task.getResult(
                                    ApiException::class.java
                                )
                                Log.e(
                                    "login_result",
                                    "onActivityResult" + googleSignInAccount.email
                                )
                                handleSignInResult(googleSignInAccount)
                            } catch (e: ApiException) {
                                Log.e("login_result", "onActivityResult" + e.message)
                            }
                        }
                    }
                }
            })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        codeDialogBinding = DialogEnterCodeBinding.inflate(layoutInflater)

        setContentView(binding.root)

        initAdapters()
        initData()
        initViews()
        initListeners()
    }

    override fun initViews() {

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
    }

    override fun initData() {

        mGoogleSignInOptions =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN) //.requestScopes(myScope)
                //  .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, mGoogleSignInOptions)


        mFacebookCallbackManager = CallbackManager.Factory.create()
        mFacebookLoginManager = LoginManager.getInstance()
    }

    override fun initAdapters() {

        languageList = ArrayList<LanguageItem>()
        languageList.add(
            LanguageItem(
                "en",
                "EN",
                getString(R.string.english),
                R.drawable.img_america_flag
            )
        )
        languageList.add(
            LanguageItem(
                "ar",
                "AR",
                getString(R.string.arabic),
                R.drawable.ic_saudiarabia
            )
        )


        val languageAdapter = LangAdapter(this, R.layout.list_item_language_spr,languageList,)
        // languageAdapter.setDropDownViewResource()
        binding.sprLanguages.adapter = languageAdapter
    }

    override fun initObservers() {
        // nothing to do
    }

    override fun initListeners() {

        initTextWatcher()
        setImeOptions()
        setClickListener()

        binding.btnRegister.setOnClickListener {

            if (validateEmpty(
                    binding.tvinName,
                    binding.tvinEmail,
                    binding.tvinMobileNumber,
                    binding.tvinPassword,
                    binding.tvinConfirmPassword
                )
            ) {

                if (validateEmails(binding.tvinEmail)) {

                    if (validateMobileIntro(binding.tvinMobileNumber)) {

                        if (validatePassword(binding.tvinPassword, binding.tvinConfirmPassword)) {

                            if (validatePasswordMatches(binding.tvinPassword, binding.tvinConfirmPassword)) {

                                mRegisterData[ApiConstants.PAR_NAME] = binding.edtName.text.toString()
                                mRegisterData[ApiConstants.PAR_EMAIL] = binding.edtEmail.text.toString()
                                mRegisterData[ApiConstants.PAR_MOBILE] = "${binding.edtMobileNumber.text.toString()}"
                                mRegisterData[ApiConstants.PAR_PASSWORD] = binding.edtPassword.text.toString()
                                mRegisterData[ApiConstants.PAR_CONFIRM_PASSWORD] = binding.edtConfirmPassword.text.toString()
                                mRegisterData[ApiConstants.PAR_DEVICE] = "android"
                                mRegisterData[ApiConstants.PAR_FCM_TOKEN] = sharedPrefUtils.fcmDeviceToken.toString()

                                registerUser(mRegisterData)
                            }
                        }
                    }
                }
            }
        }

        binding.imgBack.setOnClickListener {

            onBackPressed()
        }

        binding.tvLoginWithGoogle.setOnClickListener {

            signInWithGoogle()
        }

        binding.tvLoginWithFacebook.setOnClickListener {

            mFacebookLoginManager.logInWithReadPermissions(
                this@RegisterActivity,
                listOf("public_profile", "email")
            )

        }

        mFacebookLoginManager.registerCallback(mFacebookCallbackManager, object :
            FacebookCallback<LoginResult> {

            override fun onCancel() {
                // nothing to do
            }

            override fun onError(error: FacebookException) {
                // nothing to do
            }

            override fun onSuccess(result: LoginResult) {

                val graphRequest = GraphRequest.newMeRequest(
                    AccessToken.getCurrentAccessToken()
                ) { dataObject, _ ->
                    if (dataObject != null) {

                        //val accessToken  = AccessToken.getCurrentAccessToken()
                        val userImageObject = dataObject.getJSONObject("picture").getJSONObject("data")

                        val data = java.util.HashMap<String, String>()
                        data[ApiConstants.PAR_SOCIAL_PROVIDER] = "facebook"

//                        if(accessToken != null && !accessToken.isExpired) {
//
//                                data[ApiConstants.PAR_SOCIAL_TOKEN] = accessToken.token.toString()
//
//                        }

                        Log.e("login_result_DATA", dataObject.getString("id"))
                        Log.e("login_result_img_DATA", userImageObject.getString("url"))

                        data[ApiConstants.PAR_SOCIAL_TOKEN] = dataObject.getString("id")

                        data[ApiConstants.PAR_NAME] = dataObject.getString("name")

                        if(dataObject.getString("email").isNotEmpty()) {

                            data[ApiConstants.PAR_EMAIL] = dataObject.getString("email")
                        }

                        data[ApiConstants.PAR_IMAGE] = userImageObject.getString("url")
                        data[ApiConstants.PAR_FCM_TOKEN] = sharedPrefUtils.fcmDeviceToken.toString()
                        data[ApiConstants.PAR_DEVICE] = "android"

                        socialLogin(data)


                        try {

                        } catch (e: JSONException) {
                            e.printStackTrace()
                            Log.e("login_resulttttt_error", e.message.toString())

                        }
                    }
                }

                val bundle = Bundle()
                bundle.putString("fields", "name, id, link, email, first_name, last_name, picture.type(large)")
                graphRequest.parameters = bundle
                graphRequest.executeAsync()
            }
        })

        codeDialogBinding.btnConfirm.setOnClickListener {


            code = codeDialogBinding.edtCode1.text.toString() +
                    codeDialogBinding.edtCode2.text.toString() +
                    codeDialogBinding.edtCode3.text.toString() +
                    codeDialogBinding.edtCode4.text.toString()

            if (code.length == 4) {

                val parmas = HashMap<String,String>()
                parmas[ApiConstants.PAR_EMAIL] = binding.edtEmail.text.toString()
                parmas[ApiConstants.PAR_CODE] = code
                parmas[ApiConstants.PAR_DEVICE] = "android"
                parmas[ApiConstants.PAR_FCM_TOKEN] = sharedPrefUtils.fcmDeviceToken.toString()
                verifyRegister(parmas)
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

        binding.sprLanguages.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>,
                                        view: View, position: Int, id: Long) {


                if (!firstLoad) {

                    changeLanguage(languageList[position].key)
                }

                firstLoad = false
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }
    }

    private fun registerUser(data: Map<String, String>) {

        collectFlow(mViewModel.registerUser(data)) {

            handelApiResult(it, onResultSuccess = {

                it.resultData?.data?.let {

                    codeDialogBinding.tvMobileNumber.text = "${binding.edtEmail.text.toString()}"
                    codeDialog.show()
                }

            })
        }
    }

    private fun verifyRegister(data: Map<String, String>) {

        collectFlow(mViewModel.verifyRegister(data)) {

            handelApiResult(it, onResultSuccess = {

                it.resultData?.data?.let {

                    codeDialog.dismiss()
                    sharedPrefUtils.setCurrentUserData(it)
                    navToActivity(MainActivity::class.java)
                    finishAffinity()
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


    private fun signInWithGoogle() {

        Log.e("login_result", "signInWithGoogle")
        val signInIntent = mGoogleSignInClient.signInIntent
        someActivityResultLauncher.launch(signInIntent)
    }

    private fun handleSignInResult(googleSignInAccount: GoogleSignInAccount?) {
        Log.e("login_result", "handleSignInResult")


        // Signed in successfully, show authenticated UI.
        if (googleSignInAccount != null) {

            Log.e("login_result_id_token", googleSignInAccount.idToken.toString())


            val personName = googleSignInAccount.displayName
            val personGivenName = googleSignInAccount.givenName
            val personFamilyName = googleSignInAccount.familyName
            val personEmail = googleSignInAccount.email
            val personId = googleSignInAccount.id
            val personPhoto = googleSignInAccount.photoUrl

            Log.e(
                "login_result_DATA", "person name = $personName   --" +
                        "personGivenName = $personGivenName  --" +
                        "personFamilyName = $personFamilyName  --" +
                        "personEmail = $personEmail  --" +
                        "personId = $personId  --" +
                        "personPhoto = $personPhoto  --"
            )


            val data = java.util.HashMap<String, String>()
            data[ApiConstants.PAR_SOCIAL_PROVIDER] = "google"
            data[ApiConstants.PAR_SOCIAL_TOKEN] = personId.toString()
            data[ApiConstants.PAR_NAME] = personName.toString()
            data[ApiConstants.PAR_EMAIL] = personEmail.toString()
            data[ApiConstants.PAR_IMAGE] = personPhoto.toString()
            data[ApiConstants.PAR_FCM_TOKEN] = sharedPrefUtils.fcmDeviceToken.toString()
            data[ApiConstants.PAR_DEVICE] = "android"

            socialLogin(data)


        } else {
            Log.e("login_result", "account null")
        }
    }

    private fun socialLogin(data: Map<String, String>) {

        collectFlow(mViewModel.socialLogin(data)) {

            handelApiResult(it, onResultSuccess = {

                it.resultData?.data?.let {

                    sharedPrefUtils.setCurrentUserData(it)

                    Intent(this, MainActivity::class.java).run {

                        sharedPrefUtils.showIntroFlag = false
                        startActivity(this)
                        finishAffinity()
                    }
                }
            })
        }
    }

    private fun changeLanguage(lang: String) {

        sharedPrefUtils.appLang = lang
        LocaleLanguageUtils.setLocale(this, lang)

        startActivity(Intent(this,RegisterActivity::class.java))
        overridePendingTransition(0,0)
        finishAffinity()

    }

}