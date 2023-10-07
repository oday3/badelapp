package com.badl.apps.android.utils
import android.content.Intent
import android.graphics.Paint
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.badl.apps.android.utils.UiUtils.showView
import com.badl.apps.android.R
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

object AppUtil {
    
    fun <T> AppCompatActivity.collectFlow(flow: Flow<T>,
                                          state: Lifecycle.State = Lifecycle.State.CREATED,
                                          action: suspend (T) -> Unit): Job {

       return lifecycleScope.launch {

            repeatOnLifecycle(state) {

                flow.collect(action)
            }
        }
    }

    fun <T> Fragment.collectFlow(flow: Flow<T>,
                                          state: Lifecycle.State = Lifecycle.State.CREATED,
                                          action: suspend (T) -> Unit): Job {

     return lifecycleScope.launch {

            repeatOnLifecycle(state) {

                flow.collect(action)
            }
        }
    }


    fun AppCompatActivity.routWithSlideAnimation (intent: Intent) {

        startActivity(intent)
        overridePendingTransition(R.anim.slide_down_activity, R.anim.slide_up_activity)
    }

    fun checkDiscountPrice(hasDiscount: Int, price: Double?,
                           discountPrice: Double?, oldPriceTV: TextView,
                           currentPriceTV: TextView
    ) {

        if (hasDiscount == 1) {

            price?.let {

                oldPriceTV.showDiscountPrice(it)
            }

            discountPrice?.let {

                currentPriceTV.text = currentPriceTV.context.getString(R.string.holder_price, it.toString())

            }

        } else {

            oldPriceTV.showView(false)
            currentPriceTV.text = currentPriceTV.context.getString(R.string.holder_price, price.toString())
        }
    }

    fun TextView.showDiscountPrice(oldPrice: Double) {

        showView(true)

        text = context.getString(R.string.holder_price, oldPrice.toString())

        paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
    }

    fun <T> AppCompatActivity.getIntentExtension(destination: Class<T>): Intent {

       val intent = Intent(this, destination)
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)

        return intent
    }

    fun <T> Fragment.getIntentExtension(destination: Class<T>): Intent {

        val intent = Intent(requireActivity(), destination)
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)

        return intent
    }

    fun <T> AppCompatActivity.navToActivity(destination: Class<T>, showAnimation: Boolean = true) {

        val intent = Intent(this, destination)
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        startActivity(intent)

        if (!showAnimation) {

            overridePendingTransition(0,0)
        }
    }

    fun <T> Fragment.navToActivity(destination: Class<T>) {

        val intent = Intent(requireContext(), destination)
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)

        requireActivity().startActivity(intent)
    }


}