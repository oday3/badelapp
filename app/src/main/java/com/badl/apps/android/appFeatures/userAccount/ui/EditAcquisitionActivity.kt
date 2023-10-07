package com.badl.apps.android.appFeatures.userAccount.ui

import android.Manifest
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.badl.apps.android.R
import com.badl.apps.android.adapters.AddedProductImagesAdapter
import com.badl.apps.android.adapters.TextSpinnerAdapter
import com.badl.apps.android.appFeatures.userAccount.data.AdImageItem
import com.badl.apps.android.appFeatures.addAd.ui.AdsViewModel
import com.badl.apps.android.appFeatures.appCommon.data.DropdownItem
import com.badl.apps.android.appFeatures.main.data.SectionItem
import com.badl.apps.android.appFeatures.main.data.SubSectionItem
import com.badl.apps.android.appFeatures.userAccount.data.AdDetailsItem
import com.badl.apps.android.baseClasses.ui.BaseActivity
import com.badl.apps.android.databinding.ActivityEditAcquisitionBinding
import com.badl.apps.android.network.ApiConstants
import com.badl.apps.android.utils.AppUtil.collectFlow
import com.badl.apps.android.utils.Constants
import com.badl.apps.android.utils.FileUtilsKt
import com.badl.apps.android.utils.NetworkUtils
import com.badl.apps.android.utils.NetworkUtils.handelApiResult
import com.badl.apps.android.utils.UiUtils.clearErrors
import com.badl.apps.android.utils.UiUtils.initializeMainToolBar
import com.badl.apps.android.utils.UiUtils.showCustomToast
import com.badl.apps.android.utils.UiUtils.showView
import com.badl.apps.android.utils.ValidationUtils.isPermissionGranted
import com.badl.apps.android.utils.ValidationUtils.validateEmpty
import id.zelory.compressor.Compressor
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class EditAcquisitionActivity : BaseActivity(), AdapterView.OnItemSelectedListener {
    private lateinit var binding: ActivityEditAcquisitionBinding
    private lateinit var imagesAdapter: AddedProductImagesAdapter
    private val mViewModel by viewModels<AdsViewModel>()

    private var selectedAdType = 0
    private var selectedAdStatus = 0
    private var selectedAdRepublish = 0
    private var selectedAdDuration = 1
    private var selectedSection = 0
   // private var selectedSubsection = 0

    val adData = HashMap<String, RequestBody>()
    private var adImages = ArrayList<MultipartBody.Part?>()

    private lateinit var chooseImageSource: AlertDialog
    private var imageSource: Int = 0

    private val _capturedImageURI by lazy { MutableLiveData<Uri>() }
    val mCapturedImageURI = _capturedImageURI

    private lateinit var capturedImageURI: Uri

    private lateinit var adTypeSpinAdapter: TextSpinnerAdapter<DropdownItem>
    private val adTypeItems = arrayListOf<DropdownItem>()
    private lateinit var initAdType: DropdownItem

    private lateinit var statusSpinAdapter: TextSpinnerAdapter<DropdownItem>
    private val statusItems = arrayListOf<DropdownItem>()
    private lateinit var initStatus: DropdownItem

    private lateinit var adRepublishSpinAdapter: TextSpinnerAdapter<DropdownItem>
    private val adRepublishItems = arrayListOf<DropdownItem>()
    private lateinit var initAdRepublish: DropdownItem

    private lateinit var productSectionSpinAdapter: TextSpinnerAdapter<SectionItem>
    private val productSectionItems = arrayListOf<SectionItem>()
    private lateinit var initProductSection: SectionItem

//    private lateinit var productSubsectionSpinAdapter: TextSpinnerAdapter<SubSectionItem>
//    private val productSubsectionItems = arrayListOf<SubSectionItem>()
//    private lateinit var initProductSubSection: SubSectionItem

    private lateinit var mAdDetailsItem: AdDetailsItem
    var listOfImages = ArrayList<AdImageItem>()
    var listOfDeleteImagesIds = ArrayList<Int>()
    private var mAdId = -1
    private var firstLoad = true
    private val _pickedImageURI by lazy { MutableLiveData<Uri>() }
    private val mPickedImageURI = _pickedImageURI
    private var requestedPermission = ""

    private val requestImagePermissionLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {


                if (requestedPermission == Manifest.permission.READ_EXTERNAL_STORAGE
                    || requestedPermission == Manifest.permission.READ_MEDIA_IMAGES) {

                    getImageFromGallery.launch("image/*")


                } else if (requestedPermission == Manifest.permission.CAMERA) {


                    getImageFromCamera.launch(initializeCapturedImageFileURI())

                }
            } else {

                showCustomToast(
                    "Opss, if you don't mind we need permission to continue",
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

    private val getImageFromCamera =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { isSaved: Boolean ->

            if (isSaved) {

                //Log.e("base_activity_capturedImg", "TakePicture + ${isSaved}")

                //_capturedImageURI.value = Uri.parse("0")
                _capturedImageURI.value = capturedImageURI
                // Log.e("base_activity_capturedImg", _capturedImageURI.value.toString())
                // Log.e("base_activity_capturedImg", capturedImageURI.toString())

            } else {

                Log.e("base_activity_capturedImg", "fail to save image")
            }
        }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditAcquisitionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeMainToolBar(getString(R.string.edit_acquisition))

        if (intent.hasExtra(Constants.AD_ID)) {

            mAdId = intent.getIntExtra(Constants.AD_ID, -1)
        }

        initData()
        initAdapters()
        initViews()
        initListeners()
        initObservers()

        getAdData()
    }

    override fun initViews() {
        binding.recListOfImages.adapter = imagesAdapter

        initializeCameraDialog()
    }

    override fun initData() {

        initAdType = DropdownItem("", "0", 0, getString(R.string.choose_ad_type), "")
        adTypeItems.add(initAdType)


        initStatus = DropdownItem("", "0", 0, getString(R.string.choose_product_status), "")
        statusItems.add(initStatus)

        initAdRepublish =
            DropdownItem("", "0", 0, getString(R.string.choose_the_number_of_auto_republish), "")
        adRepublishItems.add(initAdRepublish)

        initProductSection =
            SectionItem(
                "",
                "0",
                0,
                getString(R.string.choose_section),
                arrayListOf(),
                getString(R.string.choose_section)
            )
        productSectionItems.add(initProductSection)

//        initProductSubSection =
//            SubSectionItem(
//                "",
//                "0",
//                0,
//                getString(R.string.choose_sub_section),
//                "",
//                "",
//                getString(R.string.choose_sub_section)
//            )
//        productSubsectionItems.add(initProductSubSection)

    }

    override fun initAdapters() {

        imagesAdapter = AddedProductImagesAdapter(this) { postion, item ->

            if (!item.isLocal) {
                item.id?.let {
                    listOfDeleteImagesIds.add(it)
                }
            }

            listOfImages.removeAt(postion)
            imagesAdapter.setData(listOfImages)
        }

        adTypeSpinAdapter = TextSpinnerAdapter(adTypeItems)
        statusSpinAdapter = TextSpinnerAdapter(statusItems)
        adRepublishSpinAdapter = TextSpinnerAdapter(adRepublishItems)
        productSectionSpinAdapter = TextSpinnerAdapter(productSectionItems)
       // productSubsectionSpinAdapter = TextSpinnerAdapter(productSubsectionItems)
    }

    override fun initObservers() {

        mPickedImageURI.observe(this) {

            if (it != null) {

                if (listOfImages.size >= 5) {

                    showCustomToast(
                        getString(R.string.unfortunately_the_limit_is_only_5_images),
                        Constants.TOAST_INFO
                    )

                } else {

                    listOfImages.add(AdImageItem(0, it.toString(), true))
                    imagesAdapter.setData(listOfImages)
                }
            }
        }

        mCapturedImageURI.observe(this) {

            if (it != null) {

                if (listOfImages.size >= 5) {

                    showCustomToast(
                        getString(R.string.unfortunately_the_limit_is_only_5_images),
                        Constants.TOAST_INFO
                    )

                } else {

                    listOfImages.add(AdImageItem(0, it.toString(), true))
                    imagesAdapter.setData(listOfImages)
                }
            }
        }

    }

    override fun initListeners() {

        binding.seekbarAdDuration.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

                binding.tvAdDuration.text = seekBar?.progress.toString()

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        })

        binding.linearAddImage.setOnClickListener {

//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//
//                if (isPermissionGranted(Manifest.permission.READ_MEDIA_IMAGES)) {
//
//                    getImageFromGallery.launch("image/*")
//
//                } else {
//
//                    requestedPermission = Manifest.permission.READ_MEDIA_IMAGES
//                    requestImagePermissionLauncher.launch(requestedPermission)
//                }
//
//            }
//            else {
//
//
//                if (isPermissionGranted(Manifest.permission.READ_EXTERNAL_STORAGE)) {
//
//                    getImageFromGallery.launch("image/*")
//
//                } else {
//
//                    requestedPermission = Manifest.permission.READ_EXTERNAL_STORAGE
//                    requestImagePermissionLauncher.launch(requestedPermission)
//                }
//            }

            chooseImageSource.show()
        }

        binding.tvAdStatus.setOnClickListener {

            val params = HashMap<String, String>()
            params[ApiConstants.PAR_AD_ID] = mAdId.toString()

            if (mAdDetailsItem.status == 1) {

                params[ApiConstants.PAR_STATUS] = 0.toString()

            } else {

                params[ApiConstants.PAR_STATUS] = 1.toString()

            }

            updateAdStatus(params)

        }
        binding.spinEditAdAdType.onItemSelectedListener = this
        binding.spinEditAdStatus.onItemSelectedListener = this
        binding.spinEditAdAutoRepublish.onItemSelectedListener = this

        binding.spinEditAdSection.onItemSelectedListener = this
      //  binding.spinEditAdSubSection.onItemSelectedListener = this

        binding.btnSave.setOnClickListener {

            clearErrors(
                binding.tvinProductStatus,
                binding.tvinAdName,
                binding.tvinLowestPrice,
                binding.tvinAutoRepublish,
                binding.tvinAdType,
                binding.tvinSection,
               // binding.tvinSubSection,
                binding.tvinAdDescription,
                binding.tvinReleaseDate,
                binding.tvinAddress,
                binding.tvinQuantity,
            )

            if (selectedAdStatus != 0) {

                    if (selectedAdRepublish != 0) {

                        if (selectedAdType != 0) {

                            if (selectedSection != 0) {

//                                if (selectedSubsection != 0) {

                                    if (validateEmpty(
                                            binding.tvinAdName,
                                            binding.tvinAdDescription,
                                            binding.tvinReleaseDate,
                                            binding.tvinAddress
                                        )
                                    ) {

                                        if (listOfImages.isNotEmpty()) {

                                            lifecycleScope.launch {

                                                if (selectedAdStatus != 0) {

                                                    adData[ApiConstants.PAR_PRODUCT_STATUS_ID] =
                                                        NetworkUtils.createPartFromString(
                                                            selectedAdStatus.toString()
                                                        )
                                                }

                                                if (selectedAdType != 4) {

                                                    if (validateEmpty(binding.tvinLowestPrice)) {


                                                        adData[ApiConstants.PAR_PRICE] =
                                                            NetworkUtils.createPartFromString(binding.edtLowestPrice.text.toString())

                                                        if (selectedAdType == 3) {

                                                            if (validateEmpty(binding.tvinQuantity)) {

                                                                adData[ApiConstants.PAR_QUANTITY] =
                                                                    NetworkUtils.createPartFromString(binding.edtProductQuantity.text.toString())

                                                            } else {

                                                                return@launch
                                                            }
                                                        }

                                                    } else {

                                                        return@launch
                                                    }

                                                } else {

                                                    if (validateEmpty(binding.tvinQuantity)) {

                                                        adData[ApiConstants.PAR_QUANTITY] =
                                                            NetworkUtils.createPartFromString(binding.edtProductQuantity.text.toString())


                                                    } else {

                                                        return@launch
                                                    }
                                                }

                                                    adData[ApiConstants.PAR_AD_ID] =
                                                        NetworkUtils.createPartFromString(mAdId.toString())

                                                    adData[ApiConstants.PAR_TITLE] =
                                                        NetworkUtils.createPartFromString(binding.edtAdName.text.toString())

                                                    adData[ApiConstants.PAR_AD_DURATION] =
                                                        NetworkUtils.createPartFromString(
                                                            selectedAdDuration.toString()
                                                        )

                                                    if (selectedAdRepublish != 0) {

                                                        adData[ApiConstants.PAR_AUTO_REPOST_ID] =
                                                            NetworkUtils.createPartFromString(
                                                                selectedAdRepublish.toString()
                                                            )
                                                    }

                                                    if (selectedAdType != 0) {

                                                        adData[ApiConstants.PAR_AD_TYPE_ID] =
                                                            NetworkUtils.createPartFromString(
                                                                selectedAdType.toString()
                                                            )
                                                    }

                                                    if (selectedSection != 0) {

                                                        adData[ApiConstants.PAR_SECTION_ID] =
                                                            NetworkUtils.createPartFromString(
                                                                selectedSection.toString()
                                                            )
                                                    }

//                                                    if (selectedSubsection != 0) {
//
//                                                        adData[ApiConstants.PAR_SUBSECTION_ID] =
//                                                            NetworkUtils.createPartFromString(
//                                                                selectedSubsection.toString()
//                                                            )
//                                                    }


                                                    adData[ApiConstants.PAR_LOCATION] =
                                                        NetworkUtils.createPartFromString(binding.edtAddress.text.toString())


                                                    adData[ApiConstants.PAR_RELEASE_DATE] =
                                                        NetworkUtils.createPartFromString(binding.edtReleaseDate.text.toString())


                                                    adData[ApiConstants.PAR_DESCRIPTION] =
                                                        NetworkUtils.createPartFromString(binding.edtAdDescription.text.toString())


                                                    for (i in 0..listOfImages.size - 1) {

                                                        if (listOfImages[i].toString()
                                                                .isNotEmpty()
                                                        ) {

                                                            if (listOfImages[i].isLocal) {

                                                                val adImage = Compressor.compress(
                                                                    this@EditAcquisitionActivity,
                                                                    FileUtilsKt.from(
                                                                        this@EditAcquisitionActivity,
                                                                        Uri.parse(listOfImages[i].image)
                                                                    )
                                                                )

                                                                adImages.add(
                                                                    NetworkUtils.createFilePart(
                                                                        "${ApiConstants.PAR_IMAGES}[${i}]",
                                                                        adImage
                                                                    )
                                                                )
                                                            }
                                                        }
                                                    }

                                                    for (i in listOfDeleteImagesIds.indices) {
                                                        adData["${ApiConstants.PAR_DELETED_IMAGES}[${i}]"] =
                                                            NetworkUtils.createPartFromString(
                                                                listOfDeleteImagesIds[i].toString()
                                                            )
                                                    }

                                                    updateAd(adData, adImages)
                                                }

                                            } else {

                                                showCustomToast(
                                                    getString(R.string.please_select_at_lease_one_image_for_ad),
                                                    Constants.TOAST_INFO
                                                )
                                            }
                                        }
//                                    } else {
//                                        binding.tvinSubSection.error =
//                                            getString(R.string.please_select_product_sub_section)
//                                        binding.tvinSubSection.requestFocus()
//                                    }
                                } else {
                                    binding.tvinSection.error =
                                        getString(R.string.please_select_product_section)
                                    binding.tvinSection.requestFocus()
                                }

                            } else {

                                binding.tvinAdType.error = getString(R.string.please_select_ad_type)
                                binding.tvinAdType.requestFocus()
                            }

                        } else {

                            binding.tvinAutoRepublish.error =
                                getString(R.string.please_select_auto_republish_time)
                            binding.tvinAutoRepublish.requestFocus()
                        }



                } else {

                    binding.tvinProductStatus.error =
                        getString(R.string.please_choose_product_status)
                    binding.tvinProductStatus.requestFocus()
                }

            }
        }

        private fun getAdDetails(adId: String) {

            collectFlow(mViewModel.getAdDetails(adId)) {

                handelApiResult(it,
                    onResultSuccess = {

                        it.resultData?.data?.let { adData ->

                            mAdDetailsItem = adData
                            binding.edtAdName.setText(adData.title)
                            binding.edtLowestPrice.setText(
                                getString(
                                    R.string.holder_price,
                                    adData.price.toString()
                                )
                            )

                            binding.edtProductQuantity.setText(adData.quantity.toString())

                            adData.status?.let {
                                setAdStatus(it, adData.status_text.toString())
                            }
                            binding.edtAdDescription.setText(adData.description)
                            binding.edtReleaseDate.setText(adData.release_date)
                            binding.edtAddress.setText(adData.location)
                            binding.seekbarAdDuration.progress = adData.ad_duration?.toInt() ?: 1

                            selectedAdType = adData.ad_type_id ?: 0
                            selectedAdStatus = adData.product_status_id ?: 0
                            selectedAdRepublish = adData.auto_repost_id ?: 0
                            selectedAdDuration = adData.ad_duration?.toInt() ?: 0
                            selectedSection = adData.section_id ?: 0
                           // selectedSubsection = adData.subsection_id ?: 0

                            binding.spinEditAdAdType.adapter = adTypeSpinAdapter
                            binding.spinEditAdStatus.adapter = statusSpinAdapter
                            binding.spinEditAdAutoRepublish.adapter = adRepublishSpinAdapter
                            binding.spinEditAdSection.adapter = productSectionSpinAdapter

                            for (i in statusItems.indices) {

                                if (statusItems[i].id == selectedAdStatus) {

                                    binding.spinEditAdStatus.setSelection(i)

                                    statusItems[i].id?.let { selectedCId ->

                                        selectedAdStatus = selectedCId
                                    }

                                    break
                                }
                            }

                            for (i in adRepublishItems.indices) {

                                if (adRepublishItems[i].id == selectedAdRepublish) {

                                    binding.spinEditAdAutoRepublish.setSelection(i)

                                    adRepublishItems[i].id?.let { selectedCId ->

                                        selectedAdRepublish = selectedCId
                                    }

                                    break
                                }
                            }

                            for (i in adTypeItems.indices) {

                                if (adTypeItems[i].id == selectedAdType) {

                                    binding.spinEditAdAdType.setSelection(i)

                                    adTypeItems[i].id?.let { selectedCId ->

                                        selectedAdType = selectedCId
                                    }


                                    break
                                }
                            }

                            for (i in productSectionItems.indices) {

                                if (productSectionItems[i].id == adData.section_id) {

                                    binding.spinEditAdSection.setSelection(i)

                                    productSectionItems[i].id?.let { selectedSectionId ->

                                        selectedSection = selectedSectionId

                                        binding.tvinSection.placeholderText =
                                            productSectionItems[i].title

                                        productSectionItems[i].subsections?.let { subSectionData ->

//                                            productSubsectionItems.clear()
//                                            productSubsectionItems.add(initProductSubSection)
//                                            productSubsectionItems.addAll(
//                                                1,
//                                                subSectionData as ArrayList
//                                            )
//                                            binding.spinEditAdSubSection.adapter =
//                                                productSubsectionSpinAdapter


                                            for (j in subSectionData.indices) {


                                                if (subSectionData[j].id == adData.subsection_id) {


                                                    binding.spinEditAdSubSection.setSelection(j + 1)


                                                    subSectionData[j].id?.let { selectedSubsectionId ->

                                                        //selectedSubsection = selectedSubsectionId
                                                    }

                                                    break
                                                }
                                            }

                                            lifecycleScope.launch {

                                                delay(1000)
                                                firstLoad = false
                                            }

                                        }
                                    }

                                    break
                                }
                            }

                            imagesAdapter.setData(adData.images as ArrayList)
                            listOfImages.addAll(adData.images)
                        }
                    })
            }
        }

        private fun getAdData() {

            collectFlow(mViewModel.getAdData()) {

                // binding?.swipeLayout?.isRefreshing = false
                // binding?.recSearchResultList?.showView(false)

                handelApiResult(it,
                    onResultSuccess = {

                        it.resultData?.data?.let { data ->

                            adTypeItems.addAll(data.ad_types as ArrayList)
                            binding.spinEditAdAdType.adapter = adTypeSpinAdapter

                            statusItems.addAll(data.product_statuses as ArrayList)
                            binding.spinEditAdStatus.adapter = statusSpinAdapter

                            adRepublishItems.addAll(data.auto_repost as ArrayList)
                            binding.spinEditAdAutoRepublish.adapter = adRepublishSpinAdapter

                            productSectionItems.addAll(data.sections as ArrayList)
                            binding.spinEditAdSection.adapter = productSectionSpinAdapter

//                            productSubsectionItems.addAll(arrayListOf())
//                            binding.spinEditAdSubSection.adapter = productSubsectionSpinAdapter

                            getAdDetails(mAdId.toString())
                        }
                    })
            }
        }


        private fun updateAdStatus(params: HashMap<String, String>) {

            collectFlow(mViewModel.updateAdStatus(params)) {

                // binding?.swipeLayout?.isRefreshing = false
                // binding?.recSearchResultList?.showView(false)

                handelApiResult(it,
                    onResultSuccess = {

                        it.resultData?.data?.let { data ->

                            data.status?.let {

                                setAdStatus(it, data.status_text.toString())
                            }
                        }
                    })
            }
        }


        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

            if (position == 0) {

                parent?.let {

                    if (it.getChildAt(0) != null) {

                        (it.getChildAt(0) as TextView).setTextColor(
                            Color.parseColor("#9A9A9A")
                        )
                    }
                }
            }

            when (parent?.id) {


                R.id.spin_editAd_adType -> {

                    selectedAdType = adTypeItems[position].id ?: 0

                    if (selectedAdType == 0) {

                        binding.tvinAdType.placeholderText = ""

                    } else {

                        binding.tvinAdType.placeholderText = adTypeItems[position].title

                    }

                    when (selectedAdType) {

                        1 -> {

                            binding.tvinLowestPrice.hint =
                                getString(R.string.lowest_price_for_bidding)
                            binding.tvinLowestPrice.showView(true)
                            binding.tvinQuantity.showView(false)

                        }

                        2 -> {

                            binding.tvinLowestPrice.hint = getString(R.string.price)
                            binding.tvinLowestPrice.showView(true)
                            binding.tvinQuantity.showView(false)

                        }

                        3 -> {

                            binding.tvinLowestPrice.hint = getString(R.string.price)
                            binding.tvinLowestPrice.showView(true)
                            binding.tvinQuantity.showView(true)

                        }


                        4 -> {

                            binding.tvinLowestPrice.showView(false)
                            binding.tvinQuantity.showView(true)

                        }
                    }
                }

                R.id.spin_editAd_status -> {

                    selectedAdStatus = statusItems[position].id ?: 0

                    if (selectedAdStatus == 0) {

                        binding.tvinProductStatus.placeholderText = ""

                    } else {

                        binding.tvinProductStatus.placeholderText = statusItems[position].title

                    }
                }

                R.id.spin_editAd_autoRepublish -> {

                    selectedAdRepublish = adRepublishItems[position].id ?: 0

                    if (selectedAdRepublish == 0) {

                        binding.tvinAutoRepublish.placeholderText = ""

                    } else {

                        binding.tvinAutoRepublish.placeholderText = adRepublishItems[position].title

                    }
                }

                R.id.spin_editAd_section -> {

                    selectedSection = productSectionItems[position].id ?: 0
                    Log.e("sajkdg-", firstLoad.toString())

                    //if (!firstLoad) {
                       // selectedSubsection = 0
                    //}

                    if (selectedSection == 0) {

                        binding.tvinSection.placeholderText = ""

                    } else {

                        binding.tvinSection.placeholderText = productSectionItems[position].title

//                        if (!firstLoad) {

//                            Log.e("sajkdg--", firstLoad.toString())
//                            productSubsectionItems.clear()
//                            productSubsectionItems.add(initProductSubSection)
//                            productSubsectionItems.addAll(productSectionItems[position].subsections as ArrayList)
//                            binding.spinEditAdSubSection.adapter = productSubsectionSpinAdapter
//                        }
                    }
                }

//                R.id.spin_editAd_subSection -> {
//
//                    selectedSubsection = productSubsectionItems[position].id ?: 0
//
//                    if (selectedSubsection == 0) {
//
//                        binding.tvinSubSection.placeholderText = ""
//
//                    } else {
//
//                        binding.tvinSubSection.placeholderText =
//                            productSubsectionItems[position].title
//
//                    }
//                }
            }
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {

            // nothing to do
        }

        private fun updateAd(
            updatedData: Map<String, RequestBody>,
            updatedImage: List<MultipartBody.Part?>?
        ) {

            collectFlow(mViewModel.updateAd(updatedData, updatedImage)) {

                // binding?.swipeLayout?.isRefreshing = false
                // binding?.recSearchResultList?.showView(false)

                handelApiResult(it,
                    onResultSuccess = {


                        showCustomToast(it.message, Constants.TOAST_DONE)
                        finish()
                    })
            }
        }


        private fun setAdStatus(status: Int, statusText: String) {

            binding.tvAdStatus.text = statusText

            if (status == 1) {

                binding.tvAdStatus.setTextColor(getColor(R.color.green))
                binding.tvAdStatus.setBackgroundResource(R.drawable.bg_accepted_order)

            } else {

                binding.tvAdStatus.setTextColor(getColor(R.color.red))
                binding.tvAdStatus.setBackgroundResource(R.drawable.bg_reject_order)

            }
        }

    private fun initializeCapturedImageFileURI(): Uri {


        Log.e("base_activity_capturedImg", "initializeCapturedImageFileURI")

        val photoFile = File("${externalCacheDir}", "temp_images")

        if (!photoFile.exists()) photoFile.mkdirs()

        val file = File(photoFile, "${System.currentTimeMillis()}.jpg")

        capturedImageURI = FileProvider.getUriForFile(
            this,
            "${packageName}.fileprovider",
            file
        )

        Log.e("base_activity_capturedImg", capturedImageURI.toString())


        return capturedImageURI
    }

    fun initializeCameraDialog() {

        val options = arrayOf(getString(R.string.camera), getString(R.string.gallery))
        chooseImageSource = AlertDialog.Builder(this)
            .setTitle(getString(R.string.choose_image_source))
            .setSingleChoiceItems(options, 0) { dialogInterface, i ->

                imageSource = if (i == 0) {
                    0
                } else {
                    1
                }
            }
            .setPositiveButton(getString(R.string.done)) { _, _ ->

                if (imageSource == 0) {

                    if (isPermissionGranted(Manifest.permission.CAMERA)) {

                        Log.e("base_activity_capturedImg", "setPositiveButton")

                        getImageFromCamera.launch(initializeCapturedImageFileURI())

                    } else {

                        requestedPermission = Manifest.permission.CAMERA
                        requestImagePermissionLauncher.launch(Manifest.permission.CAMERA)

                    }
                } else {

                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

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


            }.setNegativeButton(R.string.cancel) { _, _ ->

                chooseImageSource.dismiss()

            }.create()
    }
    }