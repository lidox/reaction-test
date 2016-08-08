package com.artursworld.reactiontest.view.dialogs;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.app.DialogFragment;
import android.view.View;
import android.widget.EditText;

import com.artursworld.reactiontest.controller.util.UtilsRG;

import java.util.Calendar;

/*
* A datepicker to be able to pick dates
*/
@SuppressLint("ValidFragment")
public class DatePicker extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    EditText dateEditText;

    public DatePicker(View view){
        dateEditText = (EditText) view;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(getActivity(),android.R.style.Theme_Holo_Light_Dialog_MinWidth, this, year, month, day);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        return dialog;
    }

    public void onDateSet(android.widget.DatePicker view, int year, int month, int day) {
            String sMonth = month+1+"";
            if ((month+1) < 10)
                sMonth = "0"+ (month+1);

            String sDay = day+"";
            if (day < 10)
                sDay = "0"+ day;

            String date=sDay+"."+(sMonth)+"."+year;
            UtilsRG.info("date to set: " + date);
            dateEditText.setText(date);
    }
}
