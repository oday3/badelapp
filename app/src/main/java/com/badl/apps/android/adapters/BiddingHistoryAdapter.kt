package com.badl.apps.android.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.badl.apps.android.appFeatures.biddingAndBarterSystem.data.AdOrderItem
import com.badl.apps.android.appFeatures.biddingAndBarterSystem.data.BidItem
import com.badl.apps.android.appFeatures.chat.ui.ChatActivity
import com.badl.apps.android.baseClasses.ui.BaseActivity
import com.badl.apps.android.databinding.ListItemBiddingHistoryBinding
import com.badl.apps.android.utils.AppUtil.getIntentExtension
import com.badl.apps.android.utils.Constants
import com.badl.apps.android.utils.UiUtils.setImage


class BiddingHistoryAdapter(val context: Context,
                            val from: String,
                            private val clickAction: ((item: BidItem) -> Unit)?) : RecyclerView.Adapter<BiddingHistoryAdapter.ViewHolder>() {

    private val listOfItems by lazy { ArrayList<BidItem>() }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val item = ListItemBiddingHistoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(item)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listOfItems[position])

        holder.binding.root.setOnClickListener {

            clickAction?.let { it1 -> it1(listOfItems[holder.adapterPosition]) }
        }
    }

    override fun getItemCount(): Int {
        return listOfItems.size
    }

    inner class ViewHolder(var binding: ListItemBiddingHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: BidItem) {

           // binding.imgIcon.setImage(item.)
            binding.tvBiddingValue.text = item.price
            binding.tvBidderName.text = item.user_name
            binding.tvTime.text = item.created_at
        }
    }

    fun setData(newData: List<BidItem>) {

        listOfItems.clear()
        listOfItems.addAll(newData)
        notifyDataSetChanged()
    }

    fun getItem(position: Int): BidItem {

        return listOfItems[0]
    }
}