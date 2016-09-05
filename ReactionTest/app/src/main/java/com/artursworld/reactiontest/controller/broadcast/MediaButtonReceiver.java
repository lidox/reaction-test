package com.artursworld.reactiontest.controller.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;

import com.artursworld.reactiontest.controller.util.UtilsRG;

public class MediaButtonReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        String intentAction = intent.getAction();
        UtilsRG.info("GOT EVENT");
        if (Intent.ACTION_MEDIA_BUTTON.equals(intentAction)) {
            KeyEvent event = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);

            if (event == null) {
                return;
            }
            UtilsRG.info("GOT EVENT INSIDE");
            int keycode = event.getKeyCode();
            int action = event.getAction();

            if (keycode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE || keycode == KeyEvent.KEYCODE_HEADSETHOOK) {
                if (action == KeyEvent.ACTION_DOWN) {
                    // Start your app here!
                    UtilsRG.info("IT WORKS!!!!!! :))");
                    // ...

                    if (isOrderedBroadcast()) {
                        abortBroadcast();
                    }
                }
            }
        }
    }
}