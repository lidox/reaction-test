package com.artursworld.reactiontest.view.statistics;

import android.app.Activity;
import android.os.AsyncTask;

import com.artursworld.reactiontest.R;
import com.artursworld.reactiontest.controller.helper.Type;
import com.artursworld.reactiontest.controller.util.UtilsRG;
import com.artursworld.reactiontest.model.entity.ReactionGame;
import com.artursworld.reactiontest.model.persistence.manager.ReactionGameManager;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import java.util.ArrayList;
import java.util.List;

public class ReactionGameChart {

    private BarChart chart = null;
    private Activity activity = null;

    public ReactionGameChart(int id, Activity activity) {
        chart = (BarChart) activity.findViewById(id);
        this.activity = activity;
        initChart();
    }

    private BarChart initChart() {
        if (activity != null && chart != null) {
            getDataFromDB();
        }
        return chart;
    }

    private void getDataFromDB() {

        new AsyncTask<Void, Void, BarData>() {
            @Override
            protected BarData doInBackground(Void... params) {
                ArrayList<BarEntry> reactionTimes = new ArrayList<BarEntry>();

                String operationIssue = UtilsRG.getStringByKey(UtilsRG.OPERATION_ISSUE, activity);
                String gameType = UtilsRG.getStringByKey(UtilsRG.GAME_TYPE, activity);
                String testType = Type.TestTypes.PreOperation.name();

                ArrayList<BarEntry> reactionTimesPreOperation = new ArrayList<BarEntry>();
                float preOpAvgReactionTime = (float) new ReactionGameManager(activity).getFilteredReactionGames(operationIssue, gameType, testType, "AVG");
                reactionTimesPreOperation.add(new BarEntry(0.1f, -1f));
                reactionTimesPreOperation.add(new BarEntry(0.25f, preOpAvgReactionTime));

                List<ReactionGame> reactionTimeList = new ReactionGameManager(activity).getReactionGameList(operationIssue, gameType, Type.TestTypes.InOperation.name(), "ASC");
                if (reactionTimeList != null){
                    int index = 0;
                    float j = 0.5f;
                    for(;index< reactionTimeList.size(); j+=0.25, index++){
                        reactionTimes.add(new BarEntry(j, (float) reactionTimeList.get(index).getAverageReactionTime()));
                    }
                    // end values
                    reactionTimes.add(new BarEntry(j + 0.15f, -1f));
                }


                BarDataSet inOperationSet, preOperationSet;

                preOperationSet = new BarDataSetGoGame(reactionTimesPreOperation, activity.getResources().getString(R.string.pre_operation));
                preOperationSet.setColors(new int[]{
                        activity.getResources().getColor(R.color.colorAccent),
                });

                inOperationSet = new BarDataSet(reactionTimes, activity.getResources().getString(R.string.in_operation));
                inOperationSet.setColors(new int[]{
                        activity.getResources().getColor(R.color.colorPrimary),
                });


                ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
                dataSets.add(preOperationSet);
                dataSets.add(inOperationSet);

                BarData data = new BarData(dataSets);
                data.setValueTextSize(15f);
                data.setBarWidth(0.23f);
                return data;
            }

            @Override
            protected void onPostExecute(BarData barData) {
                super.onPostExecute(barData);
                chart.setDrawBarShadow(false);
                chart.setDrawValueAboveBar(true);

                // if more than 60 entries are displayed in the chart, no values will be
                // drawn
                chart.setMaxVisibleValueCount(15);
                chart.setDescription("");
                // scaling can now only be done on x- and y-axis separately
                chart.setPinchZoom(false);
                chart.setDoubleTapToZoomEnabled(false);

                chart.setDrawGridBackground(false);

                YAxis leftAxis = chart.getAxisLeft();
                leftAxis.setAxisMinValue(0f);

                YAxis rightAxis = chart.getAxisRight();
                rightAxis.setEnabled(false);

                chart.setData(barData);
                chart.notifyDataSetChanged();
                chart.getData().notifyDataChanged();

                Legend l = chart.getLegend();
                l.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
                l.setForm(Legend.LegendForm.SQUARE);
                l.setFormSize(9f);
                l.setTextSize(11f);
                l.setXEntrySpace(4f);

                chart.zoomOut();
            }

        }.execute();
    }
}
