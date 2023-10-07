package com.badl.apps.android.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.badl.apps.android.appFeatures.userAccount.data.OrderItem
import com.badl.apps.android.databinding.ListItemUserOrderBinding
import com.badl.apps.android.utils.UiUtils.setImage


class UserOrdersAdapter(val context: Context) : RecyclerView.Adapter<UserOrdersAdapter.ViewHolder>() {

    private val listOfItems by lazy { ArrayList<OrderItem>() }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val item = ListItemUserOrderBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(item)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listOfItems[position])

        holder.binding.root.setOnClickListener {

        }

    }

    override fun getItemCount(): Int {
        return listOfItems.size
    }

    inner class ViewHolder(var binding: ListItemUserOrderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: OrderItem) {

            binding.imgOrderItemProductImg.setImage(item.image)
            binding.tvOrderItemProductName.text = item.title
            binding.tvOrderItemDesc.text = item.order_date


        }
    }

    fun setData(newData: List<OrderItem>) {

        listOfItems.clear()
        listOfItems.addAll(newData)
        notifyDataSetChanged()
    }

    fun deleteItem(position: Int) {

        listOfItems.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeRemoved(position, listOfItems.size)
    }

}