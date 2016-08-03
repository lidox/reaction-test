package com.artursworld.reactiontest.view.user;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AnalogClock;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TabHost;

import com.artursworld.reactiontest.R;
import com.artursworld.reactiontest.controller.util.UtilsRG;
import com.artursworld.reactiontest.model.entity.OperationIssue;
import com.artursworld.reactiontest.model.persistence.manager.OperationIssueManager;
import com.artursworld.reactiontest.model.persistence.manager.TrialManager;
import com.artursworld.reactiontest.view.dialogs.DialogHelper;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;


public class DetailsTabsFragment extends Fragment {

    private TrialManager trialDB;
    private OperationIssueManager issueDB;
    private Spinner operationIssueSpinner;
    private TabHost tabHost;
    public View rootView;

    private float averagePreOperationReactionTime;
    private float averageInterOperationReactionTime;
    private float averagePostOperationReactionTime;

    public static DetailsTabsFragment newInstance(int index, String selectedMedicalUserId) {
        UtilsRG.info(DetailsTabsFragment.class.getSimpleName() + " called by index:" + index + " and user(" + selectedMedicalUserId + ")");
        DetailsTabsFragment f = new DetailsTabsFragment();

        // Create arguments bundle
        Bundle args = new Bundle();
        args.putInt("index", index);
        args.putString("id", selectedMedicalUserId);
        f.setArguments(args);

        return f;
    }

    // Retrieve the index of the currently shown work group
    public int getShownIndex() {
        int index = getArguments().getInt("index", 0);
        UtilsRG.info("Read index by arguments. Got Index=" + index);
        return index;
    }

    public String getSelectedMedicalUser() {
        String selectedMedicalUserId = getArguments().getString("id", null);
        UtilsRG.info("Read selected medical user id by arguments. Got selectedMedicalUserId=" + selectedMedicalUserId);
        return selectedMedicalUserId;
    }


    @Override
    public void onAttach(Context context) {
        UtilsRG.info(DetailsTabsFragment.class.getSimpleName() + " onAttach");
        super.onAttach(context);
        trialDB = new TrialManager(getActivity().getApplicationContext());
        issueDB = new OperationIssueManager(getActivity().getApplicationContext());

        averagePreOperationReactionTime = 0.f;
        averageInterOperationReactionTime = 0.0f;
        averagePostOperationReactionTime = 0.0f;
    }

    private void setAvarageReactionTime(final String operationIssueName){
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                String operationType = "Pre operation";
                String gameType = "Go-Game";
                String filter = "AVG";
                trialDB.getFilteredReactionTime(filter, operationIssueName, operationType, gameType);
                return null;
            }
        }.execute();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        UtilsRG.info(DetailsTabsFragment.class.getSimpleName() + " onCreate");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        UtilsRG.info(DetailsTabsFragment.class.getSimpleName() + " onCreateView");
        return getTabsView(inflater, container);
    }

    private View getTabsView(LayoutInflater inflater, ViewGroup container) {
        rootView = inflater.inflate(R.layout.fragment_details, container, false);
        tabHost = (TabHost) rootView.findViewById(R.id.tabhost);
        tabHost.setup();

        fillOperationSpinner();
        initTabViews();
        return rootView;
    }

    private void initTabViews() {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        if (activity != null && toolbar != null) {
            activity.setSupportActionBar(toolbar);
            activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
        }


        TabHost.TabSpec spec = tabHost.newTabSpec("tag");
        spec.setIndicator(getResources().getString(R.string.information));
        spec.setContent(new TabHost.TabContentFactory() {
            @Override
            public View createTabContent(String tag) {
                return getInformationView();
            }
        });

        tabHost.addTab(spec);
        spec = tabHost.newTabSpec("tag1");
        spec.setIndicator(getResources().getString(R.string.statistics));
        spec.setContent(new TabHost.TabContentFactory() {

            @Override
            public View createTabContent(String tag) {
                return getStatisticsView();
            }
        });
        tabHost.addTab(spec);
        spec = tabHost.newTabSpec("tag2");
        spec.setIndicator(getResources().getString(R.string.medicaments));
        spec.setContent(new TabHost.TabContentFactory() {

            @Override
            public View createTabContent(String tag) {
                // TODO Auto-generated method stub
                return (new AnalogClock(getActivity()));
            }
        });
        tabHost.addTab(spec);
    }

    public void fillOperationSpinner() {
        UtilsRG.info("filling operations into spinner");
        operationIssueSpinner = (Spinner) rootView.findViewById(R.id.details_fragment_toolbar_operation_issue_spinner);
        initOperationIssueSpinnerAsync(operationIssueSpinner);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        UtilsRG.info(DetailsTabsFragment.class.getSimpleName() + " onViewCreated");
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        UtilsRG.info(DetailsTabsFragment.class.getSimpleName() + " onActivityCreated");
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        UtilsRG.info(DetailsTabsFragment.class.getSimpleName() + "onViewStateRestored");
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onStart() {
        UtilsRG.info(DetailsTabsFragment.class.getSimpleName() + " onStart");
        super.onStart();
    }

    @Override
    public void onResume() {
        UtilsRG.info(DetailsTabsFragment.class.getSimpleName() + " onResume");
        super.onResume();
    }

    @Override
    public void onPause() {
        UtilsRG.info(DetailsTabsFragment.class.getSimpleName() + " onPause");
        super.onPause();
    }

    @Override
    public void onStop() {
        UtilsRG.info(DetailsTabsFragment.class.getSimpleName() + " onStop");
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        UtilsRG.info(DetailsTabsFragment.class.getSimpleName() + " onDestroyView");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        UtilsRG.info(DetailsTabsFragment.class.getSimpleName() + " onDestroy");
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        UtilsRG.info(DetailsTabsFragment.class.getSimpleName() + " onDetach");
        super.onDetach();
    }

    // use startActivityForResult(...) and not getActivity().startActivityForResult(...)
    // public void clickEventOrSomething() {
    //     getActivity().startActivityForResult(...); // will only call onActivityResult(...) in Activity
    //     startActivityForResult(...); // will call onActivityResult(...) in Activity and because of the super-call also onActivityResult(...) here in Fragment
    // }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // use it here
    }

    private void initOperationIssueSpinnerAsync(final Spinner spinner) {
        new OperationIssueManager.getAllOperationIssuesByMedicoIdAsync(new OperationIssueManager.AsyncResponse() {

            @Override
            public void getAllOperationIssuesByMedicoId(List<OperationIssue> operationIssuesList) {
                addItemsOnOperationIssueSpinner(operationIssuesList, spinner);
                UtilsRG.info("Operation issues loaded for user(" + getSelectedMedicalUser() + ")=" + operationIssuesList.toString());

                if(spinner != null){
                    UtilsRG.putString(UtilsRG.OPERATION_ISSUE, spinner.getSelectedItem().toString(), getActivity());
                    setAvarageReactionTime(spinner.getSelectedItem().toString());
                }

            }

        }, getActivity().getApplicationContext()).execute(getSelectedMedicalUser());
    }

    public void addItemsOnOperationIssueSpinner(List<OperationIssue> selectedOperationIssuesList, Spinner operationIssueSpinner) {
        UtilsRG.info("start to add items to spinner");
        if (operationIssueSpinner != null) {
            List<String> list = new ArrayList<String>();
            if (selectedOperationIssuesList != null) {
                if (selectedOperationIssuesList.size() > 0) {
                    for (OperationIssue issue : selectedOperationIssuesList) {
                        list.add(issue.getDisplayName());
                    }
                } else {
                    list.add(getResources().getString(R.string.no_operation_issue));
                }
            }
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, list);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            operationIssueSpinner.setAdapter(dataAdapter);
        }
    }

    private View getInformationView() {
        View view = null;
        LayoutInflater inflater;
        Context context = getActivity().getApplicationContext();

        if (context != null) {
            if (view == null) {
                inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.medical_user_information_adapter, null);
            }

            if (view != null) {
                EditText operationDate = (EditText) view.findViewById(R.id.medical_user_information_date);
                if (operationDate != null) {
                    operationDate.setInputType(InputType.TYPE_NULL);
                    DialogHelper.onFocusOpenDatePicker(getActivity(), operationDate);
                }

                EditText intubationDateEditText = (EditText) view.findViewById(R.id.medical_user_information_intubation_time);
                if (intubationDateEditText != null) {
                    intubationDateEditText.setInputType(InputType.TYPE_NULL);
                    DialogHelper.onFocusOpenTimePicker(getActivity(), intubationDateEditText);
                    String intubationDate = issueDB.getIntubationDateByOperationIssue(UtilsRG.getStringByKey(UtilsRG.OPERATION_ISSUE, getActivity()));
                    UtilsRG.info("intubation date loaded: "+ intubationDate);
                    if(intubationDate!=null)
                     intubationDateEditText.setText(intubationDate);
                }

                EditText wakeupTime = (EditText) view.findViewById(R.id.medical_user_information_wakeup_time);
                if (wakeupTime != null) {
                    wakeupTime.setInputType(InputType.TYPE_NULL);
                    DialogHelper.onFocusOpenTimePicker(getActivity(), wakeupTime);
                }

                //TODO: save stuff in db

            }
        }

        return view;
    }

    private View getStatisticsView() {
        View view = null;
        LayoutInflater inflater;
        Context context = getActivity().getApplicationContext();

        if (context != null) {
            if (view == null) {
                inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.statistics_view, null);
            }

            BarChart barChart = (BarChart) view.findViewById(R.id.chart);
            UtilsRG.info("chart init: " + barChart);

            ArrayList<BarEntry> entries = new ArrayList<>();
            entries.add(new BarEntry(0, 0));
            entries.add(new BarEntry(1, averagePreOperationReactionTime));
            entries.add(new BarEntry(2, averageInterOperationReactionTime));
            entries.add(new BarEntry(3, averagePostOperationReactionTime));
            entries.add(new BarEntry(4, 0));

            BarDataSet dataset = new BarDataSet(entries, "# of Calls");


            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
            dataSets.add(dataset);
            dataset.setColors(ColorTemplate.COLORFUL_COLORS);
            BarData data = new BarData(dataSets);
            barChart.setData(data);
        }
        return view;

    }
}