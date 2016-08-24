package com.artursworld.reactiontest.controller.broadcast;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;

import com.artursworld.reactiontest.controller.util.UtilsRG;
import com.sdsmdg.tastytoast.TastyToast;

//TODO: delete, because not used?
public class AudioButtonReactionTestReceiver extends BroadcastReceiver{
    public AudioButtonReactionTestReceiver(){
        super();
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        String intentAction = intent.getAction();

        if(!Intent.ACTION_MEDIA_BUTTON.equals(intentAction)){
            return;
        }

        KeyEvent event = (KeyEvent) intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
        if(event == null)
            return;

        int action = event.getAction();
        if(action == KeyEvent.ACTION_DOWN){
            UtilsRG.info("static");
            TastyToast.makeText(context, "audio clicked from static!", TastyToast.LENGTH_LONG, TastyToast.SUCCESS);
        }
        abortBroadcast();
    }
}
