package com.artursworld.reactiontest.model.persistence.manager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

import com.artursworld.reactiontest.controller.helper.Type;
import com.artursworld.reactiontest.controller.util.UtilsRG;
import com.artursworld.reactiontest.model.entity.OperationIssue;
import com.artursworld.reactiontest.model.entity.ReactionGame;
import com.artursworld.reactiontest.model.persistence.EntityDbManager;
import com.artursworld.reactiontest.model.persistence.contracts.DBContracts;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/*
* Manages trials via database
*/
public class TrialManager extends EntityDbManager {

    public TrialManager(Context context) {
        super(context);
    }

    /*
    * Inserts a trial to reaction a reaction game by reaction game id async
    */
    public void insertTrialtoReactionGameAsync(final String reactionGameCreationTime, final boolean isValid, final double reactionTime) {
        if(reactionTime > 0) {
            new AsyncTask<Void, Void, Void>() {

                @Override
                protected Void doInBackground(Void... voids) {
                    ContentValues values = new ContentValues();
                    values.put(DBContracts.TrialTable.CREATION_DATE, UtilsRG.dateFormat.format(new Date()));
                    values.put(DBContracts.TrialTable.IS_VALID, (isValid) ? 1 : 0);
                    values.put(DBContracts.TrialTable.REACTION_TIME, reactionTime);
                    values.put(DBContracts.TrialTable.PK_REACTIONGAME_CREATION_DATE, reactionGameCreationTime);
                    try {
                        database.insertOrThrow(DBContracts.TrialTable.TABLE_NAME, null, values);
                        UtilsRG.info("New trial added to reaction game(" + reactionGameCreationTime + ") with reaction time(" + reactionTime + ") successfully");
                    } catch (Exception e) {
                        UtilsRG.error("Could not insert trial into db for reaction game(" + reactionGameCreationTime + ")" + e.getLocalizedMessage());
                    }
                    return null;
                }
            }.execute();
        }
    }

    /*
    * returns filtered value e.g. AVG for a reaction game
    */
    public double getFilteredReactionTimeByReactionGameId(String reactionGameId, String filter, boolean isValid) {
        Cursor cursor = null;
        try {
            String WHERE_CLAUSE = null;
            if (reactionGameId != null) {
                WHERE_CLAUSE = DBContracts.TrialTable.PK_REACTIONGAME_CREATION_DATE + " like '" + reactionGameId + "' ";
                WHERE_CLAUSE += "AND " + DBContracts.TrialTable.IS_VALID + " = " + ((isValid) ? 1 : 0);
            }
            cursor = database.query(DBContracts.TrialTable.TABLE_NAME,
                    new String[]{filter + "(" + DBContracts.TrialTable.REACTION_TIME + ")"},
                    WHERE_CLAUSE,
                    null, null, null, null);

            cursor.moveToFirst();
            UtilsRG.info("Get via database the filtered(" + filter + ") and only valid trails reaction time  by reactionGameId(" + reactionGameId + ")= " + cursor.getDouble(0));
            return cursor.getDouble(0);
        } catch (Exception e) {
            UtilsRG.error("could not get filtered reaction time. " + e.getLocalizedMessage());
        } finally {
            try {
                if (cursor != null)
                    cursor.close();
            } catch (Exception e) {
                UtilsRG.error(e.getLocalizedMessage());
            }
        }
        return -1;
    }

    public List<Integer> getAllReactionTimesList(String reactionGameId) {
        List<Integer> times = new ArrayList<Integer>();
        Cursor cursor = null;
        try {
            String WHERE_CLAUSE = null;
            if (reactionGameId != null) {
                WHERE_CLAUSE = DBContracts.TrialTable.PK_REACTIONGAME_CREATION_DATE + " like '" + reactionGameId + "' ";
                WHERE_CLAUSE += "AND " + DBContracts.TrialTable.IS_VALID + " = " + ((true) ? 1 : 0);
            }
            cursor = database.query(DBContracts.TrialTable.TABLE_NAME,
                    new String[]{DBContracts.TrialTable.REACTION_TIME},
                    WHERE_CLAUSE,
                    null, null, null, null);

            while (cursor.moveToNext()) {
                float reactionTime = cursor.getFloat(0);
                int roundedInt = Math.round(reactionTime * 1000);
                times.add(roundedInt);
            }
        } catch (Exception e) {
            UtilsRG.error("lol: " + e.getLocalizedMessage());
        } finally {
            try {
                if (cursor != null)
                    cursor.close();
            } catch (Exception e) {
                UtilsRG.error("lol2:" + e.getLocalizedMessage());
            }
        }
        return times;
    }
}
