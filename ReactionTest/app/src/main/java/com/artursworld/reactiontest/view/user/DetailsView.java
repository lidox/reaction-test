package com.artursworld.reactiontest.view.user;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.artursworld.reactiontest.controller.util.UtilsRG;

/**
 * Displays details for an operation containing multiple tabs with different content
 */
public class DetailsView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        UtilsRG.info(DetailsView.class.getSimpleName()+ " onCreate");
        super.onCreate(savedInstanceState);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // If the screen is now in landscape mode, we can show the
            // dialog in-line with the list so we don't need this activity.
            finish();
            return;
        }

        if (savedInstanceState == null) {
            // During initial setup, plug in the details fragment.
            UtilsRG.info("put extras to fragment:" +getIntent().getExtras().toString());
            DetailsTabsFragment details = new DetailsTabsFragment();
            details.setArguments(getIntent().getExtras());
            getFragmentManager().beginTransaction().add(android.R.id.content, details).commit();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // super.onActivityResult is important to forward it to the fragment
        super.onActivityResult(requestCode, resultCode, data);
    }
}
