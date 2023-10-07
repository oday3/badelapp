package com.badl.apps.android.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.badl.apps.android.appFeatures.userAccount.data.SwapOrderItem
import com.badl.apps.android.databinding.ListItemSwapOfferBinding
import com.badl.apps.android.utils.UiUtils.setImage


class SwapOffersAdapter(val context: Context,  private val clickAction: ((position: Int) -> Unit)?) : RecyclerView.Adapter<SwapOffersAdapter.ViewHolder>() {

    private val listOfItems by lazy { ArrayList<SwapOrderItem>() }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val item = ListItemSwapOfferBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(item)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listOfItems[position])

        holder.binding.root.setOnClickListener {

            clickAction?.let { it1 -> it1(holder.adapterPosition) }
        }

    }

    override fun getItemCount(): Int {
        return listOfItems.size
    }

    inner class ViewHolder(var binding: ListItemSwapOfferBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SwapOrderItem) {

            binding.imgOfferProductImg.setImage(item.image)
            binding.tvProductOfferName.text = item.title
            binding.tvTvProductOfferDesc.text = item.description
        }
    }

    fun setData(newData: List<SwapOrderItem>) {

        listOfItems.clear()
        listOfItems.addAll(newData)
        notifyDataSetChanged()
    }

    fun deleteItem(position: Int) {

        listOfItems.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeRemoved(position, listOfItems.size)
    }

    fun getItem(position: Int): SwapOrderItem {

        return listOfItems[position]
    }

}