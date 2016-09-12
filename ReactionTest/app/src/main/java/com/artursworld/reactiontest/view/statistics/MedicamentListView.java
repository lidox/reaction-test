package com.artursworld.reactiontest.view.statistics;


import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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

                /*
                android.app.FragmentManager fm = activity.getFragmentManager();
                android.app.Fragment fragment = fm.findFragmentByTag("myFragmentTag");
                if (fragment == null) {
                    android.app.FragmentTransaction ft = fm.beginTransaction();
                    fragment =new MyFragment();
                    ft.add(android.R.id.content,fragment,"myFragmentTag");
                    ft.commit();
                */
                view = inflater.inflate(R.layout.medicament_list_view, null);//medicament_list_view
            }
        }

        return view;
    }
}
