package com.artursworld.reactiontest.view.statistics;

import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.List;

public class BarDataSetGoGame extends BarDataSet {


    public BarDataSetGoGame(List<BarEntry> yVals, String label) {
        super(yVals, label);
    }

    @Override
    public int getColor(int index) {
        if(index < 2)
            return mColors.get(0);
        else
            return mColors.get(1);
    }

}
