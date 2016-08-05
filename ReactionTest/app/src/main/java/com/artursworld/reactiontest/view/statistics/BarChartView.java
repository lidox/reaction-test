package com.artursworld.reactiontest.view.statistics;


import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.artursworld.reactiontest.R;
import com.artursworld.reactiontest.controller.helper.Type;
import com.artursworld.reactiontest.controller.util.UtilsRG;
import com.artursworld.reactiontest.model.persistence.manager.ReactionGameManager;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.AxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import java.util.ArrayList;
import java.util.List;

public class BarChartView {

    private Activity activity;
    private BarChart barChart;

    public View getView(final Activity activity, View rootView){
        this.activity = activity;
        View view = null;
        LayoutInflater inflater;
        Context context = activity.getApplicationContext();
        onOperationIssueChange(rootView);


        if (context != null) {
            if (view == null && context !=null && activity !=null) {
                inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.statistics_view, null);
            }

            this.barChart = (BarChart) view.findViewById(R.id.chart);
            UtilsRG.info("chart init: " + barChart);
            initBarChartConfiguration(activity, barChart);
            initBarChartData(activity, barChart);
        }
        return view;
    }

    private void onOperationIssueChange(View rootView) {
        if( rootView != null){
            Spinner operationIssueSpinner = (Spinner) rootView.findViewById(R.id.details_fragment_toolbar_operation_issue_spinner);
            operationIssueSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if( activity != null && barChart !=null){
                        String selectedOperationIssue = parent.getItemAtPosition(position).toString();
                        UtilsRG.putString(UtilsRG.OPERATION_ISSUE, selectedOperationIssue, activity);
                        UtilsRG.info("Selected OperationIssue changed in statistics to: " + selectedOperationIssue);
                        initBarChartData(activity, barChart);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }

    private void initBarChartData(Activity activity, BarChart barChart) {
        //data: (0.46 + 0.02) * 2 + 0.04 = 1.00 -> interval per "group"
        float groupSpace = 0.04f;
        float barSpace = 0.02f;
        float barWidth = 0.46f;

        String selectedOperationIssue = UtilsRG.getStringByKey(UtilsRG.OPERATION_ISSUE, activity);
        List<BarEntry> yValsGoGame = getAverageValuesFromDB(activity, selectedOperationIssue, Type.getGameType(Type.GameTypes.GoGame));
        List<BarEntry> yValsGoNoGoGame = getAverageValuesFromDB(activity, selectedOperationIssue, Type.getGameType(Type.GameTypes.GoNoGoGame));

        BarDataSet set1, set2;

        if (barChart.getData() != null && barChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet)barChart.getData().getDataSetByIndex(0);
            set2 = (BarDataSet)barChart.getData().getDataSetByIndex(1);
            set1.setValues(yValsGoGame);
            set2.setValues(yValsGoNoGoGame);
            barChart.getData().notifyDataChanged();
            barChart.notifyDataSetChanged();
        } else {
            // create 2 datasets with different types
            set1 = new BarDataSet(yValsGoGame, activity.getResources().getString(R.string.go_game));
            set1.setColor(ContextCompat.getColor(activity.getApplicationContext(), R.color.colorAccentMiddle));
            set2 = new BarDataSet(yValsGoNoGoGame, activity.getResources().getString(R.string.go_no_go_game));
            set2.setColor(ContextCompat.getColor(activity.getApplicationContext(), R.color.colorPrimaryLight));

            List<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
            dataSets.add(set1);
            dataSets.add(set2);

            BarData data = new BarData(dataSets);
            barChart.setData(data);
        }

        barChart.getBarData().setBarWidth(barWidth);
        barChart.getXAxis().setAxisMinValue(0);
        barChart.groupBars(0, groupSpace, barSpace);
        barChart.invalidate();
    }

    private List<BarEntry> getAverageValuesFromDB(Activity activity, String selectedOperationIssue, String gameType) {
        List<BarEntry> yValues = new ArrayList<>();
        if(selectedOperationIssue != null){
            double averagePreOperationValue = new ReactionGameManager(activity).getFilteredReactionGames(selectedOperationIssue, gameType, Type.getTestType(Type.TestTypes.PreOperation), "AVG");
            double averageInOperationValue = new ReactionGameManager(activity).getFilteredReactionGames(selectedOperationIssue, gameType, Type.getTestType(Type.TestTypes.InOperation), "AVG");
            double averagePostOperationValue = new ReactionGameManager(activity).getFilteredReactionGames(selectedOperationIssue,gameType,  Type.getTestType(Type.TestTypes.PostOperation), "AVG");

            // pre in post
            yValues.add(new BarEntry(0, (float) averagePreOperationValue));
            yValues.add(new BarEntry(1, (float) averageInOperationValue));
            yValues.add(new BarEntry(2, (float) averagePostOperationValue));
            yValues.add(new BarEntry(3, 0f)); // dummy
        }
        return  yValues;
    }

    private void initBarChartConfiguration(final Activity activity, BarChart barChart) {
        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(true);
        barChart.setDescription("");
        barChart.setMaxVisibleValueCount(50);
        barChart.setPinchZoom(false);
        barChart.setDrawGridBackground(false);

        // xAxis
        XAxis xl = barChart.getXAxis();
        xl.setGranularity(1f);
        xl.setCenterAxisLabels(true);
        xl.setValueFormatter(new AxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                if(activity !=null){
                    if(value == 0)
                        return activity.getResources().getString(R.string.pre_operation);
                    if(value == 1)
                        return activity.getResources().getString(R.string.in_operation);
                    if (value == 2)
                        return  activity.getResources().getString(R.string.post_operation);
                }
                return "-";
            }

            @Override
            public int getDecimalDigits() {
                return 0;
            }
        });

        // yAxis
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setValueFormatter(new AxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return String.valueOf((int) value);
            }

            @Override
            public int getDecimalDigits() {
                return 0;
            }
        });
        leftAxis.setDrawGridLines(false);
        leftAxis.setSpaceTop(30f);
        leftAxis.setAxisMinValue(0f); // this replaces setStartAtZero(true
        barChart.getAxisRight().setEnabled(false);
        barChart.getAxisLeft().setEnabled(false);
    }


}
