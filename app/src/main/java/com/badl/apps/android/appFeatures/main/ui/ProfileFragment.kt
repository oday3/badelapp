package com.badl.apps.android.appFeatures.main.ui

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.activityViewModels
import com.badl.apps.android.R
import com.badl.apps.android.appFeatures.appCommon.ui.InfoActivity
import com.badl.apps.android.appFeatures.appCommon.ui.IntroActivity
import com.badl.apps.android.appFeatures.appCommon.ui.NotificationsActivity
import com.badl.apps.android.appFeatures.auth.ui.AuthViewModel
import com.badl.apps.android.appFeatures.auth.ui.LoginActivity
import com.badl.apps.android.appFeatures.chat.ui.ChatHistoryActivity
import com.badl.apps.android.appFeatures.userAccount.ui.EditProfileActivity
import com.badl.apps.android.appFeatures.userAccount.ui.FollowingListActivity
import com.badl.apps.android.appFeatures.userAccount.ui.MyAdsActivity
import com.badl.apps.android.appFeatures.userAccount.ui.MyTransactionsActivity
import com.badl.apps.android.appFeatures.userAccount.ui.UserAccountViewModel
import com.badl.apps.android.appFeatures.userAccount.ui.UserRatingsActivity
import com.badl.apps.android.baseClasses.ui.BaseActivity
import com.badl.apps.android.databinding.BottomChangeLangBinding
import com.badl.apps.android.databinding.FragmentProfileBinding
import com.badl.apps.android.utils.AppUtil.collectFlow
import com.badl.apps.android.utils.AppUtil.getIntentExtension
import com.badl.apps.android.utils.AppUtil.navToActivity
import com.badl.apps.android.utils.Constants
import com.badl.apps.android.utils.LocaleLanguageUtils
import com.badl.apps.android.utils.NetworkUtils.handelApiResult
import com.badl.apps.android.utils.UiUtils
import com.badl.apps.android.utils.UiUtils.setImage
import com.badl.apps.android.utils.UiUtils.showCustomToast
import com.badl.apps.android.utils.UiUtils.showView
import com.badl.apps.android.utils.ValidationUtils.isUserLogin
import com.google.android.material.bottomsheet.BottomSheetDialog

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private var selectedLang = ""

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
    private var binding: FragmentProfileBinding? = null
    private lateinit var languageBottomSheet: BottomSheetDialog
    private lateinit var languageBottomSheetBinding: BottomChangeLangBinding
    private val mViewModel by activityViewModels<AuthViewModel>()
    private val mProfileViewModel by activityViewModels<UserAccountViewModel>()

    private val mSharedPrefUtils by lazy { (requireActivity() as BaseActivity).sharedPrefUtils }

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentProfileBinding.inflate(inflater, container, false)
        binding?.constPersonalInfo?.showView((requireActivity() as BaseActivity).isUserLogin())
        binding?.constMyAds?.showView((requireActivity() as BaseActivity).isUserLogin())
        binding?.constMyOperations?.showView((requireActivity() as BaseActivity).isUserLogin())
        binding?.constMyChats?.showView((requireActivity() as BaseActivity).isUserLogin())
        binding?.constFollowingList?.showView((requireActivity() as BaseActivity).isUserLogin())
        binding?.constRatings?.showView((requireActivity() as BaseActivity).isUserLogin())
        binding?.constNotifications?.showView((requireActivity() as BaseActivity).isUserLogin())
        binding?.imgEditProfile?.showView((requireActivity() as BaseActivity).isUserLogin())
        binding?.tvLogoutLabel?.showView((requireActivity() as BaseActivity).isUserLogin())
        binding?.tvLoginLabel?.showView(!(requireActivity() as BaseActivity).isUserLogin())

        if ((requireActivity() as BaseActivity).isUserLogin()) {

            binding?.constMyAds?.showView(mSharedPrefUtils.getCurrentUserData()?.type?.toInt() == 1)
            binding?.constRatings?.showView(mSharedPrefUtils.getCurrentUserData()?.type?.toInt() == 1)

        }

        selectedLang = mSharedPrefUtils.appLang.toString()

        languageBottomSheetBinding = BottomChangeLangBinding.inflate(inflater, container, false)

        languageBottomSheet =
            UiUtils.createBottomSheetDialog(
                requireActivity(),
                -1,
                languageBottomSheetBinding.root,
                true
            )

        binding?.imgEditProfile?.setOnClickListener {

            navToActivity(EditProfileActivity::class.java)
        }

        binding?.constPersonalInfo?.setOnClickListener {

            navToActivity(EditProfileActivity::class.java)
        }


        binding?.constMyAds?.setOnClickListener {

            navToActivity(MyAdsActivity::class.java)
        }

        binding?.constMyOperations?.setOnClickListener {

            navToActivity(MyTransactionsActivity::class.java)
        }

        binding?.constMyChats?.setOnClickListener {

            navToActivity(ChatHistoryActivity::class.java)
        }

        binding?.constFollowingList?.setOnClickListener {

            navToActivity(FollowingListActivity::class.java)
        }

        binding?.constNotifications?.setOnClickListener {

            navToActivity(NotificationsActivity::class.java)
        }

        binding?.constRatings?.setOnClickListener {

            navToActivity(UserRatingsActivity::class.java)
        }

        binding?.tvLogoutLabel?.setOnClickListener {

           logout()
        }

        binding?.tvLoginLabel?.setOnClickListener {

            navToActivity(LoginActivity::class.java)

        }

        binding?.constTerms?.setOnClickListener {

            getIntentExtension(InfoActivity::class.java).run {

                putExtra(Constants.FROM, Constants.FROM_TERMS)
                putExtra(Constants.TOOLBAR_TITLE, getString(R.string.terms_of_use))

             requireActivity().startActivity(this)
            }
        }

        binding?.constPrivacy?.setOnClickListener {

            getIntentExtension(InfoActivity::class.java).run {

                putExtra(Constants.FROM, Constants.FROM_APP_PRIVACY)
                putExtra(Constants.TOOLBAR_TITLE, getString(R.string.privacy_policy))

             requireActivity().startActivity(this)
            }
        }

        binding?.constAboutApp?.setOnClickListener {

            getIntentExtension(InfoActivity::class.java).run {

                putExtra(Constants.FROM, Constants.FROM_ABOUT_US)
                putExtra(Constants.TOOLBAR_TITLE, getString(R.string.about_us))

             requireActivity().startActivity(this)
            }
        }


        binding?.constLang?.setOnClickListener {

            languageBottomSheet.show()
        }



        if (mSharedPrefUtils.appLang == "ar") {

            languageBottomSheetBinding.tvBottomChangeLangArabicLanguage.isSelected = true
            languageBottomSheetBinding.tvBottomChangeLangEnglishLanguage.isSelected = false
            binding?.tvCurrentLang?.text = "العربية"

        } else if (mSharedPrefUtils.appLang == "en") {

            languageBottomSheetBinding.tvBottomChangeLangArabicLanguage.isSelected = false
            languageBottomSheetBinding.tvBottomChangeLangEnglishLanguage.isSelected = true
            binding?.tvCurrentLang?.text = "English"

        }


        languageBottomSheetBinding.tvBottomChangeLangEnglishLanguage.setOnClickListener {

            selectedLang = "en"
            it.isSelected = true
            languageBottomSheetBinding.tvBottomChangeLangArabicLanguage.isSelected = false
        }

        languageBottomSheetBinding.tvBottomChangeLangArabicLanguage.setOnClickListener {

            selectedLang = "ar"
            it.isSelected = true
            languageBottomSheetBinding.tvBottomChangeLangEnglishLanguage.isSelected = false
        }

        languageBottomSheetBinding.btnBottomChangeLangConfirm.setOnClickListener {


            if (selectedLang != mSharedPrefUtils.appLang) {


                changeLanguage(selectedLang)

            } else {

                showCustomToast(getString(R.string.you_are_already_use_this_language), Constants.TOAST_INFO)
            }

            languageBottomSheet.dismiss()
        }

        return binding?.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onResume() {
        super.onResume()


        if ((requireActivity() as BaseActivity).isUserLogin()) {

            binding?.tvUserName?.text = mSharedPrefUtils.getCurrentUserData()?.name
            binding?.imgUserImg?.setImage(mSharedPrefUtils.getCurrentUserData()?.image)
        }

    }

    private fun logout() {

        collectFlow(mViewModel.logout()) {
            (requireActivity() as BaseActivity).handelApiResult(it,
                onResultSuccess = {

                   // (requireActivity() as BaseActivity).sharedPrefUtils.showIntroFlag = true
                    (requireActivity() as BaseActivity).sharedPrefUtils.setCurrentUserData(null)
                    (requireActivity() as BaseActivity).sharedPrefUtils.currentUserCartCount = 0
                    val intent = Intent(requireContext(), IntroActivity::class.java)
                    intent.addFlags(
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
                                or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    )
                    startActivity(intent)
                    ActivityCompat.finishAffinity(requireActivity())
                })
        }
    }

    private fun changeLanguage(lang: String) {

        collectFlow(mProfileViewModel.changeLanguage(lang)) {

            // binding?.swipeLayout?.isRefreshing = false
            // binding?.recSearchResultList?.showView(false)

            handelApiResult(it,
                onResultSuccess = {

                    mSharedPrefUtils.appLang = selectedLang
                    LocaleLanguageUtils.setLocale(requireActivity(), selectedLang)
                    val intent = Intent(requireActivity(), MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    requireActivity().finishAffinity()
                })
        }
    }

}