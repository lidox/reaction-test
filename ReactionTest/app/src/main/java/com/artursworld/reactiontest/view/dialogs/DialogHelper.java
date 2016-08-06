package com.artursworld.reactiontest.view.dialogs;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;

import java.util.Calendar;

/*
* Helper class for dialogs e.g. on focus listeners
*/
public class DialogHelper {

    /*
    * adds a focus listiner for a choise dialog
    */
    public static void onFocusOpenSingleChoiceDialog(final Activity activity, final EditText editText, final String dialogTitle, final CharSequence[] itemsToSelectList) {
        final int[] postion = new int[1];
        if (editText != null) {
            editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                public void onFocusChange(View view, boolean hasfocus) {
                    if (hasfocus) {
                        openSingleChoiseDialog(activity, dialogTitle, itemsToSelectList, postion, editText);
                    }
                }
            });
            editText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openSingleChoiseDialog(activity, dialogTitle, itemsToSelectList, postion, editText);
                }
            });
        }
    }

    /*
    * opens a dialog on focus
    */
    private static void openSingleChoiseDialog(Activity activity, String dialogTitle, final CharSequence[] itemsToSelectList, final int[] postion, final EditText genderEditText) {
        AlertDialog.Builder singlechoicedialog = new AlertDialog.Builder(activity);
        singlechoicedialog.setTitle(dialogTitle);
        singlechoicedialog.setSingleChoiceItems(itemsToSelectList, -1, new DialogInterface.OnClickListener() {
            public void onClck(DialogInterface dialog, int item) {
                postion[0] = item;
                String value = itemsToSelectList[item].toString();
                genderEditText.setText(value);
                dialog.cancel();
            }
        });

        AlertDialog alert_dialog = singlechoicedialog.create();
        alert_dialog.show();
        alert_dialog.getListView().setItemChecked(postion[0], true);
    }

    /*
    * opens a datedialog on focus to an edit text
    */
    public static void onFocusOpenDateDialog(final Activity activity, final EditText txtDate) {
        if (txtDate != null) {
            txtDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                public void onFocusChange(View view, boolean hasfocus) {
                    if (hasfocus) {
                        DateDialog dialog = new DateDialog(view);
                        FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
                        dialog.show(ft, "DatePicker");
                    }
                }
            });
        }
    }

    /*
    * opens a date picker on focus to an edit text
    */
    public static void onFocusOpenDatePicker(final Activity activity, final EditText txtDate) {
        if (txtDate != null) {
            txtDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                public void onFocusChange(View view, boolean hasfocus) {
                    if (hasfocus) {
                        DatePicker dialog = new DatePicker(view);
                        FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
                        dialog.show(ft, "DatePicker");
                    }
                }
            });
        }
    }

    /*
    * opens a time picker on focus to an edit text
    */
    public static void onFocusOpenTimePicker(final Activity activity, final EditText txtDate) {
        if (txtDate != null) {
            txtDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                TimePickerDialog.OnTimeSetListener t = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String hour = hourOfDay+"";
                        if(hourOfDay<10){
                            hour = "0"+hourOfDay;
                        }
                        String min = minute+"";
                        if (minute < 10)
                            min = "0"+minute;
                        txtDate.setText(hour + ":" + min);
                    }
                };

                public void onFocusChange(View view, boolean hasfocus) {
                    if (hasfocus) {
                        Calendar c = Calendar.getInstance();
                        new TimePickerDialog(activity, t, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show();
                    }
                }});
            }

        }
}
