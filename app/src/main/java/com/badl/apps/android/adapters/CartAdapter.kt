package com.badl.apps.android.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.badl.apps.android.databinding.ListItemCartBinding


class CartAdapter(val context: Context) : RecyclerView.Adapter<CartAdapter.ViewHolder>() {

    private val listOfItems by lazy { ArrayList<Int>() }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val item = ListItemCartBinding.inflate(
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

    inner class ViewHolder(var binding: ListItemCartBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Int) {


//            if (item.is_seen == true) {
//
//                binding.imgNotificationImg.setImageResource(R.drawable.ic_readed_notification)
//            }
//
//            binding.tvNotificationTitle.text = item.title
//            binding.tvNotificationMsg.text = item.message

        }
    }

    fun setData(newData: List<Int>) {

        listOfItems.clear()
        listOfItems.addAll(newData)
        notifyDataSetChanged()
    }
}