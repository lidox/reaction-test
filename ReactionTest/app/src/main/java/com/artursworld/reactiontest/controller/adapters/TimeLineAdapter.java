package com.artursworld.reactiontest.controller.adapters;


import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.artursworld.reactiontest.R;
import com.artursworld.reactiontest.controller.helper.Orientation;
import com.artursworld.reactiontest.model.entity.TimeLineModel;
import com.vipul.hp_hp.timelineview.TimelineView;

import java.util.List;

public class TimeLineAdapter extends RecyclerView.Adapter<TimeLineViewHolder>{

    private List<TimeLineModel> mFeedList;
    private Context mContext;
    private Orientation mOrientation;

    public TimeLineAdapter(List<TimeLineModel> feedList, Orientation orientation) {
        mFeedList = feedList;
        mOrientation = orientation;
    }

    @Override
    public TimeLineViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        mContext = parent.getContext();

        View view = View.inflate(parent.getContext(), R.layout.item_timeline, null);

        return new TimeLineViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(TimeLineViewHolder holder, int position) {

        TimeLineModel timeLineModel = mFeedList.get(position);

        holder.name.setText(R.string.label + ": "+ timeLineModel.getLabel());

    }

    @Override
    public int getItemCount() {
        return (mFeedList!=null? mFeedList.size():0);
    }
}

