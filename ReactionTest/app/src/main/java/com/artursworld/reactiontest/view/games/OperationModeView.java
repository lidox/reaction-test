package com.artursworld.reactiontest.view.games;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.artursworld.reactiontest.R;
import com.artursworld.reactiontest.controller.TimeLineItemClickListener;
import com.artursworld.reactiontest.controller.adapters.TimeLineAdapter;
import com.artursworld.reactiontest.controller.util.UtilsRG;
import com.artursworld.reactiontest.model.entity.InOpEvent;
import com.artursworld.reactiontest.model.persistence.manager.InOpEventManager;
import com.artursworld.reactiontest.view.dialogs.DialogHelper;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.AxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.Types.BoomType;
import com.nightonke.boommenu.Types.ButtonType;
import com.nightonke.boommenu.Types.PlaceType;
import com.nightonke.boommenu.Util;
import com.roughike.swipeselector.SwipeItem;
import com.roughike.swipeselector.SwipeSelector;
import com.sdsmdg.tastytoast.TastyToast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OperationModeView extends AppCompatActivity {

    private static final String TYPE_AUDIO = "Audio";
    private long nextReactionTestcountDown = 0;

    // timeline
    private RecyclerView recyclerTimeLineView;
    private List<InOpEvent> timeLineList = new ArrayList<>();
    private TimeLineItemClickListener listener = null;

    // boom button
    private BoomMenuButton addEventBtn;
    private boolean isInitialized = false;

    // add event dialog ui elements
    SwipeSelector eventTypeSwipeSelector = null;
    SwipeItem[] swipeList = null;
    EditText timePickerEditText = null;
    EditText noteEditText = null;

    // gloabal settings
    String operationIssue = null;
    OperationModeView activity = null;

    private BarChart mChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operation_mode_result_view);
        this.activity = this;
        initTimeLineViewWithClickListener();
        initChart();
        addEventBtn = (BoomMenuButton) findViewById(R.id.add_event_to_timeline_btn);
    }

    private void initChart() {
        mChart = (BarChart) findViewById(R.id.reaction_go_game_graph);

        mChart.setDrawBarShadow(false);
        mChart.setDrawValueAboveBar(true);

        mChart.setDescription("");

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        mChart.setMaxVisibleValueCount(60);

        // scaling can now only be done on x- and y-axis separately
        mChart.setPinchZoom(false);

        mChart.setDrawGridBackground(false);
        // mChart.setDrawYLabels(false);
        // xAxis
        XAxis xl = mChart.getXAxis();
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
                return "---";
            }

            @Override
            public int getDecimalDigits() {
                return 0;
            }
        });

        // yAxis
        YAxis leftAxis = mChart.getAxisLeft();
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
        /*
        IAxisValueFormatter xAxisFormatter = new DayAxisValueFormatter(mChart);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);;
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(7);
        xAxis.setValueFormatter(xAxisFormatter);

        IAxisValueFormatter custom = new MyAxisValueFormatter();

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTypeface(mTfLight);
        leftAxis.setLabelCount(8, false);
        leftAxis.setValueFormatter(custom);
        leftAxis.setPosition(YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
        */


        Legend l = mChart.getLegend();
        l.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(4f);
        // l.setExtra(ColorTemplate.VORDIPLOM_COLORS, new String[] { "abc",
        // "def", "ghj", "ikl", "mno" });
        // l.setCustom(ColorTemplate.VORDIPLOM_COLORS, new String[] { "abc",
        // "def", "ghj", "ikl", "mno" });

        setData(12, 50);
    }

    private void setData(int is, int i1) {
        float start = 0f;


        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();

        for (int i = 0; i < 3; i++) {
            float mult = (3 + 1);
            float val = (float) (Math.random() * mult);
            yVals1.add(new BarEntry(i + 1f, val));
        }

        BarDataSet set1;

        if (mChart.getData() != null &&
                mChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) mChart.getData().getDataSetByIndex(0);
            set1.setValues(yVals1);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(yVals1, "The year 2017");
            set1.setColors(ColorTemplate.MATERIAL_COLORS);

            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
            dataSets.add(set1);

            BarData data = new BarData(dataSets);
            data.setValueTextSize(10f);
            data.setBarWidth(0.9f);

            mChart.setData(data);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        loadPreferences(this);
        loadViewList();
    }

    /**
     * Initialize the time line components incl. click listener
     */
    private void initTimeLineViewWithClickListener() {
        recyclerTimeLineView = (RecyclerView) findViewById(R.id.recyclerView);
        if (recyclerTimeLineView != null) {
            recyclerTimeLineView.setLayoutManager(new LinearLayoutManager(this));
            recyclerTimeLineView.setHasFixedSize(true);

            listener = new TimeLineItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    onClickTimeLineItem(view, position);
                }

            };

            TimeLineAdapter timeLineAdapter = new TimeLineAdapter(timeLineList, listener, this);
            recyclerTimeLineView.setAdapter(timeLineAdapter);
            registerForContextMenu(recyclerTimeLineView);
        }
    }

    /**
     * @param view
     * @param position
     */
    private void onClickTimeLineItem(View view, int position) {
        if (timeLineList != null) {
            InOpEvent event = timeLineList.get(position);
            String msg = "clicked on " + event.toString();
            UtilsRG.info(msg);
            if (event.getType().equals("Audio")) {
                MaterialDialog dialog = getAudioPlayDialog(activity, event);
                new AudioRecorder(dialog, event, activity, true);
            } else if (event.getType().equals("ReactionTest")) {

            } else {
                MaterialDialog dialog = getEditNoteDialog(activity, event);
                initEventTypeSwipeSelector(dialog, activity);
                initTimePicker(dialog, activity);
                displaySelectedEvent(event);
            }

        }
    }

    private MaterialDialog getAudioPlayDialog(final OperationModeView activity, final InOpEvent event) {
        return new MaterialDialog.Builder(activity)
                .title(R.string.play_audio)
                .customView(R.layout.record_audio_dialog, true)
                .negativeText(R.string.delete)
                .onNegative(getEventDeleteDialog(event))
                .show();
    }

    private void displaySelectedEvent(InOpEvent event) {
        if (event != null) {
            if (eventTypeSwipeSelector != null) {
                Object swipeIndex = getSwipeIndexByEvent(event);
                eventTypeSwipeSelector.selectItemWithValue(swipeIndex);
            }

            if (timePickerEditText != null)
                timePickerEditText.setText(event.getHoursAndMinutes());
            if (noteEditText != null)
                noteEditText.setText(event.getAdditionalNote());
        }
    }

    private Object getSwipeIndexByEvent(InOpEvent event) {
        if (swipeList != null) {
            String title = event.getType();
            for (int i = 0; i < swipeList.length; i++) {
                if (swipeList[i].title.equals(title)) {
                    return swipeList[i].value;
                }
            }
        }
        return 0;
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (isInitialized) return;
        isInitialized = true;

        Drawable[] subButtonDrawables = new Drawable[3];
        int[] drawablesResource = new int[]{
                R.drawable.ic_alarm_add_white_36dp,
                R.drawable.ic_mic_white_36dp,
                R.drawable.ic_note_add_white_36dp
        };
        int itemCount = 3;
        for (int i = 0; i < itemCount; i++)
            subButtonDrawables[i] = ContextCompat.getDrawable(this, drawablesResource[i]);

        final String[] subButtonTexts = new String[]{getResources().getString(R.string.add_reaction_test), getResources().getString(R.string.add_audio), getResources().getString(R.string.add_event)};

        int[][] subButtonColors = new int[3][2];
        for (int i = 0; i < itemCount; i++) {
            subButtonColors[i][1] = ContextCompat.getColor(this, R.color.colorPrimary);
            subButtonColors[i][0] = Util.getInstance().getPressedColor(subButtonColors[i][1]);
        }


        addEventBtn.init(
                subButtonDrawables, // The drawables of images of sub buttons. Can not be null.
                subButtonTexts,     // The texts of sub buttons, ok to be null.
                subButtonColors,    // The colors of sub buttons, including pressed-state and normal-state.
                ButtonType.HAM,     // The button type.
                BoomType.LINE,  // The boom type.
                PlaceType.HAM_3_1,  // The place type.
                null,               // Ease type to move the sub buttons when showing.
                null,               // Ease type to scale the sub buttons when showing.
                null,               // Ease type to rotate the sub buttons when showing.
                null,               // Ease type to move the sub buttons when dismissing.
                null,               // Ease type to scale the sub buttons when dismissing.
                null,               // Ease type to rotate the sub buttons when dismissing.
                null                // Rotation degree.
        );

        final Activity activity = this;
        addEventBtn.setOnSubButtonClickListener(new BoomMenuButton.OnSubButtonClickListener() {
            @Override
            public void onClick(int buttonIndex) {
                //TastyToast.makeText(getApplicationContext(), subButtonTexts[buttonIndex] + " clicked", TastyToast.LENGTH_LONG, TastyToast.SUCCESS);
                int addNewReactionTestIndex = 0;
                int recordAudioIndex = 1;
                int addEventIndex = 2;

                if (buttonIndex == recordAudioIndex) {
                    UtilsRG.info("add audio record has been selected");
                    //TODO: open record audio dialog
                    InOpEvent event = new InOpEvent(operationIssue, new Date(), TYPE_AUDIO, null);
                    MaterialDialog audioDialog = getAudioDialog(activity);
                    AudioRecorder recorder = new AudioRecorder(audioDialog, event, activity, false);
                } else if (buttonIndex == addEventIndex) {
                    UtilsRG.info("addEventIndex has been selected");
                    MaterialDialog dialog = initNoteDialog(activity);
                    initEventTypeSwipeSelector(dialog, activity);
                    initTimePicker(dialog, activity);

                } else if (buttonIndex == addNewReactionTestIndex) {
                    UtilsRG.info("addNewReactionTestIndex has been selected");
                    Intent intent = new Intent(activity, GoGameView.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                    //TODO: open reaction test
                }
            }
        });
    }

    private void initEventTypeSwipeSelector(MaterialDialog dialog, Activity activity) {
        eventTypeSwipeSelector = (SwipeSelector) dialog.getCustomView().findViewById(R.id.event_type_swipe_selector);
        swipeList = new SwipeItem[5];
        swipeList[0] = new SwipeItem(0, activity.getResources().getString(R.string.note), activity.getResources().getString(R.string.add_note_description));
        swipeList[1] = new SwipeItem(1, activity.getResources().getString(R.string.intubation), activity.getResources().getString(R.string.intubation_description));
        swipeList[2] = new SwipeItem(2, activity.getResources().getString(R.string.extubation), activity.getResources().getString(R.string.extubation_description));
        swipeList[3] = new SwipeItem(3, activity.getResources().getString(R.string.sedation), activity.getResources().getString(R.string.sedation_description));
        swipeList[4] = new SwipeItem(4, activity.getResources().getString(R.string.wakeup), activity.getResources().getString(R.string.wakeup_time_description));
        eventTypeSwipeSelector.setItems(swipeList);
    }

    private MaterialDialog getAudioDialog(Activity activity) {
        return new MaterialDialog.Builder(activity)
                .title(R.string.add_audio)
                .customView(R.layout.record_audio_dialog, true)
                .positiveText(R.string.save)
                .negativeText(R.string.cancel)
                .show();
    }

    private MaterialDialog initNoteDialog(Activity activity) {
        boolean wrapInScrollView = true;
        return new MaterialDialog.Builder(activity)
                .title(R.string.add_event)
                .customView(R.layout.add_in_operation_event_view, wrapInScrollView)
                .positiveText(R.string.add)
                .negativeText(R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog1, @NonNull DialogAction which) {
                        UtilsRG.info("'add' button clicked in order to add new event for operation");
                        InOpEvent event = getInOpEventByUI();
                        new AsyncTask<InOpEvent, Void, Void>() {
                            @Override
                            protected Void doInBackground(InOpEvent... params) {
                                InOpEvent event = params[0];
                                new InOpEventManager(getApplicationContext()).insertEvent(event);
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Void aVoid) {
                                super.onPostExecute(aVoid);
                                loadViewList();
                            }
                        }.execute(event);

                    }
                })
                .show();
    }

    private MaterialDialog getEditNoteDialog(final Activity activity, final InOpEvent event) {
        boolean wrapInScrollView = true;
        return new MaterialDialog.Builder(activity)
                .title(R.string.edit_event)
                .customView(R.layout.add_in_operation_event_view, wrapInScrollView)
                .positiveText(R.string.save)
                .negativeText(R.string.delete)

                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        UtilsRG.info("display/edit event: " + event);
                        InOpEvent event = getInOpEventByUI();
                        new AsyncTask<InOpEvent, Void, Void>() {
                            @Override
                            protected Void doInBackground(InOpEvent... params) {
                                InOpEvent event = params[0];
                                new InOpEventManager(getApplicationContext()).updateEvent(event);
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Void aVoid) {
                                super.onPostExecute(aVoid);
                                loadViewList();
                            }
                        }.execute(event);

                    }
                })
                .onNegative(getEventDeleteDialog(event))
                .show();
    }

    public MaterialDialog.SingleButtonCallback getEventDeleteDialog(final InOpEvent event) {
        return new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                UtilsRG.info("delete event has been clicked: " + event);

                new MaterialDialog.Builder(activity)
                        .title(R.string.delete)
                        .content(R.string.delete_event)
                        .positiveText(R.string.agree)
                        .negativeText(R.string.cancel)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {

                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                new AsyncTask<InOpEvent, Void, Void>() {
                                    @Override
                                    protected Void doInBackground(InOpEvent... params) {
                                        InOpEvent event = params[0];
                                        new InOpEventManager(getApplicationContext()).deleteEvent(event);
                                        return null;
                                    }

                                    @Override
                                    protected void onPostExecute(Void aVoid) {
                                        super.onPostExecute(aVoid);
                                        loadViewList();
                                    }
                                }.execute(event);
                            }
                        })
                        .show();
            }
        };
    }

    private InOpEvent getInOpEventByUI() {
        SwipeItem selectedItem = eventTypeSwipeSelector.getSelectedItem();
        String eventType = null;
        String note = null;

        if (selectedItem != null)
            eventType = selectedItem.title;

        Date timestamp = DialogHelper.getDateTimeFromUI(new Date(), timePickerEditText);

        if (noteEditText != null)
            note = noteEditText.getText().toString();

        return new InOpEvent(operationIssue, timestamp, eventType, note);
    }


    /**
     * Initializes time picker edit text and sets current time as text
     */
    private void initTimePicker(MaterialDialog dialog, Activity activity) {
        View view = dialog.getCustomView();
        timePickerEditText = (EditText) view.findViewById(R.id.event_time_picker);
        noteEditText = (EditText) view.findViewById(R.id.event_note);
        if (timePickerEditText != null) {
            timePickerEditText.setInputType(InputType.TYPE_NULL);
            DialogHelper.onFocusOpenTimePicker(activity, timePickerEditText);

            try {
                String currentHourAndMinutes = UtilsRG.timeFormat.format(new Date());
                timePickerEditText.setText(currentHourAndMinutes);
            } catch (Exception e) {
                UtilsRG.error(e.getLocalizedMessage());
            }
        }
    }

    /**
     * Load items from database to display in the timeline
     */
    private void loadViewList() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                timeLineList = new InOpEventManager(activity).getInOpEventListByOperationIssue(operationIssue, InOpEventManager.SORT_ASC);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                TimeLineAdapter timeLineAdapter = new TimeLineAdapter(timeLineList, listener, activity);
                recyclerTimeLineView.setAdapter(timeLineAdapter);
            }
        }.execute();
    }

    /**
     * Displays a count with a text in a textview
     *
     * @param countDown_sec the seconds to wait
     * @param prefixText    the text shown right to the countdown
     * @param textView      the textView where to display the countdown
     */
    private void runNextReactionTestCountDown(long countDown_sec, final String prefixText, final TextView textView) {
        if ((textView != null) && (countDown_sec > 0)) {
            new CountDownTimer((countDown_sec + 1) * 1000, 1000) {
                public void onTick(long millisUntilFinished) {
                    long countdownNumber = (millisUntilFinished / 1000) - 1;


                    if (countdownNumber != 0) {
                        long minutes = (countdownNumber % 3600) / 60;
                        long seconds = countdownNumber % 60;
                        String timeString = String.format("%02d:%02d", minutes, seconds);
                        String countdownNumberAsText = prefixText + timeString;
                        textView.setText(countdownNumberAsText);
                    } else {
                        textView.setText(R.string.make_a_new_try);
                    }

                }

                public void onFinish() {
                    //TODO: vibrate, make noise and wackel dackel button
                }
            }.start();
        }
    }

    /**
     * Loads some settings from shared pereferances
     *
     * @param activity
     */
    private void loadPreferences(final Activity activity) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                String countDownInSecondsKey = getResources().getString(R.string.operation_mode_next_reaction_test_countdown);
                nextReactionTestcountDown = UtilsRG.getIntByKey(countDownInSecondsKey, activity, 5) * 60;
                operationIssue = UtilsRG.getStringByKey(UtilsRG.OPERATION_ISSUE, activity);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                TextView countDownTextView = (TextView) findViewById(R.id.operation_mode_next_game_estimated_in_text);
                String estimatedTime = getResources().getString(R.string.next_game_estimated_in);
                runNextReactionTestCountDown(nextReactionTestcountDown, estimatedTime + ": ", countDownTextView);
            }
        }.execute();
    }
}
