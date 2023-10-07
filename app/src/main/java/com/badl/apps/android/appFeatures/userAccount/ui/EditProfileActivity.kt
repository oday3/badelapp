package com.badl.apps.android.appFeatures.userAccount.ui

import android.Manifest
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.badl.apps.android.R
import com.badl.apps.android.baseClasses.ui.BaseActivity
import com.badl.apps.android.databinding.ActivityEditProfileBinding
import com.badl.apps.android.network.ApiConstants
import com.badl.apps.android.utils.AppUtil.collectFlow
import com.badl.apps.android.utils.Constants
import com.badl.apps.android.utils.FileUtilsKt
import com.badl.apps.android.utils.NetworkUtils
import com.badl.apps.android.utils.NetworkUtils.handelApiResult
import com.badl.apps.android.utils.UiUtils.initializeMainToolBar
import com.badl.apps.android.utils.UiUtils.setImage
import com.badl.apps.android.utils.UiUtils.showCustomToast
import com.badl.apps.android.utils.ValidationUtils.isPermissionGranted
import com.badl.apps.android.utils.ValidationUtils.validateEmails
import com.badl.apps.android.utils.ValidationUtils.validateEmpty
import com.badl.apps.android.utils.ValidationUtils.validateMobileIntro
import com.badl.apps.android.utils.ValidationUtils.validatePassword
import com.badl.apps.android.utils.ValidationUtils.validatePasswordMatches
import id.zelory.compressor.Compressor
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class EditProfileActivity : BaseActivity() {
    private lateinit var binding: ActivityEditProfileBinding
    private val mViewModel by viewModels<UserAccountViewModel>()

    private val _pickedImageURI by lazy { MutableLiveData<Uri>(Uri.parse("")) }
    val mPickedImageURI = _pickedImageURI
    private var requestedPermission = ""
    val updatedData = HashMap<String, RequestBody>()
    private val requestImagePermissionLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {

                getImageFromGallery.launch("image/*")

            } else {

                this.showCustomToast(
                    getString(R.string.we_need_storage_permission_to_update_image),
                    Constants.TOAST_WARNING
                )
            }
        }

    private val getImageFromGallery =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            // Handle the returned Uri

            if (uri != null) {

                _pickedImageURI.value = uri
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeMainToolBar(getString(R.string.personal_info))


        initListeners()
        initObservers()

        getProfile()
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

        mPickedImageURI.observe(this) {

            if (it != null) {

                binding.imgUserImg.setImage(it.toString())
            }
        }
    }

    override fun initListeners() {

        binding.imgEditUserImg.setOnClickListener {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

                if (isPermissionGranted(Manifest.permission.READ_MEDIA_IMAGES)) {

                    getImageFromGallery.launch("image/*")

                } else {

                    requestedPermission = Manifest.permission.READ_MEDIA_IMAGES
                    requestImagePermissionLauncher.launch(requestedPermission)
                }

            } else {


                if (isPermissionGranted(Manifest.permission.READ_EXTERNAL_STORAGE)) {

                    getImageFromGallery.launch("image/*")

                } else {

                    requestedPermission = Manifest.permission.READ_EXTERNAL_STORAGE
                    requestImagePermissionLauncher.launch(requestedPermission)
                }
            }
        }

        binding.btnUpdateInfo.setOnClickListener {

            lifecycleScope.launch {

                if (validateEmpty(binding.tvinName, binding.tvinEmail, binding.tvinMobileNumber)) {

                    if (validateEmails(binding.tvinEmail)) {

                        if (validateMobileIntro(binding.tvinMobileNumber)) {


                            updatedData[ApiConstants.PAR_NAME] =
                                NetworkUtils.createPartFromString(binding.edtName.text.toString())

                            updatedData[ApiConstants.PAR_EMAIL] =
                                NetworkUtils.createPartFromString(binding.edtEmail.text.toString())

                            updatedData[ApiConstants.PAR_MOBILE] =
                                NetworkUtils.createPartFromString(binding.edtMobileNumber.text.toString())



                            var image: MultipartBody.Part? = null


                            mPickedImageURI.value?.let {

                                if (it.toString().isNotEmpty()) {

                                    val profileImage = Compressor.compress(
                                        this@EditProfileActivity,
                                        FileUtilsKt.from(
                                            this@EditProfileActivity, it)
                                    )

                                    image = NetworkUtils.createFilePart(
                                        ApiConstants.PAR_IMAGE,
                                        profileImage
                                    )
                                }
                            }


                           if (binding.edtPassword.text.toString().isNotEmpty())  {

                               if (validateEmpty(binding.tvinConfirmPassword)) {

                                   if (validatePassword(binding.tvinPassword, binding.tvinConfirmPassword)) {

                                       if (validatePasswordMatches(binding.tvinPassword, binding.tvinConfirmPassword)) {


                                           updatedData[ApiConstants.PAR_PASSWORD] =
                                               NetworkUtils.createPartFromString(binding.edtPassword.text.toString())

                                           updatedData[ApiConstants.PAR_CONFIRM_PASSWORD] =
                                               NetworkUtils.createPartFromString(binding.edtConfirmPassword.text.toString())

                                           updateProfile(updatedData, image)
                                       }
                                   }
                               }

                           } else {

                               updateProfile(updatedData, image)
                           }




                        }
                    }
                }
            }
        }
    }


    private fun getProfile() {

        collectFlow(mViewModel.getProfile()) {

            // binding?.swipeLayout?.isRefreshing = false
            // binding?.recSearchResultList?.showView(false)

            handelApiResult(it,
                onResultSuccess = {

                    it.resultData?.data?.let { profileData ->

                        sharedPrefUtils.setCurrentUserData(profileData)

                        binding.imgUserImg.setImage(profileData.image)
                        binding.tvUserName.text = profileData.name
                        binding.edtName.setText(profileData.name)
                        binding.edtEmail.setText(profileData.email)
                        binding.edtMobileNumber.setText(profileData.mobile)

                    }
                })
        }
    }


    private fun updateProfile(
        updatedData: Map<String, RequestBody>,
        updatedImage: MultipartBody.Part?
    ) {

        collectFlow(mViewModel.updateProfile(updatedData, updatedImage)) {

            // binding?.swipeLayout?.isRefreshing = false
            // binding?.recSearchResultList?.showView(false)

            handelApiResult(it,
                onResultSuccess = {

                    it.resultData?.data?.let { profileData ->

                        sharedPrefUtils.setCurrentUserData(profileData)

                        binding.imgUserImg.setImage(profileData.image)
                        binding.tvUserName.text = profileData.name
                        binding.edtName.setText(profileData.name)
                        binding.edtEmail.setText(profileData.email)
                        binding.edtMobileNumber.setText(profileData.mobile)


                        showCustomToast(it.message.toString(), Constants.TOAST_DONE)

                    }
                })
        }
    }

}