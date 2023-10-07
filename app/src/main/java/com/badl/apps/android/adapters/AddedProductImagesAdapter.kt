package com.badl.apps.android.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.badl.apps.android.appFeatures.userAccount.data.AdImageItem
import com.badl.apps.android.databinding.ListItemAddedProductImgBinding
import com.badl.apps.android.utils.UiUtils.setImage


class AddedProductImagesAdapter(val context: Context,
                                private val deleteAction: ((position: Int, item: AdImageItem) -> Unit)?,
) : RecyclerView.Adapter<AddedProductImagesAdapter.ViewHolder>() {

    private val listOfItems by lazy { ArrayList<AdImageItem>() }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val item = ListItemAddedProductImgBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(item)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listOfItems[position])

        holder.binding.imgRemoveImg.setOnClickListener {

            deleteAction?.let { it1 -> it1(holder.adapterPosition, listOfItems[holder.adapterPosition]) }
        }
    }

    override fun getItemCount(): Int {
        return listOfItems.size
    }

    inner class ViewHolder(var binding: ListItemAddedProductImgBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: AdImageItem) {

            binding.imgAddedProductImg.setImage(item.image)

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