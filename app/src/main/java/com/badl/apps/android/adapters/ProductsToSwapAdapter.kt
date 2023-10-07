package com.badl.apps.android.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.badl.apps.android.appFeatures.userAccount.data.UserAdItem
import com.badl.apps.android.databinding.ListItemProductToSwapBinding
import com.badl.apps.android.utils.UiUtils.setImage


class ProductsToSwapAdapter(
    private val context: Context,
    private val isMultiSelect: Boolean,
    private var defaultSelectedItem: Int,
) : RecyclerView.Adapter<ProductsToSwapAdapter.ViewHolder>() {

    private val listOfData by lazy {   ArrayList<UserAdItem>() }

    private var selectedItems: ArrayList<UserAdItem> = ArrayList<UserAdItem>()
    private var selectedItem: UserAdItem? = null
    private var prevSelectedIndex = 0
    private var currentSelectedIndex = 0
    private val _itemSelected = MutableLiveData<UserAdItem>()
    public val itemSelected: LiveData<UserAdItem> = _itemSelected

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            ListItemProductToSwapBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    init {


        if (listOfData.isNotEmpty()) {

            listOfData[defaultSelectedItem].isSelected = true
            selectedItem = listOfData[defaultSelectedItem]
            currentSelectedIndex = defaultSelectedItem
            _itemSelected.value = listOfData[defaultSelectedItem]
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listOfData[position])

        holder.binding.root.setOnClickListener {

            if (isMultiSelect) {

                if (listOfData[holder.adapterPosition].isSelected) {

                    listOfData[holder.adapterPosition].isSelected = false
                    selectedItems.remove(listOfData[holder.adapterPosition])


                } else {

                    listOfData[holder.adapterPosition].isSelected = true
                    selectedItems.add(listOfData[holder.adapterPosition])

                }

                notifyItemChanged(holder.adapterPosition)
            } else {

                if (currentSelectedIndex != holder.adapterPosition) {

                    prevSelectedIndex = currentSelectedIndex
                    currentSelectedIndex = holder.adapterPosition
                    listOfData[prevSelectedIndex].isSelected = false
                    listOfData[currentSelectedIndex].isSelected = true
                    _itemSelected.value = listOfData[currentSelectedIndex]
                    selectedItem = listOfData[currentSelectedIndex]
                    notifyItemChanged(prevSelectedIndex)
                    notifyItemChanged(currentSelectedIndex)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return listOfData.size
    }

    inner class ViewHolder(var binding: ListItemProductToSwapBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: UserAdItem) {

            binding.btnCheck.isChecked = item.isSelected
            binding.imgProductImg.setImage(item.image)
            binding.tvSwapProductName.text = item.title
        }
    }


    fun setData(newData: List<UserAdItem>) {

        listOfData.clear()
        listOfData.addAll(newData)

        if (newData.isNotEmpty()) {

            defaultSelectedItem = 0
            currentSelectedIndex = defaultSelectedItem
            listOfData[0].isSelected = true
            selectedItem = listOfData[0]
            _itemSelected.value = listOfData[0]

            notifyDataSetChanged()
        }

    }


    fun getSelectedItem(): UserAdItem? {
        return selectedItem
    }

    fun getSelectedItems(): ArrayList<UserAdItem> {
        return selectedItems
    }


}