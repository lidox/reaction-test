package com.artursworld.reactiontest.model.entity;


import android.app.Activity;

import java.util.Date;

public interface ITimeLineItem extends Comparable<ITimeLineItem> {
    public String getTimeLineLabel(Activity activity);
    public Date getTimeStamp();
    //public String getType();

    @Override
    int compareTo(ITimeLineItem another);
}
