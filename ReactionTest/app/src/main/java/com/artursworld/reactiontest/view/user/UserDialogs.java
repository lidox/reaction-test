package com.artursworld.reactiontest.view.user;


import android.app.Activity;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.artursworld.reactiontest.R;
import com.artursworld.reactiontest.controller.helper.Gender;
import com.artursworld.reactiontest.controller.util.UtilsRG;
import com.artursworld.reactiontest.model.entity.MedicalUser;
import com.artursworld.reactiontest.model.persistence.manager.MedicalUserManager;
import com.artursworld.reactiontest.view.dialogs.DialogHelper;

import java.text.ParseException;

public class UserDialogs {

    /**
     * Opens an Edit-User-Dialog to edit selected user
     * @param userId the user to edit
     * @param activity the running activity
     * @return the edit dialog
     */
    public static MaterialDialog openEditUserDialog(final String userId, final Activity activity) {
        final MaterialDialog dialog = new MaterialDialog.Builder(activity)
                .title(R.string.edit_user)
                .customView(R.layout.dialog_edit_user, true)
                .positiveText(R.string.save)
                .negativeText(R.string.cancel)
                .show();

        setUIValuesByUser(userId, activity, dialog);
        return dialog;
    }

    private static void setUIValuesByUser(final String userId, final Activity activity, MaterialDialog dialog) {
        try {

            View dialogView = dialog.getCustomView();
            final EditText birthDate = (EditText) dialogView.findViewById(R.id.x_edit_user_birthdate_txt);
            final EditText name = (EditText) dialogView.findViewById(R.id.x_edit_user);
            final EditText genderEditText = initGenderEditText(activity, dialogView);
            final EditText bmi = (EditText) dialogView.findViewById(R.id.x_edit_user_bmi_txt);
            final MedicalUser mUser = new MedicalUser();

            birthDate.setInputType(InputType.TYPE_NULL);
            DialogHelper.onFocusOpenDatePicker(activity, birthDate);
            new AsyncTask<Void, Void, MedicalUser>() {
                @Override
                protected MedicalUser doInBackground(Void... params) {
                    return new MedicalUserManager(activity).getUserByMedicoId(userId);
                }

                @Override
                protected void onPostExecute(MedicalUser user) {
                    super.onPostExecute(user);
                    name.setText(user.getMedicalId());
                    genderEditText.setText(user.getGender().toString());
                    bmi.setText(user.getBmi() + "");
                    birthDate.setText(user.getBirthDateAsString());
                    mUser.setCreationDate(user.getCreationDate());
                    mUser.setMarkedAsDeleted(user.isMarkedAsDeleted());
                    mUser.setUpdateDate(user.getUpdateDate());
                    mUser.setMedicalId(user.getMedicalId());
                    mUser.setGender(user.getGender());
                    mUser.setBirthDate(user.getBirthDate());
                }
            }.execute();

            dialog.getBuilder().onPositive(new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog1, @NonNull DialogAction which) {
                    updateUserByUI(mUser, name, birthDate, bmi, genderEditText, activity);
                    activity.finish();
                    activity.startActivity(activity.getIntent());
                }
            });

        } catch (Exception e){
            UtilsRG.error(e.getLocalizedMessage());
        }
    }

    private static void updateUserByUI(MedicalUser mUser, EditText name, EditText birthdate, EditText bmi, EditText genderEditText, Activity activity) {
        UtilsRG.info("'edit user' button clicked");
        mUser.setMedicalId(name.getText().toString());
        try {
            java.util.Date d =  UtilsRG.germanDateFormat.parse(birthdate.getText().toString());
            mUser.setBirthDate(d);
        } catch (ParseException e) {
            UtilsRG.error(e.getLocalizedMessage());
        }
        mUser.setBmi(Double.parseDouble(bmi.getText().toString()));
        String textgender = genderEditText.getText().toString();
        Gender g = Gender.findByName(textgender);
        mUser.setGender(g);
        new MedicalUserManager(activity).updateMedicalUser(mUser);
    }

    @NonNull
    private static EditText initGenderEditText(final Activity activity, View dialogView) {
        final EditText genderEditText = (EditText) dialogView.findViewById(R.id.x_gender_txt);
        genderEditText.setInputType(InputType.TYPE_NULL);
        final String titleGender = activity.getResources().getString(R.string.gender);
        final CharSequence[] maleOrFemaleList = {activity.getResources().getString(R.string.male), activity.getResources().getString(R.string.female)};

        Runnable task = new Runnable() {
            @Override
            public void run() {

                AlertDialog.Builder singleChoiceDialog = new AlertDialog.Builder(activity);
                singleChoiceDialog.setTitle(titleGender);
                final int[] selectedGenderIndex = new int[1];
                singleChoiceDialog.setSingleChoiceItems(maleOrFemaleList, -1, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int clickedPosition) {
                        selectedGenderIndex[0] = clickedPosition;
                        String value = maleOrFemaleList[clickedPosition].toString();
                        genderEditText.setText(value);
                        UtilsRG.info("checked gender at position: " +clickedPosition);
                        dialog.cancel();
                    }
                });
                AlertDialog alert_dialog = singleChoiceDialog.create();
                alert_dialog.show();
                alert_dialog.getListView().setItemChecked(selectedGenderIndex[0], true);
            }


        };
        DialogHelper.onFocusOpenSingleChoiceDialog(activity, genderEditText, titleGender, maleOrFemaleList, task);
        return genderEditText;
    }
}
