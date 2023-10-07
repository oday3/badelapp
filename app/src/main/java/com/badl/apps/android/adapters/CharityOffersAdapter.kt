package com.badl.apps.android.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.badl.apps.android.appFeatures.biddingAndBarterSystem.data.AdOrderItem
import com.badl.apps.android.databinding.ListItemCharityOfferBinding


class CharityOffersAdapter(val context: Context, private val acceptRejectAction: ((position: Int, type: Int) -> Unit)?,) : RecyclerView.Adapter<CharityOffersAdapter.ViewHolder>() {

    private val listOfItems by lazy { ArrayList<AdOrderItem>() }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val item = ListItemCharityOfferBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(item)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listOfItems[position])

        holder.binding.tvAcceptOrder.setOnClickListener {


            acceptRejectAction?.let { it1 -> it1(holder.adapterPosition, 1) }
        }

        holder.binding.tvRejectOrder.setOnClickListener {


            acceptRejectAction?.let { it1 -> it1(holder.adapterPosition, 0) }
        }

    }

    override fun getItemCount(): Int {
        return listOfItems.size
    }

    inner class ViewHolder(var binding: ListItemCharityOfferBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: AdOrderItem) {


            binding.tvCharityName.text = item.owner_name
        }
    }

    fun setData(newData: List<AdOrderItem>) {

        listOfItems.clear()
        listOfItems.addAll(newData)
        notifyDataSetChanged()
    }

    fun deleteItem(position: Int) {

        listOfItems.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeRemoved(position, listOfItems.size)
    }

    fun getItem(position: Int): AdOrderItem {

        return listOfItems[position]
    }

}