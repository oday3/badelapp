package com.badl.apps.android.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.badl.apps.android.appFeatures.appCommon.data.DropdownItem
import com.badl.apps.android.appFeatures.main.data.SectionItem
import com.badl.apps.android.appFeatures.main.data.SubSectionItem
import com.badl.apps.android.databinding.SpinnerTextLayoutBinding



class TextSpinnerAdapter<T> (private val listOfItems: ArrayList<T>): BaseAdapter() {

    private lateinit var binding: SpinnerTextLayoutBinding
    override fun getCount(): Int {
        return listOfItems.size
    }

    override fun getItem(position: Int): T {


        return listOfItems[position] as T

    }

    override fun getItemId(position: Int): Long {

        when {

            listOfItems[position] is SectionItem -> {

                return (listOfItems[position] as SectionItem).id?.toLong() ?: -1

            }

            listOfItems[position] is SubSectionItem -> {

                return (listOfItems[position] as SubSectionItem).id?.toLong() ?: -1

            }

            listOfItems[position] is DropdownItem -> {

                return (listOfItems[position] as DropdownItem).id?.toLong() ?: -1

            }

            listOfItems[position] is String -> {

                return 0
            }

            else -> {

                return  -1
            }
        }
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        binding = SpinnerTextLayoutBinding.inflate(LayoutInflater.from(parent?.context), parent, false)

        when {

            listOfItems[position] is SectionItem -> {

                binding.text1.text = (listOfItems[position] as SectionItem).title

            }

            listOfItems[position] is SubSectionItem -> {

                binding.text1.text = (listOfItems[position] as SubSectionItem).title

            }


            listOfItems[position] is DropdownItem -> {

                binding.text1.text = (listOfItems[position] as DropdownItem).title

            }


            listOfItems[position] is String -> {

                binding.text1.text = (listOfItems[position] as String)
            }
        }


        return binding.root
    }

}