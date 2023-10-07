package com.badl.apps.android.baseClasses.ui

import android.content.Context
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView

class BaseSelectionAdapter<T>(private val context: Context,
                           val isMultiSelect: Boolean,
                           private val defaultSelectedItem: Int,) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val listOfData by lazy {   ArrayList<T>() }

    private val selectedItems by lazy { ArrayList<T>() }

   // private var selectedItem: T? = null
    private var prevSelectedIndex = 0
    private var currentSelectedIndex = 0
    private val _itemSelected = MutableLiveData<T>()
    public val itemSelected: LiveData<T> = _itemSelected

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }
}