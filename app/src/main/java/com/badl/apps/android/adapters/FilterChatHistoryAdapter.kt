package com.badl.apps.android.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.badl.apps.android.appFeatures.chat.data.FilterChatItem
import com.badl.apps.android.databinding.ListItemFilterChatBinding


class FilterChatHistoryAdapter(
    private val context: Context,
    private val isMultiSelect: Boolean,
    private var defaultSelectedItem: Int,
) : RecyclerView.Adapter<FilterChatHistoryAdapter.ViewHolder>() {

    private val listOfData by lazy {   ArrayList<FilterChatItem>() }

    private var selectedItems: ArrayList<FilterChatItem> = ArrayList<FilterChatItem>()
    private var selectedItem: FilterChatItem? = null
    private var prevSelectedIndex = 0
    private var currentSelectedIndex = 0
    private val _itemSelected = MutableLiveData<FilterChatItem>()
    public val itemSelected: LiveData<FilterChatItem> = _itemSelected
    private var firstLoad = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            ListItemFilterChatBinding.inflate(
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

                if (currentSelectedIndex == 0 && firstLoad) {

                    selectItem(holder.adapterPosition)

                }

                if (currentSelectedIndex != holder.adapterPosition) {

                    selectItem(holder.adapterPosition)
                }
            }
        })
    }

    override fun getItemCount(): Int {
        return listOfData.size
    }

    inner class ViewHolder(var binding: ListItemFilterChatBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: FilterChatItem) {


            binding.root.isSelected = item.isSelected
            binding.tvFilterTxt.text = item.title
            binding.tvChatsCount.text = item.chats_count.toString()
        }

    }


    fun setData(newData: List<FilterChatItem>) {

        listOfData.clear()
        listOfData.addAll(newData)

        if (newData.isNotEmpty()) {

//            defaultSelectedItem = 0
//            currentSelectedIndex = defaultSelectedItem
//            listOfData[0].isSelected = true
//            selectedItem = listOfData[0]
//            _itemSelected.value = listOfData[0]

            notifyDataSetChanged()
        }

    }


    fun getSelectedItem(): FilterChatItem? {
        return selectedItem
    }

    fun getSelectedItems(): ArrayList<FilterChatItem> {
        return selectedItems
    }

    private fun selectItem(holderPosition: Int) {

        prevSelectedIndex = currentSelectedIndex
        currentSelectedIndex = holderPosition
        listOfData[prevSelectedIndex].isSelected = false
        listOfData[currentSelectedIndex].isSelected = true
        _itemSelected.value = listOfData[currentSelectedIndex]
        selectedItem = listOfData[currentSelectedIndex]

        notifyItemChanged(prevSelectedIndex)
        notifyItemChanged(currentSelectedIndex)
    }


}