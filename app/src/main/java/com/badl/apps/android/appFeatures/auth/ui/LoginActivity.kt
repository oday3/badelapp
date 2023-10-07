package com.badl.apps.android.appFeatures.auth.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.badl.apps.android.R
import com.badl.apps.android.adapters.LangAdapter
import com.badl.apps.android.adapters.LanguageAdapter
import com.badl.apps.android.appFeatures.appCommon.data.LanguageItem
import com.badl.apps.android.appFeatures.main.ui.MainActivity
import com.badl.apps.android.baseClasses.ui.BaseActivity
import com.badl.apps.android.databinding.ActivityLoginBinding
import com.badl.apps.android.network.ApiConstants
import com.badl.apps.android.utils.AppUtil.collectFlow
import com.badl.apps.android.utils.AppUtil.navToActivity
import com.badl.apps.android.utils.LocaleLanguageUtils
import com.badl.apps.android.utils.NetworkUtils.handelApiResult
import com.badl.apps.android.utils.ValidationUtils.validateEmpty
import com.badl.apps.android.utils.ValidationUtils.validateMobileIntro
import com.badl.apps.android.utils.ValidationUtils.validatePassword
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

class LoginActivity : BaseActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val mViewModel by viewModels<AuthViewModel>()

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

    private val currentLang by lazy { sharedPrefUtils.appLang }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        initAdapters()
        initViews()
        initData()
        initListeners()
    }

    override fun initViews() {

        if (currentLang == "ar") {

            binding.sprLanguages.setSelection(1)

        } else {

            binding.sprLanguages.setSelection(0)
        }
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

        binding.linearCreateAccount.setOnClickListener {

            navToActivity(RegisterActivity::class.java)
        }

        binding.tvForgotPasswordLabel.setOnClickListener {

            navToActivity(ForgotPasswordActivity::class.java)
        }

        binding.btnLogin.setOnClickListener {


            if (validateEmpty(binding.tvinMobileNumber, binding.tvinPassword)) {

                if (validateMobileIntro(binding.tvinMobileNumber)) {

                    if (validatePassword(binding.tvinPassword)) {

                        val data = HashMap<String,String>()
                        data[ApiConstants.PAR_MOBILE] = "${binding.edtMobileNumber.text.toString()}"
                        data[ApiConstants.PAR_PASSWORD] = binding.edtPassword.text.toString()
                        data[ApiConstants.PAR_DEVICE] = "android"
                        data[ApiConstants.PAR_FCM_TOKEN] = sharedPrefUtils.fcmDeviceToken.toString()
                        loginUser(data)
                    }
                }
            }
        }

        binding.tvLoginWithGoogle.setOnClickListener {

            signInWithGoogle()
        }

        binding.tvLoginWithFacebook.setOnClickListener {

            mFacebookLoginManager.logInWithReadPermissions(
                this@LoginActivity,
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

    private fun loginUser(data: Map<String, String>) {

        collectFlow(mViewModel.loginUser(data)) {

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



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        mFacebookCallbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }


    private fun changeLanguage(lang: String) {

                    sharedPrefUtils.appLang = lang
                    LocaleLanguageUtils.setLocale(this, lang)

                    startActivity(Intent(this,LoginActivity::class.java))
                    overridePendingTransition(0,0)
                    finishAffinity()

    }
}