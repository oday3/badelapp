package com.badl.apps.android.appFeatures.main.ui

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import com.badl.apps.android.R
import com.badl.apps.android.appFeatures.addAd.ui.AddAdActivity
import com.badl.apps.android.appFeatures.appCommon.ui.AppCommonViewModel
import com.badl.apps.android.application.BadelApp
import com.badl.apps.android.baseClasses.ui.BaseActivity
import com.badl.apps.android.databinding.ActivityMainBinding
import com.badl.apps.android.databinding.DialogRatingBinding
import com.badl.apps.android.network.ApiConstants
import com.badl.apps.android.utils.AppUtil.collectFlow
import com.badl.apps.android.utils.AppUtil.navToActivity
import com.badl.apps.android.utils.NetworkUtils.handelApiResult
import com.badl.apps.android.utils.UiUtils
import com.badl.apps.android.utils.UiUtils.addOnBackPressedDispatcher
import com.badl.apps.android.utils.UiUtils.extensionSetBackground
import com.badl.apps.android.utils.UiUtils.getScreenDensity
import com.badl.apps.android.utils.UiUtils.initializeMainToolBar
import com.badl.apps.android.utils.UiUtils.showMessage
import com.badl.apps.android.utils.UiUtils.showView
import com.badl.apps.android.utils.ValidationUtils.isUserLogin
import com.badl.apps.android.utils.ValidationUtils.validateEmpty

class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    private var prevFragment: Fragment? = null
    private var activeFragment: Fragment? = null
    private var showExitToast = true
    private  val homeFragment: HomeFragment = HomeFragment()
    private  val sectionsFragment: SectionsFragment = SectionsFragment()
    private val ordersFragment: OrdersFragment = OrdersFragment()
    private val profileFragment: ProfileFragment = ProfileFragment()
    private lateinit var rateDialog: Dialog
    private lateinit var rateDialogBinding: DialogRatingBinding
    private val rateData = HashMap<String, String>()
    var mRatedOrderId = -1
    private val mAppCommonViewModel by viewModels<AppCommonViewModel>()
    private var firstLoade = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        rateDialogBinding = DialogRatingBinding.inflate(layoutInflater)

        setContentView(binding.root)

        initializeMainToolBar()

       // activeFragment = homeFragment
        configureToolBar(R.id.homeFrag, "")
        replaceFragment(homeFragment, "homeFrag")

        initListeners()
        initViews()
    }


    override fun onResume() {
        super.onResume()


        if (binding.bottomNavigationView.selectedItemId != binding.containerMain.getFragment<Fragment?>()?.id) {

            binding.bottomNavigationView.selectedItemId = binding.bottomNavigationView.selectedItemId
        }


        if ((applicationContext as BadelApp).navToBidding) {

            (applicationContext as BadelApp).navToBidding = false

               binding.bottomNavigationView.selectedItemId = R.id.ordersFrag

       }




        if (isUserLogin()) {


            if (sharedPrefUtils.getCurrentUserData()?.type?.toInt() == 2) {

                binding.imgAddAd.showView(false)

                binding.bottomNavigationView.menu.removeItem(R.id.addF)
            }


            if (firstLoade) {

                changeLang(sharedPrefUtils.appLang.toString())

            }
        }

        Log.e("asugdfa", sharedPrefUtils.getCurrentUserData().toString())
    }

    override fun initViews() {

        binding.bottomNavigationView.menu.getItem(2).isEnabled = false

        rateDialog =
            UiUtils.createDialog(
                this,
                themeResId = R.style.TransparentAlertDialog,
                windowAnimationResId = R.style.SlideDialogAnimation,
                true, rateDialogBinding.root
            )

        rateDialog.extensionSetBackground(
            (getScreenDensity() * 14).toInt(),
            AppCompatResources.getDrawable(this, R.drawable.bg_transparent),
        )

//        supportFragmentManager.beginTransaction().setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out).add(R.id.container_main, homeFragment, "homeFrag").commit()
//        supportFragmentManager.beginTransaction().setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out).add(R.id.container_main, sectionsFragment, "sectionsFrag").commit()
//        supportFragmentManager.beginTransaction().setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out).add(R.id.container_main, ordersFragment, "ordersFrag").commit()
//        supportFragmentManager.beginTransaction().setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out).add(R.id.container_main, profileFragment, "profileFrag").commit()


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

        addOnBackPressedDispatcher() {

            if (showExitToast) {

                showExitToast = false

                showMessage(getString(R.string.get_out_off_app_msg))
            } else {

                finish()
            }
        }

        binding.bottomNavigationView.setOnItemSelectedListener {

            showExitToast = true
            when (it.itemId) {

                R.id.homeFrag -> {

                    (applicationContext as BadelApp).refreshOrders = true
                    configureToolBar(R.id.homeFrag, "")
                    replaceFragment(homeFragment, "homeFrag")
                    //showFragment(homeFragment)
                }

                R.id.sectionsFrag -> {

                    (applicationContext as BadelApp).refreshOrders = true


//                    if (!this::sectionsFragment.isInitialized) {
//
//                        sectionsFragment = SectionsFragment()
//                    }

                    replaceFragment(sectionsFragment, "sectionsFrag")
                    //showFragment(sectionsFragment)
                    configureToolBar(R.id.sectionsFrag, getString(R.string.sections))
                    // binding.container.currentItem = 1

                }


                R.id.addF -> {

                    if (isUserLogin()) {

                        navToActivity(AddAdActivity::class.java)
                    }

                }


                R.id.ordersFrag -> {

//                    if (!this::ordersFragment.isInitialized) {
//
//                        ordersFragment = OrdersFragment()
//                    }

                    Log.e("asdasdw", "inside ordersFragNav")
                    replaceFragment(ordersFragment, "ordersFrag")
                    // showFragment(ordersFragment)
                    configureToolBar(R.id.ordersFrag, getString(R.string.bidding))

                    // binding.container.currentItem = 2

                }

                R.id.profileFrag -> {

                    (applicationContext as BadelApp).refreshOrders = true


//                    if (!this::profileFragment.isInitialized) {
//
//                        profileFragment = ProfileFragment()
//                    }

                    replaceFragment(profileFragment, "profileFrag")
                    // showFragment(profileFragment)
                    configureToolBar(R.id.profileFrag, getString(R.string.profile_2))

                    // binding.container.currentItem = 3
                }
            }

            true
            // mFragmentTransaction.commit()
        }

        binding.imgAddAd.setOnClickListener {


            if (isUserLogin(true)) {

                navToActivity(AddAdActivity::class.java)
            }
        }

        rateDialogBinding.btnRatingDoneDialogMyOrders.setOnClickListener {

            if (validateEmpty(rateDialogBinding.edtRatDialogMessage)) {


                rateData[ApiConstants.PAR_AD_ID] = mRatedOrderId.toString()
                rateData[ApiConstants.PAR_RATE] =
                    rateDialogBinding.ratingbarRatDialog.rating.toString()
                rateData[ApiConstants.PAR_MESSAGE] =
                    rateDialogBinding.edtRatDialogMessage.text.toString()

                rate(rateData)
            }
        }
    }

    fun navToMain() {

        //  binding.container.currentItem = 0
        binding.bottomNavigationView.selectedItemId = R.id.homeFrag
    }

    private fun replaceFragment(fragment: Fragment, tag: String) {

        Log.e("asdasdw", "inside replaceFragment $tag")

        for (fragment1 in supportFragmentManager.fragments) {
            supportFragmentManager.beginTransaction().hide(fragment1).commit()
            Log.e("asdasdw", "Hide ${fragment1.tag}")
        }

        if (fragment.isAdded) {
            Log.e("asdasdw", "Added $tag")
            supportFragmentManager.beginTransaction().show(fragment).commit()

        } else {
            Log.e("asdasdw", "Not added $tag")

            supportFragmentManager.beginTransaction().add(R.id.container_main, fragment, tag).commit()

        }

        Log.e("asdasdw", "Number " + supportFragmentManager.fragments.size.toString())

//        if (activeFragment != null) {
//
//            fragmentManager.beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).hide(activeFragment!!).commit()
//
//            activeFragment = fragment
//
//        }
    }


    fun showNotificationBadge() {
        binding.toolbarMain.imgMainToolBarNotification.showView(true)
    }

    fun hideNotificationBadge() {
        binding.toolbarMain.imgMainToolBarNotification.showView(false)
    }


    fun configureToolBar(sectionId: Int, toolbarTitle: String) {

        when (sectionId) {

            R.id.homeFrag -> {

                configMainToolBar()
            }

            R.id.sectionsFrag -> {

                binding.toolbarMain.root.showView(true)
                binding.toolbarMain.imgMainToolBarNotification.showView(false)
                binding.toolbarMain.imgMainToolBarCart.showView(false)
                binding.toolbarMain.imgMainToolbarBack.showView(false)
                binding.toolbarMain.tvMainToolbarTitle.text = getString(R.string.sections)

            }

            R.id.ordersFrag -> {


                binding.toolbarMain.root.showView(true)
                binding.toolbarMain.imgMainToolBarNotification.showView(isUserLogin())
                binding.toolbarMain.imgMainToolBarCart.showView(false)
                binding.toolbarMain.imgMainToolbarBack.showView(false)
                binding.toolbarMain.tvMainToolbarTitle.text = getString(R.string.my_orders)
            }


            R.id.profileFrag -> {


                binding.toolbarMain.root.showView(true)
                binding.toolbarMain.imgMainToolBarNotification.showView(isUserLogin())
                binding.toolbarMain.imgMainToolBarCart.showView(false)
                binding.toolbarMain.imgMainToolbarBack.showView(false)
                binding.toolbarMain.tvMainToolbarTitle.text = getString(R.string.profile_2)
            }

            else -> {

                binding.toolbarMain.root.showView(true)
                binding.toolbarMain.imgMainToolBarNotification.showView(isUserLogin())
                binding.toolbarMain.imgMainToolBarCart.showView(false)
                binding.toolbarMain.imgMainToolbarBack.showView(false)
                binding.toolbarMain.tvMainToolbarTitle.text = ""

            }
        }
    }

    private fun configMainToolBar() {

        binding.toolbarMain.root.showView(true)
        binding.toolbarMain.imgMainToolBarNotification.showView(isUserLogin())
        binding.toolbarMain.imgMainToolBarCart.showView(false)
        binding.toolbarMain.imgMainToolbarBack.showView(false)
        binding.toolbarMain.tvMainToolbarTitle.text = getString(R.string.home)

    }


    fun showRateDialog(orderId: Int) {

        this.mRatedOrderId = orderId
        rateDialog.show()
    }

    private fun rate(data: HashMap<String, String>) {

        collectFlow(mAppCommonViewModel.rate(data)) {

            handelApiResult(it,

                onResultSuccess = {
                    it.resultData?.data?.let {


                        rateDialog.dismiss()

                    }
                })
        }
    }

    private fun changeLang(params: String) {

        collectFlow(mAppCommonViewModel.changeLang(params)) {

            handelApiResult(it, onLoading = {},

                onResultSuccess = {

                    firstLoade = false

                })
        }

    }
}