package com.artursworld.reactiontest.view.statistics;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.artursworld.reactiontest.R;
import com.artursworld.reactiontest.controller.util.UtilsRG;
import com.artursworld.reactiontest.model.persistence.manager.OperationIssueManager;
import com.artursworld.reactiontest.view.dialogs.DialogHelper;

public class InformationView {

    OperationIssueManager issueDB;

    public View getView(final Activity activity, View rootView) {
        View view = null;
        LayoutInflater inflater;
        Context context = activity.getApplicationContext();

        if (activity != null && context != null) {
            if (view == null) {
                inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.medical_user_information_adapter, null);
            }

            if (view != null) {
                EditText operationDateEditText = (EditText) view.findViewById(R.id.medical_user_information_date);
                if (operationDateEditText != null) {
                    operationDateEditText.setInputType(InputType.TYPE_NULL);
                    DialogHelper.onFocusOpenDatePicker(activity, operationDateEditText);
                    addOnTextChangeListener(activity, operationDateEditText);

                    //TODO: async and correct displays
                    if(operationDateEditText != null){
                        final String selectedOperationIssue = UtilsRG.getStringByKey(UtilsRG.OPERATION_ISSUE, activity);
                        if (issueDB == null)
                            issueDB = new OperationIssueManager(activity.getApplicationContext());
                        String operationDateText = issueDB.getOperationDateByOperationIssue(selectedOperationIssue);
                        if (operationDateText != null)
                            operationDateEditText.setText(operationDateText);
                    }

                }

                EditText intubationDateEditText = (EditText) view.findViewById(R.id.medical_user_information_intubation_time);
                if (intubationDateEditText != null) {
                    intubationDateEditText.setInputType(InputType.TYPE_NULL);
                    DialogHelper.onFocusOpenTimePicker(activity, intubationDateEditText);
                    if (issueDB == null) {
                        issueDB = new OperationIssueManager(activity.getApplicationContext());
                    }
                    String intubationDate = issueDB.getIntubationDateByOperationIssue(UtilsRG.getStringByKey(UtilsRG.OPERATION_ISSUE, activity));
                    UtilsRG.info("intubation date loaded: " + intubationDate);
                    if (intubationDate != null)
                        intubationDateEditText.setText(intubationDate);
                }

                EditText wakeupTime = (EditText) view.findViewById(R.id.medical_user_information_wakeup_time);
                if (wakeupTime != null) {
                    wakeupTime.setInputType(InputType.TYPE_NULL);
                    DialogHelper.onFocusOpenTimePicker(activity, wakeupTime);
                }

                //TODO: save stuff in db

            }
        }

        return view;
    }

    private void addOnTextChangeListener(final Activity activity, EditText operationDateEditText) {
        operationDateEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(final Editable operationDate) {
                final String selectedOperationIssue = UtilsRG.getStringByKey(UtilsRG.OPERATION_ISSUE, activity);
                UtilsRG.info("OperationDate for OperationIssue("+selectedOperationIssue+") has been chenged to:" + operationDate.toString());

                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... unusedParams) {
                        if (issueDB == null) {
                            issueDB = new OperationIssueManager(activity.getApplicationContext());
                        }
                        issueDB.updateOperationDateByOperationIssue(selectedOperationIssue, operationDate.toString());
                        return null;
                    }
                }.execute();
            }
        });
    }
}
