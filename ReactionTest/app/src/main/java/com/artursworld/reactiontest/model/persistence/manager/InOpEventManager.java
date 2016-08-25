package com.artursworld.reactiontest.model.persistence.manager;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.artursworld.reactiontest.controller.util.UtilsRG;
import com.artursworld.reactiontest.model.entity.InOpEvent;
import com.artursworld.reactiontest.model.persistence.EntityDbManager;
import com.artursworld.reactiontest.model.persistence.contracts.DBContracts;

import java.util.Date;

public class InOpEventManager extends EntityDbManager {

    public InOpEventManager(Context context) {
        super(context);
    }


    /**
     * Insert In-OP-Event into database
     *
     * @param event the event to insert
     */
    public void insertEvent(InOpEvent event) {
        if(event == null){
            UtilsRG.error("the event to insert is null!");
            return;
        }

        ContentValues values = new ContentValues();

        if (event.getTimeStamp() != null)
            values.put(DBContracts.InOpEventTable.TIMESTAMP, UtilsRG.dateFormat.format(event.getTimeStamp()));

        if (event.getOperationIssue() != null)
            values.put(DBContracts.InOpEventTable.OPERATION_ISSUE, event.getOperationIssue());

        if (event.getType() != null)
            values.put(DBContracts.InOpEventTable.TYPE, event.getType());

        if (event.getAdditionalNote() != null)
            values.put(DBContracts.InOpEventTable.ADDITIONAL_NOTE, event.getAdditionalNote());

        try {
            database.insertOrThrow(DBContracts.InOpEventTable.TABLE_NAME, null, values);
            UtilsRG.info("New In-OP-Event added successfully:" + event.toString());
        } catch (Exception e) {
            UtilsRG.error("Could not insert new In-OP-Event into db: " + event.toString() + "! " + e.getLocalizedMessage());
        }
    }

    //TODO: change or delete. this is just a snippet to have an example
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