package com.badl.apps.android.utils

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Insets
import android.graphics.Point
import android.graphics.drawable.Drawable
import android.graphics.drawable.InsetDrawable
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.Nullable
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayout
import com.badl.apps.android.R
import com.badl.apps.android.appFeatures.appCommon.ui.NotificationsActivity
import com.badl.apps.android.appFeatures.cart.ui.CartActivity
import com.badl.apps.android.databinding.CustomToastBinding
import com.badl.apps.android.utils.AppUtil.navToActivity
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.onStart


object UiUtils {

    private const val DRAWABLE_LEFT_INDEX = 0
    private const val DRAWABLE_TOP_INDEX = 1
    private const val DRAWABLE_RIGHT_INDEX = 2
    private const val DRAWABLE_BOTTOM_INDEX = 3

    fun TabLayout.addIndicatorsTabs(numOfTabs: Int) {

        this.removeAllTabs()
        for (i in 0 until numOfTabs) {

            addTab(newTab().setText(""))
        }
    }

    fun TabLayout.setTabMargins(
        topMargin: Int = 0, startMargin: Int = 0,
        endMargin: Int = 0, bottomMargin: Int = 0, density: Float
    ) {

        for (i in 0 until tabCount - 1) {

            val tab = (getChildAt(0) as ViewGroup).getChildAt(i)
            val p = tab.layoutParams as ViewGroup.MarginLayoutParams


            p.marginEnd = (endMargin * density).toInt()
            p.marginStart = (startMargin * density).toInt()
            p.topMargin = (topMargin * density).toInt()
            p.bottomMargin = (bottomMargin * density).toInt()
            tab.requestLayout()
        }
    }


    fun EditText.textChanges(): Flow<CharSequence?> {
        return callbackFlow<CharSequence?> {
            val listener = object : TextWatcher {
                override fun afterTextChanged(s: Editable?) = Unit
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) = Unit

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    trySend(s)
                }
            }
            addTextChangedListener(listener)
            awaitClose { removeTextChangedListener(listener) }
        }.onStart { emit(text) }
    }

    fun Activity.setTransparentStatusBar(
        isLightStatusBar: Boolean = true,
        statusBarColor: Int = Color.TRANSPARENT,
        navigationBarColor: Int = Color.TRANSPARENT
    ) {

        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars =
            isLightStatusBar
        window.statusBarColor = statusBarColor
        //window.navigationBarColor = navigationBarColor

    }


    fun BottomSheetDialog.extensionSetShowListener(dialogView: View) {

        setOnShowListener {
            dialogView.let { sheet ->

                this.behavior.state
                // this.behavior.peekHeight = sheet.height
                this.behavior.peekHeight = Resources.getSystem().displayMetrics.heightPixels
                sheet.parent.parent.requestLayout()
            }
        }
    }


    fun createBottomSheetDialog(
        context: Context,
        themeResId: Int,
        contentView: View,
        isCancelable: Boolean
    ): BottomSheetDialog {


        val bottomSheetDialog = if (themeResId != -1) {

            BottomSheetDialog(context, themeResId)

        } else {

            BottomSheetDialog(context, R.style.AppBottomSheetDialogTheme)

        }

        bottomSheetDialog.setContentView(contentView)
        bottomSheetDialog.setCancelable(isCancelable)

        return bottomSheetDialog
    }


    fun BottomSheetDialog.expandFullDialog(view: View) {

        setOnShowListener {
            val dialog = it as BottomSheetDialog

            view.let { sheet ->
                dialog.behavior.peekHeight = sheet.height
                sheet.parent.parent.requestLayout()
            }
        }

    }

    fun AppCompatActivity.showMessage(message: String?) {

        runOnUiThread { Toast.makeText(this, message, Toast.LENGTH_LONG).show() }
    }

    fun androidx.fragment.app.Fragment.showMessage(message: String?) {

        Toast.makeText(requireActivity(), message, Toast.LENGTH_LONG).show()
    }

    fun Dialog.extensionSetBackground(
        inset: Int,
        background: Drawable?,

        ) {

        val insetConfirm = InsetDrawable(background, inset)
        this.window?.setBackgroundDrawable(insetConfirm)
        this.window?.attributes?.width = ActionBar.LayoutParams.MATCH_PARENT
        // this.window?.attributes?.windowAnimations = windowAnimationResId
        //  this.window?.setWindowAnimations(windowAnimationResId)
    }


    fun createDialog(context: Context, isCancelable: Boolean, contentView: View): Dialog {

        val dialog = Dialog(context)
        dialog.setCancelable(isCancelable)
        dialog.setContentView(contentView)

        return dialog
    }

    fun createDialog(
        context: Context,
        themeResId: Int,
        windowAnimationResId: Int,
        isCancelable: Boolean, contentView: View
    ): Dialog {

        val dialog = if (themeResId == 0) {
            Dialog(context)
        } else {
            Dialog(context, themeResId)
        }

        dialog.setCancelable(isCancelable)
        dialog.setContentView(contentView)
        dialog.window?.attributes?.windowAnimations = windowAnimationResId


        return dialog
    }


    fun View.showKeyboard(inputMethodManager: InputMethodManager) {
        this.requestFocus()
        inputMethodManager.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT);

    }


    fun View.hideKeyboard(inputMethodManager: InputMethodManager) {
        inputMethodManager.hideSoftInputFromWindow(this.windowToken, 0);
    }


//    fun AppCompatActivity.initializeAuthToolBar(
//        title: String? = "", backAction: (() -> Unit)? = null,
//        toolBarBackground: Int = Color.TRANSPARENT
//    ) {
//
//        val toolbar = findViewById<Toolbar>(R.id.toolBar_auth)
//
//        //(toolbar.parent as AppBarLayout).setBackgroundResource(getColor(toolBarBackground))
//        // toolbar.minimumHeight =  toolbar.background.minimumHeight
//        //toolbar.minimumHeight = toolbar.height
//
//        val barTitle = toolbar.findViewById<TextView>(R.id.tv_authToolbar_title)
//        val backArrow = toolbar.findViewById<ImageView>(R.id.img_authToolbar_back)
//
//        backArrow.setOnClickListener {
//            if (backAction != null) {
//                backAction() ?: Unit
//            }
//        }
//
//        barTitle.text = title
//        setSupportActionBar(toolbar)
////        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_toolbar_back_white_rounded)
////        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//
//    }


    @SuppressLint("SuspiciousIndentation")
    fun AppCompatActivity.initializeMainToolBar(
        title: String? = "", backAction: (() -> Unit) = { finish() }, notificationCount: Int = 0,
        toolBarBackground: Int = Color.TRANSPARENT, showNotificationIcon:Boolean = false,
        showCartIcon:Boolean = false,
        showChatIcon:Boolean = false,
        chatAction: (() -> Unit)  = { }) {

         val toolbar = findViewById<Toolbar>(R.id.toolBar_main)

        toolbar.setPadding(0,(15 * getScreenDensity()).toInt(),0,(15 * getScreenDensity()).toInt())
//        (toolbar.parent as AppBarLayout).setBackgroundResource(getColor(toolBarBackground))
//         toolbar.minimumHeight =  toolbar.background.minimumHeight
//        toolbar.minimumHeight = toolbar.height

         val barTitle = toolbar.findViewById<TextView>(R.id.tv_mainToolbar_title)
         val backArrow = toolbar.findViewById<ImageView>(R.id.img_mainToolbar_back)
         val notificationIcon = toolbar.findViewById<ImageView>(R.id.img_mainToolBar_notification)
         val cartIcon = toolbar.findViewById<ImageView>(R.id.img_mainToolBar_cart)
         val chatIcon = toolbar.findViewById<ImageView>(R.id.img_mainToolBar_chats)

        notificationIcon.showView(showNotificationIcon)
        cartIcon.showView(showCartIcon)
        chatIcon.showView(showChatIcon)

        barTitle.text = title

         backArrow.setOnClickListener {

             backAction()
         }

        notificationIcon.setOnClickListener {

            navToActivity(NotificationsActivity::class.java)
         }

        cartIcon.setOnClickListener {

            navToActivity(CartActivity::class.java)
        }

        chatIcon.setOnClickListener {

           chatAction()
        }

    }

    // barTitle.text = title
    // setSupportActionBar(toolbar)
//        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_toolbar_back_white_rounded)
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)



//
//    fun AppCompatActivity.initializeSecondToolBar(
//        title: String? = "", showImage: Boolean = false,
//        toolBarBackground: Int = Color.TRANSPARENT
//    ) {
//
//        val toolbar = findViewById<Toolbar>(R.id.toolBar_baseSecond)
//
//        //(toolbar.parent as AppBarLayout).setBackgroundResource(getColor(toolBarBackground))
//        // toolbar.minimumHeight =  toolbar.background.minimumHeight
//        //toolbar.minimumHeight = toolbar.height
//
//        val barTitle = toolbar.findViewById<TextView>(R.id.tv_baseSecondToolBar_title)
//        val barImg = toolbar.findViewById<ShapeableImageView>(R.id.img_baseSecondToolBar_img)
//        val backArrow = toolbar.findViewById<ImageView>(R.id.img_baseSecondToolBar_back)
//
//        barImg.showView(showImage)
//
//        backArrow.setOnClickListener {
//            onBackPressed()
//        }
//
//        barTitle.text = title
//        setSupportActionBar(toolbar)
////        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_toolbar_back_white_rounded)
////        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//
//    }


    fun AppCompatActivity.getScreenDensity(): Float {

        return resources.displayMetrics.density
    }

    fun View.getScreenDensity(): Float {

        return resources.displayMetrics.density
    }

    fun Fragment.getScreenDensity(): Float {

        return resources.displayMetrics.density
    }

    fun AppCompatActivity.getInputMethodManager(): InputMethodManager {

        return getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }

    fun setViewBottomInset(view: View, margin: Int = 24) {

        ViewCompat.setOnApplyWindowInsetsListener(view) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.navigationBars())
            // Apply the insets as a margin to the view. Here the system is setting
            // only the bottom, left, and right dimensions, but apply whichever insets are
            // appropriate to your layout. You can also update the view padding
            // if that's more appropriate.


            Log.e("nav_bar_inset", insets.bottom.toString())
//            view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
//                bottomMargin =
//                    insets.bottom + ((view.context as AppCompatActivity).getScreenDensity() * margin).toInt()
//            }


            view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                updateMargins(insets.left, insets.top, insets.right, insets.bottom + ((view.context as AppCompatActivity).getScreenDensity() * margin).toInt())
            }
            // Return CONSUMED if you don't want want the window insets to keep being
            // passed down to descendant views.
            WindowInsetsCompat.CONSUMED
        }
    }

    fun ImageView.setImage(imageUrl: String?) {

        Glide.with(context).load(imageUrl).into(this)
    }

    fun TextView.setPrice(price: Any?) {

        text = context.getString(R.string.holder_price, price.toString())
    }

    fun View.showView(showView: Boolean) {

        visibility = if (showView) {

            View.VISIBLE

        } else {

            View.GONE
        }

    }


    fun EditText.addSearchListener(actionToDo: (editText: EditText) -> Unit) {


        setOnEditorActionListener(object :
            TextView.OnEditorActionListener {

            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {


                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    actionToDo(this@addSearchListener)

                    return true
                }

                return false
            }
        })
    }

    fun EditText.isTextEmpty(): Boolean {

        return text.isEmpty()
    }


    fun View.setViewAnimation(animationResId: Int) {


        startAnimation(AnimationUtils.loadAnimation(context, animationResId));

    }
//    fun View.setScaleAnimation() {
//
//
//        startAnimation(AnimationUtils.loadAnimation(context, R.anim.item_animation_fall_down));
//
////        val anim = ScaleAnimation(
////            0.0f,
////            1.0f,
////            0.0f,
////            1.0f,
////            Animation.RELATIVE_TO_SELF,
////            0.0f,
////            Animation.RELATIVE_TO_SELF,
////            0.0f
////        )
////        anim.duration = 700
////        this.startAnimation(anim)
//    }


    fun View.setRAnimation() {

        val anim = ValueAnimator.ofFloat(0.5f, 1f)
        anim.duration = 1000
        anim.addUpdateListener { animation ->
            this.scaleX = animation.animatedValue as Float
            this.scaleY = animation.animatedValue as Float
        }
        anim.repeatCount = Animation.INFINITE
        anim.repeatMode = ValueAnimator.REVERSE
        anim.start()
    }


    fun ImageView.loadBitmapWithGlide(
        imageUrl: String, onLoadStarted: () -> Unit,
        onResourceReady: (bitmap: Bitmap) -> Unit, onLoadCleared: () -> Unit
    ) {


        Glide.with(context)
            .asBitmap()
            .load(imageUrl)
            .into(object : CustomTarget<Bitmap?>() {

                override fun onLoadStarted(placeholder: Drawable?) {
                    super.onLoadStarted(placeholder)

                    onLoadStarted()
                }

                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap?>?
                ) {

                    onResourceReady(resource)

                }

                override fun onLoadCleared(@Nullable placeholder: Drawable?) {

                    onLoadCleared()
                }
            })
    }


    fun AppCompatActivity.addOnBackPressedDispatcher(onBackPressed: () -> Unit = { }) {
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    onBackPressed.invoke()
                }
            }
        )
    }

    fun View.setCompactBottomInset() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

            setViewBottomInset(this, margin = 24)

        } else {

            this.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                bottomMargin = getNavigationBarHeight(resources) + (getScreenDensity() * 24).toInt()
            }
        }
    }

    fun AppCompatActivity.showCustomToast(message: String?, type: Int) {


        val toast = Toast(this)
        toast.setGravity(Gravity.TOP or Gravity.FILL_HORIZONTAL, 0, 50)

        toast.setMargin(20 * getScreenDensity(), 0f)

        val customToastLayout = CustomToastBinding.inflate(layoutInflater)


        when (type) {

            Constants.TOAST_ERROR -> {

                customToastLayout.toastCardView.setCardBackgroundColor(getColor(R.color.white))
                customToastLayout.animationView.setImageResource(R.drawable.ic_error_circle_fill)
                customToastLayout.tvMessage.text = message
            }


            Constants.TOAST_DONE -> {

                customToastLayout.toastCardView.setCardBackgroundColor(getColor(R.color.white))
                customToastLayout.animationView.setImageResource(R.drawable.ic_done_circle_fill)
                customToastLayout.tvMessage.text = message
            }


            Constants.TOAST_WARNING -> {

                customToastLayout.toastCardView.setCardBackgroundColor(getColor(R.color.white))
                customToastLayout.animationView.setImageResource(R.drawable.ic_warning_circle_fill)
                customToastLayout.tvMessage.text = message
            }


            Constants.TOAST_INFO -> {

                customToastLayout.toastCardView.setCardBackgroundColor(getColor(R.color.white))
                customToastLayout.animationView.setImageResource(R.drawable.ic_info_circle_fill)
                customToastLayout.tvMessage.text = message
            }
        }


        toast.duration = Toast.LENGTH_LONG
        toast.view = customToastLayout.root
        toast.show();
    }

    fun Fragment.showCustomToast(message: String?, type: Int) {


        val toast = Toast(requireActivity())
        toast.setGravity(Gravity.TOP or Gravity.FILL_HORIZONTAL, 0, 50)

        toast.setMargin(20 * getScreenDensity(), 0f)

        val customToastLayout = CustomToastBinding.inflate(layoutInflater)


        when (type) {

            Constants.TOAST_ERROR -> {

                customToastLayout.toastCardView.setCardBackgroundColor(requireContext().getColor(R.color.white))
                customToastLayout.animationView.setImageResource(R.drawable.ic_error_circle_fill)
                customToastLayout.tvMessage.text = message
            }


            Constants.TOAST_DONE -> {

                customToastLayout.toastCardView.setCardBackgroundColor(requireContext().getColor(R.color.white))
                customToastLayout.animationView.setImageResource(R.drawable.ic_done_circle_fill)
                customToastLayout.tvMessage.text = message
            }


            Constants.TOAST_WARNING -> {

                customToastLayout.toastCardView.setCardBackgroundColor(requireContext().getColor(R.color.white))
                customToastLayout.animationView.setImageResource(R.drawable.ic_warning_circle_fill)
                customToastLayout.tvMessage.text = message
            }


            Constants.TOAST_INFO -> {

                customToastLayout.toastCardView.setCardBackgroundColor(requireContext().getColor(R.color.white))
                customToastLayout.animationView.setImageResource(R.drawable.ic_info_circle_fill)
                customToastLayout.tvMessage.text = message
            }
        }


        toast.duration = Toast.LENGTH_LONG
        toast.view = customToastLayout.root
        toast.show();
    }

    fun View.addAnimation(animationResId: Int) {

        startAnimation(AnimationUtils.loadAnimation(context, animationResId))
    }


    // Conversion from dp to px
    fun Int.toPx(): Int {
        val metrics = Resources.getSystem().displayMetrics
        val px = this * (metrics.densityDpi / 160f)
        return Math.round(px)
    }

    // Get screen dimension
    fun Context.screenDimension(): Point {
        val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager // Either getting WindowManager from Activity or Application context
        val display = windowManager.getDefaultDisplay()
        val size = Point()
        display.getSize(size)
        return size
    }
    fun Context.screenWidth(): Int = screenDimension().x
    fun Context.screenHeight(): Int = screenDimension().y


    fun AppCompatActivity.getScreenWidthInPx(): Int {

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics: WindowMetrics = windowManager.currentWindowMetrics
            val insets: Insets = windowMetrics.windowInsets
                .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
            windowMetrics.bounds.width() - insets.left - insets.right
        } else {
            val displayMetrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(displayMetrics)
            displayMetrics.widthPixels
        }
    }

    fun getNavigationBarHeight(resources: Resources): Int {
        val resourceId: Int = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        return if (resourceId > 0) {
            resources.getDimensionPixelSize(resourceId)
        } else 0
    }

     fun AppCompatActivity.clearErrors(vararg textViews: TextInputLayout) {

        for (i in 0..textViews.size - 1) {

            textViews[i].error = null
        }
    }

    fun Fragment.refreshData(action: () -> Unit) {

        action()
    }


    fun RecyclerView.setFlowLayoutManager() {

        val layoutManager = FlexboxLayoutManager(context)
        layoutManager.flexDirection = FlexDirection.ROW
        layoutManager.justifyContent = JustifyContent.CENTER
        this.layoutManager = layoutManager
    }


    fun AppCompatActivity.createActivityResultLauncher (action : (data: ActivityResult) -> Unit): ActivityResultLauncher<Intent> {

        return registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {

            action(it)
        }
    }
}