package com.badl.apps.android.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.badl.apps.android.R
import com.badl.apps.android.appFeatures.biddingAndBarterSystem.data.SellerAdItem
import com.badl.apps.android.appFeatures.main.data.ProductSectionItem
import com.badl.apps.android.databinding.ListItemProductSectionHorizantalBinding
import com.badl.apps.android.databinding.ListItemSellerAdBinding
import com.badl.apps.android.utils.UiUtils.setImage
import com.badl.apps.android.utils.UiUtils.setPrice


class SellerActiveAdsAdapter(val context: Context,
                             private val favAction: ((position: Int, item: SellerAdItem) -> Unit)?,
) : RecyclerView.Adapter<SellerActiveAdsAdapter.ViewHolder>() {

    private val listOfItems by lazy { ArrayList<SellerAdItem>() }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val item = ListItemSellerAdBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(item)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listOfItems[position])
        holder.binding.model = listOfItems[position]

        holder.binding.root.setOnClickListener {

        }

        holder.binding.imgFavIcon.setOnClickListener {

            favAction?.let { it1 -> it1(holder.adapterPosition,  listOfItems[holder.adapterPosition]) }

        }
    }

    override fun getItemCount(): Int {
        return listOfItems.size
    }

    inner class ViewHolder(var binding: ListItemSellerAdBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SellerAdItem) {


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

    fun setData(newData: List<SellerAdItem>) {

        listOfItems.clear()
        listOfItems.addAll(newData)
        notifyDataSetChanged()
    }


    fun getItem(itemPosition: Int): SellerAdItem {

        return listOfItems[itemPosition]
    }



    fun refreshItem(itemPosition: Int) {

        notifyItemChanged(itemPosition, listOfItems[itemPosition])

    }
}