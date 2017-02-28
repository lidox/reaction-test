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
                insertOperationIssueByMedId(medUserId, operationName);
                return null;
            }
        }.execute();
    }

    public void insertOperationIssueByMedId(String medUserId, String operationName) {
        ContentValues values = new ContentValues();
        values.put(DBContracts.OperationIssueTable.MEDICAL_USER_ID, medUserId);
        values.put(DBContracts.OperationIssueTable.OPERATION_ISSUE_NAME, operationName);
        values.put(DBContracts.OperationIssueTable.CREATION_DATE, UtilsRG.dateFormat.format(new Date()));
        values.put(DBContracts.OperationIssueTable.UPDATE_DATE, UtilsRG.dateFormat.format(new Date()));
        try {
            database.insertOrThrow(DBContracts.OperationIssueTable.TABLE_NAME, null, values);
            UtilsRG.info("new operation issue created successfully for user: " + medUserId);
        } catch (Exception e) {
            UtilsRG.error("Could not insert operation issue into db for user(" + medUserId + ") " + e.getLocalizedMessage());
        }
    }

    /*
    * Returns all operations by user id
    */
    public List<OperationIssue> getAllOperationIssuesByMedicoId(String medicoId) {
        String sortOrder = DBContracts.OperationIssueTable.CREATION_DATE + " DESC";
        List<OperationIssue> operationIssuesList = new ArrayList<OperationIssue>();
        Cursor cursor = database.query(DBContracts.OperationIssueTable.TABLE_NAME,
                new String[]{
                        DBContracts.OperationIssueTable.MEDICAL_USER_ID,
                        DBContracts.OperationIssueTable.OPERATION_ISSUE_NAME,
                        DBContracts.OperationIssueTable.CREATION_DATE,
                        DBContracts.OperationIssueTable.UPDATE_DATE,
                },
                DBContracts.OperationIssueTable.MEDICAL_USER_ID + " like '" + medicoId + "'",
                null, null, null, sortOrder);

        while (cursor.moveToNext()) {
            OperationIssue op = new OperationIssue();
            try {
                op.setMedicalUserId(cursor.getString(0));
                if (cursor.getString(1) != null)
                    op.setDisplayName(cursor.getString(1));
                if (cursor.getString(2) != null)
                    op.setCreationDate(UtilsRG.dateFormat.parse(cursor.getString(2)));
                if ((cursor.getString(3)) != null)
                    op.setUpdateDate(UtilsRG.dateFormat.parse(cursor.getString(3)));

            } catch (Exception e) {
                UtilsRG.error("Failed to get operation issue by MedUser(" + medicoId + ") " + e.getLocalizedMessage());
            }
            operationIssuesList.add(op);
        }
        UtilsRG.info("Got " +operationIssuesList.size() + " operation issues for user(" + medicoId +")");

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
                if(cursor.getString(0) != null){
                    Date date = UtilsRG.dateFormat.parse(cursor.getString(0));
                    UtilsRG.info("Got "+tableRow +" by OperationIssueName(" + operationIssueName + ")");
                    return date;
                }
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

