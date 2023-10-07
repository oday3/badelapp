package com.badl.apps.android.baseClasses.ui

import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.badl.apps.android.application.BadelApp
import com.badl.apps.android.utils.LocaleLanguageUtils
import com.badl.apps.android.utils.SharedPrefUtils
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
abstract class BaseActivity : AppCompatActivity() {

    @Inject
    lateinit var sharedPrefUtils: SharedPrefUtils

//    private var requestedPermission = ""
//    private lateinit var chooseImageSource: AlertDialog
//    private var imageSource: Int = 0
    lateinit var dialog: ProgressBarDialog
    lateinit var mLifecycleObserver: DefaultLifecycleObserver

//    private lateinit var capturedImageURI: Uri
//    private val networkLiveData by lazy {   MutableLiveData<Boolean>() }
//
//    private val _capturedImageURI by lazy { MutableLiveData<Uri>() }
//    val mCapturedImageURI = _capturedImageURI
//    private lateinit var myClipboard: ClipboardManager
//
//    private val _pickedImageURI  by lazy { MutableLiveData<Uri>() }
//    val mPickedImageURI = _pickedImageURI
//
//    private val _pickedFileURI by lazy { MutableLiveData<Uri>() }
//    val mPickedFileURI = _pickedFileURI
//
//    private val resetNotifierData by lazy { HashMap<String, String>() }
//
//    private val requestImagePermissionLauncher: ActivityResultLauncher<String> =
//        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
//            if (isGranted) {
//                if (requestedPermission == Manifest.permission.READ_EXTERNAL_STORAGE
//                    || requestedPermission == Manifest.permission.READ_MEDIA_IMAGES) {
//
//                    getImageFromGallery.launch("image/*")
//
//                } else if (requestedPermission == Manifest.permission.CAMERA) {
//
//                    getImageFromCamera.launch(initializeCapturedImageFileURI())
//                }
//            } else {
//
//                this.showMessage("Opss, if you don't mind we need permission to continue")
//            }
//        }
//
//    private val requestFilePermissionLauncher: ActivityResultLauncher<String> =
//        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
//            if (isGranted) {
//                if (requestedPermission == Manifest.permission.READ_EXTERNAL_STORAGE) {
//
//                    getFileFromStorage.launch(arrayOf("*/*"))
//
//                }
//            } else {
//
//                this.showMessage("Opss, if you don't mind we need permission to continue")
//            }
//        }
//
//    private val getImageFromGallery  =
//        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
//            // Handle the returned Uri
//
//            if (uri != null) {
//
//                _pickedImageURI.value = uri
//
//            }
//        }
//
//    private val getFileFromStorage =
//        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
//            // Handle the returned Uri
//
//            if (uri != null) {
//
//                _pickedFileURI.value = uri
//
//            }
//        }
//
//    private val getImageFromCamera =
//        registerForActivityResult(ActivityResultContracts.TakePicture()) { isSaved: Boolean ->
//
//            if (isSaved) {
//
//                _capturedImageURI.value = capturedImageURI
//                Log.e("base_activity_capturedImg", _capturedImageURI.value.toString())
//
//            }
//        }

    private lateinit var myClipboard: ClipboardManager

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        LocaleLanguageUtils.setLocale(
            this, sharedPrefUtils.appLang)


    }


    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(LocaleLanguageUtils.onAttach(newBase))
        LocaleLanguageUtils.setLocale(
            newBase, (applicationContext as BadelApp).sharedPrefUtils.appLang)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        LocaleLanguageUtils.setLocale(
            this, (applicationContext as BadelApp).sharedPrefUtils.appLang)

        mLifecycleObserver = object: DefaultLifecycleObserver{

            override fun onCreate(owner: LifecycleOwner) {
                super.onCreate(owner)
                is_activity_active = true
                Log.e("activity_cycle_observer", " =====> Activity name = ${this@BaseActivity.javaClass.simpleName} <===== \n" +
                        "        Lifecycle = onCreate \n" +
                        "        Value = ${is_activity_active.toString()} \n " +
                        "=_=_=_=_=_=_=_=_=_=_=_=_=_=_=_=_=_=_=_=_=_=_=_=_=_=_=_=_=_=_=_=_=_=\n")
            }

            override fun onStart(owner: LifecycleOwner) {
                super.onStart(owner)
                is_activity_active = true
                Log.e("activity_cycle_observer", " =====> Activity name = ${this@BaseActivity.javaClass.simpleName} <===== \n" +
                        "        Lifecycle = onStart \n" +
                        "        Is activity active = ${is_activity_active.toString()} \n " +
                        "=_=_=_=_=_=_=_=_=_=_=_=_=_=_=_=_=_=_=_=_=_=_=_=_=_=_=_=_=_=_=_=_=_=\n")
            }

            override fun onResume(owner: LifecycleOwner) {
                super.onResume(owner)
                is_activity_active = true

                Log.e("activity_cycle_observer", " =====> Activity name = ${this@BaseActivity.javaClass.simpleName} <===== \n" +
                        "        Lifecycle = onResume \n" +
                        "        Is activity active = ${is_activity_active.toString()} \n " +
                        "=_=_=_=_=_=_=_=_=_=_=_=_=_=_=_=_=_=_=_=_=_=_=_=_=_=_=_=_=_=_=_=_=_=\n")
            }

            override fun onStop(owner: LifecycleOwner) {
                super.onStop(owner)
                is_activity_active = false

                Log.e("activity_cycle_observer", " =====> Activity name = ${this@BaseActivity.javaClass.simpleName} <===== \n" +
                        "        Lifecycle = onStop \n" +
                        "        Is activity active = ${is_activity_active.toString()} \n " +
                        "=_=_=_=_=_=_=_=_=_=_=_=_=_=_=_=_=_=_=_=_=_=_=_=_=_=_=_=_=_=_=_=_=_=\n")
            }

            override fun onDestroy(owner: LifecycleOwner) {
                super.onDestroy(owner)
                is_activity_active = false

                Log.e("activity_cycle_observer", " =====> Activity name = ${this@BaseActivity.javaClass.simpleName} <===== \n" +
                        "        Lifecycle = onDestroy \n" +
                        "        Is activity active = ${is_activity_active.toString()} \n " +
                        "=_=_=_=_=_=_=_=_=_=_=_=_=_=_=_=_=_=_=_=_=_=_=_=_=_=_=_=_=_=_=_=_=_=\n")
            }

        }

        lifecycle.addObserver(mLifecycleObserver)
    }

//    override fun attachBaseContext(newBase: Context?) {
//        super.attachBaseContext(LocaleLanguageUtils.onAttach(newBase))
////        _root_ide_package_.com.darroaf.android.feapp.utils.LocaleLanguageUtils.setLocale(
////            this, (applicationContext as TheContentApp)
////                .getServiceLocator().shardPrefUtil.appLang
////        )
//    }

//    override fun onAttachedToWindow() {
//        super.onAttachedToWindow()
//        LocaleLanguageUtils.setLocale(
//            this, (applicationContext as EyadtyApp)
//                .getServiceLocator().shardPrefUtil.appLang)
//    }


//    override fun finish() {
//        super.finish()
//        onLeaveThisActivity()
//    }
//
//    protected open fun onLeaveThisActivity() {
//        overridePendingTransition(com.badel.apps.android.R.anim.enter_from_left, com.badel.apps.android.R.anim.exit_to_right)
//    }
    override fun onResume() {
        super.onResume()

//        com.badel.apps.android.firebaseMessage.FCMMessagingService.actionNotifier.observe(this) {
//
//            if (it[Constants.FROM] == Constants.NOTIFICATION_TYPE_ACTIVATION) {
//
//                val isActive = it[Constants.IS_ACTIVE]
//
//                if (isActive == "0") {
//
//                    val intent = Intent(this, AccountSuspendedActivity::class.java)
//                    startActivity(intent)
//                    finishAffinity()
//                    com.badel.apps.android.firebaseMessage.FCMMessagingService.actionNotifier.value = resetNotifierData
//                } else if (isActive == "1"){
//
//                    val intent = Intent(this, MainActivity::class.java)
//                    startActivity(intent)
//                    finishAffinity()
//                    com.badel.apps.android.firebaseMessage.FCMMessagingService.actionNotifier.value = resetNotifierData
//                }
//
//
//            } else if (it[Constants.FROM] == Constants.NOTIFICATION_TYPE_USER_DESTROY) {
//
//                val intent = Intent(this, LoginActivity::class.java)
//                intent.addFlags(
//                    Intent.FLAG_ACTIVITY_CLEAR_TASK
//                            or Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                startActivity(intent)
//                finishAffinity()
//                com.badel.apps.android.firebaseMessage.FCMMessagingService.actionNotifier.value = resetNotifierData
//            }
//        }
    }



    abstract fun initViews()
    abstract fun initData()
    abstract fun initAdapters()
    abstract fun initObservers()
    abstract fun initListeners()

//    open fun setupNetworkState() {
//        networkLiveData.postValue(checkNetworkState())
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            val connectivityManager =
//                this.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
//            connectivityManager.registerDefaultNetworkCallback(object :
//                ConnectivityManager.NetworkCallback() {
//                override fun onAvailable(network: Network) {
//                    super.onAvailable(network)
//                    networkLiveData.postValue(checkNetworkState())
//                }
//
//                override fun onLost(network: Network) {
//                    super.onLost(network)
//                    networkLiveData.postValue(checkNetworkState())
//                }
//            })
//        }
//        (applicationContext as EyadtyApp).initializeNetworkLiter(this)
//    }

//    open fun getNetworkStateChanged(): MutableLiveData<Boolean> {
//        return networkLiveData
//    }


//    override fun networkStateChanged(bbb: Boolean) {
//        // not implemented
//    }

//     fun initializeCameraDialog() {
//
//        val options = arrayOf(getString(R.string.camera), getString(R.string.gallery))
//        chooseImageSource = AlertDialog.Builder(this)
//            .setTitle(getString(R.string.choose_image_source))
//            .setSingleChoiceItems(options, 0) { dialogInterface, i ->
//
//                imageSource = if (i == 0) {
//                    0
//                } else {
//                    1
//                }
//            }
//            .setPositiveButton(getString(R.string.done)) { _, _ ->
//
//                if (imageSource == 0) {
//
//                    if (isPermissionGranted(Manifest.permission.CAMERA)) {
//                        getImageFromCamera.launch(initializeCapturedImageFileURI())
//                    } else {
//
//                        requestedPermission = Manifest.permission.CAMERA
//                        requestImagePermissionLauncher.launch(Manifest.permission.CAMERA)
//
//                    }
//                } else {
//
//
//                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//
//                        if (isPermissionGranted(Manifest.permission.READ_MEDIA_IMAGES)) {
//
//                            getImageFromGallery.launch("image/*")
//
//                        } else {
//
//                            requestedPermission = Manifest.permission.READ_MEDIA_IMAGES
//                            requestImagePermissionLauncher.launch(requestedPermission)
//                        }
//
//                    } else {
//
//
//                        if (isPermissionGranted(Manifest.permission.READ_EXTERNAL_STORAGE)) {
//
//                            getImageFromGallery.launch("image/*")
//
//                        } else {
//
//                            requestedPermission = Manifest.permission.READ_EXTERNAL_STORAGE
//                            requestImagePermissionLauncher.launch(requestedPermission)
//                        }
//
//                    }
//                }
//
//
//            }.setNegativeButton(R.string.cancel) { _, _ ->
//
//                chooseImageSource.dismiss()
//
//            }.create()
//    }

//    private fun initializeCapturedImageFileURI(): Uri {
//
//        val photoFile = File("$externalCacheDir", "temp_images")
//
//        if (!photoFile.exists()) photoFile.mkdirs()
//
//        val file = File(photoFile, "${System.currentTimeMillis()}.jpg")
//
//        capturedImageURI = FileProvider.getUriForFile(
//            this,
//            "${packageName}.fileprovider",
//            file
//        )
//
//
//        return capturedImageURI
//    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        if (item.itemId == android.R.id.home) {
//            onBackPressed()
//        }
//        return true
//    }

//    fun showImagePicker() {
//
//        chooseImageSource.show()
//    }
//
//    fun showFilePicker() {
//
//        if (isPermissionGranted(Manifest.permission.READ_EXTERNAL_STORAGE)) {
//
//            getFileFromStorage.launch(arrayOf("*/*"))
//
//        } else {
//
//            requestedPermission = Manifest.permission.READ_EXTERNAL_STORAGE
//            requestFilePermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
//        }
//    }
//
    fun showDialog(showDialog: Boolean) {


        if (!this::dialog.isInitialized) {

            dialog = ProgressBarDialog(this@BaseActivity)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        }

        if (showDialog) {
            dialog.show()

        } else {

            dialog.dismiss()

        }
    }
//
    fun getClipboard(): ClipboardManager {

        return if (this::myClipboard.isInitialized) {

            myClipboard

        } else {

            getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        }
    }


    override fun onDestroy() {
        super.onDestroy()

        lifecycle.removeObserver(mLifecycleObserver)

    }

    companion object {

        var     is_activity_active = false
    }
}