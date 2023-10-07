package com.badl.apps.android.appFeatures.chat.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.lifecycleScope
import com.badl.apps.android.R
import com.badl.apps.android.adapters.AddressesAdapter
import com.badl.apps.android.appFeatures.chat.data.AddressItem
import com.badl.apps.android.baseClasses.ui.BaseActivity
import com.badl.apps.android.databinding.ActivityChooseLoacationBinding
import com.badl.apps.android.databinding.DialogAddAddressBinding
import com.badl.apps.android.databinding.DialogEnableGpsBinding
import com.badl.apps.android.network.ApiConstants
import com.badl.apps.android.utils.AppUtil.collectFlow
import com.badl.apps.android.utils.Constants
import com.badl.apps.android.utils.LocationUtils
import com.badl.apps.android.utils.NetworkUtils.handelApiResult
import com.badl.apps.android.utils.UiUtils
import com.badl.apps.android.utils.UiUtils.extensionSetBackground
import com.badl.apps.android.utils.UiUtils.getScreenDensity
import com.badl.apps.android.utils.UiUtils.initializeMainToolBar
import com.badl.apps.android.utils.UiUtils.showCustomToast
import com.badl.apps.android.utils.ValidationUtils.validateEmpty
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.libraries.places.api.Places
import com.google.maps.GeoApiContext
import com.google.maps.PlacesApi
import com.google.maps.model.PlaceType
import com.google.maps.model.PlacesSearchResponse
import com.google.maps.model.RankBy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.Locale


class ChooseLocationActivity : BaseActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityChooseLoacationBinding
    private val mViewModel by viewModels<ChatViewModel>()
    private lateinit var adapter: AddressesAdapter
    private lateinit var mMap: GoogleMap
    private lateinit var mLocationMarker: Marker
    private lateinit var geocoder: Geocoder
    private lateinit var mCenterOfMap: LatLng

    private lateinit var enableGPS: Dialog
    private lateinit var enableGPSBinding: DialogEnableGpsBinding

    private lateinit var addAddressDialog: Dialog
    private lateinit var addAddressDialogBinding: DialogAddAddressBinding

    private lateinit var userLocation: LatLng
    private val looper = Looper.getMainLooper()
    private var zoomToMyLocationFlag = false
    private var checkUserLocation = false
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationManager: LocationManager
    private lateinit var mapFragment: SupportMapFragment
    private val addressData = HashMap<String, String>()

    private val mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {

            showDialog(false)
            Log.e("location_test", "mLocationCallback")

            Log.e("aswigfaks", "onLocationResult")
            val mLastLocation = locationResult.lastLocation
            if (mLastLocation != null) {

                userLocation = LatLng(mLastLocation.latitude, mLastLocation.longitude)


                Log.e("location_test", userLocation.toString())

                // if (zoomToMyLocationFlag) {

                zoomToMyLocationFlag = false

                val cameraPosition = CameraPosition.Builder()
                    .target(userLocation).zoom(15f).build()

                if (this@ChooseLocationActivity::mLocationMarker.isInitialized) {

                    mLocationMarker.remove()

                }

//                mMap.addMarker(MarkerOptions().position(userLocation))?.let {
//
//                    mLocationMarker = it
//                }

                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

                showDialog(false)

                showLocationName(userLocation)
            }
        }
    }

    private val requestPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->

            //(requireActivity() as BaseActivity).showDialog(false)
            if (isGranted) { // permission is granted

                showDialog(true)
                getLastLocation()

            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseLoacationBinding.inflate(layoutInflater)
        enableGPSBinding = DialogEnableGpsBinding.inflate(layoutInflater)
        addAddressDialogBinding =  DialogAddAddressBinding.inflate(layoutInflater)

        setContentView(binding.root)

        initializeMainToolBar(getString(R.string.choose_location))

        Places.initialize(applicationContext, getString(R.string.map_api_key));


        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(this)
        locationManager =
            this.getSystemService(Context.LOCATION_SERVICE) as LocationManager


        geocoder = Geocoder(this, Locale.getDefault())


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment

        mapFragment.getMapAsync(this)

        initAdapters()
        initViews()
        initListeners()

        getAddresses()
    }


    override fun onResume() {
        super.onResume()

        if (LocationUtils.isLocationEnabled(locationManager) && checkUserLocation) {

            checkUserLocation = false
            showDialog(true)
            getLastLocation(true)

        } else {

            showDialog(false)
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation(firstTime: Boolean = false) {

        if (LocationUtils.isFinePermissionLocationGranted(this)
            && LocationUtils.isCoarsePermissionLocationGranted(this)
        ) {


            if (LocationUtils.isLocationEnabled(locationManager)) {

                if (firstTime) {

                    LocationUtils.requestNewLocationData(
                        fusedLocationProviderClient,
                        mLocationCallback,
                        looper
                    )

                } else {

                    fusedLocationProviderClient.lastLocation.addOnCompleteListener {

                        showDialog(false)
                        val currentLocation = it.result

                        if (currentLocation == null) {

                            Log.e("location_test", "user_location_null")
                            LocationUtils.requestNewLocationData(
                                fusedLocationProviderClient,
                                mLocationCallback,
                                looper
                            )
                            // showDialog(true)
                        } else {


                            Log.e("location_test", "user_location_not_null")
                            //showDialog(false)

                            userLocation =
                                LatLng(currentLocation.latitude, currentLocation.longitude)


                            Log.e("location_test", "user_location = ${userLocation.toString()}")


                            //  if (zoomToMyLocationFlag) {

                            zoomToMyLocationFlag = false
                            val cameraPosition = CameraPosition.Builder()
                                .target(userLocation).zoom(15f).build()


                            if (this@ChooseLocationActivity::mLocationMarker.isInitialized) {

                                mLocationMarker.remove()

                            }

//                            mMap.addMarker(MarkerOptions().position(userLocation))?.let {
//
//                                mLocationMarker = it
//                            }

                            mMap.animateCamera(
                                CameraUpdateFactory.newCameraPosition(
                                    cameraPosition
                                )
                            )


                            showLocationName(userLocation)

                        }
                    }.addOnFailureListener {

                        Log.e("loaction_exeption", "${it.message}")
                        showDialog(false)
                    }
                }


            } else {

                enableGPS.show()

            }

        } else {

            showCustomToast(
                getString(R.string.please_check_location_permission),
                Constants.TOAST_INFO
            )

            requestPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    override fun initViews() {

        binding.recListOfAddresses.adapter = adapter

        enableGPS =
            UiUtils.createDialog(
                this,
                themeResId =
                R.style.TransparentAlertDialog,
                windowAnimationResId =
                R.style.SlideDialogAnimation,
                true, enableGPSBinding.root
            )

        enableGPS.extensionSetBackground(
            (getScreenDensity() * 24).toInt(),
            AppCompatResources.getDrawable(
                this,
                R.drawable.bg_dialog_
            ),)


        addAddressDialog =
            UiUtils.createDialog(
                this,
                themeResId =
                R.style.TransparentAlertDialog,
                windowAnimationResId =
                R.style.SlideDialogAnimation,
                true, addAddressDialogBinding.root
            )

        addAddressDialog.extensionSetBackground(
            (getScreenDensity() * 24).toInt(),
            AppCompatResources.getDrawable(
                this,
                R.drawable.bg_dialog_
            ),)
    }

    override fun initData() {
        // nothing to do
    }

    override fun initAdapters() {

        adapter = AddressesAdapter(this, ::onAddressClick)

    }

    override fun initObservers() {
        // nothing to do
    }

    override fun initListeners() {

        enableGPSBinding.tvEnableGpsDialogNo.setOnClickListener {

            showDialog(false)
            enableGPS.dismiss()
        }

        enableGPSBinding.tvEnableGpsDialogEnable.setOnClickListener {

            checkUserLocation = true
            enableGPS.dismiss()
            Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).run {
                startActivity(this)
            }
        }

        binding.imgMyLocation.setOnClickListener {

            zoomToMyLocationFlag = true
            showDialog(true)
            getLastLocation()
        }

        binding.tvSaveLocation.setOnClickListener {

            addAddressDialogBinding.tvAdAddressDialogDescription.text = binding.tvLocationName.text.toString()
            addAddressDialog.show()

        }

        addAddressDialogBinding.tvAdAddressDialogCancel.setOnClickListener {

            addAddressDialog.dismiss()
        }

        addAddressDialogBinding.tvAdAddressDialogSave.setOnClickListener {

            if (validateEmpty(addAddressDialogBinding.edtAdAddressDialogAddress)) {

                addressData[ApiConstants.PAR_LAT] = mCenterOfMap.latitude.toString()
                addressData[ApiConstants.PAR_LNG] = mCenterOfMap.longitude.toString()
                addressData[ApiConstants.PAR_TITLE] = addAddressDialogBinding.edtAdAddressDialogAddress.text.toString()
                addressData[ApiConstants.PAR_ADDRESS] = binding.tvLocationName.text.toString()

                addAddress(addressData)

                addAddressDialog.dismiss()

            }
        }
    }

    override fun onMapReady(p0: GoogleMap) {

        mMap = p0
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.setOnCameraIdleListener {


            mCenterOfMap = mMap.cameraPosition.target

            showLocationName(mMap.cameraPosition.target)

            val ss = com.google.maps.model.LatLng()
            ss.lat = mCenterOfMap.latitude
            ss.lng = mCenterOfMap.longitude

            handelPlacesResult(getPlaces(ss))
        }


        showDialog(true)
        getLastLocation(true)
    }

    private fun showLocationName(location: LatLng) {

        lifecycleScope.launch(Dispatchers.IO) {

            LocationUtils.geLocationName(location, geocoder).let {

                withContext(Dispatchers.Main) {

                    binding.tvLocationName.text = it

                    if (TextUtils.isEmpty(it)) binding.tvLocationName.text =
                        getString(
                            R.string.unknown_location
                        )
                }
            }
        }
    }

    fun getPlaces(location: com.google.maps.model.LatLng): PlacesSearchResponse {

        var request = PlacesSearchResponse()
        val context: GeoApiContext = GeoApiContext.Builder()
            .apiKey(getString(R.string.map_api_key))
            .build()

        try {
            request = PlacesApi.nearbySearchQuery(context, location)
                .radius(5000)
                .rankby(RankBy.PROMINENCE)
                .keyword("cruise")
                .language("en")
                .type(PlaceType.RESTAURANT)
                .await()
        } catch (e: ApiException) {

            Log.e("asnkjbd", e.message.toString())
            e.printStackTrace()
        } catch (e: IOException) {
            Log.e("asnkjbd", e.message.toString())

            e.printStackTrace()
        } catch (e: InterruptedException) {
            Log.e("asnkjbd", e.message.toString())

            e.printStackTrace()
        } finally {
            return request
        }
    }


    fun handelPlacesResult(response: PlacesSearchResponse) {

        try {

            Log.e("asnkjbd", response.toString())

        } catch (e: Exception) {

            Log.e("asnkjbd", e.message.toString())

        }
    }

    private fun getAddresses(sendMessage: Boolean = false) {

        collectFlow(mViewModel.getAddresses()) {

            handelApiResult(it,
                onResultSuccess = {

                    it.resultData?.data?.let { data ->

                        adapter.setAdapterData(newData = data)

                        if (sendMessage) {

                            lifecycleScope.launch {

                                delay(1500)

                                val resultIntent = Intent()

                                resultIntent.putExtra(
                                    Constants.ADDRESS_TEXT_ADDRESS,
                                    binding.tvLocationName.text.toString()
                                )

                                resultIntent.putExtra(
                                    Constants.ADDRESS_LAT_COORDINATOR,
                                    mCenterOfMap.latitude.toString()
                                )

                                resultIntent.putExtra(
                                    Constants.ADDRESS_LNG_COORDINATOR,
                                    mCenterOfMap.longitude.toString()
                                )

                                setResult(Activity.RESULT_OK, resultIntent)
                                finish()
                            }

                        }
                    }
                })
        }
    }

    private fun addAddress(data: Map<String, String>) {

        collectFlow(mViewModel.addAddress(data)) {

            handelApiResult(it,
                onResultSuccess = {

                    it.resultData?.data?.let { data ->

                        addAddressDialogBinding.edtAdAddressDialogAddress.setText("")
                        getAddresses(true)
                    }
                })
        }
    }

    private fun onAddressClick(item: AddressItem) {

        val resultIntent = Intent()

        resultIntent.putExtra(Constants.ADDRESS_TEXT_ADDRESS, item.address)

        resultIntent.putExtra(Constants.ADDRESS_LAT_COORDINATOR, item.lat)

        resultIntent.putExtra(Constants.ADDRESS_LNG_COORDINATOR, item.lng)

        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }
}