package com.artursworld.reactiontest.view.user;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
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
import com.artursworld.reactiontest.view.statistics.BarChartView;
import com.artursworld.reactiontest.view.statistics.InformationView;

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
}