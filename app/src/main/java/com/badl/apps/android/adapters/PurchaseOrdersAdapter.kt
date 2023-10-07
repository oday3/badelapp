package com.badl.apps.android.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.badl.apps.android.appFeatures.biddingAndBarterSystem.data.AdOrderItem
import com.badl.apps.android.databinding.ListItemPurchaseOrderBinding
import com.badl.apps.android.utils.UiUtils.setPrice
import com.badl.apps.android.utils.UiUtils.showView


class PurchaseOrdersAdapter(val context: Context, val from: Int,
                            private val acceptRejectAction: ((position: Int, type: Int) -> Unit)?,
                            private val chatAction: ((item: AdOrderItem) -> Unit)?) : RecyclerView.Adapter<PurchaseOrdersAdapter.ViewHolder>() {

    private val listOfItems by lazy { ArrayList<AdOrderItem>() }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val item = ListItemPurchaseOrderBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)


        return ViewHolder(item)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listOfItems[position])

        holder.binding.tvAcceptOrder.setOnClickListener {


            acceptRejectAction?.let { it1 -> it1(holder.adapterPosition, 1) }
        }

        holder.binding.tvRejectOrder.setOnClickListener {

            acceptRejectAction?.let { it1 -> it1(holder.adapterPosition, 2) }
        }

        holder.binding.imgChat.setOnClickListener {

            chatAction?.let { it1 -> it1(listOfItems[holder.adapterPosition]) }
        }

    }

    override fun getItemCount(): Int {
        return listOfItems.size
    }

    inner class ViewHolder(var binding: ListItemPurchaseOrderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: AdOrderItem) {


            if (from == 1) {
                binding.tvOfferedPriceLabel.showView(false)
                binding.tvOfferedPrice.showView(false)
            } else {

                binding.tvOfferedPriceLabel.showView(true)
                binding.tvOfferedPrice.showView(true)
            }

            binding.tvFrom.text = item.owner_name
            binding.tvQuantity.text = "X${item.quantity}"
            binding.tvOfferedPrice.setPrice(item.price)
//            if (item.is_seen == true) {
//
//                binding.imgNotificationImg.setImageResource(R.drawable.ic_readed_notification)
//            }
//
//            binding.tvNotificationTitle.text = item.title
//            binding.tvNotificationMsg.text = item.message

        }
    }

    fun setData(newData: List<AdOrderItem>) {

        listOfItems.clear()
        listOfItems.addAll(newData)
        notifyDataSetChanged()
    }


    fun getItem(position: Int): AdOrderItem {

        return listOfItems[position]
    }

    fun deleteItem(position: Int) {

        listOfItems.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeRemoved(position, listOfItems.size)
    }
}