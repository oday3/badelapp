package com.badl.apps.android.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.badl.apps.android.appFeatures.biddingAndBarterSystem.data.SellerRateItem
import com.badl.apps.android.databinding.ListItemSellerRatingBinding
import com.badl.apps.android.utils.UiUtils.setImage


class SellerRatingsAdapter(val context: Context) : RecyclerView.Adapter<SellerRatingsAdapter.ViewHolder>() {

    private val listOfItems by lazy { ArrayList<SellerRateItem>() }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val item = ListItemSellerRatingBinding.inflate(
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

    inner class ViewHolder(var binding: ListItemSellerRatingBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SellerRateItem) {

            binding.imgUserImg.setImage(item.user_image)
            binding.tvUserName.text = item.user_name
            binding.tvTime.text = item.rated_since
            binding.tvUserComment.text = item.message
            binding.tvSellerRate.text = item.rate.toString()

        }
    }

    fun setData(newData: List<SellerRateItem>) {

        listOfItems.clear()
        listOfItems.addAll(newData)
        notifyDataSetChanged()
    }
}