package com.badl.apps.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.badl.apps.android.R;
import com.badl.apps.android.appFeatures.appCommon.data.LanguageItem;
import com.badl.apps.android.databinding.ListItemLanguageSprBinding;

import java.util.List;

public class LanguageAdapter extends ArrayAdapter<LanguageItem> {

    private final Context context;
    private final List<LanguageItem> languageList;

    public LanguageAdapter(Context context, List<LanguageItem> languageList) {
        super(context, R.layout.list_item_language_spr, R.id.tv_title, languageList);
        this.context = context;
        this.languageList = languageList;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        View rowView = convertView;
        if (rowView == null) {
            holder = new ViewHolder();
            holder.binding = ListItemLanguageSprBinding.inflate(LayoutInflater.from(context), parent, false);
            rowView = holder.binding.getRoot();
            rowView.setTag(holder);
        } else {
            holder = (ViewHolder) rowView.getTag();
        }

        LanguageItem item = languageList.get(position);

        //holder.binding.tvTitle.setText(item.getName());
        holder.binding.imgImage.setImageResource(item.getResImage());
        holder.binding.imgArrow.setVisibility(View.GONE);

        return rowView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        View rowView = convertView;
        if (rowView == null) {
            holder = new ViewHolder();
            holder.binding = ListItemLanguageSprBinding.inflate(LayoutInflater.from(context), parent, false);
            rowView = holder.binding.getRoot();
            rowView.setTag(holder);
        } else {
            holder = (ViewHolder) rowView.getTag();
        }

        LanguageItem item = languageList.get(position);

        //holder.binding.tvTitle.setText(item.getCode());
        holder.binding.imgImage.setImageResource(item.getResImage());
        holder.binding.imgArrow.setVisibility(View.VISIBLE);

        return rowView;
    }

    static class ViewHolder {
        public ListItemLanguageSprBinding binding;
    }
}
