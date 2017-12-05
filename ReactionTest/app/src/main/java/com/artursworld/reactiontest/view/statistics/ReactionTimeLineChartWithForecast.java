package com.artursworld.reactiontest.view.statistics;


import android.app.Activity;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.artursworld.reactiontest.R;
import com.artursworld.reactiontest.controller.analysis.forecast.NelderMeadOptimizer;
import com.artursworld.reactiontest.controller.analysis.forecast.TripleExponentialSmoothing;
import com.artursworld.reactiontest.controller.helper.Type;
import com.artursworld.reactiontest.controller.util.App;
import com.artursworld.reactiontest.controller.util.Global;
import com.artursworld.reactiontest.controller.util.Lists;
import com.artursworld.reactiontest.controller.util.Strings;
import com.artursworld.reactiontest.controller.util.UtilsRG;
import com.artursworld.reactiontest.model.entity.ReactionGame;
import com.artursworld.reactiontest.model.persistence.manager.ReactionGameManager;
import com.artursworld.reactiontest.model.persistence.manager.TrialManager;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.apache.commons.math3.stat.descriptive.rank.Median;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

/**
 * A line chart displaying reaction time data in percentage combined with a forecast
 */
public class ReactionTimeLineChartWithForecast extends Observable {

    private LineChart chart = null;
    private Activity activity = null;

    /**
     * Displays a line chart
     *
     * @param chartResourceId the line charts resource id
     * @param activity        the active activity
     */
    public ReactionTimeLineChartWithForecast(int chartResourceId, Activity activity) {
        chart = (LineChart) activity.findViewById(chartResourceId);
        this.activity = activity;
        initChart();
        addToggleChartListener(R.id.line_chart_title, R.id.line_chart_expand_icon);
    }

    /**
     * Initializes the line chart
     */
    private void initChart() {
        UtilsRG.debug("init chart " + ReactionTimeLineChartWithForecast.class.getSimpleName());

        LineData lineData = getReactionTimeInPercentageLineData();

        setLineChartStylingAndRefreshChart(lineData);

    }

    /**
     * Get combined line data to be displayed in the line chart
     *
     * @return combined line data to be displayed in the line chart
     */
    @NonNull
    private LineData getReactionTimeInPercentageLineData() {

        // get selected user
        String userId = Global.getSelectedUser();
        UtilsRG.debug("selected user!: " + userId);

        // get percentage medians by user
        final Double[] reactionTimesInPercentage = getReactionGameMedians(userId);

        // get predicted median in percentage
        final Double[] reactionTimesForecastInPercentage = getNextForecastReactionMedianInPercentage();

        // fill reaction times
        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < reactionTimesInPercentage.length; i++) {
            entries.add(new Entry(i, reactionTimesInPercentage[i].intValue()));
        }

        // fill forecast
        List<Entry> entriesForecast = new ArrayList<>();
        entriesForecast.add(new Entry(reactionTimesInPercentage.length - 1, reactionTimesInPercentage[reactionTimesInPercentage.length - 1].intValue()));
        for (int i = 0; i < reactionTimesForecastInPercentage.length; i++) {
            entriesForecast.add(new Entry(reactionTimesInPercentage.length + i, reactionTimesForecastInPercentage[i].intValue()));
        }

        // curved line
        LineDataSet.Mode lineMode = LineDataSet.Mode.HORIZONTAL_BEZIER;

        // reaction time in percentage data set
        LineDataSet dataSet = new LineDataSet(entries, Strings.getStringByRId(R.string.forecast_performance));
        dataSet.setMode(lineMode);
        dataSet = setLineDataStyling(dataSet, R.color.colorPrimary);

        // reaction time forecast in percentage data set
        LineDataSet dataSetForecast = new LineDataSet(entriesForecast, Strings.getStringByRId(R.string.forecast_performance));
        dataSetForecast.setMode(lineMode);
        dataSetForecast = setLineDataStyling(dataSetForecast, R.color.colorPrimaryLight);
        dataSetForecast.enableDashedLine(10, 10, 1000);

        // combine data sets
        LineData lineData = new LineData(dataSet, dataSetForecast);
        lineData.setValueFormatter(new BarChartPercentFormatter());
        lineData.setValueTextSize(15f);

        return lineData;
    }

    /**
     * Forecast the next median in percentage
     *
     * @return the forecast of the next median in percentage
     */
    @NonNull
    private Double[] getNextForecastReactionMedianInPercentage() {

        // get historical and database data
        ReactionGameManager dbManager = new ReactionGameManager(App.getAppContext());
        double[] dataPoints = dbManager.getAllReactionTimesInPercentage("reaction-data-july-2017.csv", Global.getReactionTestCountPerOperation());
        double[] dataPointsWithNewInOpData = getHistoricalDataAndNewInOpData(dbManager, dataPoints);

        // run some parameter optimization
        NelderMeadOptimizer.Parameters optimizedParams = NelderMeadOptimizer.optimize(dataPointsWithNewInOpData, Global.getReactionTestCountPerOperation());

        // make forecast
        List<Double> predictedValueList = TripleExponentialSmoothing.getPredictions(
                dataPoints,
                Global.getReactionTestCountPerOperation(),
                optimizedParams.getAlpha(),
                optimizedParams.getBeta(),
                optimizedParams.getGamma(),
                Global.getReactionTestCountPerOperation());

        int current = Global.getReactionTestCountPerOperation() - (dataPointsWithNewInOpData.length - dataPoints.length) - 1;
        UtilsRG.debug("nr. of reaction tests in the OP = " + current);

        // return forecast value
        return new Double[]{predictedValueList.get(current >= 0 ? current : 0)};
    }

    private double[] getHistoricalDataAndNewInOpData(ReactionGameManager dbManager, double[] dataPoints) {
        // get operation issue
        String selectedOperationIssue = UtilsRG.getStringByKey(UtilsRG.OPERATION_ISSUE, activity);

        // get all games
        List<ReactionGame> gameList = dbManager.getAllReactionGameList(selectedOperationIssue, "ASC");

        // filter games
        List<ReactionGame> inOpGameList = getReactionGamesFilteredByGameType(gameList, Type.TestTypes.InOperation);
        List<ReactionGame> preOpGameList = getReactionGamesFilteredByGameType(gameList, Type.TestTypes.PreOperation);

        ;
        // get pre op median
        double preOpMedian = getPreOpMedian(preOpGameList);

        // get in op medians
        List<Double> missingMedianInPercentage = new ArrayList<>();
        for (ReactionGame item : inOpGameList) {
            double[] reactionTimesByDB = item.getReactionTimesByDB();
            if (reactionTimesByDB != null) {
                double inOpmedian = new Median().evaluate(reactionTimesByDB);
                missingMedianInPercentage.add((preOpMedian / (inOpmedian * 1000.)) * 100.);
            }
        }
        // pre / inop
        double[] dataPointsWithNewInOpData = new double[missingMedianInPercentage.size() + dataPoints.length];
        System.arraycopy(dataPoints, 0, dataPointsWithNewInOpData, 0, dataPoints.length);
        for (int i = 0; i < missingMedianInPercentage.size(); i++) {
            dataPointsWithNewInOpData[dataPoints.length + i] = missingMedianInPercentage.get(i);
        }
        return dataPointsWithNewInOpData;
    }

    /**
     * Get the median of a list
     *
     * @param list the list
     * @return the median
     */
    public static double getPreOpMedian(List<ReactionGame> list) {
        List<Double> inOpReactionTimes = new ArrayList<>();
        for (ReactionGame item : list) {
            for (Double rt : item.getReactionTimesByDB()) {
                inOpReactionTimes.add(rt * 1000.);
            }
        }
        return new Median().evaluate(Lists.getArray(inOpReactionTimes));
    }

    public static List<ReactionGame> getReactionGamesFilteredByGameType(List<ReactionGame> gameList, Type.TestTypes testType) {
        List<ReactionGame> filteredGameList = new ArrayList<>();
        for (ReactionGame item : gameList) {
            if (item.getTestType() == testType) {
                filteredGameList.add(item);
            }
        }
        return filteredGameList;
    }

    /**
     * Get reaction game medians by user id
     *
     * @param userId the user id
     * @return the reaction game medians by user id
     */
    @NonNull
    private Double[] getReactionGameMedians(String userId) {
        ReactionGameManager manager = new ReactionGameManager(App.getAppContext());
        final double[] reactionGameMedians = manager.getAllReactionTimesInPercentageByUser(userId, Global.getReactionTestCountPerOperation());

        final Double[] reactionTimesInPercentage = new Double[reactionGameMedians.length + 1];
        reactionTimesInPercentage[0] = 100.;
        for (int index = 0; index < reactionGameMedians.length; index++)
            reactionTimesInPercentage[index + 1] = reactionGameMedians[index];
        return reactionTimesInPercentage;
    }

    /**
     * Set the line charts styling
     *
     * @param lineData the data to style
     */
    private void setLineChartStylingAndRefreshChart(LineData lineData) {
        // style axis
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setAxisMinValue(0f);
        leftAxis.setDrawGridLines(false);
        leftAxis.setTextSize(15);

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setDrawLabels(false);
        rightAxis.setDrawGridLines(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawLabels(false);

        // add threshold limit line
        String thresholdDescription = "";
        LimitLine limitLine = new LimitLine(100f, thresholdDescription);
        limitLine.setLineColor(Color.RED);
        limitLine.setLineWidth(1f);
        limitLine.setTextColor(Color.RED);
        limitLine.setTextSize(15f);

        if (leftAxis.getLimitLines().size() < 1)
            leftAxis.addLimitLine(limitLine);

        // add legend
        Legend l = chart.getLegend();
        l.setFormSize(10f);
        l.setForm(Legend.LegendForm.CIRCLE);
        l.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
        l.setTextSize(12f);
        l.setTextColor(Color.BLACK);
        l.setXEntrySpace(5f);
        l.setYEntrySpace(5f);
        String[] labels = {Strings.getStringByRId(R.string.median_performance), Strings.getStringByRId(R.string.median_performance_forecast), Strings.getStringByRId(R.string.pre_operation_performance)};
        int[] colors = {ContextCompat.getColor(activity.getApplicationContext(), R.color.colorPrimary), ContextCompat.getColor(activity.getApplicationContext(), R.color.colorPrimaryLight), Color.RED};
        l.setCustom(colors, labels);


        // style chart and refresh
        chart.setDescription("");
        chart.setPinchZoom(false);
        chart.setDoubleTapToZoomEnabled(false);
        chart.setDrawGridBackground(false);
        chart.setData(lineData);
        chart.invalidate();
    }

    /**
     * Set the styling of the data set
     *
     * @param dataSet the data set to style
     * @return the styled data set
     */
    private LineDataSet setLineDataStyling(LineDataSet dataSet, int lineColor) {
        int primeColor = ContextCompat.getColor(activity.getApplicationContext(), lineColor);
        dataSet.setColor(primeColor);
        dataSet.setValueTextColor(primeColor);
        dataSet.setCircleColor(ContextCompat.getColor(activity.getApplicationContext(), R.color.colorPrimaryDark));
        dataSet.setLineWidth(4);
        return dataSet;
    }

    /**
     * Adds toggle function to selected label and image in order to show or hide the line chart
     *
     * @param chartLabelId the label resource id to add toggle function to
     * @param chartImageId the image resource id to add toggle function to
     */
    private void addToggleChartListener(int chartLabelId, int chartImageId) {
        TextView reactionPerformanceLabel = (TextView) activity.findViewById(chartLabelId);
        final ImageView expandImage = (ImageView) activity.findViewById(chartImageId);

        if (reactionPerformanceLabel != null && expandImage != null) {

            reactionPerformanceLabel.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    toggleShowHideChart(expandImage);
                }
            });

            expandImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleShowHideChart(expandImage);
                }
            });
        }
    }

    /**
     * Toggle chart to show or hide its content
     */
    private void toggleShowHideChart(ImageView expandImage) {
        if (chart.getVisibility() == View.VISIBLE) {
            expandImage.setImageResource(R.drawable.ic_expand_more_black_24dp);
            if (chart != null) {
                chart.setVisibility(View.GONE);
            }
        } else {
            expandImage.setImageResource(R.drawable.ic_expand_less_black_24dp);
            if (chart != null) {
                chart.setVisibility(View.VISIBLE);
            }
        }
    }

}
