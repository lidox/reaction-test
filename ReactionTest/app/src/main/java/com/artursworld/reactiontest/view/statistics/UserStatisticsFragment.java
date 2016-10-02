package com.artursworld.reactiontest.view.statistics;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.artursworld.reactiontest.R;
import com.artursworld.reactiontest.controller.export.ExportViaJSON;
import com.artursworld.reactiontest.controller.util.UtilsRG;
import com.github.mikephil.charting.charts.BarChart;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserStatisticsFragment extends Fragment {


    public UserStatisticsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_statistics, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        BarChartView view = new BarChartView();
        view.initBarChartView(getActivity(), this.getView());
        //view.getView(getActivity(), inflater.inflate(R.layout.fragment_user_statistics, container, false));
    }
}
