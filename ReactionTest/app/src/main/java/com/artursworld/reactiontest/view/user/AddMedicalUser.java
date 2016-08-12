package com.artursworld.reactiontest.view.user;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;

import com.artursworld.reactiontest.R;
import com.artursworld.reactiontest.controller.util.UtilsRG;
import com.artursworld.reactiontest.model.entity.MedicalUser;
import com.artursworld.reactiontest.model.persistence.manager.MedicalUserManager;
import com.artursworld.reactiontest.view.dialogs.DialogHelper;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medical_user);
        activity = this;
        medUserDb = new MedicalUserManager(activity);
   }

    @Override
    public void onStart(){
        super.onStart();
        addBirthDatePicker();
        addGenderSingleChoiceDialog();
    }

    /**
     * Initializes birthdate picker and sets on focus listener
     */
    private void addBirthDatePicker() {
        final EditText editText = (EditText) findViewById(R.id.add_medical_user_birthdate_txt);
        editText.setInputType(InputType.TYPE_NULL);
        DialogHelper.onFocusOpenDatePicker(activity,editText);
    }

    /**
     * Opens gender choise dialog on focus edit text
     */
    private void addGenderSingleChoiceDialog() {
        final EditText genderEditText = (EditText) findViewById(R.id.start_game_settings_operation_issue_selector);
        genderEditText.setInputType(InputType.TYPE_NULL);
        String titleGender = getResources().getString(R.string.gender);
        final CharSequence[] maleOrFemaleList = {getResources().getString(R.string.male), getResources().getString(R.string.female)};
        DialogHelper.onFocusOpenSingleChoiceDialog(activity, genderEditText, titleGender, maleOrFemaleList);
    }

    /**
     * Adds user via database on button clicK and switch back to user management view
     * */
    public void onAddUserButtonClick(View view){
        String medicalId =  ((EditText) findViewById(R.id.add_medical_user_medico_id)).getText().toString();
        double bmi = 0;
        Date birthdate = new Date();
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        String birthdateString = ((EditText) findViewById(R.id.add_medical_user_birthdate_txt)).getText().toString();

        try {
            birthdate = df.parse(birthdateString );
            bmi = Double.parseDouble(((EditText) findViewById(R.id.add_medical_user_bmi_txt)).getText().toString());
        } catch (ParseException e) {
            UtilsRG.error("Could not parse date input to date: "+e.getLocalizedMessage());
        }
        String gender = ((EditText) findViewById(R.id.start_game_settings_operation_issue_selector)).getText().toString();


        MedicalUser medicalUser = new MedicalUser(medicalId, birthdate, gender, bmi);

        medUserDb.insert(medicalUser);

        finish();
    }
}
