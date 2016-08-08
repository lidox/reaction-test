package com.artursworld.reactiontest.view.dialogs;

import java.util.Calendar;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

//TODO: is this class used?
/*
* Dialog to display a datepicker e.g. to select birthdate
*/
@SuppressLint("ValidFragment")
public class DateDialog extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    EditText birthdate_picker;

    public DateDialog(View view){
        birthdate_picker = (EditText) view;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        String sMonth = month+1+"";
        if ((month+1) < 10)
            sMonth = "0"+ (month+1);

        String sDay = day+"";
        if (day < 10)
            sDay = "0"+ day;

        String date=sDay+"."+(sMonth)+"."+year;
        birthdate_picker.setText(date);
    }

}
