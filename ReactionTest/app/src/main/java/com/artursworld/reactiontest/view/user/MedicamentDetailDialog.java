package com.artursworld.reactiontest.view.user;

import android.app.Activity;
import android.app.Dialog;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.artursworld.reactiontest.R;
import com.artursworld.reactiontest.controller.util.UtilsRG;
import com.artursworld.reactiontest.model.entity.Medicament;
import com.artursworld.reactiontest.model.persistence.manager.MedicamentManager;
import com.artursworld.reactiontest.model.util.TinyDB;
import com.artursworld.reactiontest.view.dialogs.DialogHelper;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Observable;

/**
 * Opens a dialog to add or edit a medicament
 */
public class MedicamentDetailDialog extends Observable {

    private Activity activity = null;
    private Medicament medicament = null;
    private boolean needToCreateMedicament = true;
    private String[] medicamentNames = null;

    // UI
    private MaterialDialog dialog = null;
    private Spinner medicamentNameSpinner = null;
    private Spinner unitSpinner = null;
    private MaterialEditText doseEditText = null;
    private MaterialEditText timeStampEditText = null;
    private MedicamentDetailDialog self = null;

    /**
     * Opens a dialog to add or edit a medicament
     *
     * @param activity   the activity where to display the dialog
     * @param medicament the medicament to edit. if medicament ==null,
     *                   the dialog creates new medicament by UI input
     */
    public MedicamentDetailDialog(Activity activity, Medicament medicament) {
        this.self = this;
        this.activity = activity;
        this.medicament = medicament;
        if (medicament != null) {
            needToCreateMedicament = false;
        }
        dialog = buildDialog();
        initTimePicker(dialog, activity, R.id.dl_time_stamp);
        addOnAddMedicamentListener(dialog, R.id.dl_add_medicament_to_spinner);
        if (!needToCreateMedicament) {
            UtilsRG.info("open up edit medicament dialog");
            setDialogByMedicament(medicament, dialog);
        }

        initMedicamentNames(activity);

        dialog.show();
    }

    /**
     * Initializes the medicament name spinner and its content by TinyDB
     * @param activity the activity displayed
     */
    private void initMedicamentNames(Activity activity) {
        TinyDB tinydb = new TinyDB(activity);
        String medicamentNameKey = activity.getResources().getString(R.string.c_medicament_names);
        ArrayList<String> medicamentNameList = tinydb.getListString(medicamentNameKey);

        boolean isEmptyList = medicamentNameList.size() < 1;
        if (isEmptyList){
            UtilsRG.info("medicament names not initialized yet. Thus init them!");
            medicamentNames = activity.getResources().getStringArray(R.array.medicament_value);
            tinydb.putListString(medicamentNameKey, new ArrayList<String>(Arrays.asList(medicamentNames)));
        }
        else{
            UtilsRG.info("medicament names already initialized. Thus show them!");
            ArrayList<String> medicamentNamesList =  tinydb.getListString(medicamentNameKey);
            String[] medicamentNameArrays = new String[medicamentNamesList.size()];
            medicamentNames  = medicamentNamesList.toArray(medicamentNameArrays);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, medicamentNames);
        medicamentNameSpinner.setAdapter(adapter);
    }

    /**
     * Adds new item to spinner
     *
     * @param dialog         the medicament dialog
     * @param spinnerId      the id of the spinner to add item
     * @param medicamentName the @String to add into the spinner
     */
    private void addMedicamentToSpinner(MaterialDialog dialog, int spinnerId, String medicamentName) {
        try {
            if (medicamentNameSpinner == null)
                medicamentNameSpinner = (Spinner) dialog.getView().findViewById(spinnerId);

            TinyDB tinydb = new TinyDB(activity);

            // get list
            String medicamentNameKey = activity.getResources().getString(R.string.c_medicament_names);
            ArrayList<String> medicamentNameList = tinydb.getListString(medicamentNameKey);

            // add new item to list and commit
            medicamentNameList.add(medicamentName);
            tinydb.putListString(medicamentNameKey, medicamentNameList);

            // get array by string
            String[] medicamentNameArrays = new String[medicamentNameList.size()];
            medicamentNames  = medicamentNameList.toArray(medicamentNameArrays);

            // set adapter
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, medicamentNames);
            medicamentNameSpinner.setAdapter(adapter);
        } catch (Exception e) {
            UtilsRG.error("Could not add item to spinner dynamically. " + e.getLocalizedMessage());
        }
    }

    /**
     * Adds a listener to image view to open new medicament name dialog
     *
     * @param dialog      the dialog to create or edit medicament
     * @param imageViewId the image view button to add the listener
     */
    private void addOnAddMedicamentListener(MaterialDialog dialog, int imageViewId) {
        try {
            View view = dialog.getCustomView();
            ImageView addImageView = (ImageView) view.findViewById(imageViewId);
            addImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openAttentionDialog();
                }
            });
        } catch (Exception e) {
            UtilsRG.error("could not add on click listener to ImageView: " + e.getLocalizedMessage());
        }
    }

    /**
     * It's time to start a new reaction test, so display attention dialog
     */
    private void openAttentionDialog() {
        new MaterialDialog.Builder(activity)
                .title(R.string.add_new_medicament)
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input(R.string.operation_issue_name, R.string.operation_issue_name, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog d, CharSequence input) {
                        if (input != null) {
                            if (!input.toString().trim().equals("")) {
                                UtilsRG.info("got new medicament: " + input.toString());
                                addMedicamentToSpinner(dialog, R.id.dl_medicament_spinner, input.toString());
                            }
                        }
                    }
                }).show();
    }

    /**
     * Builds a dialog to display a medicament
     *
     * @return the dialog
     */
    private MaterialDialog buildDialog() {
        MaterialDialog dialog = new MaterialDialog.Builder(activity)
                .title(R.string.medicament)
                .customView(R.layout.dialog_medicament, true)
                .negativeText(R.string.cancel)
                .positiveText(R.string.save)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Medicament newMedicament = getMedicamentByUI(dialog);
                        if (needToCreateMedicament) {
                            insertMedicamentAsync(newMedicament);
                        } else {
                            newMedicament.setCreationDate(medicament.getCreationDate());
                            updateMedicamentAsync(newMedicament);
                        }
                    }
                }).build();
        return dialog;
    }

    /**
     * Updates a medicament
     *
     * @param newMedicament the medicament to update
     */
    private void updateMedicamentAsync(final Medicament newMedicament) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                new MedicamentManager(activity.getApplicationContext()).updateMedicament(newMedicament);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                UtilsRG.info("notify that new medicament has been update");
                self.setChanged();
                self.notifyObservers();
            }
        }.execute();
    }

    private void setDialogByMedicament(Medicament medicament, MaterialDialog dialog) {
        try {
            initUIElements(dialog);
            timeStampEditText.setText(medicament.getTime());
            doseEditText.setText(medicament.getDosage()+"");
            medicamentNameSpinner.setSelection(((ArrayAdapter<String>)medicamentNameSpinner.getAdapter()).getPosition(medicament.getName()));
            unitSpinner.setSelection(((ArrayAdapter<String>)unitSpinner.getAdapter()).getPosition(medicament.getUnit()));
        } catch (Exception e) {
            UtilsRG.error("Unexpected error: " + e.getLocalizedMessage());
        }
    }

    /**
     * Reads
     *
     * @param dialog
     * @return
     */
    private Medicament getMedicamentByUI(MaterialDialog dialog) {
        try {

            initUIElements(dialog);

            String operationIssue = UtilsRG.getStringByKey(UtilsRG.OPERATION_ISSUE, activity);
            String name = medicamentNameSpinner.getSelectedItem().toString();
            int dose = Integer.parseInt(doseEditText.getText().toString());
            String unit = unitSpinner.getSelectedItem().toString();
            Date timeStamp = DialogHelper.getDateTimeFromUI(new Date(), timeStampEditText);
            Medicament ret = new Medicament(operationIssue, name, dose, unit, timeStamp);
            UtilsRG.info("Read Medicament by UI: " + ret);
            return ret;

        } catch (Exception e) {
            UtilsRG.error("Could not read Medicament by UI! " + e.getLocalizedMessage());
            return null;
        }
    }

    /**
     * Initializes UI elements of edit medicament dialog
     *
     * @param dialog the dialog containing the UI elements
     */
    private void initUIElements(MaterialDialog dialog) {
        if (medicamentNameSpinner == null)
            medicamentNameSpinner = (Spinner) dialog.getView().findViewById(R.id.dl_medicament_spinner);

        if (unitSpinner == null)
            unitSpinner = (Spinner) dialog.getView().findViewById(R.id.dl_unit_spinner);

        if (doseEditText == null)
            doseEditText = (MaterialEditText) dialog.getView().findViewById(R.id.dl_dose_text);

        if (timeStampEditText == null)
            timeStampEditText = (MaterialEditText) dialog.getView().findViewById(R.id.dl_time_stamp);
    }

    /**
     * Inserts new medicament ansync
     *
     * @param newMedicament the medicament to insert
     */
    private void insertMedicamentAsync(final Medicament newMedicament) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                new MedicamentManager(activity.getApplicationContext()).insertMedicament(newMedicament);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                UtilsRG.info("notify that new medicament has been inserted");
                self.setChanged();
                self.notifyObservers();
            }
        }.execute();
    }

    /**
     * Initializes time picker edit text and sets current time as text
     */
    private void initTimePicker(MaterialDialog dialog, Activity activity, int id) {
        try {
            View view = dialog.getCustomView();
            MaterialEditText timePickerEditText = (MaterialEditText) view.findViewById(id);
            if (timePickerEditText != null) {
                timePickerEditText.setInputType(InputType.TYPE_NULL);
                DialogHelper.onFocusOpenTimePicker(activity, timePickerEditText);
                String currentHourAndMinutes = UtilsRG.timeFormat.format(new Date());
                timePickerEditText.setText(currentHourAndMinutes);
            }
        } catch (Exception e) {
            UtilsRG.error("Unexpected error: " + e.getLocalizedMessage());
        }
    }
}
