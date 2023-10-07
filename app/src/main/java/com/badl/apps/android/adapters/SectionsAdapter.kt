package com.badl.apps.android.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.badl.apps.android.appFeatures.main.data.SectionItem
import com.badl.apps.android.appFeatures.main.ui.AdListActivity
import com.badl.apps.android.databinding.ListItemSectionsBinding
import com.badl.apps.android.utils.AppUtil.getIntentExtension
import com.badl.apps.android.utils.Constants
import com.badl.apps.android.utils.UiUtils.setImage


class SectionsAdapter(val context: Context) : RecyclerView.Adapter<SectionsAdapter.ViewHolder>() {

    private val listOfItems by lazy { ArrayList<SectionItem>() }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val item = ListItemSectionsBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(item)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listOfItems[position])

        holder.binding.root.setOnClickListener {


            (context as AppCompatActivity).getIntentExtension(AdListActivity::class.java).run {

                putExtra(Constants.TOOLBAR_TITLE, listOfItems[holder.adapterPosition].title)
                putExtra(Constants.SECTION_ID, listOfItems[holder.adapterPosition].id)
                putExtra(Constants.FROM, Constants.FROM_SECTION)
                context.startActivity(this)
            }
        }
    }

    override fun getItemCount(): Int {
        return listOfItems.size
    }

    inner class ViewHolder(var binding: ListItemSectionsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SectionItem) {

            binding.imgCategoryHHH.setImage(item.image)
            binding.tvCategoryName.text = item.title

        }
    }

    fun setData(newData: List<SectionItem>) {

        listOfItems.clear()
        listOfItems.addAll(newData)
        notifyDataSetChanged()
    }
}