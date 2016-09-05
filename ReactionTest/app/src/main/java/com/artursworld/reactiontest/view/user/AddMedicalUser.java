package com.artursworld.reactiontest.view.user;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.artursworld.reactiontest.R;
import com.artursworld.reactiontest.controller.helper.Gender;
import com.artursworld.reactiontest.controller.util.UtilsRG;
import com.artursworld.reactiontest.model.entity.InOpEvent;
import com.artursworld.reactiontest.model.entity.MedicalUser;
import com.artursworld.reactiontest.model.persistence.manager.InOpEventManager;
import com.artursworld.reactiontest.model.persistence.manager.MedicalUserManager;
import com.artursworld.reactiontest.view.dialogs.DialogHelper;
import com.artursworld.reactiontest.view.games.StartGameSettings;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.sdsmdg.tastytoast.TastyToast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Displays view to add a new user
 */
public class AddMedicalUser extends AppCompatActivity {

    private MedicalUserManager medUserDb;
    private Activity activity;

    private int selectedGenderIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medical_user);
    }

    @Override
    protected void onResume() {
        super.onResume();
        activity = this;
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... unusedParams) {
                medUserDb = new MedicalUserManager(activity);
                return null;
            }
        }.execute();
        addBirthDatePicker();
        addGenderSingleChoiceDialog();
    }

    /**
     * Initializes birthDate picker and sets on focus listener
     */
    private void addBirthDatePicker() {
        final EditText editText = (EditText) findViewById(R.id.add_medical_user_birthdate_txt);
        editText.setInputType(InputType.TYPE_NULL);
        DialogHelper.onFocusOpenDatePicker(activity, editText);
    }

    /**
     * Opens gender choise dialog on focus edit text
     */
    private void addGenderSingleChoiceDialog() {
        final EditText genderEditText = (EditText) findViewById(R.id.start_game_settings_operation_issue_selector);
        genderEditText.setInputType(InputType.TYPE_NULL);
        final String titleGender = getResources().getString(R.string.gender);
        final CharSequence[] maleOrFemaleList = {getResources().getString(R.string.male), getResources().getString(R.string.female)};

        Runnable task = new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder singleChoiceDialog = new AlertDialog.Builder(activity);
                singleChoiceDialog.setTitle(titleGender);
                singleChoiceDialog.setSingleChoiceItems(maleOrFemaleList, -1, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int clickedPosition) {
                        selectedGenderIndex = clickedPosition;
                        String value = maleOrFemaleList[clickedPosition].toString();
                        genderEditText.setText(value);
                        UtilsRG.info("checked gender at position: " +clickedPosition);
                        dialog.cancel();
                    }
                });
                AlertDialog alert_dialog = singleChoiceDialog.create();
                alert_dialog.show();
                alert_dialog.getListView().setItemChecked(selectedGenderIndex, true);
            }


        };
        DialogHelper.onFocusOpenSingleChoiceDialog(activity, genderEditText, titleGender, maleOrFemaleList, task);

    }


    /**
     * Adds user via database on button clicK and switch back to user management view
     */
    public void onAddUserButtonClick(View view) {
        String medicalId = ((MaterialEditText) findViewById(R.id.add_medical_user_medico_id)).getText().toString();
        boolean isEmptyId = medicalId.trim().equals("");
        double bmi = 0;
        Date birthdate = null;
        if (!isEmptyId) {
            String birthdateString = ((EditText) findViewById(R.id.add_medical_user_birthdate_txt)).getText().toString();

            try {
                birthdate = UtilsRG.germanDateFormat.parse(birthdateString);
                EditText bmiText = (EditText) findViewById(R.id.add_medical_user_bmi_txt);
                if (bmiText != null) {
                    if (bmiText.getText() != null)
                        if (!bmiText.getText().toString().trim().equals(""))
                            bmi = Double.parseDouble(bmiText.getText().toString().trim());

                }
            } catch (ParseException e) {
                UtilsRG.error("Could not parse date input to date: " + e.getLocalizedMessage());
            }

            String gender;
            if (selectedGenderIndex == 0)
                gender = Gender.MALE.name();
            else
                gender = Gender.FEMALE.name();

            final MedicalUser medicalUser = new MedicalUser(medicalId, birthdate, Gender.valueOf(gender), bmi);

            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... unusedParams) {
                    medUserDb.insert(medicalUser);
                    return null;
                }
            }.execute();

            try {
                String hasNoUserInDBKey = getResources().getString(R.string.no_user_in_the_database);
                boolean hasStartedAppFirstTime = getIntent().getExtras().getBoolean(hasNoUserInDBKey);
                if(hasStartedAppFirstTime){
                    UtilsRG.info("Start game settings");
                    Intent intent = new Intent(activity, StartGameSettings.class);
                    startActivity(intent);
                }
                else{
                    finish();
                }
            }catch (Exception e){
                UtilsRG.info("could not detect that no user in db, so close this activity");
                finish();
            }


        } else {
            try {
                String warningMessage = getResources().getString(R.string.no_medical_id);
                TastyToast.makeText(getApplicationContext(), warningMessage, TastyToast.LENGTH_LONG, TastyToast.WARNING);
            } catch (Exception e) {
                UtilsRG.error("Could not display Tasty Toast");
            }
        }
    }

    @Override
    public void onBackPressed() {
        try{
            String hasNoUserInDBKey = getResources().getString(R.string.no_user_in_the_database);
            boolean hasStartedAppFirstTime = getIntent().getExtras().getBoolean(hasNoUserInDBKey);
            if(hasStartedAppFirstTime){
                UtilsRG.info("No user in database, so no way to go back");
                new MaterialDialog.Builder(activity)
                        .title(R.string.attention)
                        .content(R.string.please_create_user_before_use_app)
                        .show();
                return;
            }
        }catch (Exception e){
            UtilsRG.error("Unexpected Exception:" +e.getLocalizedMessage());
        }
        super.onBackPressed();
    }
}
