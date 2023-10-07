package com.badl.apps.android.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.badl.apps.android.R
import com.badl.apps.android.appFeatures.userAccount.data.OrderItem
import com.badl.apps.android.appFeatures.userAccount.data.UserAdItem
import com.badl.apps.android.databinding.ListItemMyAdBinding
import com.badl.apps.android.utils.OrderItemsDiffUtils
import com.badl.apps.android.utils.UiUtils.setImage
import com.badl.apps.android.utils.UserAdsDiffUtils


class MyAdsAdapter(val context: Context, val adType: Int,
                   private val clickAction: ((position: Int) -> Unit)?,
                   private val editAction: ((position: Int) -> Unit)?,
                   private val deleteAction: ((position: Int, item: UserAdItem, adapter: MyAdsAdapter) -> Unit)?,
                   private val refreshAction: ((data: List<UserAdItem>, refreshData: Boolean) -> Unit)?
) : RecyclerView.Adapter<MyAdsAdapter.ViewHolder>() {

    private val listOfItems by lazy { ArrayList<UserAdItem>() }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val item = ListItemMyAdBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(item)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listOfItems[position])

        holder.binding.root.setOnClickListener {


            clickAction?.let { it1 -> it1(holder.adapterPosition) }
        }

        holder.binding.imgMyAdListItemEdit.setOnClickListener {


            editAction?.let { it1 -> it1(holder.adapterPosition) }
        }

        holder.binding.imgMyAdListItemClose.setOnClickListener {


            deleteAction?.let { it1 -> it1(holder.adapterPosition, listOfItems[holder.adapterPosition], this) }
        }

    }

    override fun getItemCount(): Int {
        return listOfItems.size
    }

    inner class ViewHolder(var binding: ListItemMyAdBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: UserAdItem) {


            binding.tvMyAdListItemProductImg.setImage(item.image)
            binding.tvMyAdListItemProductName.text = item.title
            binding.tvMyAdListItemAdType.text = item.count_text

            binding.tvAdStatus.text = item.status_text

            if (item.status == 1) {

                binding.tvAdStatus.setTextColor(context.getColor(R.color.green))
                binding.tvAdStatus.setBackgroundResource(R.drawable.bg_accepted_order)

            } else {

                binding.tvAdStatus.setTextColor(context.getColor(R.color.red))
                binding.tvAdStatus.setBackgroundResource(R.drawable.bg_reject_order)

            }
//            when(adType) {
//
//                1 -> {
//
//
//                    binding.tvMyAdListItemAdType.text = context.getString(R.string.bidding)
//
//                }
//
//                2 -> {
//
//                    binding.tvMyAdListItemAdType.text = context.getString(R.string.swap_offers)
//
//                }
//
//                3 -> {
//
//
//                    binding.tvMyAdListItemAdType.text = context.getString(R.string.purchase_offers)
//
//                }
//
//                4 -> {
//
//
//                    binding.tvMyAdListItemAdType.text = context.getString(R.string.associations_requests)
//
//
//                }
//
//
//            }


        }
    }


    fun setAdapterData (refreshContent: Boolean = true, newData: List<UserAdItem>) {

        if (refreshContent) {

            setRefreshData(newData)

        } else {

            setData(newData)
        }

    }


    fun setRefreshData(newData: List<UserAdItem>) {

        listOfItems.clear()
        listOfItems.addAll(newData)
        notifyDataSetChanged()
        Log.e("returned_data", "setRefreshData")

    }

    fun setData(newData: List<UserAdItem>) {

        val oldList = this.listOfItems
        listOfItems.addAll(newData)
        val diffResult = DiffUtil.calculateDiff(UserAdsDiffUtils(oldList, listOfItems))
        diffResult.dispatchUpdatesTo(this)

    }

    fun deleteItem(position: Int) {

        listOfItems.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeRemoved(position, listOfItems.size)

        refreshAction?.let { it(listOfItems, false) }
    }


    fun getItem (position: Int): UserAdItem? {

        return if (listOfItems.isNotEmpty()) {

            listOfItems[position]

        } else {

            null
        }
    }
}