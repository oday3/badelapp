package com.badl.apps.android.appFeatures.biddingAndBarterSystem.ui

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.badl.apps.android.R
import com.badl.apps.android.adapters.ProductImagesAdapter
import com.badl.apps.android.adapters.ProductsToSwapAdapter
import com.badl.apps.android.appFeatures.addAd.ui.AddAdActivity
import com.badl.apps.android.appFeatures.chat.ui.ChatActivity
import com.badl.apps.android.appFeatures.main.ui.MainActivity
import com.badl.apps.android.appFeatures.userAccount.data.AdDetailsItem
import com.badl.apps.android.application.BadelApp
import com.badl.apps.android.baseClasses.ui.BaseActivity
import com.badl.apps.android.databinding.ActivityProductDetailsBinding
import com.badl.apps.android.databinding.BottomChrityOrderBinding
import com.badl.apps.android.databinding.BottomProductBidBinding
import com.badl.apps.android.databinding.BottomPurchaseProductBinding
import com.badl.apps.android.databinding.BottomSwapProductBinding
import com.badl.apps.android.databinding.DialogBiddingDoneBinding
import com.badl.apps.android.databinding.DialogOfferDoneBinding
import com.badl.apps.android.firebaseMessage.FCMMessagingService
import com.badl.apps.android.network.ApiConstants
import com.badl.apps.android.utils.AppUtil.collectFlow
import com.badl.apps.android.utils.AppUtil.getIntentExtension
import com.badl.apps.android.utils.AppUtil.navToActivity
import com.badl.apps.android.utils.Constants
import com.badl.apps.android.utils.NetworkUtils.handelApiResult
import com.badl.apps.android.utils.UiUtils
import com.badl.apps.android.utils.UiUtils.addIndicatorsTabs
import com.badl.apps.android.utils.UiUtils.extensionSetBackground
import com.badl.apps.android.utils.UiUtils.extensionSetShowListener
import com.badl.apps.android.utils.UiUtils.getScreenDensity
import com.badl.apps.android.utils.UiUtils.setImage
import com.badl.apps.android.utils.UiUtils.setTabMargins
import com.badl.apps.android.utils.UiUtils.showCustomToast
import com.badl.apps.android.utils.UiUtils.showView
import com.badl.apps.android.utils.ValidationUtils.isUserLogin
import com.badl.apps.android.utils.ValidationUtils.validateEmpty
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ProductDetailsActivity : BaseActivity() {

    private lateinit var binding: ActivityProductDetailsBinding
    private lateinit var productImagesAdapter: ProductImagesAdapter
    private lateinit var productsToSwapAdapter: ProductsToSwapAdapter

    private lateinit var purchaseBottomSheet: BottomSheetDialog
    private lateinit var purchaseBottomSheetBinding: BottomPurchaseProductBinding

    private lateinit var charityBottomSheet: BottomSheetDialog
    private lateinit var charityBottomSheetBinding: BottomChrityOrderBinding

    private lateinit var swapBottomSheet: BottomSheetDialog
    private lateinit var swapBottomSheetBinding: BottomSwapProductBinding

    private lateinit var biddingBottomSheet: BottomSheetDialog
    private lateinit var biddingBottomSheetBinding: BottomProductBidBinding

    private lateinit var bidDoneDialog: Dialog
    private lateinit var bidDoneDialogBinding: DialogBiddingDoneBinding

    private lateinit var offerDoneDialog: Dialog
    private lateinit var offerDoneDialogBinding: DialogOfferDoneBinding

    private val favData = HashMap<String, String>()
    private val orderData = HashMap<String, String>()

    private val mViewModel by viewModels<BiddingBarterViewModel>()

    private val adsParams = HashMap<String, String>()

    private var from = Constants.FROM_AD_DETAILS

    private lateinit var mAdDataItem: AdDetailsItem
    private var mQuantity = 1
    var isUserScrolling = false
    var currentPosition = 0
    private var itemId = -1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailsBinding.inflate(layoutInflater)
        purchaseBottomSheetBinding = BottomPurchaseProductBinding.inflate(layoutInflater)
        swapBottomSheetBinding = BottomSwapProductBinding.inflate(layoutInflater)
        biddingBottomSheetBinding = BottomProductBidBinding.inflate(layoutInflater)
        bidDoneDialogBinding = DialogBiddingDoneBinding.inflate(layoutInflater)
        offerDoneDialogBinding = DialogOfferDoneBinding.inflate(layoutInflater)
        charityBottomSheetBinding = BottomChrityOrderBinding.inflate(layoutInflater)

        setContentView(binding.root)


        if (intent.hasExtra(Constants.ITEM_ID)) {

            itemId = intent.getIntExtra(Constants.ITEM_ID, -1)

        } else {

            intent?.data?.let {

                if ((it.queryParameterNames?.size ?: 0) > 0) {

                    Log.e("sjbdal", "${it.queryParameterNames?.size?.toString()}")
                    it.getQueryParameter("id")?.toInt()?.let {

                        itemId = it
                    }
                }
            }
        }


        if (intent.hasExtra(Constants.FROM)) {

            from = intent.getStringExtra(Constants.FROM).toString()
        }

        initAdapters()
        initViews()
        initListeners()


        Log.e("afet", from)

        if (from == Constants.FROM_AD_DETAILS) {

            getAdDetails(adId = itemId.toString())

        } else if (from == Constants.FROM_ORDER_DETAILS) {

            getOrderDetails(orderId = itemId.toString())

        }

    }

    override fun initViews() {

        binding.recListOfProductImages.adapter = productImagesAdapter

        swapBottomSheetBinding.recListOfProducts.adapter = productsToSwapAdapter
        PagerSnapHelper().attachToRecyclerView(binding.recListOfProductImages)

        purchaseBottomSheet =
            UiUtils.createBottomSheetDialog(
                this,
                -1,
                purchaseBottomSheetBinding.root,
                true
            )

        charityBottomSheet =
            UiUtils.createBottomSheetDialog(
                this,
                -1,
                charityBottomSheetBinding.root,
                true
            )

        swapBottomSheet =
            UiUtils.createBottomSheetDialog(
                this,
                -1,
                swapBottomSheetBinding.root,
                true
            )

        swapBottomSheet.extensionSetShowListener(swapBottomSheetBinding.root)


        biddingBottomSheet =
            UiUtils.createBottomSheetDialog(
                this,
                -1,
                biddingBottomSheetBinding.root,
                true
            )


        binding.tvChatWithSeller.showView(isUserLogin())

    }

    override fun initData() {
        // nothing to do
    }

    override fun initAdapters() {

        productImagesAdapter = ProductImagesAdapter(this)
        productsToSwapAdapter = ProductsToSwapAdapter(this, false, -1)

    }

    override fun initObservers() {


        FCMMessagingService.actionNotifier.observe(this) {

            if (it[Constants.FROM] == Constants.NOTIFICATION_TYPE_ORDER) {

                getOrderDetails(orderId = itemId.toString())

            }
        }
    }

    override fun initListeners() {

        binding.consSellerInfo.setOnClickListener {

            getIntentExtension(SellerProfileActivity::class.java).run {
                putExtra(Constants.USER_ID, mAdDataItem.user_id?.toInt())

                startActivity(this)
            }
        }

        binding.recListOfProductImages.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                isUserScrolling = true
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                currentPosition =
                    (binding.recListOfProductImages.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                if (isUserScrolling) {

                    binding.tabLayoutProductDetails.getTabAt(currentPosition)?.select()
                }
            }
        })

        binding.tabLayoutProductDetails.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {

                Log.e("werty", tab?.position.toString())
                tab?.position?.let {

                    if (currentPosition != it) {

                        binding.recListOfProductImages.smoothScrollToPosition(it)
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // nothing to do
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // nothing to do
            }

        })

        binding.linearProductDetailsUpcomingBidding.setOnClickListener {

            getIntentExtension(BiddingHistoryActivity::class.java).run {

                putExtra(Constants.FROM, "")
                putExtra(Constants.AD_ID, itemId)
                startActivity(this)
            }
        }

        binding.tvBuy.setOnClickListener {

            purchaseBottomSheet.show()
        }

        purchaseBottomSheetBinding.btnSendOffer.setOnClickListener {

            purchaseBottomSheet.dismiss()
        }

        purchaseBottomSheetBinding.imgAddQuantity.setOnClickListener {


            purchaseBottomSheetBinding.tvProductDetailsQuantity.text = (++mQuantity).toString()
        }

        purchaseBottomSheetBinding.imgDeleteQuantity.setOnClickListener {

            if (mQuantity > 1) {

                purchaseBottomSheetBinding.tvProductDetailsQuantity.text = (--mQuantity).toString()

            }
        }

        binding.tvSwap.setOnClickListener {

            swapBottomSheet.show()
        }

        swapBottomSheetBinding.btnNewItem.setOnClickListener {

            navToActivity(AddAdActivity::class.java)
        }

        purchaseBottomSheetBinding.btnSendOffer.setOnClickListener {

            if (validateEmpty(purchaseBottomSheetBinding.tvinPurchasePrice)) {

                orderData.clear()
                orderData[ApiConstants.PAR_AD_ID] = itemId.toString()
                orderData[ApiConstants.PAR_PRICE] =
                    purchaseBottomSheetBinding.edtPurchasePrice.text.toString()

                orderData[ApiConstants.PAR_QUANTITY] = mQuantity.toString()

                order(orderData)

                purchaseBottomSheet.dismiss()
            }
        }

        binding.tvBidNow.setOnClickListener {

            biddingBottomSheet.show()
        }

        biddingBottomSheetBinding.btnBidNow.setOnClickListener {

            if (validateEmpty(biddingBottomSheetBinding.tvinBiddingValue)) {

                if (biddingBottomSheetBinding.edtBiddingValue.text.toString().toInt() > 0) {

                    orderData[ApiConstants.PAR_AD_ID] = itemId.toString()
                    orderData[ApiConstants.PAR_PRICE] =
                        biddingBottomSheetBinding.edtBiddingValue.text.toString()

                    order(orderData)

                    biddingBottomSheet.dismiss()
                }
            }
        }

        bidDoneDialogBinding.btnBiddingList.setOnClickListener {

            bidDoneDialog.dismiss()
            (applicationContext as BadelApp).navToBidding = true

            val resultIntent = Intent()
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }

        swapBottomSheetBinding.btnSendOffer.setOnClickListener {

            orderData.clear()

            if (productsToSwapAdapter.itemCount > 0) {

                if (productsToSwapAdapter.getSelectedItem() != null) {


                    orderData[ApiConstants.PAR_BARTER_ITEM_ID] =
                        productsToSwapAdapter.getSelectedItem()?.id.toString()

                    orderData[ApiConstants.PAR_AD_ID] = itemId.toString()

                    orderData[ApiConstants.PAR_PRICE] =
                        swapBottomSheetBinding.edtAdditionalPrice.text.toString()

                    order(orderData)

                    swapBottomSheet.dismiss()

                } else {

                    showCustomToast(
                        getString(R.string.please_select_item_to_swap),
                        Constants.TOAST_WARNING
                    )
                }

            } else {

                showCustomToast(
                    getString(R.string.there_is_no_products_to_swap_please_add_one),
                    Constants.TOAST_WARNING
                )
            }
        }

        binding.imgBack.setOnClickListener {

            finish()
        }

        binding.imgFavProduct.setOnClickListener {

            if (isUserLogin(true)) {

                favData[ApiConstants.PAR_AD_ID] = itemId.toString()
                favProduct(favData)
            }
        }

        binding.tvOrderProduct.setOnClickListener {

            charityBottomSheet.show()
        }

        charityBottomSheetBinding.imgAddQuantity.setOnClickListener {


            charityBottomSheetBinding.tvProductDetailsQuantity.text = (++mQuantity).toString()
        }

        charityBottomSheetBinding.imgDeleteQuantity.setOnClickListener {

            if (mQuantity > 1) {

                charityBottomSheetBinding.tvProductDetailsQuantity.text = (--mQuantity).toString()

            }
        }

        charityBottomSheetBinding.btnSendOffer.setOnClickListener {

            orderData.clear()
            orderData[ApiConstants.PAR_AD_ID] = itemId.toString()
            orderData[ApiConstants.PAR_QUANTITY] = mQuantity.toString()
            order(orderData)

            charityBottomSheet.dismiss()
        }

        binding.tvChatWithSeller.setOnClickListener {

            getIntentExtension(ChatActivity::class.java).run {

                putExtra(Constants.AD_ID, mAdDataItem.id)
                putExtra(Constants.OWNER_ID, mAdDataItem.user_id?.toInt())
                putExtra(Constants.OWNER_IMAGE, mAdDataItem.user_details?.image)
                putExtra(Constants.FROM, Constants.FROM_AD_DETAILS)
                startActivity(this)
            }
        }

        binding.imgShareProduct.setOnClickListener {

            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(
                    Intent.EXTRA_TEXT, """${mAdDataItem.title} 
                        | https://badel.marvel.com.sa/ads?id=${mAdDataItem.id}
                """.trimMargin()
                )
                type = "text/plain"
            }

            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }
    }


    private fun getAdDetails(adId: String) {

        collectFlow(mViewModel.getAdDetails(adId)) {

            handelApiResult(it,
                onResultSuccess = {

                    it.resultData?.data?.let { adData ->

                        setUiData(adData)
                    }
                })
        }
    }

    private fun getOrderDetails(orderId: String) {

        collectFlow(mViewModel.getOrderDetails(orderId)) {

            handelApiResult(it,
                onResultSuccess = {

                    it.resultData?.data?.let { adData ->

                        setUiData(adData)
                    }
                })
        }
    }


    private fun favProduct(
        favData: HashMap<String, String>
    ) {

        collectFlow(mViewModel.favoriteAd(favData)) {

            handelApiResult(it, onLoading = {},
                onResultSuccess = {

                    it.resultData?.data?.let {

//                        adapter.getItem(itemPosition).is_favorite = !(adapter.getItem(itemPosition).is_favorite ?: false)
//                        adapter.getItem(itemPosition).is_loading = false
//                        adapter.refreshItem(itemPosition)

                        if (from == Constants.FROM_ORDER_DETAILS) {

                            getOrderDetails(itemId.toString())

                        } else if (from == Constants.FROM_AD_DETAILS) {

                            getAdDetails(itemId.toString())
                        }
                    }

                }, onResultFail = {
//                    adapter.getItem(itemPosition).is_loading = false
//                    adapter.refreshItem(itemPosition)
                })
        }
    }


    private fun getAds(params: HashMap<String, String>) {

        collectFlow(mViewModel.getAds(params)) {

            handelApiResult(it, onLoading = {},
                onResultSuccess = {

                    it.resultData?.data?.let { adsData ->

                        productsToSwapAdapter.setData(adsData)
                    }

                })
        }
    }


    private fun order(data: HashMap<String, String>) {

        collectFlow(mViewModel.order(data)) {

            handelApiResult(it,
                onResultSuccess = {

                    it.resultData?.data?.let {


                        when (mAdDataItem.ad_type_id) {

                            1 -> {


                                bidDoneDialog.show()


                            }

                            2 -> {


                                offerDoneDialog.show()
                                lifecycleScope.launch {
                                    delay(1500)
                                    offerDoneDialog.dismiss()
                                }
                            }

                            3 -> {

                                offerDoneDialog.show()
                                lifecycleScope.launch {
                                    delay(1500)
                                    offerDoneDialog.dismiss()
                                }

                            }

                            4 -> {

                                offerDoneDialog.show()
                                lifecycleScope.launch {
                                    delay(1500)
                                    offerDoneDialog.dismiss()
                                }
                            }
                        }
                    }
                })
        }
    }

    private fun createOfferDialog(userName: String) {


        offerDoneDialogBinding.tvDescription.text =
            getString(R.string.holder_offer_sentـsuccessfully, userName)
        offerDoneDialog =
            UiUtils.createDialog(
                this,
                themeResId = R.style.TransparentAlertDialog,
                windowAnimationResId = R.style.SlideDialogAnimation,
                true, offerDoneDialogBinding.root
            )

        offerDoneDialog.extensionSetBackground(
            (getScreenDensity() * 14).toInt(),
            AppCompatResources.getDrawable(this, R.drawable.bg_dialog_),
        )
    }

    private fun createBidDialog() {

        bidDoneDialog =
            UiUtils.createDialog(
                this,
                themeResId = R.style.TransparentAlertDialog,
                windowAnimationResId = R.style.SlideDialogAnimation,
                true, bidDoneDialogBinding.root
            )

        bidDoneDialog.extensionSetBackground(
            (getScreenDensity() * 14).toInt(),
            AppCompatResources.getDrawable(this, R.drawable.bg_dialog_),
        )
    }

    private fun setUiData(adData: AdDetailsItem) {


        mAdDataItem = adData
        adData.images?.let {

            productImagesAdapter.setData(it)


            if (adData.is_favorite == true) {

                binding.imgFavProduct.setImageResource(R.drawable.fav_seq)

            } else {

                binding.imgFavProduct.setImageResource(R.drawable.ic_fav_white)

            }

            binding.tabLayoutProductDetails.addIndicatorsTabs(productImagesAdapter.getData().size)

            binding.tabLayoutProductDetails.setTabMargins(
                density = getScreenDensity(),
                endMargin = 0
            )
        }

        binding.tvProductName.text = adData.title
        binding.tvUserRate.text = getString(
            R.string.holder_ad_ratings,
            adData.user_details?.rate.toString(), adData.user_details?.rates_count.toString()
        )

        binding.tvSectionName.text = adData.section_title
        binding.tvSubSectionName.text = adData.subsection_title
        binding.tvStatus.text = adData.product_status_title
        binding.tvEndDate.text = adData.end_date
        binding.tvLocationName.text = adData.location
        binding.tvReleaseDate.text = adData.release_date
        binding.tvDescription.text = adData.description

        binding.imgSellerImg.setImage(adData.user_details?.image)
        binding.tvSellerName.text = adData.user_details?.name
        binding.tvSellerNameDetail.text = adData.user_details?.name
        binding.tvMemberSince.text = adData.user_details?.member_since
        binding.tvSellerRate.text = adData.user_details?.rate.toString()
        binding.tvAuthSeller.showView(adData.user_details?.is_trusted == 1)
        binding.tvAdTypeLabel.text = adData.ad_type_text


        if (from == Constants.FROM_AD_DETAILS) {

            binding.consUserActions.showView(true)
            binding.tvOrderStatus.showView(false)

            when (adData.ad_type_id) {

                1 -> {

                    createBidDialog()
                    binding.tvBidNow.showView(true)
                    binding.linearProductDetailsUpcomingBidding.showView(true)
                    binding.tvSwap.showView(false)
                    binding.tvBuy.showView(false)
                    binding.tvOrderProduct.showView(false)

                    binding.tvMostBiddingValue.text = adData.highest_bid.toString()
                    binding.tvBiddingCount.text = getString(
                        R.string.holder_bidding_count,
                        adData.bids_count.toString()
                    )


                    biddingBottomSheetBinding.imgSellerImg.setImage(adData.user_details?.image)
                    biddingBottomSheetBinding.tvSellerNameDetail.text = adData.user_details?.name

                    biddingBottomSheetBinding.tvSellerRate.text = getString(
                        R.string.holder_ad_ratings,
                        adData.user_details?.rates_count.toString(),
                        adData.user_details?.rate.toString()
                    )

                    biddingBottomSheetBinding.tvBiddingProductName.text = adData.title
                    biddingBottomSheetBinding.tvBidProductSection.text = adData.section_title

                }

                2 -> {


                    createOfferDialog(mAdDataItem.user_details?.name.toString())

                    binding.tvBidNow.showView(false)
                    binding.linearProductDetailsUpcomingBidding.showView(false)
                    binding.tvSwap.showView(true)
                    binding.tvBuy.showView(false)
                    binding.tvOrderProduct.showView(false)

                    swapBottomSheetBinding.imgSellerImg.setImage(adData.user_details?.image)
                    swapBottomSheetBinding.tvSellerNameDetail.text = adData.user_details?.name

                    swapBottomSheetBinding.tvSellerRate.text = getString(
                        R.string.holder_ad_ratings,
                        adData.user_details?.rates_count.toString(),
                        adData.user_details?.rate.toString()
                    )

                    swapBottomSheetBinding.tvSwapProductName.text = adData.title
                    swapBottomSheetBinding.tvSwapProductSection.text = adData.section_title

                    adsParams[Constants.FILTER_TYPE] = 2.toString()

                    if (isUserLogin()) {

                        getAds(adsParams)
                    }

                }

                3 -> {


                    createOfferDialog(mAdDataItem.user_details?.name.toString())
                    binding.tvBidNow.showView(false)
                    binding.linearProductDetailsUpcomingBidding.showView(false)
                    binding.tvSwap.showView(false)
                    binding.tvBuy.showView(true)
                    binding.tvOrderProduct.showView(false)

                }

                4 -> {


                    binding.tvBidNow.showView(false)
                    binding.linearProductDetailsUpcomingBidding.showView(false)
                    binding.tvSwap.showView(false)
                    binding.tvBuy.showView(false)
                    binding.tvOrderProduct.showView(true)
                    createOfferDialog(mAdDataItem.user_details?.name.toString())

                }
            }

            binding.consUserActions.showView(isUserLogin())
        } else if (from == Constants.FROM_ORDER_DETAILS) {

            binding.consUserActions.showView(false)
            binding.tvOrderStatus.showView(true)

            binding.tvOrderStatus.text = mAdDataItem.order_status_text

            when (mAdDataItem.order_status) {

                0 -> {

                    binding.tvOrderStatus.setTextColor(getColor(R.color.orange))
                    binding.tvOrderStatus.setBackgroundResource(R.drawable.bg_pendding_order)
                }

                1 -> {

                    binding.tvOrderStatus.setTextColor(getColor(R.color.green))
                    binding.tvOrderStatus.setBackgroundResource(R.drawable.bg_accepted_order)

                }

                2 -> {

                    binding.tvOrderStatus.setTextColor(getColor(R.color.red))
                    binding.tvOrderStatus.setBackgroundResource(R.drawable.bg_reject_order)

                }

            }
        }
    }

    companion object {

        var isActivityActive = is_activity_active

    }
}