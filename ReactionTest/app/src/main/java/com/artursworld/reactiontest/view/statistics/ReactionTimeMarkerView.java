package com.artursworld.reactiontest.view.statistics;


import android.content.Context;
import android.widget.TextView;

import com.artursworld.reactiontest.R;
import com.artursworld.reactiontest.controller.util.Strings;
import com.artursworld.reactiontest.controller.util.UtilsRG;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;

public class ReactionTimeMarkerView extends MarkerView {

    private TextView tvContent;

    public ReactionTimeMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);
        tvContent = (TextView) findViewById(R.id.tvContent);
    }

    // callbacks every time the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        String reactionTime = e.getData().toString();
        double rt = Double.parseDouble(reactionTime);


        try {
            reactionTime = String.valueOf( (int)(rt * 1000));
            reactionTime = String.format("%.1f", reactionTime);
        }
        catch (Exception ex){
            UtilsRG.info("could not format label so use default");
        }
        tvContent.setText(reactionTime + " " + Strings.getStringByRId(R.string.milliseconds)); // set the entry-value as the display text
    }

    @Override
    public int getXOffset(float xpos) {
        // this will center the marker-view horizontally
        return -(getWidth() / 2);
    }

    @Override
    public int getYOffset(float ypos) {
        // this will cause the marker-view to be above the selected value
        return -getHeight();
    }

}