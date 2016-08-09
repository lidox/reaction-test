package com.artursworld.reactiontest.model.persistence.manager;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

import com.artursworld.reactiontest.controller.util.UtilsRG;
import com.artursworld.reactiontest.model.entity.MedicalUser;
import com.artursworld.reactiontest.model.entity.OperationIssue;
import com.artursworld.reactiontest.model.persistence.EntityDbManager;
import com.artursworld.reactiontest.model.persistence.contracts.DBContracts;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/*
* Manages database issues for users operations
*/
public class OperationIssueManager extends EntityDbManager {

    public OperationIssueManager(Context context) {
        super(context);
    }

    /*
    * inserts an operation issue async
    */
    public void insertOperationIssueByMedIdAsync(final String medUserId, final String operationName) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... unusedParams) {
                ContentValues values = new ContentValues();
                values.put(DBContracts.OperationIssueTable.MEDICAL_USER_ID, medUserId);
                values.put(DBContracts.OperationIssueTable.OPERATION_ISSUE_NAME, operationName);
                values.put(DBContracts.OperationIssueTable.CREATION_DATE, UtilsRG.dateFormat.format(new Date()));
                values.put(DBContracts.OperationIssueTable.UPDATE_DATE, UtilsRG.dateFormat.format(new Date()));
                try {
                    database.insertOrThrow(DBContracts.OperationIssueTable.TABLE_NAME, null, values);
                    UtilsRG.info("new operation issue created successfully for user: " + medUserId);
                } catch (Exception e) {
                    UtilsRG.error("Could not insert operation issue into db for user(" + medUserId + ")" + e.getLocalizedMessage());
                }
                return null;
            }
        }.execute();
    }

    /*
    * Returns all operations by user id
    */
    public List<OperationIssue> getAllOperationIssuesByMedicoId(String medicoId) {
        String sortOrder = DBContracts.OperationIssueTable.CREATION_DATE + " DESC";
        List<OperationIssue> operationIssuesList = new ArrayList<OperationIssue>();
        Cursor cursor = database.query(DBContracts.OperationIssueTable.TABLE_NAME,
                new String[]{
                        //DBContracts.OperationIssueTable._ID,
                        DBContracts.OperationIssueTable.MEDICAL_USER_ID,
                        DBContracts.OperationIssueTable.OPERATION_ISSUE_NAME,
                        DBContracts.OperationIssueTable.CREATION_DATE,
                        DBContracts.OperationIssueTable.UPDATE_DATE,
                        DBContracts.OperationIssueTable.INTUBATION_TIME,
                        DBContracts.OperationIssueTable.WAKE_UP_TIME,
                        DBContracts.OperationIssueTable.NARCOSIS_DURATION
                },
                DBContracts.OperationIssueTable.MEDICAL_USER_ID + " like '" + medicoId + "'",
                null, null, null, sortOrder);

        while (cursor.moveToNext()) {
            OperationIssue op = new OperationIssue();
            try {
                //op.set_ID(cursor.getLong(0));
                op.setMedicalUserId(cursor.getString(0));
                if (cursor.getString(1) != null)
                    op.setDisplayName(cursor.getString(1));
                if (cursor.getString(2) != null)
                    op.setCreationDate(UtilsRG.dateFormat.parse(cursor.getString(2)));
                if ((cursor.getString(3)) != null)
                    op.setUpdateDate(UtilsRG.dateFormat.parse(cursor.getString(3)));
                if ((cursor.getString(4)) != null)
                    op.setIntubationDate(UtilsRG.dateFormat.parse(cursor.getString(4)));
                if ((cursor.getString(5)) != null)
                    op.setWakeUpDate(UtilsRG.dateFormat.parse(cursor.getString(5)));
                op.setNarcosisDuration(cursor.getDouble(6));
            } catch (Exception e) {
                UtilsRG.error("Failed to get MedUser(" + medicoId + ") by MedicalID: " + e.getLocalizedMessage());
            }
            operationIssuesList.add(op);
        }

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return operationIssuesList;
    }

    /*
     * Returns the date of the operation by operation name (its primary key)
    */
    public Date getDateByOperationIssue(String operationIssueName, String tableRow) {
        if (operationIssueName != null) {
            Cursor cursor = null;
            try {
                String WHERE_CLAUSE = DBContracts.OperationIssueTable.OPERATION_ISSUE_NAME + " like '" + operationIssueName + "'";

                cursor = database.query(
                        DBContracts.OperationIssueTable.TABLE_NAME,
                        new String[]{tableRow},
                        WHERE_CLAUSE,
                        null, null, null, null);
                cursor.moveToFirst();
                Date date = UtilsRG.dateFormat.parse(cursor.getString(0));
                UtilsRG.info("Got "+tableRow +" by OperationIssueName(" + operationIssueName + ")");
                return date;
                //return UtilsRG.germanDateFormat.format(date);
            } catch (Exception e) {
                UtilsRG.error("Exception at getting OperationDate: " + e.getLocalizedMessage());
            } finally {
                try {
                    if (cursor != null)
                        cursor.close();
                } catch (Exception e) {
                    UtilsRG.error(e.getLocalizedMessage());
                }
                if (cursor != null && !cursor.isClosed()) {
                    cursor.close();
                }
            }

        }
        return null;
    }


    /*
    * Returns the intubation time of the user
    */
    public String getIntubationDateByOperationIssue(String operationIssueName) {
        if (operationIssueName != null) {
            UtilsRG.info("try to getIntubationTimeByOperationIssue(" + operationIssueName + ")");
            Cursor cursor = null;
            try {
                String WHERE_CLAUSE = null;
                if (operationIssueName != null) {
                    WHERE_CLAUSE = DBContracts.OperationIssueTable.OPERATION_ISSUE_NAME + " like '" + operationIssueName + "'";
                }
                cursor = database.query(DBContracts.OperationIssueTable.TABLE_NAME,
                        new String[]{DBContracts.OperationIssueTable.INTUBATION_TIME},
                        WHERE_CLAUSE,
                        null, null, null, null);

                cursor.moveToFirst();
                return UtilsRG.germanDateFormat.parse(cursor.getString(0)).toString();
            } catch (Exception e) {
                UtilsRG.error("Exception at getIntubationTimeByOperationIssue. " + e.getLocalizedMessage());
            } finally {
                try {
                    if (cursor != null)
                        cursor.close();
                } catch (Exception e) {
                    UtilsRG.error(e.getLocalizedMessage());
                }
                if (cursor != null && !cursor.isClosed()) {
                    cursor.close();
                }
            }

        }
        return null;
    }

    /*
    * Updates the operation date by operation name
    */
    public void updateOperationDateByOperationIssue(String operationIssueName, Date operationDate, String dateTableRow) {
        ContentValues valuesToUpdate = new ContentValues();
        try {
            valuesToUpdate.put(dateTableRow, UtilsRG.dateFormat.format(operationDate));
            database.update(
                    DBContracts.OperationIssueTable.TABLE_NAME,
                    valuesToUpdate,
                    DBContracts.OperationIssueTable.OPERATION_ISSUE_NAME + "= ?", new String[]{operationIssueName});
            UtilsRG.info("Updated " +dateTableRow+" by OperationIssue(" + operationIssueName + ")=" + operationDate);
        } catch (Exception e) {
            UtilsRG.error("Exception! Could not update "+dateTableRow+"(" + operationDate + ") for OperationIssue(" + operationIssueName + ") " + e.getLocalizedMessage());
        }
    }

    /**
     * public void updateAverageReactionTimeById(String creationDateId, double averageReactionTime){
     * ContentValues valuesToUpdate = new ContentValues();
     * valuesToUpdate.put(DBContracts.ReactionGame.COLUMN_NAME_AVERAGE_REACTION_TIME, averageReactionTime);
     * try {
     * database.update(DBContracts.ReactionGame.TABLE_NAME, valuesToUpdate, DBContracts.ReactionGame.COLUMN_NAME_CREATION_DATE + "= ?", new String[] {creationDateId});
     * UtilsRG.info("Updated averageReactionTime("+averageReactionTime+") for reactionGame with creatioDate[Id]("+creationDateId+")");
     * }
     * catch (Exception e){
     * UtilsRG.error("Exception! Could not update new average ReactionTime! " + e.getLocalizedMessage());
     * }
     * }
     */

    // Usefull class to get all operations issues by user async
    public interface AsyncResponse {
        void getAllOperationIssuesByMedicoId(List<OperationIssue> operationIssuesList);
    }

    public static class getAllOperationIssuesByMedicoIdAsync extends AsyncTask<String, Void, List<OperationIssue>> {
        public AsyncResponse delegate = null;
        private Context context;

        public getAllOperationIssuesByMedicoIdAsync(AsyncResponse delegate, Context c) {
            this.context = c;
            this.delegate = delegate;
        }

        @Override
        protected List<OperationIssue> doInBackground(String... medicoIds) {
            List<OperationIssue> operationsList = new ArrayList<OperationIssue>();
            String medId = medicoIds[0];
            if (medId != null) {
                OperationIssueManager dbManager = new OperationIssueManager(context);
                operationsList = dbManager.getAllOperationIssuesByMedicoId(medId);
            }
            return operationsList;
        }

        @Override
        protected void onPostExecute(List<OperationIssue> result) {
            super.onPostExecute(result);
            delegate.getAllOperationIssuesByMedicoId(result);
        }
    }

}

