package com.artursworld.reactiontest.controller.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.artursworld.reactiontest.R;
import com.vipul.hp_hp.timelineview.TimelineView;

public class TimeLineViewHolder extends RecyclerView.ViewHolder {
    public TextView name;
    public  TimelineView mTimelineView;

    public TimeLineViewHolder(View itemView, int viewType) {
        super(itemView);
        name = (TextView) itemView.findViewById(R.id.tx_name);
        mTimelineView = (TimelineView) itemView.findViewById(R.id.time_marker);
        mTimelineView.initLine(viewType);
    }
}
