package com.badl.apps.android.adapters

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.badl.apps.android.R
import com.badl.apps.android.appFeatures.appCommon.data.NotificationItem
import com.badl.apps.android.appFeatures.biddingAndBarterSystem.ui.ProductDetailsActivity
import com.badl.apps.android.appFeatures.chat.ui.ChatActivity
import com.badl.apps.android.databinding.ListItemNotificationBinding
import com.badl.apps.android.utils.AppUtil.getIntentExtension
import com.badl.apps.android.utils.Constants
import com.badl.apps.android.utils.UiUtils.setImage


class NotificationsAdapter(val context: Context) : RecyclerView.Adapter<NotificationsAdapter.ViewHolder>() {

    private val listOfItems by lazy { ArrayList<NotificationItem>() }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val item = ListItemNotificationBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(item)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listOfItems[position])

        holder.binding.root.setOnClickListener {

            when (listOfItems[holder.adapterPosition].type) {

                Constants.NOTIFICATION_NEW_MESSAGE -> {

                    (context as AppCompatActivity).getIntentExtension(ChatActivity::class.java).run {

                        putExtra(Constants.FROM, Constants.FROM_NOTIFICATIONS)
                        putExtra(Constants.OWNER_IMAGE, listOfItems[holder.adapterPosition].image)
                        putExtra(Constants.FIREBASE_KEY, listOfItems[holder.adapterPosition].firebase_key)
                        context.startActivity(this)
                    }
                }

                Constants.NOTIFICATION_TYPE_ORDER -> {

                    (context as AppCompatActivity).getIntentExtension(ProductDetailsActivity::class.java).run {

                        putExtra(Constants.ITEM_ID, listOfItems[holder.adapterPosition].reference_id?.toInt())
                        putExtra(Constants.FROM, Constants.FROM_ORDER_DETAILS)

                        context.startActivity(this)
                    }
                }

                Constants.NOTIFICATION_TYPE_NEW_AD -> {


                    (context as AppCompatActivity).getIntentExtension(ProductDetailsActivity::class.java).run {

                        putExtra(Constants.ITEM_ID, listOfItems[holder.adapterPosition].reference_id?.toInt())
                        putExtra(Constants.FROM, Constants.FROM_AD_DETAILS)

                        context.startActivity(this)
                    }



                }




            }
        }
    }

    override fun getItemCount(): Int {
        return listOfItems.size
    }

    inner class ViewHolder(var binding: ListItemNotificationBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: NotificationItem) {


            if (item.is_seen == 1) {

                binding.imgNotificationImg.setImageResource(R.drawable.ic_readed_notification)
            }
//
            binding.tvNotificationTitle.text = item.title
            binding.tvNotificationMsg.text = item.message

            if (!TextUtils.isEmpty(item.image)) {

                binding.imgNotificationImg.setImage(item.image)

            }
        }
    }

    fun setData(newData: List<NotificationItem>) {

        listOfItems.clear()
        listOfItems.addAll(newData)
        notifyDataSetChanged()
    }
}