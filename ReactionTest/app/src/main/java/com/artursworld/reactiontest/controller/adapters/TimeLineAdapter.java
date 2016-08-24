package com.artursworld.reactiontest.controller.adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.view.ViewGroup;

import com.artursworld.reactiontest.R;
import com.artursworld.reactiontest.controller.TimeLineItemClickListener;
import com.artursworld.reactiontest.model.entity.TimeLineModel;

import java.util.List;

public class TimeLineAdapter extends RecyclerView.Adapter<TimeLineViewHolder> {

    private List<TimeLineModel> itemList;
    private Context mContext;
    private TimeLineItemClickListener listener;

    public TimeLineAdapter(List<TimeLineModel> feedList, TimeLineItemClickListener listener) {
        this.itemList = feedList;
        this.listener = listener;
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

        TimeLineModel timeLineModel =  itemList.get(position);

        holder.name.setText(R.string.label + ": "+ timeLineModel.getLabel());

    }

    @Override
    public int getItemCount() {
        return ( itemList!=null?  itemList.size():0);
    }

}

