package com.artursworld.reactiontest.view.statistics;

import android.app.Activity;
import android.os.AsyncTask;
import android.view.View;

import com.artursworld.reactiontest.R;
import com.artursworld.reactiontest.controller.helper.Type;
import com.artursworld.reactiontest.controller.util.UtilsRG;
import com.artursworld.reactiontest.model.entity.ReactionGame;
import com.artursworld.reactiontest.model.persistence.manager.ReactionGameManager;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Observable;

public class ReactionGameChart extends Observable {

    private BarChart chart = null;
    private Activity activity = null;
    private float barChartMaxValue = 0;
    private boolean containsInOpReactionTests = false;
    private Date latestInOpReactionTestDate = null;
    private ReactionGameChart self = null;

    public ReactionGameChart(int id, Activity activity) {
        this.self = this;
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

    public void showChart() {
        if (chart != null) {
            chart.setVisibility(View.VISIBLE);
        }
    }


    public void hideChart() {
        if (chart != null) {
            chart.setVisibility(View.GONE);
        }
    }

    public boolean containsInOpReactionTests() {
        return containsInOpReactionTests;
    }

    public Date getLatestInOpReactionTestDate(){
        return latestInOpReactionTestDate;
    }

    private void getDataFromDB() {

        new AsyncTask<Void, Void, BarData>() {
            @Override
            protected BarData doInBackground(Void... params) {
                ArrayList<BarEntry> reactionTimesInOperation = new ArrayList<BarEntry>();

                String operationIssue = UtilsRG.getStringByKey(UtilsRG.OPERATION_ISSUE, activity);
                String gameType = UtilsRG.getStringByKey(UtilsRG.GAME_TYPE, activity);
                String testType = Type.TestTypes.PreOperation.name();

                ArrayList<BarEntry> reactionTimesPreOperation = new ArrayList<BarEntry>();
                float preOpAvgReactionTime = (float) new ReactionGameManager(activity).getFilteredReactionGames(operationIssue, gameType, testType, "AVG");

                if (preOpAvgReactionTime > 0) {
                    float xAxisStartValue = 0.125f;
                    reactionTimesPreOperation.add(new BarEntry(xAxisStartValue, 100, preOpAvgReactionTime));

                    List<ReactionGame> reactionTimeInOpList = new ReactionGameManager(activity).getReactionGameList(operationIssue, gameType, Type.TestTypes.InOperation.name(), "ASC");
                    if (reactionTimeInOpList != null) {
                        if (reactionTimeInOpList.size() > 0) {
                            containsInOpReactionTests = true;
                            latestInOpReactionTestDate = reactionTimeInOpList.get(reactionTimeInOpList.size() - 1).getUpdateDate();
                        }


                        int index = 0;
                        float j = xAxisStartValue + 0.25f;
                        for (; index < reactionTimeInOpList.size(); j += 0.25, index++) {
                            float averageReactionInOpForSingleTest = (float) reactionTimeInOpList.get(index).getAverageReactionTime();
                            float percentageComparedWithPreOpValue = (preOpAvgReactionTime / averageReactionInOpForSingleTest) * 100;
                            reactionTimesInOperation.add(new BarEntry(j, percentageComparedWithPreOpValue, averageReactionInOpForSingleTest));
                        }

                        barChartMaxValue = j;
                        // just to show pretty chart, where the first bar fills not the whole screen
                        if(reactionTimeInOpList.size() < 3){
                            barChartMaxValue = xAxisStartValue + 0.75f;
                        }
                    }
                } else {
                    //TODO: there is no pre-op value
                }

                BarDataSet inOperationSet, preOperationSet;

                preOperationSet = new BarDataSetGoGame(reactionTimesPreOperation, activity.getResources().getString(R.string.pre_operation));
                preOperationSet.setColors(new int[]{
                        activity.getResources().getColor(R.color.colorAccent),
                });

                inOperationSet = new BarDataSet(reactionTimesInOperation, activity.getResources().getString(R.string.in_operation));
                inOperationSet.setColors(new int[]{
                        activity.getResources().getColor(R.color.colorPrimary),
                });


                ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
                dataSets.add(preOperationSet);
                dataSets.add(inOperationSet);

                BarData data = new BarData(dataSets);
                data.setValueFormatter(new PercentFormatter());
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

                XAxis xAxis = chart.getXAxis();
                xAxis.setAxisMinValue(0);
                xAxis.setAxisMaxValue(barChartMaxValue);
                xAxis.setDrawLabels(false);

                chart.setData(barData);

                Legend l = chart.getLegend();
                l.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
                l.setForm(Legend.LegendForm.SQUARE);
                l.setFormSize(9f);
                l.setTextSize(11f);
                l.setXEntrySpace(4f);

                ReactionTimeMarkerView mv = new ReactionTimeMarkerView(activity.getApplicationContext(), R.layout.chart_marker_view);
                // set the marker to the chart
                chart.setMarkerView(mv);

                chart.notifyDataSetChanged();
                chart.getData().notifyDataChanged();
                chart.zoomOut();
                if(self != null){
                    // notify that chart has been loaded
                    self.setChanged();
                    self.notifyObservers();
                }
            }

        }.execute();
    }
}
