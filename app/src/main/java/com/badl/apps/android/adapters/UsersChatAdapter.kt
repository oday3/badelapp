package com.badl.apps.android.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.badl.apps.android.appFeatures.main.data.SectionItem
import com.badl.apps.android.appFeatures.main.ui.AdListActivity
import com.badl.apps.android.databinding.ListItemSectionsBinding
import com.badl.apps.android.databinding.ListItemUserChatBinding
import com.badl.apps.android.utils.AppUtil.getIntentExtension
import com.badl.apps.android.utils.Constants
import com.badl.apps.android.utils.UiUtils.setImage


class UsersChatAdapter(val context: Context) : RecyclerView.Adapter<UsersChatAdapter.ViewHolder>() {

    private val listOfItems by lazy { ArrayList<Int>() }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val item = ListItemUserChatBinding.inflate(
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

    inner class ViewHolder(var binding: ListItemUserChatBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Int) {



        }
    }

    fun setData(newData: List<Int>) {

        listOfItems.clear()
        listOfItems.addAll(newData)
        notifyDataSetChanged()
    }
}