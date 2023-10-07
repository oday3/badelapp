package com.badl.apps.android.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.badl.apps.android.R
import com.badl.apps.android.appFeatures.main.data.ProductSectionItem
import com.badl.apps.android.databinding.ListItemProductSectionBinding
import com.badl.apps.android.databinding.ListItemProductSectionFullBinding
import com.badl.apps.android.utils.UiUtils.setImage
import com.badl.apps.android.utils.UiUtils.setPrice
import com.badl.apps.android.utils.UiUtils.showView


class ProductSectionAdapter(val context: Context,
                            private val clickAction: ((productId: Int) -> Unit)?,
                            private val favAction: ((position: Int, adapter: ProductSectionAdapter) -> Unit)?,
                            val orientation: String, val userType: Int) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val listOfItems by lazy { ArrayList<ProductSectionItem>() }
    private val viewPool = RecyclerView.RecycledViewPool()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {


        return if (viewType == 1) {

            val item = ListItemProductSectionBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)

            HorizontalViewHolder(item)

        } else {

            val item = ListItemProductSectionFullBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)

            VerticalViewHolder(item)
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder.itemViewType == 1) {

            (holder as HorizontalViewHolder).bindInfo(listOfItems[position])

            holder.binding.model = listOfItems[position]
            holder.binding.imgFavIcon.setOnClickListener {

                favAction?.let { it1 -> it1(holder.adapterPosition,  this) }

            }

            holder.binding.linear.showView(userType != 2)

        } else {

            (holder as VerticalViewHolder).bind(listOfItems[position])

            holder.binding.model = listOfItems[position]

            holder.binding.imgFavIcon.setOnClickListener {

                favAction?.let { it1 -> it1(holder.adapterPosition,  this) }

            }

            holder.binding.linear.showView(userType != 2)

        }

        holder.itemView.setOnClickListener {



                clickAction?.let { it1 -> it1(listOfItems[holder.adapterPosition].id ?: -1) }

        }


    }

    override fun getItemCount(): Int {
        return listOfItems.size
    }

    inner class VerticalViewHolder(var binding: ListItemProductSectionFullBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ProductSectionItem) {

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

                when(item.ad_type_id) {

                    1 -> {

                        binding.linear.setBackgroundResource(R.drawable.bg_bid_now)
                        binding.tvProductSectionItemUserActionLabel.text = context.getString(R.string.bid_now)
                        binding.tvProductSectionItemUserActionLabel.setTextColor(context.getColor(R.color.secondary_app_color))
                        binding.tvProductSectionItemPrice.setTextColor(context.getColor(R.color.secondary_app_color))

                    }

                    2 -> {

                        binding.tvProductSectionItemUserActionLabel.text = context.getString(R.string.swap_now)

                    }

                    3 -> {


                        binding.linear.setBackgroundResource(R.drawable.bg_buy_now)
                        binding.tvProductSectionItemUserActionLabel.text = context.getString(R.string.buy_now)
                        binding.tvProductSectionItemUserActionLabel.setTextColor(context.getColor(R.color.main_app_color))
                        binding.tvProductSectionItemPrice.setTextColor(context.getColor(R.color.main_app_color))
                    }

                    4 -> {

                    }


                }

            }
        }


    inner class HorizontalViewHolder(var binding: ListItemProductSectionBinding) :
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

            when(item.ad_type_id) {

                1 -> {

                    binding.linear.setBackgroundResource(R.drawable.bg_bid_now)
                    binding.tvProductSectionItemUserActionLabel.text = context.getString(R.string.bid_now)
                    binding.tvProductSectionItemUserActionLabel.setTextColor(context.getColor(R.color.secondary_app_color))
                    binding.tvProductSectionItemPrice.setTextColor(context.getColor(R.color.secondary_app_color))

                }

                2 -> {

                }

                3 -> {


                    binding.linear.setBackgroundResource(R.drawable.bg_buy_now)
                    binding.tvProductSectionItemUserActionLabel.text = context.getString(R.string.buy_now)
                    binding.tvProductSectionItemUserActionLabel.setTextColor(context.getColor(R.color.main_app_color))
                    binding.tvProductSectionItemPrice.setTextColor(context.getColor(R.color.main_app_color))
                }

                4 -> {

                }


            }

        }
    }

    fun setData(newData: List<ProductSectionItem>) {

        listOfItems.clear()
        listOfItems.addAll(newData)
        notifyDataSetChanged()
    }


    fun getItem(itemPosition: Int): ProductSectionItem {

        return listOfItems[itemPosition]
    }

    fun setItem(itemPosition: Int, data: ProductSectionItem) {

         listOfItems[itemPosition] = data
    }


    fun refreshItem(itemPosition: Int) {

        notifyItemChanged(itemPosition, listOfItems[itemPosition])

    }

    override fun getItemViewType(position: Int): Int {

        return if (orientation == "1") {

            1
        } else {
            2
        }
    }
}