package com.badl.apps.android.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.badl.apps.android.appFeatures.userAccount.data.UserAdItem
import com.badl.apps.android.databinding.ListItemAdAcquisitionBinding
import com.badl.apps.android.utils.UiUtils.setImage


class UserAdsAdapter(val context: Context,
                     private val deleteAction: ((item: UserAdItem, position: Int) -> Unit)?,
                     private val editAction: ((item: UserAdItem, position: Int) -> Unit)?,
) : RecyclerView.Adapter<UserAdsAdapter.ViewHolder>() {

    private val listOfItems by lazy { ArrayList<UserAdItem>() }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val item = ListItemAdAcquisitionBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(item)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listOfItems[position])

        holder.binding.root.setOnClickListener {

        }

        holder.binding.tvAdItemEdit.setOnClickListener {

            editAction?.let { it1 -> it1(listOfItems[holder.adapterPosition], holder.adapterPosition) }
        }

        holder.binding.imgDeleteItem.setOnClickListener {

            deleteAction?.let { it1 -> it1(listOfItems[holder.adapterPosition], holder.adapterPosition) }
        }
    }

    override fun getItemCount(): Int {
        return listOfItems.size
    }

    inner class ViewHolder(var binding: ListItemAdAcquisitionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: UserAdItem) {

            binding.imgAdItemProductImg.setImage(item.image)
            binding.tvAdItemProductName.text = item.title
            binding.tvAdItemDesc.text = item.end_date


        }
    }

    fun setData(newData: List<UserAdItem>) {

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