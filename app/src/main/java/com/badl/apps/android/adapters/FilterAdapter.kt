package com.badl.apps.android.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.badl.apps.android.appFeatures.appCommon.data.FilterItem
import com.badl.apps.android.databinding.ListItemFilterBinding



class FilterAdapter(
    private val context: Context,
    private val isMultiSelect: Boolean,
    private var defaultSelectedItem: Int,
) : RecyclerView.Adapter<FilterAdapter.ViewHolder>() {

    private val listOfData by lazy {   ArrayList<FilterItem>() }

    private var selectedItems: ArrayList<FilterItem> = ArrayList<FilterItem>()
    private var selectedItem: FilterItem? = null
    private var prevSelectedIndex = 0
    private var currentSelectedIndex = 0
    private val _itemSelected = MutableLiveData<FilterItem>()
    public val itemSelected: LiveData<FilterItem> = _itemSelected

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            ListItemFilterBinding.inflate(
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

        holder.binding.root.setOnClickListener(View.OnClickListener {

            if (isMultiSelect) {

                if (listOfData[holder.adapterPosition].isSelected) {

                    listOfData[holder.adapterPosition].isSelected = false
                    selectedItems.remove(listOfData[holder.adapterPosition])
                    //holder.binding.root.isSelected = false


                } else {

                    listOfData[holder.adapterPosition].isSelected = true
                    selectedItems.add(listOfData[holder.adapterPosition])
                    //holder.binding.root.isSelected = true

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
        })
    }

    override fun getItemCount(): Int {
        return listOfData.size
    }

    inner class ViewHolder(var binding: ListItemFilterBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: FilterItem) {


            binding.root.isSelected = item.isSelected

            binding.tvAttributeName.text = item.title
        }

    }


    fun setData(newData: List<FilterItem>) {

        listOfData.clear()
        listOfData.addAll(newData)

        defaultSelectedItem = 0
       // setSelectedItem(0)

        notifyDataSetChanged()
    }


    fun getSelectedItem(): FilterItem? {
        return selectedItem
    }

    fun getSelectedItems(): ArrayList<FilterItem> {
        return selectedItems
    }

    fun clearSelectedFilter() {

        listOfData[prevSelectedIndex].isSelected = false
        listOfData[currentSelectedIndex].isSelected = false
        this.selectedItem = null
        notifyItemChanged(prevSelectedIndex)
        notifyItemChanged(currentSelectedIndex)
    }



    fun setSelectedItem(position: Int) {

        currentSelectedIndex = defaultSelectedItem
        listOfData[position].isSelected = true
        this.selectedItem = listOfData[position]
        _itemSelected.value = listOfData[position]
    }

    fun deleteItem(position: Int) {

        listOfData.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeRemoved(position, listOfData.size)
    }
}