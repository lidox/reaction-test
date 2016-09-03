package com.artursworld.reactiontest.controller.adapters;


import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.artursworld.reactiontest.R;

import java.util.List;

public class CustomSpinnerAdapter extends BaseAdapter implements SpinnerAdapter {

    private final Context activity;
    private List<String> itemList;

    public CustomSpinnerAdapter(Context context,List<String> asr) {
        this.itemList=asr;
        activity = context;
    }



    public int getCount()
    {
        return itemList.size();
    }

    public Object getItem(int i)
    {
        return itemList.get(i);
    }

    public long getItemId(int i)
    {
        return (long)i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView txt = new TextView(activity.getApplicationContext());
        txt.setGravity(Gravity.CENTER);
        txt.setPadding(16, 16, 16, 16);
        txt.setTextSize(16);
        txt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_down, 0);
        txt.setText(itemList.get(position));
        txt.setTextColor(ContextCompat.getColor(activity.getApplicationContext(), R.color.colorPrimary));
        return  txt;
    }


    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView txt = new TextView(activity.getApplicationContext());
        txt.setPadding(16, 16, 16, 16);
        txt.setTextSize(18);
        txt.setGravity(Gravity.CENTER_VERTICAL);
        txt.setText(itemList.get(position));
        txt.setTextColor(Color.parseColor("#000000"));
        return  txt;
    }


}

