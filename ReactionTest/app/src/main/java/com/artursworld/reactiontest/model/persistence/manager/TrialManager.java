package com.artursworld.reactiontest.model.persistence.manager;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;

import com.artursworld.reactiontest.controller.util.UtilsRG;
import com.artursworld.reactiontest.model.persistence.EntityDbManager;
import com.artursworld.reactiontest.model.persistence.contracts.DBContracts;

import java.util.Date;

public class TrialManager extends EntityDbManager{

    public TrialManager(Context context) {
        super(context);
    }


    public void insertTrialtoReactionGameAsync(final String reactionGameCreationTime, final boolean isValid, final double reactionTime){
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                ContentValues values = new ContentValues();
                values.put(DBContracts.TrialTable.CREATION_DATE, UtilsRG.dateFormat.format(new Date()));
                values.put(DBContracts.TrialTable.IS_VALID, isValid);
                values.put(DBContracts.TrialTable.REACTION_TIME, reactionTime);
                values.put(DBContracts.TrialTable.PK_REACTIONGAME_CREATION_DATE, reactionGameCreationTime);

                try {
                    database.insertOrThrow(DBContracts.TrialTable.TABLE_NAME, null, values);
                    UtilsRG.info("New trial added to reaction game with reaction time("+reactionTime+") successfully");
                }
                catch (Exception e){
                    UtilsRG.error("Could not insert trial into db for reaction game("+reactionGameCreationTime +")" + e.getLocalizedMessage());
                }
                return null;
            }
        }.execute();
    }

}
