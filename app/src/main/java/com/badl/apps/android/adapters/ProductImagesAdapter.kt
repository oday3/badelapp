package com.badl.apps.android.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.badl.apps.android.appFeatures.userAccount.data.AdImageItem
import com.badl.apps.android.databinding.ListItemProductImgBinding
import com.badl.apps.android.utils.UiUtils.setImage


class ProductImagesAdapter(val context: Context) : RecyclerView.Adapter<ProductImagesAdapter.ViewHolder>() {

    private val listOfItems by lazy { ArrayList<AdImageItem>() }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val item = ListItemProductImgBinding.inflate(
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

    inner class ViewHolder(var binding: ListItemProductImgBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: AdImageItem) {

            binding.tvProductImg.setImage(item.image)

        }
    }

    fun setData(newData: List<AdImageItem>) {

        listOfItems.clear()
        listOfItems.addAll(newData)
        notifyDataSetChanged()
    }

    fun getData(): List<AdImageItem> {

        return listOfItems
    }
}