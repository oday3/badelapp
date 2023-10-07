package com.badl.apps.android.utils

import androidx.recyclerview.widget.DiffUtil
import com.badl.apps.android.appFeatures.chat.data.FirebaseMessageItem

class MessageItemDiffUtils (var oldData: List<FirebaseMessageItem>,
                            var newData: List<FirebaseMessageItem>
) : DiffUtil.Callback()  {

    override fun getOldListSize(): Int {
        return oldData.size
    }

    override fun getNewListSize(): Int {
        return newData.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldData[oldItemPosition].id == newData[newItemPosition].id
    }

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        return super.getChangePayload(oldItemPosition, newItemPosition)
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldData[oldItemPosition] == newData[newItemPosition]
    }
}