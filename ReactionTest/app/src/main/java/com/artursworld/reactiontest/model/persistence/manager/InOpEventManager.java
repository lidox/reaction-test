package com.artursworld.reactiontest.model.persistence.manager;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.artursworld.reactiontest.controller.util.UtilsRG;
import com.artursworld.reactiontest.model.persistence.EntityDbManager;
import com.artursworld.reactiontest.model.persistence.contracts.DBContracts;

import java.util.Date;

public class InOpEventManager extends EntityDbManager {

    public InOpEventManager(Context context) {
        super(context);
    }





    /*
    * Inserts a trial to reaction a reaction game by reaction game id async
    */
    public void insertTrialtoReactionGameAsync(final String reactionGameCreationTime, final boolean isValid, final double reactionTime) {
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
}