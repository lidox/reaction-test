package com.artursworld.reactiontest.view.statistics;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.artursworld.reactiontest.R;
import com.artursworld.reactiontest.controller.util.UtilsRG;
import com.artursworld.reactiontest.model.entity.OperationIssue;
import com.artursworld.reactiontest.model.persistence.contracts.DBContracts;
import com.artursworld.reactiontest.model.persistence.manager.OperationIssueManager;
import com.artursworld.reactiontest.view.dialogs.DialogHelper;
import com.github.mikephil.charting.utils.Utils;

import java.text.ParseException;
import java.util.Date;

/**
 * Displays informations about an operation in a tab view.
 * E.g. the intubation time, operation date etc.
 */
public class InformationView {

    OperationIssueManager issueDB;

    /**
     * Returns the view to display information about user's operation
     */
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
                initOperationDateEditText(activity, view);
                initIntubationTime(activity, view);
                initWakeUpTime(activity, view);
            }
        }

        return view;
    }

    private void initWakeUpTime(Activity activity, View view) {
        EditText wakeupTime = (EditText) view.findViewById(R.id.medical_user_information_wakeup_time);
        if (wakeupTime != null) {
            wakeupTime.setInputType(InputType.TYPE_NULL);
            DialogHelper.onFocusOpenTimePicker(activity, wakeupTime);
            addOnTextChangeListener(activity, wakeupTime, DBContracts.OperationIssueTable.WAKE_UP_TIME);
            setOperationDate(activity, wakeupTime, DBContracts.OperationIssueTable.WAKE_UP_TIME);
        }
    }

    private void initIntubationTime(Activity activity, View view) {
        EditText intubationDateEditText = (EditText) view.findViewById(R.id.medical_user_information_intubation_time);
        if (intubationDateEditText != null) {
            intubationDateEditText.setInputType(InputType.TYPE_NULL);
            DialogHelper.onFocusOpenTimePicker(activity, intubationDateEditText);
            addOnTextChangeListener(activity, intubationDateEditText, DBContracts.OperationIssueTable.INTUBATION_TIME);
            setOperationDate(activity, intubationDateEditText, DBContracts.OperationIssueTable.INTUBATION_TIME);
        }
    }

    /**
     * Initializes the operation date ui element to display the operation date for selected operation issue
     *
     * @param activity
     * @param view
     */
    private void initOperationDateEditText(Activity activity, View view) {
        EditText operationDateEditText = (EditText) view.findViewById(R.id.medical_user_information_date);

        if (operationDateEditText != null) {
            operationDateEditText.setInputType(InputType.TYPE_NULL);
            DialogHelper.onFocusOpenDatePicker(activity, operationDateEditText);
            addOnTextChangeListener(activity, operationDateEditText, DBContracts.OperationIssueTable.OPERATION_DATE);
            setOperationDate(activity, operationDateEditText, DBContracts.OperationIssueTable.OPERATION_DATE);
        }
    }

    /**
     * Sets opeartion date to an edittext
     * @param activity
     * @param dateOrTimeEditText
     */
    private void setOperationDate(final Activity activity, final EditText dateOrTimeEditText, final String tableRow) {
        if (dateOrTimeEditText != null) {
            final String selectedOperationIssue = UtilsRG.getStringByKey(UtilsRG.OPERATION_ISSUE, activity);

            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... unusedParams) {
                    if (issueDB == null)
                        issueDB = new OperationIssueManager(activity.getApplicationContext());
                    if (selectedOperationIssue != null) {
                        Date date = issueDB.getDateByOperationIssue(selectedOperationIssue, tableRow);
                        String operationDateText = null;
                        if(tableRow.equals(DBContracts.OperationIssueTable.OPERATION_DATE)){
                            if (date!=null)
                                operationDateText = UtilsRG.germanDateFormat.format(date);
                        }
                        else{
                            if (date!=null)
                                operationDateText = UtilsRG.timeFormat.format(date);
                        }

                        final String finalOperationDateText = operationDateText;
                        UtilsRG.info(tableRow + " for operation issue(" + selectedOperationIssue + ")=" + operationDateText);
                        if (operationDateText != null) {
                            activity.runOnUiThread(new Runnable() {
                                public void run() {
                                    dateOrTimeEditText.setText(finalOperationDateText);
                                }
                            });

                        } else {
                            activity.runOnUiThread(new Runnable() {
                                public void run() {
                                    dateOrTimeEditText.setHint(activity.getResources().getText(R.string.click_to_select_a_date));
                                }
                            });

                        }
                    }
                    return null;
                }
            }.execute();
        }
    }

    /**
     * Saves new date in database if user changes it
     */
    private void addOnTextChangeListener(final Activity activity, EditText dateEditText, final String dateTableRow) {
        dateEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(final Editable selectedDateOrTime) {
                final String selectedOperationIssue = UtilsRG.getStringByKey(UtilsRG.OPERATION_ISSUE, activity);
                UtilsRG.info(dateTableRow + " for OperationIssue(" + selectedOperationIssue + ") has been chenged to: " + selectedDateOrTime.toString());

                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... unusedParams) {
                        if (issueDB == null) {
                            issueDB = new OperationIssueManager(activity.getApplicationContext());
                        }
                        try {
                            Date date;
                            if(dateTableRow.equals(DBContracts.OperationIssueTable.OPERATION_DATE)){
                                date = UtilsRG.germanDateFormat.parse(selectedDateOrTime.toString());
                            }
                            else{
                                date = UtilsRG.timeFormat.parse(selectedDateOrTime.toString());
                            }
                            issueDB.updateOperationDateByOperationIssue(selectedOperationIssue, date, dateTableRow);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                }.execute();
            }
        });
    }
}
