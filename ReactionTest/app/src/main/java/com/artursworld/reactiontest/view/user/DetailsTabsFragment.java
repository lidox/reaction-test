package com.artursworld.reactiontest.view.user;

import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
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
import android.widget.TextView;

import com.artursworld.reactiontest.R;
import com.artursworld.reactiontest.controller.util.UtilsRG;
import com.artursworld.reactiontest.model.entity.OperationIssue;
import com.artursworld.reactiontest.model.persistence.contracts.DBContracts;
import com.artursworld.reactiontest.model.persistence.manager.OperationIssueManager;
import com.artursworld.reactiontest.model.persistence.manager.TrialManager;
import com.artursworld.reactiontest.view.dialogs.DialogHelper;
import com.artursworld.reactiontest.view.statistics.BarChartView;
import com.artursworld.reactiontest.view.statistics.InformationView;
import com.artursworld.reactiontest.view.statistics.MedicamentListView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Displays user details containing tabs in a fragment
 */
public class DetailsTabsFragment extends Fragment {

    private Spinner operationIssueSpinner;
    private TabHost tabHost;
    public View rootView;
    public boolean isOperationIssueAvailable = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        UtilsRG.info(DetailsTabsFragment.class.getSimpleName() + " onCreateView");
        try {
            checkOperationIssueAvailability().get();
            return getTabsView(inflater, container);
        } catch (Exception e) {
            UtilsRG.error("Exception while creating tabs for detailted infromation");
        }
        return null;
    }

    private AsyncTask checkOperationIssueAvailability() {
        AsyncTask task = new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... unusedParams) {
                if (getActivity() != null) {
                    if (getActivity().getApplicationContext() != null) {
                        OperationIssueManager issueManager = new OperationIssueManager(getActivity().getApplicationContext());
                        if (issueManager != null) {
                            String medId = UtilsRG.getStringByKey(UtilsRG.MEDICAL_USER, getActivity());
                            List<OperationIssue> list = issueManager.getAllOperationIssuesByMedicoId(medId);
                            if (list != null) {
                                if(list.size() > 0){
                                    isOperationIssueAvailable = true;
                                }
                                else{
                                    isOperationIssueAvailable = false;
                                }
                            }
                        }
                    }
                }
                UtilsRG.info("isOperationIssueAvailable = " + isOperationIssueAvailable);
                return null;
            }
        }.execute();
        return task;
    }

    @Override
    public void onResume() {
        super.onResume();
        UtilsRG.info("onResume " + DetailsTabsFragment.class.getSimpleName());
    }

    /**
     * Returns view with different tabs to display details for operation.
     * If no operation exist display empty view with user feedback
     * @param inflater
     * @param container
     * @return
     */
    private View getTabsView(LayoutInflater inflater, ViewGroup container) {
        rootView = inflater.inflate(R.layout.fragment_details, container, false);
        tabHost = (TabHost) rootView.findViewById(R.id.tabhost);
        tabHost.setup();
        if (isOperationIssueAvailable) {
            UtilsRG.info("Display details");
            fillOperationSpinner(rootView);
            initTabViews(rootView);
        } else {
            UtilsRG.info("Hide details, because no operationIssue found");
            rootView = inflater.inflate(R.layout.empty_view, container, false);
        }
        return rootView;
    }

    /**
     * Returns instance of detail fragment
     */
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        UtilsRG.info(DetailsTabsFragment.class.getSimpleName() + " onCreate");
        super.onCreate(savedInstanceState);
    }

    /**
     * Initializes tabs in the details fragment
     */
    private void initTabViews(final View rootView) {
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
                return new InformationView().getView(getActivity(), rootView);//getInformationView();
            }
        });

        tabHost.addTab(spec);
        spec = tabHost.newTabSpec("tag1");
        spec.setIndicator(getResources().getString(R.string.statistics));
        spec.setContent(new TabHost.TabContentFactory() {

            @Override
            public View createTabContent(String tag) {
                BarChartView view = new BarChartView();
                return view.getView(getActivity(), rootView);
            }
        });
        tabHost.addTab(spec);
        spec = tabHost.newTabSpec("tag2");
        spec.setIndicator(getResources().getString(R.string.medicaments));
        spec.setContent(new TabHost.TabContentFactory() {

            @Override
            public View createTabContent(String tag) {
                MedicamentListView view = new MedicamentListView();
                return view.getView(getActivity(), rootView);
            }
        });
        tabHost.addTab(spec);
    }

    /**
     * get operation spinner instance and adds items
     */
    public void fillOperationSpinner(View rootView) {
        UtilsRG.info("filling operations into spinner");
        operationIssueSpinner = (Spinner) rootView.findViewById(R.id.details_fragment_toolbar_operation_issue_spinner);
        initOperationIssueSpinnerAsync(operationIssueSpinner);
    }

    /**
     * Reads operation issues from database and adds items to spinner asynchronous
     */
    private void initOperationIssueSpinnerAsync(final Spinner spinner) {
        new OperationIssueManager.getAllOperationIssuesByMedicoIdAsync(new OperationIssueManager.AsyncResponse() {

            @Override
            public void getAllOperationIssuesByMedicoId(List<OperationIssue> operationIssuesList) {
                addItemsOnOperationIssueSpinner(operationIssuesList, spinner);
                UtilsRG.info("Operation issues loaded for user(" +UtilsRG.getStringByKey(UtilsRG.MEDICAL_USER, getActivity())  + ")=" + operationIssuesList.toString());

                if (spinner != null) {
                    if (spinner.getSelectedItem() != null)
                        UtilsRG.putString(UtilsRG.OPERATION_ISSUE, spinner.getSelectedItem().toString(), getActivity());
                }

            }

        }, getActivity().getApplicationContext()).execute(UtilsRG.getStringByKey(UtilsRG.MEDICAL_USER, getActivity()));
    }

    /**
     * Add elements from list to a spinner
     */
    public void addItemsOnOperationIssueSpinner(List<OperationIssue> selectedOperationIssuesList, Spinner operationIssueSpinner) {
        UtilsRG.info("start to add items to spinner");
        if (operationIssueSpinner != null) {
            List<String> list = new ArrayList<String>();
            if (selectedOperationIssuesList != null) {
                if (selectedOperationIssuesList.size() > 0) {
                    for (OperationIssue issue : selectedOperationIssuesList) {
                        list.add(issue.getDisplayName());
                    }
                }
            }
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, list);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            operationIssueSpinner.setAdapter(dataAdapter);
        }
    }
}