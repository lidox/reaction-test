package com.artursworld.reactiontest.view.statistics;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
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
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.AxisValueFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.ChartInterface;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import java.util.ArrayList;
import java.util.List;

/**
 * Returns the tab view to display statistics in a bar chart
 */
public class BarChartView {

    private Activity activity;
    private BarChart barChart;
    private CombinedChart mChart;
    List<BarEntry> yValsGoNoGoGame;
    List<BarEntry> yValsGoGame;

    /**
     * Returns the view to display bar chart
     */
    public View getView(final Activity activity, View rootView) {
        this.activity = activity;
        View view = null;
        LayoutInflater inflater;
        Context context = activity.getApplicationContext();
        onOperationIssueChange(rootView);


        if (context != null) {
            if (view == null && context != null && activity != null) {
                inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.statistics_view, null);
            }

            BarChart barChart = (BarChart) view.findViewById(R.id.chart);
            UtilsRG.info("chart init: " + barChart);
            initBarChartConfiguration(activity, barChart);
            initBarChartDataAsync(activity, barChart);


            initCombinedChartConfiguration(activity, view);
            initCombinedChartDataAsync(activity,mChart);
        }
        return view;
    }

    private void initCombinedChartConfiguration(final Activity activity, View view) {
        this.mChart = (CombinedChart) view.findViewById(R.id.combined_chart);
        mChart.setDescription("");
        mChart.setBackgroundColor(Color.WHITE);
        mChart.setDrawGridBackground(false);
        mChart.setDrawBarShadow(false);
        mChart.setHighlightFullBarEnabled(false);

        // draw bars behind lines
        mChart.setDrawOrder(new CombinedChart.DrawOrder[]{
                CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.LINE
        });

        Legend l = mChart.getLegend();
        l.setWordWrapEnabled(true);
        l.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
       // rightAxis.setStartAtZero(true);// setAxisMinimum(0f); // this replaces setStartAtZero(true)

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        //leftAxis.setStartAtZero(true);//setAxisMinimum(0f); // this replaces setStartAtZero(true)

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTH_SIDED);
        //xAxis.setAxisMinimum(0f);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new AxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                if (activity != null) {
                    if (value == 0)
                        return activity.getResources().getString(R.string.pre_operation);
                    if (value == 1)
                        return activity.getResources().getString(R.string.in_operation);
                    if (value == 2)
                        return activity.getResources().getString(R.string.post_operation);
                }
                return "-";
            }

            @Override
            public int getDecimalDigits() {
                return 0;
            }
        });
    }

    /**
     * Add bar chart data from database
     */
    private void initCombinedChartDataAsync(final Activity activity, final CombinedChart barChart) {
        activity.runOnUiThread(new Runnable() {
            public void run() {
                new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... params) {
                        if (yValsGoNoGoGame == null){
                            String selectedOperationIssue = UtilsRG.getStringByKey(UtilsRG.OPERATION_ISSUE, activity);
                            yValsGoNoGoGame = getAverageValuesFromDB(activity, selectedOperationIssue, Type.getGameType(Type.GameTypes.GoNoGoGame));
                            yValsGoGame = getAverageValuesFromDB(activity, selectedOperationIssue, Type.getGameType(Type.GameTypes.GoGame));
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void voids) {
                        super.onPostExecute(voids);

                        if (yValsGoGame != null && yValsGoNoGoGame != null) {
                            CombinedData data = new CombinedData();
                            data.setData(getBarDataAverageReactionTimes(mChart));
                            data.setData(generateLineData());
                            mChart.setData(data);
                            mChart.invalidate();
                        }
                    }

                }.execute();
            }
        });
    }

    private LineData generateLineData() {
        LineData d = new LineData();

        ArrayList<Entry> entries = new ArrayList<Entry>();

        for (int index = 0; index < 3; index++)
            entries.add(new Entry(index + 0.5f, UtilsRG.getRandomNumberInRange(0, 1)));

        LineDataSet set = new LineDataSet(entries, "Line DataSet");
        set.setColor(R.color.colorPrimaryLight);
        set.setLineWidth(2.5f);
        set.setCircleColor(R.color.colorPrimaryLight);
        set.setCircleRadius(5f);
        set.setFillColor(R.color.colorPrimaryLight);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setDrawValues(true);
        set.setValueTextSize(10f);
        set.setValueTextColor(R.color.colorPrimaryLight);

        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        d.addDataSet(set);

        return d;
    }

    /**
     * IF user changes operation issue --> display new data for new operation
     */
    private void onOperationIssueChange(View rootView) {
        if (rootView != null) {
            Spinner operationIssueSpinner = (Spinner) rootView.findViewById(R.id.details_fragment_toolbar_operation_issue_spinner);
            operationIssueSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (activity != null && barChart != null) {
                        String selectedOperationIssue = parent.getItemAtPosition(position).toString();
                        UtilsRG.putString(UtilsRG.OPERATION_ISSUE, selectedOperationIssue, activity);
                        UtilsRG.info("Selected OperationIssue changed in statistics to: " + selectedOperationIssue);
                        initBarChartDataAsync(activity, barChart);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }

    /**
     * creates bar sets for graph
     *
     * @return the bar data
     */
    private BarData getBarDataAverageReactionTimes(ChartInterface chart) {
        BarDataSet set1, set2;
        if (chart.getData() != null && chart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) chart.getData().getDataSetByIndex(0);
            set2 = (BarDataSet) chart.getData().getDataSetByIndex(1);
            set1.setValues(yValsGoGame);
            set2.setValues(yValsGoNoGoGame);
            chart.getData().notifyDataChanged();
        } else {
            set1 = new BarDataSet(yValsGoGame, activity.getResources().getString(R.string.go_game));
            set1.setColor(ContextCompat.getColor(activity.getApplicationContext(), R.color.colorAccentMiddle));
            set2 = new BarDataSet(yValsGoNoGoGame, activity.getResources().getString(R.string.go_no_go_game));
            set2.setColor(ContextCompat.getColor(activity.getApplicationContext(), R.color.colorPrimaryLight));
        }
        List<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
        dataSets.add(set1);
        dataSets.add(set2);

        return new BarData(dataSets);
    }

    /**
     * Add bar chart data from database
     */
    private void initBarChartDataAsync(final Activity activity, final BarChart barChart) {
        activity.runOnUiThread(new Runnable() {
            public void run() {
                //data: (0.46 + 0.02) * 2 + 0.04 = 1.00 -> interval per "group"
                final float groupSpace = 0.04f;
                final float barSpace = 0.02f;
                final float barWidth = 0.46f;

                new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... params) {
                        String selectedOperationIssue = UtilsRG.getStringByKey(UtilsRG.OPERATION_ISSUE, activity);
                        yValsGoNoGoGame = getAverageValuesFromDB(activity, selectedOperationIssue, Type.getGameType(Type.GameTypes.GoNoGoGame));
                        yValsGoGame = getAverageValuesFromDB(activity, selectedOperationIssue, Type.getGameType(Type.GameTypes.GoGame));
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void voids) {
                        super.onPostExecute(voids);

                        if (yValsGoGame != null && yValsGoNoGoGame != null) {
                            BarData data = getBarDataAverageReactionTimes(barChart);
                            barChart.setData(data);

                            barChart.getBarData().setBarWidth(barWidth);
                            barChart.getXAxis().setAxisMinValue(0);
                            barChart.groupBars(0, groupSpace, barSpace);
                            barChart.invalidate();
                        }
                    }

                }.execute();
            }
        });
    }

    /**
     * Returns a list of average reaction times from database
     */
    private List<BarEntry> getAverageValuesFromDB(Activity activity, String selectedOperationIssue, String gameType) {
        List<BarEntry> yValues = new ArrayList<>();
        if (selectedOperationIssue != null) {
            double averagePreOperationValue = new ReactionGameManager(activity).getFilteredReactionGames(selectedOperationIssue, gameType, Type.getTestType(Type.TestTypes.PreOperation), "AVG");
            double averageInOperationValue = new ReactionGameManager(activity).getFilteredReactionGames(selectedOperationIssue, gameType, Type.getTestType(Type.TestTypes.InOperation), "AVG");
            double averagePostOperationValue = new ReactionGameManager(activity).getFilteredReactionGames(selectedOperationIssue, gameType, Type.getTestType(Type.TestTypes.PostOperation), "AVG");

            // pre, in and post operation values
            yValues.add(new BarEntry(0, (float) averagePreOperationValue));
            yValues.add(new BarEntry(1, (float) averageInOperationValue));
            yValues.add(new BarEntry(2, (float) averagePostOperationValue));
            yValues.add(new BarEntry(3, 0f)); // dummy
        }
        return yValues;
    }

    /**
     * Initializes bar chart and sets configuration
     */
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
                if (activity != null) {
                    if (value == 0)
                        return activity.getResources().getString(R.string.pre_operation);
                    if (value == 1)
                        return activity.getResources().getString(R.string.in_operation);
                    if (value == 2)
                        return activity.getResources().getString(R.string.post_operation);
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
