package com.artursworld.reactiontest.view.user;


import android.app.Activity;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.EditText;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.artursworld.reactiontest.R;
import com.artursworld.reactiontest.controller.util.UtilsRG;
import com.artursworld.reactiontest.model.entity.MedicalUser;
import com.artursworld.reactiontest.model.persistence.manager.MedicalUserManager;

import java.text.ParseException;

public class UserDialogs {

    public static MaterialDialog openEditUserDialog(final String userId, final Activity activity) {
        final MaterialDialog dialog = new MaterialDialog.Builder(activity)
                .title(R.string.save)
                .customView(R.layout.dialog_edit_user, true)
                .positiveText(R.string.edit)
                .negativeText(R.string.cancel)
                .show();

        try {


            View dialogView = dialog.getCustomView();
            final EditText birthdate = (EditText) dialogView.findViewById(R.id.x_edit_user_birthdate_txt);
            final EditText name = (EditText) dialogView.findViewById(R.id.x_edit_user);
            final EditText gender = (EditText) dialogView.findViewById(R.id.x_gender_txt);
            final EditText bmi = (EditText) dialogView.findViewById(R.id.x_edit_user_bmi_txt);
            final MedicalUser mUser = new MedicalUser();
            new AsyncTask<Void, Void, MedicalUser>() {
                @Override
                protected MedicalUser doInBackground(Void... params) {
                    return new MedicalUserManager(activity).getUserByMedicoId(userId);
                }

                @Override
                protected void onPostExecute(MedicalUser user) {
                    super.onPostExecute(user);
                    name.setText(user.getMedicalId());
                    gender.setText(user.getGender().toString());
                    bmi.setText(user.getBmi() + "");
                    birthdate.setText(user.getBirthDateAsString());
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
                    UtilsRG.info("'edit user' button clicked");
                    mUser.setMedicalId(name.getText().toString());
                    try {
                        java.util.Date d =  UtilsRG.dateFormat.parse(birthdate.getText().toString());
                        mUser.setBirthDate(d);
                    } catch (ParseException e) {
                        UtilsRG.error(e.getLocalizedMessage());
                    }
                    mUser.setBmi(Double.parseDouble(bmi.getText().toString()));
                    new MedicalUserManager(activity).updateMedicalUser(mUser);

                    //user.setGender();
                }
            });

        } catch (Exception e){
            UtilsRG.error(e.getLocalizedMessage());
        }
        return dialog;
    }
}
