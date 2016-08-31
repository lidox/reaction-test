package com.artursworld.reactiontest.view.games;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;

import com.artursworld.reactiontest.controller.util.UtilsRG;

public class MediaButtonIntentReceiver extends BroadcastReceiver {

    public MediaButtonIntentReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String intentAction = intent.getAction();
        if (!Intent.ACTION_MEDIA_BUTTON.equals(intentAction)) {
            return;
        }
        UtilsRG.info("Ooh a media button has been clicked");
        KeyEvent event = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
        if (event == null) {
            return;
        }
        int action = event.getAction();
        if (action == KeyEvent.ACTION_DOWN) {
            // do something
            //TastyToast.makeText(context, "BUTTON PRESSED!", TastyToast.LENGTH_SHORT).show();
            UtilsRG.info("clicked media button!!");
        }
        abortBroadcast();
    }
}