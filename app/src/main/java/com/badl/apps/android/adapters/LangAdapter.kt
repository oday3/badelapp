package com.badl.apps.android.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.badl.apps.android.appFeatures.appCommon.data.LanguageItem
import com.badl.apps.android.databinding.ListItemLanguageSprBinding


class LangAdapter(context: Context, resourceId: Int, private val arrayList: List<LanguageItem>):
    ArrayAdapter<LanguageItem>(context, resourceId, arrayList) {

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {

        val holder: ListItemLanguageSprBinding

        var rowView = convertView
        if (rowView == null) {
            holder = ListItemLanguageSprBinding.inflate(LayoutInflater.from(context), parent, false)
            rowView = holder.root
            rowView.tag = holder
        } else {

            holder = convertView?.tag as ListItemLanguageSprBinding
        }

        val item = arrayList[position]
        holder.imgImage.setImageResource(item.resImage)
        holder.imgArrow.visibility = View.GONE
        holder.tvTitle.visibility = View.VISIBLE
        holder.tvTitle.text = item.name
        return rowView
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val holder: ListItemLanguageSprBinding

        var rowView = convertView
        if (rowView == null) {
            holder = ListItemLanguageSprBinding.inflate(LayoutInflater.from(context), parent, false)
            rowView = holder.root
            rowView.tag = holder
        } else {

            holder = convertView?.tag as ListItemLanguageSprBinding
        }

        val item = arrayList[position]
        holder.imgImage.setImageResource(item.resImage)
        holder.imgArrow.visibility = View.VISIBLE
        holder.tvTitle.visibility = View.VISIBLE
        return rowView
    }
}