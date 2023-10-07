package com.badl.apps.android.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.badl.apps.android.R
import com.badl.apps.android.appFeatures.chat.data.AddressItem
import com.badl.apps.android.appFeatures.main.data.ProductSectionItem
import com.badl.apps.android.databinding.ListItemAddressBinding
import com.badl.apps.android.databinding.ListItemProductSectionFullBinding
import com.badl.apps.android.databinding.ListItemProductSectionHorizantalBinding
import com.badl.apps.android.utils.AdItemsDiffUtils
import com.badl.apps.android.utils.UiUtils.setImage
import com.badl.apps.android.utils.UiUtils.setPrice

class AddressesAdapter(
    val context: Context,
    private val clickAction: ((item: AddressItem) -> Unit)?,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var listOfItems = ArrayList<AddressItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

//
//        val view: View = if (viewType == VIEW_TYPE_LINE) {
//            LayoutInflater.from(parent.context).inflate(R.layout.list_item_product_section_horizantal, parent, false)
//        } else {
//            LayoutInflater.from(parent.context).inflate(R.layout.list_item_product_section_full, parent, false)
//        }
//
//        return ViewHolder(view, viewType)

            val item = ListItemAddressBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)

       return ViewHolder(item)

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        (holder as ViewHolder).bindInfo(listOfItems[position])

        holder.binding.root.setOnClickListener {

            clickAction?.let { it1 -> it1(listOfItems[holder.adapterPosition]) }
        }

    }

    override fun getItemCount(): Int {
        return listOfItems.size
    }


    inner class ViewHolder(var binding: ListItemAddressBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindInfo(item: AddressItem) {

            binding.tvLocationName.text = item.title
            binding.imgIconHHH.setImage(item.icon)
        }
    }




    fun setData(newData: List<AddressItem>) {

        val oldList = this.listOfItems
        listOfItems.addAll(newData)
        //val diffResult = DiffUtil.calculateDiff(AdItemsDiffUtils(oldList, listOfItems))
       //    diffResult.dispatchUpdatesTo(this)

    }


    fun setAdapterData (refreshContent: Boolean = true, newData: List<AddressItem>) {

        if (refreshContent) {

            setRefreshData(newData)

        } else {

            setData(newData)
        }

    }

    fun setRefreshData(newData: List<AddressItem>) {

        listOfItems.clear()
        listOfItems.addAll(newData)
        notifyDataSetChanged()
        Log.e("returned_data", "setRefreshData")

    }



    fun getItem(position: Int): AddressItem {

        return listOfItems[position]
    }

    fun getItems(): List<AddressItem> {

        return listOfItems
    }

    fun refreshItem(itemPosition: Int) {

        notifyItemChanged(itemPosition, listOfItems[itemPosition])
    }


    fun setItem(itemPosition: Int, data: AddressItem) {

        listOfItems[itemPosition] = data
    }
}