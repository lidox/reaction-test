package com.artursworld.reactiontest.view.user;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

    private void addBirthDatePicker() {
        final EditText editText = (EditText) findViewById(R.id.add_medical_user_birthdate_txt);
        DialogHelper.onFocusOpenDatePicker(activity,editText);
    }

    private void addGenderSingleChoiceDialog() {
        final EditText genderEditText = (EditText) findViewById(R.id.add_medical_user_gender_txt);
        String titleGender = getResources().getString(R.string.gender);
        final CharSequence[] maleOrFemaleList = {getResources().getString(R.string.male), getResources().getString(R.string.female)};
        DialogHelper.onFocusOpenSingleChoiceDialog(activity, genderEditText, titleGender, maleOrFemaleList);
    }

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

        String gender = ((EditText) findViewById(R.id.add_medical_user_gender_txt)).getText().toString();


        MedicalUser medicalUser = new MedicalUser();
        medicalUser.setMedicalId(medicalId);
        medicalUser.setBirthDate(birthdate);
        medicalUser.setGender(gender);
        medicalUser.setBmi(bmi);
        medUserDb.insert(medicalUser);
    }

}
