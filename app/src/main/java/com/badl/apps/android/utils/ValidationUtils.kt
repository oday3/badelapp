package com.badl.apps.android.utils

import android.content.pm.PackageManager
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.badl.apps.android.R
import com.badl.apps.android.baseClasses.ui.BaseActivity
import com.badl.apps.android.utils.UiUtils.showCustomToast
import com.google.android.material.textfield.TextInputLayout

object ValidationUtils {

     fun AppCompatActivity.validateEmpty(msg: String, vararg textViews: TextView, errorType: Int = 0): Boolean {
        for (textView in textViews) {
            if (TextUtils.isEmpty(textView.text)) {
                showCustomToast(message = msg, errorType)
                textView.requestFocus()
                return false
            }
        }
        return true
    }

    fun AppCompatActivity.validateEmpty(vararg textViews: TextView): Boolean {

        for (i in 0..textViews.size - 1) {
            if (TextUtils.isEmpty(textViews[i].text)) {
                textViews[i].error = getString(R.string.this_field_required)
                textViews[i].requestFocus()
                return false
            }
        }
        return true
    }

    fun AppCompatActivity.validateEmpty(vararg textViews: TextInputLayout): Boolean {

        for (i in 0..textViews.size - 1) {
            if (TextUtils.isEmpty(textViews[i].editText?.text.toString())) {
                textViews[i].error = getString(R.string.this_field_required)
                textViews[i].requestFocus()
                return false
            }
        }
        return true
    }

    fun Fragment.validateEmpty(vararg textViews: TextInputLayout): Boolean {

        for (i in 0..textViews.size - 1) {
            if (TextUtils.isEmpty(textViews[i].editText?.text.toString())) {
                textViews[i].error = getString(R.string.this_field_required)
                textViews[i].requestFocus()
                return false
            }
        }
        return true
    }

    fun Fragment.validateEmpty(vararg textViews: TextView): Boolean {

        for (i in 0..textViews.size - 1) {
            if (TextUtils.isEmpty(textViews[i].text.toString())) {
                textViews[i].error = getString(R.string.this_field_required)
                textViews[i].requestFocus()
                return false
            }
        }
        return true
    }


    fun AppCompatActivity.validateEmpty(messages: List<String>, errorType: Int = 0, vararg textViews: TextView): Boolean {

        for (i in 0..textViews.size - 1) {
            if (TextUtils.isEmpty(textViews[i].text)) {

               showCustomToast(message = messages[i], errorType)
                textViews[i].requestFocus()
                return false
            }
        }
        return true
    }

    fun AppCompatActivity.validatePassword(vararg textViews: TextView, errorType: Int = 0): Boolean {

        for (i in 0..textViews.size - 1) {
            if (textViews[i].text.length < 8) {
                showCustomToast(message = getString(R.string.password_must_contain_at_least_8_digits), errorType)
                textViews[i].requestFocus()
                return false
            }
        }
        return true
    }

    fun AppCompatActivity.validatePassword(vararg textViews: TextInputLayout, errorType: Int = 0): Boolean {

        for (i in 0..textViews.size - 1) {
            textViews[i].error = null
            if (textViews[i].editText?.text.toString().length < 6) {
                showCustomToast(message = getString(R.string.password_must_contain_at_least_8_digits), errorType)
                textViews[i].error = getString(R.string.password_must_contain_at_least_8_digits)
                textViews[i].requestFocus()
                return false
            }
        }
        return true
    }


    fun AppCompatActivity.validateEmails(vararg textViews: TextView): Boolean {
        for (textView in textViews) {
            if (!Patterns.EMAIL_ADDRESS.matcher(textView.text.toString()).matches()) {
                textView.error = getString(R.string.enter_valid_email)
                textView.requestFocus()
                return false
            }
        }
        return true
    }

    fun AppCompatActivity.validateEmails(vararg textViews: TextInputLayout): Boolean {
        for (textView in textViews) {
            if (!Patterns.EMAIL_ADDRESS.matcher(textView.editText?.text.toString()).matches()) {
                textView.error = getString(R.string.enter_valid_email)
                textView.requestFocus()
                return false
            }
        }
        return true
    }

    fun AppCompatActivity.validatePasswordMatches(password: TextView, confirmedPassword: TextView, errorType: Int = 0): Boolean {
        if (password.text.toString() != confirmedPassword.text.toString()) {
            (confirmedPassword.context as AppCompatActivity).showCustomToast(message = getString(R.string.password_dont_match), errorType)

            confirmedPassword.requestFocus()
            return false
        }
        return true
    }

    fun AppCompatActivity.validatePasswordMatches(password: TextInputLayout, confirmedPassword: TextInputLayout, errorType: Int = 0): Boolean {
        if (password.editText?.text.toString() != confirmedPassword.editText?.text.toString()) {
            showCustomToast(message = getString(R.string.password_dont_match), errorType)

            confirmedPassword.requestFocus()
            return false
        }
        return true
    }

    fun AppCompatActivity.validateMobileLength(vararg textViews: TextView): Boolean {

        for (textView in textViews) {
            if (textView.length() in 16 downTo 8) {
                textView.error = getString(R.string.enter_valid_mobile)
                textView.requestFocus()
                return false
            }
        }
        return true
    }

    fun AppCompatActivity.validateMobileIntro(vararg textViews: TextView, errorType: Int = 0): Boolean {

        for (textView in textViews) {

            Log.e("mob_intro", textView.text.take(2).toString())

            //textView.text.take(2).toString() != "05" &&

            if(textView.text.take(1).toString() != "5") {

                showCustomToast(message = getString(R.string.enter_valid_mobile_intro), errorType)
                textView.requestFocus()
                return false

            }

            if(textView.length()  != 9) {

//                if (textView.length()  == 9 && textView.text.take(1).toString() != "0") {
//
//
//                } else {
//
//
//                }

                showCustomToast(message = getString(R.string.enter_valid_mobile_length), errorType)
                textView.requestFocus()
                return false
            }

        }
        return true
    }

    fun AppCompatActivity.validateMobileIntro(vararg textViews: TextInputLayout, errorType: Int = 0): Boolean {

        for (textView in textViews) {


            if(textView.editText?.text.toString().take(1).toString() != "5") {

                showCustomToast(message = getString(R.string.enter_valid_mobile_intro), errorType)
                textView.requestFocus()
                return false

            }

            if(textView.editText?.text.toString().length  != 9) {

                showCustomToast(message = getString(R.string.enter_valid_mobile_length), errorType)
                textView.requestFocus()
                return false
            }

        }
        return true
    }

    fun AppCompatActivity.validateTaxNumber(vararg textViews: TextView, errorType: Int = 0): Boolean {

        for (textView in textViews) {

            if(textView.length()  != 15) {

//                if (textView.length()  == 9 && textView.text.take(1).toString() != "0") {
//
//
//                } else {
//
//
//                }

                showCustomToast(message = getString(R.string.enter_valid_tax_num_length), errorType)
                textView.requestFocus()
                return false
            }


            Log.e("mob_intro", " first  = ${textView.text.first()} // last  = ${textView.text.last()}")


            if (textView.text.first() != '3' || textView.text.last() != '3') {


                showCustomToast(message = getString(R.string.tax_num_must_start_3), errorType)
                textView.requestFocus()
                return false
            }

        }
        return true
    }

    fun AppCompatActivity.isPermissionGranted(permission: String): Boolean {

        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun Fragment.isPermissionGranted(permission: String): Boolean {

        return ContextCompat.checkSelfPermission(
            requireActivity(),
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }


    fun BaseActivity.isUserLogin(showDialog: Boolean = false): Boolean {

        return if (sharedPrefUtils.getCurrentUserData() != null) {

            true
        } else {

            if (showDialog) {

                showCustomToast(getString(R.string.please_login_to_continue), Constants.TOAST_ERROR)

            }
            false
        }
    }


}