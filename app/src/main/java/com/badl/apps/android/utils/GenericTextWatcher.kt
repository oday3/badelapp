package com.badl.apps.android.utils
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import com.badl.apps.android.R

class GenericTextWatcher internal constructor(
    private val currentView: View,
    private val nextView: View?,
    private val firstEditTextId: EditText,
    private val secondEditTextId: EditText,
    private val thirdEditTextId: EditText,
    private val fourthEditTextId: EditText
) : TextWatcher {
    override fun afterTextChanged(editable: Editable) { // TODO Auto-generated method stub
        val text = editable.toString()
        when (currentView.id) {
            firstEditTextId.id -> if (text.length == 1) nextView!!.requestFocus()
            secondEditTextId.id -> if (text.length == 1) nextView!!.requestFocus()
            thirdEditTextId.id -> if (text.length == 1) nextView!!.requestFocus()
            //You can use EditText4 same as above to hide the keyboard
        }

        when {
            firstEditTextId.text.hashCode() == editable.hashCode() -> {
                firstEditTextId.setBackgroundResource(
                    if (editable.toString().trim().isEmpty())
                        R.drawable.bg_main_edt
                    else
                        R.drawable.bg_main_edt
                )
            }

            secondEditTextId.text.hashCode() == editable.hashCode() -> {
                secondEditTextId.setBackgroundResource(
                    if (editable.toString().trim().isEmpty())
                        R.drawable.bg_main_edt
                    else
                        R.drawable.bg_main_edt
                )
            }

            thirdEditTextId.text.hashCode() == editable.hashCode() -> {
                thirdEditTextId.setBackgroundResource(
                    if (editable.toString().trim().isEmpty())
                        R.drawable.bg_main_edt
                    else
                        R.drawable.bg_main_edt
                )
            }

            fourthEditTextId.text.hashCode() == editable.hashCode() -> {
                fourthEditTextId.setBackgroundResource(
                    if (editable.toString().trim().isEmpty())
                        R.drawable.bg_main_edt
                    else
                        R.drawable.bg_main_edt
                )
            }
        }
    }

    override fun beforeTextChanged(
        arg0: CharSequence,
        arg1: Int,
        arg2: Int,
        arg3: Int
    ) { // TODO Auto-generated method stub
    }

    override fun onTextChanged(
        arg0: CharSequence,
        arg1: Int,
        arg2: Int,
        arg3: Int
    ) { // TODO Auto-generated method stub
    }

}
