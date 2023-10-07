package com.badl.apps.android.appFeatures.addAd.ui

import android.Manifest
import android.app.Dialog
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.FileProvider
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.badl.apps.android.R
import com.badl.apps.android.adapters.AddedProductImagesAdapter
import com.badl.apps.android.adapters.TextSpinnerAdapter
import com.badl.apps.android.appFeatures.userAccount.data.AdImageItem
import com.badl.apps.android.appFeatures.main.data.SectionItem
import com.badl.apps.android.appFeatures.main.data.SubSectionItem
import com.badl.apps.android.baseClasses.ui.BaseActivity
import com.badl.apps.android.databinding.DialogBiddingDoneBinding
import com.badl.apps.android.databinding.FragmentAddAdProducBinding
import com.badl.apps.android.network.ApiConstants
import com.badl.apps.android.utils.AppUtil.collectFlow
import com.badl.apps.android.utils.Constants
import com.badl.apps.android.utils.FileUtilsKt
import com.badl.apps.android.utils.NetworkUtils
import com.badl.apps.android.utils.NetworkUtils.handelApiResult
import com.badl.apps.android.utils.UiUtils
import com.badl.apps.android.utils.UiUtils.extensionSetBackground
import com.badl.apps.android.utils.UiUtils.getScreenDensity
import com.badl.apps.android.utils.UiUtils.showCustomToast
import com.badl.apps.android.utils.ValidationUtils.isPermissionGranted
import com.badl.apps.android.utils.ValidationUtils.validateEmpty
import com.google.android.material.textfield.TextInputLayout
import id.zelory.compressor.Compressor
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddAdProducFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddAdProducFragment : Fragment(), AdapterView.OnItemSelectedListener {
    private var binding: FragmentAddAdProducBinding? = null
    private lateinit var imagesAdapter: AddedProductImagesAdapter
    private val mViewModel by activityViewModels<AdsViewModel>()
    val adData = HashMap<String, RequestBody>()
    private var adImages = ArrayList<MultipartBody.Part?>()
    var listOfImages = ArrayList<AdImageItem>()


    private var param1: String? = null
    private var param2: String? = null

    private var selectedSection = 0
    //private var selectedSubsection = 0

    private lateinit var chooseImageSource: AlertDialog
    private var imageSource: Int = 0

    private lateinit var productSectionSpinAdapter: TextSpinnerAdapter<SectionItem>
    private val productSectionItems = arrayListOf<SectionItem>()
    private lateinit var initProductSection: SectionItem

//    private lateinit var productSubsectionSpinAdapter: TextSpinnerAdapter<SubSectionItem>
//    private val productSubsectionItems = arrayListOf<SubSectionItem>()
//    private lateinit var initProductSubSection: SubSectionItem

    private val _pickedImageURI by lazy { MutableLiveData<Uri>() }
    private val mPickedImageURI = _pickedImageURI

    private val _capturedImageURI by lazy { MutableLiveData<Uri>() }
    val mCapturedImageURI = _capturedImageURI

    private lateinit var capturedImageURI: Uri


    private var requestedPermission = ""

    private lateinit var doneDialog: Dialog
    private lateinit var doneDialogBinding: DialogBiddingDoneBinding
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

                (requireActivity() as BaseActivity).showCustomToast(
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
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddAdProducBinding.inflate(inflater, container, false)
        doneDialogBinding = DialogBiddingDoneBinding.inflate(layoutInflater)

        imagesAdapter = AddedProductImagesAdapter(requireActivity()) { position , item ->

            listOfImages.removeAt(position)
            imagesAdapter.setData(listOfImages)
        }

        initializeCameraDialog()


        binding?.recListOfImages?.adapter = imagesAdapter

        binding?.linearAddImage?.setOnClickListener {

            chooseImageSource.show()
        }

        mPickedImageURI.observe(viewLifecycleOwner) {

            if (it != null) {

                if (listOfImages.size >= 5) {

                    (requireActivity() as BaseActivity).showCustomToast(
                        getString(R.string.unfortunately_the_limit_is_only_5_images),
                        Constants.TOAST_INFO
                    )

                } else {

                    listOfImages.add(AdImageItem(0, it.toString(), true))
                    imagesAdapter.setData(listOfImages)
                }
            }
        }

        mCapturedImageURI.observe(viewLifecycleOwner) {

            if (it != null) {

                if (listOfImages.size >= 5) {

                    (requireActivity() as BaseActivity).showCustomToast(
                        getString(R.string.unfortunately_the_limit_is_only_5_images),
                        Constants.TOAST_INFO
                    )

                } else {

                    listOfImages.add(AdImageItem(0, it.toString(), true))
                    imagesAdapter.setData(listOfImages)
                }
            }
        }


        initProductSection = SectionItem(
            "",
            "0",
            0,
            getString(R.string.choose_section),
            arrayListOf(),
            getString(R.string.choose_section)
        )

        productSectionItems.add(initProductSection)

//        initProductSubSection = SubSectionItem(
//            "",
//            "0",
//            0,
//            getString(R.string.choose_sub_section),
//            "",
//            "",
//            getString(R.string.choose_sub_section)
//        )
//        productSubsectionItems.add(initProductSubSection)


        productSectionSpinAdapter = TextSpinnerAdapter(productSectionItems)
      //  productSubsectionSpinAdapter = TextSpinnerAdapter(productSubsectionItems)


        binding?.spinSection?.adapter = productSectionSpinAdapter
       // binding?.spinSubSection?.adapter = productSubsectionSpinAdapter


        binding?.spinSection?.onItemSelectedListener = this
       // binding?.spinSubSection?.onItemSelectedListener = this


        doneDialog =
            UiUtils.createDialog(
                requireContext(),
                themeResId = R.style.TransparentAlertDialog,
                windowAnimationResId = R.style.SlideDialogAnimation,
                true, doneDialogBinding.root
            )

        doneDialog.extensionSetBackground(
            (getScreenDensity() * 14).toInt(),
            AppCompatResources.getDrawable(requireContext(), R.drawable.bg_dialog_),
        )

        doneDialogBinding.tvTitle.text = getString(R.string.added_successfully)
        doneDialogBinding.tvDescription.text = getString(R.string.the_ad_has_been_successfully_added)
        doneDialogBinding.btnBiddingList.text = getString(R.string.done)

        return binding?.root

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        (requireActivity() as AddAdActivity).adData.observe(viewLifecycleOwner) {

            it?.let {

                productSectionItems.addAll(it.sections as ArrayList)
                binding?.spinSection?.adapter = productSectionSpinAdapter

               // productSubsectionItems.addAll(arrayListOf())
              //  binding?.spinSubSection?.adapter = productSubsectionSpinAdapter

            }
        }

        binding?.btnPublish?.setOnClickListener {

            clearErrors(
                binding?.tvinSection!!,
                binding?.tvinSubSection!!,
                binding?.tvinAdDescription!!,
                binding?.tvinReleaseDate!!,
                binding?.tvinAddress!!
            )

            if (selectedSection != 0) {

               // if (selectedSubsection != 0) {

                    if (validateEmpty(
                            binding?.tvinAdDescription!!,
                            binding?.tvinReleaseDate!!,
                            binding?.tvinAddress!!
                        )
                    ) {

                        if (listOfImages.isNotEmpty()) {

                            lifecycleScope.launch {

                                if ((requireActivity() as AddAdActivity).mAdProductStatus != 0) {

                                    adData[ApiConstants.PAR_PRODUCT_STATUS_ID] =
                                        NetworkUtils.createPartFromString((requireActivity() as AddAdActivity).mAdProductStatus.toString())
                                }


                                adData[ApiConstants.PAR_TITLE] =
                                    NetworkUtils.createPartFromString((requireActivity() as AddAdActivity).mAdName.toString())

                                adData[ApiConstants.PAR_PRICE] =
                                    NetworkUtils.createPartFromString((requireActivity() as AddAdActivity).mAdPrice.toString())

                                adData[ApiConstants.PAR_QUANTITY] =
                                    NetworkUtils.createPartFromString((requireActivity() as AddAdActivity).mProductQuantity.toString())

                                adData[ApiConstants.PAR_AD_DURATION] =
                                    NetworkUtils.createPartFromString((requireActivity() as AddAdActivity).mAdDuration.toString())

                                if ((requireActivity() as AddAdActivity).mAdAutoRepublishTimes != 0) {

                                    adData[ApiConstants.PAR_AUTO_REPOST_ID] =
                                        NetworkUtils.createPartFromString((requireActivity() as AddAdActivity).mAdAutoRepublishTimes.toString())
                                }

                                if ((requireActivity() as AddAdActivity).mAdType != 0) {

                                    adData[ApiConstants.PAR_AD_TYPE_ID] =
                                        NetworkUtils.createPartFromString((requireActivity() as AddAdActivity).mAdType.toString())
                                }

                                if (selectedSection != 0) {

                                    adData[ApiConstants.PAR_SECTION_ID] =
                                        NetworkUtils.createPartFromString(selectedSection.toString())
                                }

//                                if (selectedSubsection != 0) {
//
//                                    adData[ApiConstants.PAR_SUBSECTION_ID] =
//                                        NetworkUtils.createPartFromString(selectedSubsection.toString())
//                                }


                                adData[ApiConstants.PAR_LOCATION] =
                                    NetworkUtils.createPartFromString(binding?.edtAddress?.text.toString())


                                adData[ApiConstants.PAR_RELEASE_DATE] =
                                    NetworkUtils.createPartFromString(binding?.edtReleaseDate?.text.toString())


                                adData[ApiConstants.PAR_DESCRIPTION] =
                                    NetworkUtils.createPartFromString(binding?.edtAdDescription?.text.toString())


                                for (i in 0..listOfImages.size - 1) {

                                    if (listOfImages[i].toString().isNotEmpty()) {

                                        val adImage = Compressor.compress(
                                            requireContext(),
                                            FileUtilsKt.from(
                                                requireContext(), Uri.parse(listOfImages[i].image)
                                            )
                                        )


                                        adImages.add(NetworkUtils.createFilePart(
                                            "${ApiConstants.PAR_IMAGES}[${i}]",
                                            adImage))

                                    }
                                }

                                addAd(adData, adImages)
                            }

                        } else {

                            showCustomToast(
                                getString(R.string.please_select_at_lease_one_image_for_ad),
                                Constants.TOAST_INFO
                            )
                        }
                    }
//                } else {
//
//                    binding?.tvinSubSection?.error =
//                        getString(R.string.please_select_product_sub_section)
//                    binding?.tvinSubSection?.requestFocus()
//                }
            } else {

                binding?.tvinSection?.error = getString(R.string.please_select_product_section)
                binding?.tvinSection?.requestFocus()
            }
        }

        doneDialogBinding.btnBiddingList.setOnClickListener {

            doneDialog.dismiss()
            requireActivity().finish()
        }
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
         * @return A new instance of fragment AddAdProducFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddAdProducFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

        if (position == 0) {

            parent?.let {

                (it.getChildAt(0) as TextView).setTextColor(
                    Color.parseColor("#9A9A9A")
                )
            }
        }

        when (parent?.id) {

            R.id.spin_section -> {

                selectedSection = productSectionItems[position].id ?: 0
              //  selectedSubsection = 0

                if (selectedSection == 0) {

                    binding?.tvinSection?.placeholderText = ""

                } else {

                    binding?.tvinSection?.placeholderText = productSectionItems[position].title

//                    productSubsectionItems.clear()
//                    productSubsectionItems.add(initProductSubSection)
//                    productSubsectionItems.addAll(productSectionItems[position].subsections as ArrayList)
                   // binding?.spinSubSection?.adapter = productSubsectionSpinAdapter

                }
            }

//            R.id.spin_subSection -> {
//
//                selectedSubsection = productSubsectionItems[position].id ?: 0
//
//                if (selectedSubsection == 0) {
//
//                    binding?.tvinSubSection?.placeholderText = ""
//
//                } else {
//
//                    binding?.tvinSubSection?.placeholderText =
//                        productSubsectionItems[position].title
//
//                }
//            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

        // nothing to do
    }

    private fun clearErrors(vararg textViews: TextInputLayout) {

        for (i in 0..textViews.size - 1) {

            textViews[i].error = null
        }
    }

    private fun addAd(
        updatedData: Map<String, RequestBody>,
        updatedImage: List<MultipartBody.Part?>?
    ) {

        collectFlow(mViewModel.addAd(updatedData, updatedImage)) {

            // binding?.swipeLayout?.isRefreshing = false
            // binding?.recSearchResultList?.showView(false)

            handelApiResult(it,
                onResultSuccess = {


                    doneDialog.show()

                })
        }
    }

    private fun initializeCapturedImageFileURI(): Uri {


        Log.e("base_activity_capturedImg", "initializeCapturedImageFileURI")

        val photoFile = File("${(requireActivity() as BaseActivity).externalCacheDir}", "temp_images")

        if (!photoFile.exists()) photoFile.mkdirs()

        val file = File(photoFile, "${System.currentTimeMillis()}.jpg")

        capturedImageURI = FileProvider.getUriForFile(
            requireActivity(),
            "${(requireActivity() as BaseActivity).packageName}.fileprovider",
            file
        )

        Log.e("base_activity_capturedImg", capturedImageURI.toString())


        return capturedImageURI
    }

    fun initializeCameraDialog() {

        val options = arrayOf(getString(R.string.camera), getString(R.string.gallery))
        chooseImageSource = AlertDialog.Builder(requireActivity())
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