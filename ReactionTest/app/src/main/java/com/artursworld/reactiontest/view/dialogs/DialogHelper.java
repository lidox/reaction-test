package com.artursworld.reactiontest.view.dialogs;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;

import com.artursworld.reactiontest.R;
import com.artursworld.reactiontest.controller.util.UtilsRG;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/*
* Helper class for dialogs e.g. on focus listeners
*/
public class DialogHelper {

    /*
    * adds a focus listener for a choice dialog
    */
    public static void onFocusOpenSingleChoiceDialog(final Activity activity, final EditText editText, final String dialogTitle, final CharSequence[] itemsToSelectList, final Runnable task) {
        final int[] position = new int[1];
        if (editText != null) {
            editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        task.run();
                    }
                }
            });
            editText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    task.run();
                    //openSingleChoiceDialog(activity, dialogTitle, itemsToSelectList, position, editText);
                }
            });
        }
    }

    /*
    * opens a dialog on focus
    */
    private static AlertDialog openSingleChoiceDialog(Activity activity, String dialogTitle, final CharSequence[] itemsToSelectList, final int[] position, final EditText genderEditText) {
        AlertDialog.Builder singleChoiceDialog = new AlertDialog.Builder(activity);
        singleChoiceDialog.setTitle(dialogTitle);
        singleChoiceDialog.setSingleChoiceItems(itemsToSelectList, -1, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int clickedPosition) {
                position[0] = clickedPosition;
                String value = itemsToSelectList[clickedPosition].toString();
                genderEditText.setText(value);
                dialog.cancel();
            }
        });

        AlertDialog alert_dialog = singleChoiceDialog.create();
        alert_dialog.show();
        alert_dialog.getListView().setItemChecked(position[0], true);
        return alert_dialog;
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
                TimePickerDialog.OnTimeSetListener listener = getOnTimeSetListener(txtDate);

                public void onFocusChange(View view, boolean hasfocus) {
                    if (hasfocus) {
                        openTimePickerDialog(activity, listener);
                    }
                }

            });

            txtDate.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    TimePickerDialog.OnTimeSetListener listener = getOnTimeSetListener(txtDate);
                    openTimePickerDialog(activity, listener);
                    return false;
                }
            });
        }
    }

    private static void openTimePickerDialog(Activity activity, TimePickerDialog.OnTimeSetListener listener) {
        Calendar c = Calendar.getInstance();
        TimePickerDialog picker = new TimePickerDialog(activity, R.style.TimePicker, listener, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true);
        picker.show();
    }

    private static TimePickerDialog.OnTimeSetListener getOnTimeSetListener(final EditText txtDate) {
        TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String hour = hourOfDay + "";
                if (hourOfDay < 10) {
                    hour = "0" + hourOfDay;
                }
                String min = minute + "";
                if (minute < 10)
                    min = "0" + minute;
                txtDate.setText(hour + ":" + min);
            }
        };
        return listener;
    }

    /**
     * Read a datetime by UI and transmit it to a @Date
     *
     * @param timeStamp          the date to set e.g. 07.07
     * @param timePickerEditText the edit text where the time is set
     * @return the combined Date of timeStamp date and the time
     */
    public static Date getDateTimeFromUI(Date timeStamp, EditText timePickerEditText) {
        String selectedTime = null;
        if (timePickerEditText != null)
            selectedTime = timePickerEditText.getText().toString();

        UtilsRG.info("selected event time: " + selectedTime);
        try {
            SimpleDateFormat format = UtilsRG.timeFormat;
            Date date = format.parse(selectedTime);
            Calendar srcCalendar = Calendar.getInstance();
            srcCalendar.setTime(date);
            int hours = srcCalendar.get(Calendar.HOUR_OF_DAY);
            int minutes = srcCalendar.get(Calendar.MINUTE);

            Calendar destCalendar = Calendar.getInstance();
            destCalendar.setTime(timeStamp);
            destCalendar.set(destCalendar.get(Calendar.YEAR), destCalendar.get(Calendar.MONTH), destCalendar.get(Calendar.DAY_OF_MONTH), hours, minutes);
            timeStamp = destCalendar.getTime();
        } catch (Exception e) {
            UtilsRG.error("Could not parse seletec time to date. " + e.getLocalizedMessage());
        }
        return timeStamp;
    }

}
