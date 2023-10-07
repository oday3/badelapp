package com.badl.apps.android.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.badl.apps.android.R
import com.badl.apps.android.appFeatures.biddingAndBarterSystem.ui.ProductDetailsActivity
import com.badl.apps.android.appFeatures.main.data.ProductSectionItem
import com.badl.apps.android.appFeatures.userAccount.data.OrderItem
import com.badl.apps.android.databinding.ListItemBidOrderBinding
import com.badl.apps.android.utils.AdItemsDiffUtils
import com.badl.apps.android.utils.AppUtil.getIntentExtension
import com.badl.apps.android.utils.Constants
import com.badl.apps.android.utils.OrderItemsDiffUtils
import com.badl.apps.android.utils.UiUtils.setImage
import com.badl.apps.android.utils.UiUtils.showView


class OrdersAdapter(val context: Context, private val rateAction: ((item: OrderItem) -> Unit)?) : RecyclerView.Adapter<OrdersAdapter.ViewHolder>() {

    private val listOfItems by lazy { ArrayList<OrderItem>() }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val item = ListItemBidOrderBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(item)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listOfItems[position])

        holder.binding.root.setOnClickListener {


            (context as AppCompatActivity).getIntentExtension(ProductDetailsActivity::class.java).run {

                putExtra(Constants.FROM, Constants.FROM_ORDER_DETAILS)
                putExtra(Constants.ITEM_ID, listOfItems[holder.adapterPosition].id)

                context.startActivity(this)
            }
        }

        holder.binding.tvRateOrder.setOnClickListener {

            rateAction?.let { it1 -> it1(listOfItems[holder.adapterPosition]) }
        }
    }

    override fun getItemCount(): Int {
        return listOfItems.size
    }

    inner class ViewHolder(var binding: ListItemBidOrderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: OrderItem) {

            binding.tvBidOrderListItemProductImg.setImage(item.image)
            binding.tvBidOrderListItemProductName.text = item.title
            binding.tvBidOrderListItemOrderStatus.text = item.order_status_text
            binding.tvRateOrder.showView(item.order_status != 0)
            when(item.order_status) {

                0 -> {

                    binding.tvBidOrderListItemOrderStatus.setTextColor(context.getColor(R.color.orange))

                }

                1 ->  {

                    binding.tvBidOrderListItemOrderStatus.setTextColor(context.getColor(R.color.green))

                }

                2 -> {

                    binding.tvBidOrderListItemOrderStatus.setTextColor(context.getColor(R.color.red))

                }

            }

        }
    }


    fun deleteItem(position: Int) {

        listOfItems.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeRemoved(position, listOfItems.size)
    }


    fun setAdapterData (refreshContent: Boolean = true, newData: List<OrderItem>) {

        if (refreshContent) {

            setRefreshData(newData)

        } else {

            setData(newData)
        }

    }


    fun setRefreshData(newData: List<OrderItem>) {

        listOfItems.clear()
        listOfItems.addAll(newData)
        notifyDataSetChanged()
        Log.e("returned_data", "setRefreshData")

    }

    fun setData(newData: List<OrderItem>) {

        val oldList = this.listOfItems
        listOfItems.addAll(newData)
        val diffResult = DiffUtil.calculateDiff(OrderItemsDiffUtils(oldList, listOfItems))
        diffResult.dispatchUpdatesTo(this)

    }
}