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
import com.badl.apps.android.appFeatures.main.data.ProductSectionItem
import com.badl.apps.android.appFeatures.userAccount.data.TransactionItem
import com.badl.apps.android.databinding.ListItemProductSectionFullBinding
import com.badl.apps.android.databinding.ListItemProductSectionHorizantalBinding
import com.badl.apps.android.databinding.ListItemTransactionBinding
import com.badl.apps.android.utils.AdItemsDiffUtils
import com.badl.apps.android.utils.TransactionItemsDiffUtils
import com.badl.apps.android.utils.UiUtils.setImage
import com.badl.apps.android.utils.UiUtils.setPrice
import com.badl.apps.android.utils.UiUtils.showView

class TransactionsAdapter(
    val context: Context,
    private val chatAction: ((item: TransactionItem) -> Unit)?,
    private val clickAction: ((item: TransactionItem) -> Unit)?,
) : RecyclerView.Adapter<TransactionsAdapter.ViewHolder>() {

    private var listOfItems = ArrayList<TransactionItem>()


    private val VIEW_TYPE_GRID = 1
    private val VIEW_TYPE_LINE = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

//
//        val view: View = if (viewType == VIEW_TYPE_LINE) {
//            LayoutInflater.from(parent.context).inflate(R.layout.list_item_product_section_horizantal, parent, false)
//        } else {
//            LayoutInflater.from(parent.context).inflate(R.layout.list_item_product_section_full, parent, false)
//        }
//
//        return ViewHolder(view, viewType)


        val item = ListItemTransactionBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )


        return ViewHolder(item)


    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        holder.bindInfo(listOfItems[position])

        holder.binding.imgChat.setOnClickListener {

            chatAction?.let { it1 -> it1(listOfItems[holder.adapterPosition]) }
        }

        holder.binding.root.setOnClickListener {

            if(listOfItems[holder.adapterPosition].ad_type == 2) {

                clickAction?.let { it1 -> it1(listOfItems[holder.adapterPosition]) }
            }
        }
    }

    override fun getItemCount(): Int {
        return listOfItems.size
    }


    inner class ViewHolder(var binding: ListItemTransactionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindInfo(item: TransactionItem) {

            binding.imgProductImg.setImage(item.image)
            binding.tvProductName.text = item.title
            binding.tvFrom.text = item.user_name

            when (item.ad_type) {

                1 -> {

                    binding.constSwapTransactionData.showView(false)
                    binding.constBidTransactionData.showView(true)
                    binding.constBuyTransactionData.showView(false)
                    binding.constCharityTransactionData.showView(false)

                    binding.tvBidTransactionPrice.setPrice(item.price)
                    binding.tvTvBidTransactionDate.text = item.order_date

                    binding.tvTypeDescLabel.text = context.getString(R.string.bidder)

                }

                2 -> {


                    binding.constSwapTransactionData.showView(true)
                    binding.constBidTransactionData.showView(false)
                    binding.constBuyTransactionData.showView(false)
                    binding.constCharityTransactionData.showView(false)


                    binding.tvSwapProduct.text = item.exchange_ad?.title
                    binding.tvSwapPrice.setPrice(item.price)
                    binding.tvTvSwapTransactionDate.text = item.order_date

                    binding.tvTypeDescLabel.text = context.getString(R.string.swapper)

                }

                3 -> {


                    binding.constSwapTransactionData.showView(false)
                    binding.constBidTransactionData.showView(false)
                    binding.constBuyTransactionData.showView(true)
                    binding.constCharityTransactionData.showView(false)


                    binding.tvBuyQuantity.text = "X${item.quantity}"
                    binding.tvBuyTransactionPrice.setPrice(item.price)
                    binding.tvTvBuyTransactionDate.text = item.order_date

                    binding.tvTypeDescLabel.text = context.getString(R.string.buyer)

                }

                4 -> {


                    binding.constSwapTransactionData.showView(false)
                    binding.constBidTransactionData.showView(false)
                    binding.constBuyTransactionData.showView(false)
                    binding.constCharityTransactionData.showView(true)

                    binding.tvCharityQuantity.text = "X${item.quantity}"
                    binding.tvTvCharityTransactionDate.text = item.order_date
                    
                    binding.tvTypeDescLabel.text = context.getString(R.string.association)

                }
            }
        }
    }


    fun setData(newData: List<TransactionItem>) {

        val oldList = this.listOfItems
        listOfItems.addAll(newData)
        val diffResult = DiffUtil.calculateDiff(TransactionItemsDiffUtils(oldList, listOfItems))
        diffResult.dispatchUpdatesTo(this)

    }


    fun setAdapterData(refreshContent: Boolean = true, newData: List<TransactionItem>) {

        if (refreshContent) {

            setRefreshData(newData)

        } else {

            setData(newData)
        }

    }

    fun setRefreshData(newData: List<TransactionItem>) {

        listOfItems.clear()
        listOfItems.addAll(newData)
        notifyDataSetChanged()
        Log.e("returned_data", "setRefreshData")

    }


    fun getItem(position: Int): TransactionItem {

        return listOfItems[position]
    }

    fun getItems(): List<TransactionItem> {

        return listOfItems
    }

    fun refreshItem(itemPosition: Int) {

        notifyItemChanged(itemPosition, listOfItems[itemPosition])
    }


    fun setItem(itemPosition: Int, data: TransactionItem) {

        listOfItems[itemPosition] = data
    }
}