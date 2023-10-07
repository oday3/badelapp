package com.badl.apps.android.baseClasses.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.badl.apps.android.R;


public class ProgressBarDialog extends AlertDialog {

    private final Context context;
    private int mainAppColor = -1;
    public ProgressBarDialog(Context context) {
        super(context);
        this.context = context;

//        TypedValue value = new TypedValue();
//        getContext().getTheme().resolveAttribute(R.attr.mainAppColor, value, true);
//       mainAppColor = value.data;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout mView = new LinearLayout(context);
        mView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mView.setGravity(Gravity.CENTER);
        mView.setElevation(5);
        ProgressBar progressBar = new ProgressBar(context);
        progressBar.setIndeterminateTintList(ColorStateList.valueOf(context.getColor(R.color.secondary_app_color)));
        mView.addView(progressBar);
        setContentView(mView);
        setCanceledOnTouchOutside(false);
        setCancelable(false);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().setDimAmount(0.0f);

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

}
