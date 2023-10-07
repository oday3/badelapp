package com.badl.apps.android.interfaces

import androidx.recyclerview.widget.RecyclerView

interface RecyclerItemTouchHelperListener {
    fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int, position: Int)
    fun onItemMove(fromPosition: Int, toPosition: Int): Boolean
}