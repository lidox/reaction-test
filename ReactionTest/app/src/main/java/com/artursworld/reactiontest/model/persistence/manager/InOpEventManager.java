package com.artursworld.reactiontest.model.persistence.manager;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.artursworld.reactiontest.controller.util.UtilsRG;
import com.artursworld.reactiontest.model.entity.InOpEvent;
import com.artursworld.reactiontest.model.entity.MedicalUser;
import com.artursworld.reactiontest.model.persistence.EntityDbManager;
import com.artursworld.reactiontest.model.persistence.contracts.DBContracts;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class InOpEventManager extends EntityDbManager {

    public static final String SORT_DESC = "DESC";
    public static final String SORT_ASC = "ASC";

    public InOpEventManager(Context context) {
        super(context);
    }


    /**
     * Insert In-OP-Event into database
     *
     * @param event the event to insert
     */
    public void insertEvent(InOpEvent event) {
        if (event == null) {
            UtilsRG.error("the event to insert is null!");
            return;
        }

        ContentValues values = getEventContentValues(event);

        try {
            database.insertOrThrow(DBContracts.InOpEventTable.TABLE_NAME, null, values);
            UtilsRG.info("New In-OP-Event added successfully:" + event.toString());
        } catch (Exception e) {
            UtilsRG.error("Could not insert new In-OP-Event into db: " + event.toString() + "! " + e.getLocalizedMessage());
        }
    }

    @NonNull
    private ContentValues getEventContentValues(InOpEvent event) {
        ContentValues values = new ContentValues();

        if (event.getTimeStamp() != null)
            values.put(DBContracts.InOpEventTable.TIMESTAMP, UtilsRG.dateFormat.format(event.getTimeStamp()));

        if (event.getOperationIssue() != null)
            values.put(DBContracts.InOpEventTable.OPERATION_ISSUE, event.getOperationIssue());

        if (event.getType() != null)
            values.put(DBContracts.InOpEventTable.TYPE, event.getType());

        if (event.getAdditionalNote() != null)
            values.put(DBContracts.InOpEventTable.ADDITIONAL_NOTE, event.getAdditionalNote());
        return values;
    }

    public List<InOpEvent> getInOpEventListByOperationIssue(String operationIssue, String sortingOrder) {
        if (operationIssue == null) {
            UtilsRG.error("cannot get In-Op-Events by operationIssue, because operationIssue = null");
            return null;
        }

        String sortOrder = DBContracts.InOpEventTable.TIMESTAMP + " " + sortingOrder;
        List<InOpEvent> eventList = new ArrayList<InOpEvent>();
        String WHERE_CLAUSE = DBContracts.InOpEventTable.OPERATION_ISSUE + " like '" + operationIssue + "'";
        Cursor cursor = database.query(DBContracts.InOpEventTable.TABLE_NAME,
                new String[]{
                        DBContracts.InOpEventTable.OPERATION_ISSUE,
                        DBContracts.InOpEventTable.ADDITIONAL_NOTE,
                        DBContracts.InOpEventTable.TYPE,
                        DBContracts.InOpEventTable.TIMESTAMP,
                }, WHERE_CLAUSE, null, null, null, sortOrder);

        while (cursor.moveToNext()) {
            String operationIssueName = cursor.getString(0);
            String note = cursor.getString(1);
            String type = cursor.getString(2);
            Date timeStamp = null;

            try {
                timeStamp = (UtilsRG.dateFormat.parse(cursor.getString(3)));
            } catch (Exception e) {
                UtilsRG.info("Could not parse the date of the event, while try to read the event from database: " + e.getLocalizedMessage());
            }

            InOpEvent event = new InOpEvent(operationIssueName, timeStamp, type, note);
            eventList.add(event);
        }
        UtilsRG.info(eventList.size() + ". In-OP-Events has been found:");
        UtilsRG.info(eventList.toString());

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        UtilsRG.info(eventList.size() + ". Events loaded for operationIssue("+operationIssue+")");
        return eventList;
    }

    public int deleteEvent(InOpEvent event) {
        int resultCode = 0;

        // validation
        if (event == null)
            return resultCode;

        String WHERE_CLAUSE = DBContracts.InOpEventTable.TIMESTAMP + " =?";
        try {
            resultCode = database.delete(
                    DBContracts.InOpEventTable.TABLE_NAME,
                    WHERE_CLAUSE,
                    new String[]{UtilsRG.dateFormat.format(event.getTimeStamp())}
            );
            UtilsRG.info("Event has been deleted from database: " + event);
        } catch (Exception e) {
            UtilsRG.error("Exception! Could not delete from database: " + event + " " + e.getLocalizedMessage());
        }

        return resultCode;
    }

    //TODO: does not work yet. But don't know why
    public int updateEvent(InOpEvent event) {
        try {
            ContentValues values = getEventContentValues(event);

            String whereClause = DBContracts.InOpEventTable.TIMESTAMP + "= ?";
            String timeStamp = UtilsRG.dateFormat.format(event.getTimeStamp());
            int resultCode = database.update(DBContracts.InOpEventTable.TABLE_NAME, values, whereClause, new String[]{timeStamp});
            UtilsRG.info("updated " + resultCode + ". Events. " + event.toString());
            return resultCode;
        } catch (Exception e) {
            UtilsRG.info("Exception! Could not update event" + event.toString() + " " + e.getLocalizedMessage());
            e.printStackTrace();
            return 0;
        }


        /*
        // Build SQL Query
        String sql = "UPDATE " + DBContracts.InOpEventTable.TABLE_NAME + " SET "
                + DBContracts.InOpEventTable.TYPE + " = '" + event.getType()+"'"


                + " WHERE " + DBContracts.InOpEventTable.TIMESTAMP + " = ?";
        // Execute query
        String date = UtilsRG.dateFormat.format(event.getTimeStamp());
        UtilsRG.info("date= "+date);
        database.execSQL(sql, new String[]{date});
        return 1;
        */
    }

}