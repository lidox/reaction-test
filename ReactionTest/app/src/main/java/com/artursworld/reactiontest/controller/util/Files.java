package com.artursworld.reactiontest.controller.util;

import android.content.Intent;
import android.net.Uri;

import com.artursworld.reactiontest.R;

import java.io.File;
import java.util.Date;

public class Files {

    /**
     * Share file via chooser
     *
     * @param file the file to share
     */
    public static void share(File file) {

        if (file != null) {

            Uri u1 = null;
            u1 = Uri.fromFile(file);
            Intent sendIntent = new Intent(Intent.ACTION_SEND);

            // get subject
            StringBuilder builder = new StringBuilder();
            builder.append(Strings.getStringByRId(R.string.app_name));
            builder.append(" ");
            builder.append(Strings.getStringByRId(R.string.exported_at));
            builder.append(" ");
            builder.append(UtilsRG.dayAndhourFormat.format(new Date()));
            String subject = builder.toString();


            sendIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
            sendIntent.putExtra(Intent.EXTRA_STREAM, u1);
            sendIntent.setType("text/html");
            Intent intent = Intent.createChooser(sendIntent, Strings.getStringByRId(R.string.share));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            App.getAppContext().startActivity(intent);
        }
    }
}
