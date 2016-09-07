package com.artursworld.reactiontest.view.user;

import android.app.Activity;

import com.afollestad.materialdialogs.MaterialDialog;
import com.artursworld.reactiontest.R;
import com.artursworld.reactiontest.model.entity.Medicament;

public class MedicamentDetailDialog {

    private Activity activity = null;
    private Medicament medicament = null;
    private boolean needToCreateMedicament = true;

    public MedicamentDetailDialog(Activity activity, Medicament medicament) {
        this.activity = activity;
        this.medicament = medicament;
        if(medicament != null){
            needToCreateMedicament = false;
        }

        showDialog();
    }

    private MaterialDialog showDialog(){
        if(needToCreateMedicament){
            return new MaterialDialog.Builder(activity)
                    .title(R.string.medicament)
                    .customView(R.layout.dialog_medicament, true)
                    .negativeText(R.string.cancel)
                    .positiveText(R.string.save)
                    .show();
        }
        else {
            return null;
        }
    }
}
