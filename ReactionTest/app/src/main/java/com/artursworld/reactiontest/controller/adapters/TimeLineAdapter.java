package com.artursworld.reactiontest.controller.adapters;


import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.artursworld.reactiontest.R;
import com.artursworld.reactiontest.controller.TimeLineItemClickListener;
import com.artursworld.reactiontest.controller.util.UtilsRG;
import com.artursworld.reactiontest.model.entity.ITimeLineItem;
import com.artursworld.reactiontest.model.entity.InOpEvent;
import com.artursworld.reactiontest.model.persistence.manager.InOpEventManager;

import java.util.List;

public class TimeLineAdapter extends RecyclerView.Adapter<TimeLineViewHolder>  {

    private List<ITimeLineItem> itemList;
    private Context mContext;
    private TimeLineItemClickListener listener;
    private Activity activity = null;

    public TimeLineAdapter(List<ITimeLineItem> feedList, TimeLineItemClickListener listener, Activity activity) {
        this.itemList = feedList;
        this.listener = listener;
        this.activity = activity;
    }

    @Override
    public TimeLineViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        mContext = parent.getContext();

        View view = View.inflate(parent.getContext(), R.layout.item_timeline, null);
        final ViewHolder mViewHolder = new TimeLineViewHolder(view, viewType);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(v, mViewHolder.getAdapterPosition());
            }
        });

        return (TimeLineViewHolder) mViewHolder;
    }

    @Override
    public void onBindViewHolder(TimeLineViewHolder holder, int position) {

        ITimeLineItem timeLineModel =  itemList.get(position);

        holder.name.setText(timeLineModel.getTimeLineLabel(activity));

    }

    @Override
    public int getItemCount() {
        return ( itemList!=null?  itemList.size():0);
    }
 }

