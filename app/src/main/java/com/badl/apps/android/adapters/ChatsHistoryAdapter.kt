package com.badl.apps.android.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.badl.apps.android.appFeatures.chat.data.ChatHistoryItem
import com.badl.apps.android.appFeatures.chat.ui.ChatActivity
import com.badl.apps.android.baseClasses.ui.BaseActivity
import com.badl.apps.android.databinding.ListItemChatHistoryBinding
import com.badl.apps.android.utils.AppUtil.getIntentExtension
import com.badl.apps.android.utils.Constants
import com.badl.apps.android.utils.UiUtils.setImage


class ChatsHistoryAdapter(val context: Context) : RecyclerView.Adapter<ChatsHistoryAdapter.ViewHolder>() {

    private val listOfItems by lazy { ArrayList<ChatHistoryItem>() }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val item = ListItemChatHistoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(item)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listOfItems[position])

        holder.binding.root.setOnClickListener {

            (context as BaseActivity).getIntentExtension(ChatActivity::class.java).run {

                    putExtra(Constants.AD_ID, listOfItems[holder.adapterPosition].ad_id)
                    putExtra(Constants.OWNER_ID, listOfItems[holder.adapterPosition].owner_id)
                    putExtra(Constants.SEND_USER_ID, listOfItems[holder.adapterPosition].user_id)
                    putExtra(Constants.OWNER_IMAGE, listOfItems[holder.adapterPosition].user_image)
                    putExtra(Constants.FROM, Constants.FROM_CHAT)

                context.startActivity(this)
            }

        }
    }

    override fun getItemCount(): Int {
        return listOfItems.size
    }

    inner class ViewHolder(var binding: ListItemChatHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ChatHistoryItem) {

            binding.imgChatHistoryItemUserImg.setImage(item.user_image)
            binding.tvChatHistoryItemTime.text = item.last_message_time
            binding.tvChatHistoryItemLastMsg.text = item.last_message

        }
    }

    fun setData(newData: List<ChatHistoryItem>) {

        listOfItems.clear()
        listOfItems.addAll(newData)
        notifyDataSetChanged()
    }
}