package com.artursworld.reactiontest.view.dialogs;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;

public class DialogHelper {

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

    private static void openSingleChoiseDialog(Activity activity, String dialogTitle, final CharSequence[] itemsToSelectList, final int[] postion, final EditText genderEditText) {
        AlertDialog.Builder singlechoicedialog = new AlertDialog.Builder(activity);
        singlechoicedialog.setTitle(dialogTitle);
        singlechoicedialog.setSingleChoiceItems(itemsToSelectList, -1, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
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

    public static void onFocusOpenDatePicker(final Activity activity, final EditText txtDate) {
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

}
