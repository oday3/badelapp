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
import com.badl.apps.android.databinding.ListItemProductSectionFullBinding
import com.badl.apps.android.databinding.ListItemProductSectionHorizantalBinding
import com.badl.apps.android.utils.AdItemsDiffUtils
import com.badl.apps.android.utils.UiUtils.setImage
import com.badl.apps.android.utils.UiUtils.setPrice
import com.badl.apps.android.utils.UiUtils.showView

class AdListAdapter(
    val context: Context,
    private val clickAction: ((productId: Int) -> Unit)?,
    private val favAction: ((position: Int) -> Unit)?,
    private val mLayoutManager: GridLayoutManager,  val userType: Int
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var listOfItems = ArrayList<ProductSectionItem>()


    private val VIEW_TYPE_GRID = 1
    private val VIEW_TYPE_LINE = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

//
//        val view: View = if (viewType == VIEW_TYPE_LINE) {
//            LayoutInflater.from(parent.context).inflate(R.layout.list_item_product_section_horizantal, parent, false)
//        } else {
//            LayoutInflater.from(parent.context).inflate(R.layout.list_item_product_section_full, parent, false)
//        }
//
//        return ViewHolder(view, viewType)

        return if (viewType == VIEW_TYPE_LINE) {

            val item = ListItemProductSectionHorizantalBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)

            LinearViewHolder(item)

        } else {

            val item = ListItemProductSectionFullBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)

            GridViewHolder(item)
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {


//        (holder as ViewHolder).bind(listOfItems[holder.adapterPosition])
//
//        holder.favImage.setOnClickListener {
//
//            favAction?.let { it1 ->
//                it1(
//                    holder.adapterPosition,
//                    listOfItems[holder.adapterPosition]
//                )
//            }
//        }
        if (holder.itemViewType == VIEW_TYPE_LINE) {

            (holder as LinearViewHolder).bindInfo(listOfItems[position])
            holder.binding.model = listOfItems[position]
            holder.binding.imgFavIcon.setOnClickListener {

                favAction?.let { it1 ->
                    it1(
                        holder.adapterPosition,
                    )
                }

            }
            holder.binding.linear.showView(userType != 2)

        } else {

            (holder as GridViewHolder).bindInfo(listOfItems[position])
            holder.binding.model = listOfItems[position]
            holder.binding.linear.showView(userType != 2)



            holder.binding.imgFavIcon.setOnClickListener {

                favAction?.let { it1 ->
                    it1(
                        holder.adapterPosition,
                    )
                }
            }
        }


        holder.itemView.setOnClickListener {

            clickAction?.let { it1 ->
                it1(
                   listOfItems[ holder.adapterPosition].id ?: -1,
                )
            }
        }
    }

    override fun getItemCount(): Int {
        return listOfItems.size
    }


    inner class LinearViewHolder(var binding: ListItemProductSectionHorizantalBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindInfo(item: ProductSectionItem) {

            binding.imgProductSectionItemProductImg.setImage(item.image)
            binding.tvProductSectionItemCloseDate.text = item.remaining_days
            binding.tvProductSectionItemNumOfViews.text = item.views.toString()
            binding.tvProductSectionItemProductName.text = item.title
            binding.tvProductSectionItemDesc.text = item.description
            binding.tvProductSectionItemPrice.setPrice(item.price)

            if (item.is_favorite == true) {

                binding.imgFavIcon.setImageResource(R.drawable.ic_fav_fill)
            } else {
                binding.imgFavIcon.setImageResource(R.drawable.ic_unfav)

            }

            when (item.ad_type_id) {

                1 -> {

                    binding.linear.setBackgroundResource(R.drawable.bg_bid_now)
                    binding.tvProductSectionItemUserActionLabel.text =
                        context.getString(R.string.bid_now)
                    binding.tvProductSectionItemUserActionLabel.setTextColor(context.getColor(R.color.secondary_app_color))
                    binding.tvProductSectionItemPrice.setTextColor(context.getColor(R.color.secondary_app_color))

                }

                2 -> {


                    binding.linear.setBackgroundResource(R.drawable.bg_buy_now)
                    binding.tvProductSectionItemUserActionLabel.text =
                        context.getString(R.string.swap_now)
                    binding.tvProductSectionItemUserActionLabel.setTextColor(context.getColor(R.color.main_app_color))
                    binding.tvProductSectionItemPrice.setTextColor(context.getColor(R.color.main_app_color))

                }

                3 -> {


                    binding.linear.setBackgroundResource(R.drawable.bg_buy_now)
                    binding.tvProductSectionItemUserActionLabel.text =
                        context.getString(R.string.buy_now)
                    binding.tvProductSectionItemUserActionLabel.setTextColor(context.getColor(R.color.main_app_color))
                    binding.tvProductSectionItemPrice.setTextColor(context.getColor(R.color.main_app_color))
                }

                4 -> {

                }


            }

        }
    }


    inner class GridViewHolder(var binding: ListItemProductSectionFullBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindInfo(item: ProductSectionItem) {

            if (item.is_favorite == true) {

                binding.imgFavIcon.setImageResource(R.drawable.ic_fav_fill)
            } else {
                binding.imgFavIcon.setImageResource(R.drawable.ic_unfav)

            }
            binding.imgProductSectionItemProductImg.setImage(item.image)
            binding.tvProductSectionItemCloseDate.text = item.remaining_days
            binding.tvProductSectionItemNumOfViews.text = item.views.toString()
            binding.tvProductSectionItemProductName.text = item.title
            binding.tvProductSectionItemDesc.text = item.description
            binding.tvProductSectionItemPrice.setPrice(item.price)

            when (item.ad_type_id) {

                1 -> {

                    binding.linear.setBackgroundResource(R.drawable.bg_bid_now)
                    binding.tvProductSectionItemUserActionLabel.text =
                        context.getString(R.string.bid_now)
                    binding.tvProductSectionItemUserActionLabel.setTextColor(context.getColor(R.color.secondary_app_color))
                    binding.tvProductSectionItemPrice.setTextColor(context.getColor(R.color.secondary_app_color))

                }

                2 -> {

                    binding.tvProductSectionItemUserActionLabel.text =
                        context.getString(R.string.swap_now)

                }

                3 -> {


                    binding.linear.setBackgroundResource(R.drawable.bg_buy_now)
                    binding.tvProductSectionItemUserActionLabel.text =
                        context.getString(R.string.buy_now)
                    binding.tvProductSectionItemUserActionLabel.setTextColor(context.getColor(R.color.main_app_color))
                    binding.tvProductSectionItemPrice.setTextColor(context.getColor(R.color.main_app_color))
                }

                4 -> {

                }
            }

        }
    }

//
//    inner class ViewHolder(var binding: View, val viewType: Int) :
//        RecyclerView.ViewHolder(binding) {
//
//        var adImage = binding.findViewById<ImageView>(R.id.img_productSectionItem_productImg)
//        var favImage = binding.findViewById<ImageView>(R.id.img_favIcon)
//        var favProgressbar = binding.findViewById<ProgressBar>(R.id.progress_productSectionItem_favProgress)
//        var numOfViews = binding.findViewById<TextView>(R.id.tv_productSectionItem_numOfViews)
//        var endDate = binding.findViewById<TextView>(R.id.tv_productSectionItem_closeDate)
//        var adName = binding.findViewById<TextView>(R.id.tv_productSectionItem_productName)
//        var adDescription = binding.findViewById<TextView>(R.id.tv_productSectionItem_desc)
//        var actionLabel = binding.findViewById<TextView>(R.id.tv_productSectionItem_userActionLabel)
//        var actionView = binding.findViewById<LinearLayout>(R.id.linear)
//        var price = binding.findViewById<TextView>(R.id.tv_productSectionItem_price)
//
//
//        fun bind(item: ProductSectionItem) {
//
//            adName.text = item.title
//            adDescription.text = item.description
//            endDate.text = item.end_date
//            numOfViews.text = item.views.toString()
//            price.setPrice(item.price)
//            adImage.setImage(item.image)
//
//            if (item.is_favorite == true) {
//
//                favImage.setImageResource(R.drawable.ic_fav_fill)
//
//            } else {
//
//                favImage.setImageResource(R.drawable.ic_unfav)
//            }
//
//            when(item.ad_type_id) {
//
//                1 -> {
//
//                    actionView.setBackgroundResource(R.drawable.bg_bid_now)
//                    actionLabel.text = context.getString(R.string.bid_now)
//                    actionLabel.setTextColor(context.getColor(R.color.secondary_app_color))
//                    price.setTextColor(context.getColor(R.color.secondary_app_color))
//
//                }
//
//                2 -> {
//
//                    actionView.setBackgroundResource(R.drawable.bg_bid_now)
//                    actionLabel.text = context.getString(R.string.swap_now)
//
//                }
//
//                3 -> {
//
//
//                    actionView.setBackgroundResource(R.drawable.bg_buy_now)
//                    actionLabel.text = context.getString(R.string.buy_now)
//                    actionLabel.setTextColor(context.getColor(R.color.main_app_color))
//                   price.setTextColor(context.getColor(R.color.main_app_color))
//                }
//
//                4 -> {
//
//                }
//
//
//            }
//        }
//    }

    fun setData(newData: List<ProductSectionItem>) {

        val oldList = this.listOfItems
        listOfItems.addAll(newData)
        val diffResult = DiffUtil.calculateDiff(AdItemsDiffUtils(oldList, listOfItems))
        diffResult.dispatchUpdatesTo(this)

    }


    fun setAdapterData (refreshContent: Boolean = true, newData: List<ProductSectionItem>) {

        if (refreshContent) {

            setRefreshData(newData)

        } else {

            setData(newData)
        }

    }

    fun setRefreshData(newData: List<ProductSectionItem>) {

        listOfItems.clear()
        listOfItems.addAll(newData)
        notifyDataSetChanged()
        Log.e("returned_data", "setRefreshData")

    }


    override fun getItemViewType(position: Int): Int {
        val spanCount = mLayoutManager.spanCount
        return if (spanCount == 1) {
            VIEW_TYPE_LINE
        } else {
            VIEW_TYPE_GRID
        }
    }

    fun getItem(position: Int): ProductSectionItem {

        return listOfItems[position]
    }

    fun getItems(): List<ProductSectionItem> {

        return listOfItems
    }

    fun refreshItem(itemPosition: Int) {

        notifyItemChanged(itemPosition, listOfItems[itemPosition])
    }


    fun setItem(itemPosition: Int, data: ProductSectionItem) {

        listOfItems[itemPosition] = data
    }
}