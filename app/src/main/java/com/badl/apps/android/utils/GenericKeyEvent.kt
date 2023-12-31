package com.badl.apps.android.utils
import android.view.KeyEvent
import android.view.View
import android.widget.EditText


class GenericKeyEvent internal constructor(
    private val currentView: EditText,
    private val previousView: EditText?,
    private val firstEditTextId: View
) : View.OnKeyListener {
    override fun onKey(
        p0: View?,
        keyCode: Int,
        event: KeyEvent?
    ): Boolean {
        if (event!!.action == KeyEvent.ACTION_DOWN &&
            keyCode == KeyEvent.KEYCODE_DEL &&
            currentView.id != firstEditTextId.id &&
            currentView.text.isEmpty()
        ) {
            //If current is empty then previous EditText's number will also be deleted
            previousView!!.text = null
            previousView.requestFocus()
            return true
        }
        return false
    }
}
