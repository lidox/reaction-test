package com.artursworld.reactiontest.view.statistics;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.artursworld.reactiontest.R;

public class MedicamentListView {

    /**
     * Returns the view to display information about user's operation
     */
    public View getView(final Activity activity, View rootView) {
        View view = null;
        LayoutInflater inflater;
        Context context = activity.getApplicationContext();

        if (activity != null && context != null) {
            if (view == null) {
                inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.medicament_list_view, null);
            }
        }

        return view;
    }
}
