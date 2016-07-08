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
import java.util.Date;
import java.util.List;

public class OperationIssueManager extends EntityDbManager{

    public OperationIssueManager(Context context) {
        super(context);
    }

    public void insertOperationIssueByMedIdAsync(String medUserId){
        new AsyncTask<String, Void, Void>() {

            @Override
            protected Void doInBackground( String... medUser ) {
                ContentValues values = new ContentValues();
                values.put(DBContracts.OperationIssueTable.MEDICAL_USER_ID, medUser[0]);
                values.put(DBContracts.OperationIssueTable.OPERATION_ISSUE_NAME, "Operation "+UtilsRG.dateFormat.format(new Date()));
                values.put(DBContracts.OperationIssueTable.CREATION_DATE , UtilsRG.dateFormat.format(new Date()));
                values.put(DBContracts.OperationIssueTable.UPDATE_DATE , UtilsRG.dateFormat.format(new Date()));
                try {
                    database.insertOrThrow(DBContracts.OperationIssueTable.TABLE_NAME, null, values);
                    UtilsRG.info("new operation issue created successfully for user: " +medUser[0]);
                }
                catch (Exception e){
                    UtilsRG.error("Could not insert operation issue into db for user("+medUser[0] +")" + e.getLocalizedMessage());
                }
                return null;
            }
        }.execute(medUserId);
    }

    public List<OperationIssue> getAllOperationIssuesByMedicoId(String medicoId){
        List<OperationIssue> operationIssuesList = new ArrayList<OperationIssue>();
        Cursor cursor = database.query(DBContracts.OperationIssueTable.TABLE_NAME,
                new String[] {
                        DBContracts.OperationIssueTable._ID,
                        DBContracts.OperationIssueTable.MEDICAL_USER_ID,
                        DBContracts.OperationIssueTable.OPERATION_ISSUE_NAME,
                        DBContracts.OperationIssueTable.CREATION_DATE,
                        DBContracts.OperationIssueTable.UPDATE_DATE,
                        DBContracts.OperationIssueTable.INTUBATION_DATE,
                        DBContracts.OperationIssueTable.WAKE_UP_DATE,
                        DBContracts.OperationIssueTable.NARCOSIS_DURATION
                },
                DBContracts.OperationIssueTable.MEDICAL_USER_ID + " like '" +medicoId+"'",
                null, null, null, null);

        while (cursor.moveToNext()) {
            OperationIssue op = new OperationIssue();
            try {
                op.set_ID(cursor.getLong(0));
                op.setMedicalUserId(cursor.getString(1));
                if(cursor.getString(2) != null)
                    op.setDisplayName(cursor.getString(2));
                if(cursor.getString(3) != null)
                    op.setCreationDate(UtilsRG.dateFormat.parse(cursor.getString(3)));
                if((cursor.getString(4)) != null)
                    op.setUpdateDate(UtilsRG.dateFormat.parse(cursor.getString(4)));
                if((cursor.getString(5)) != null)
                    op.setIntubationDate(UtilsRG.dateFormat.parse(cursor.getString(5)));
                if((cursor.getString(6)) != null)
                    op.setWakeUpDate(UtilsRG.dateFormat.parse(cursor.getString(6)));
                op.setNarcosisDuration(cursor.getDouble(7));
            } catch (Exception e) {
                UtilsRG.error("Failed to get MedUser("+medicoId+") by MedicalID: " +e.getLocalizedMessage());
            }
            operationIssuesList.add(op);
        }
        return operationIssuesList;
    }

    public interface AsyncResponse {
        void getAllOperationIssuesByMedicoId(List<OperationIssue> operationIssuesList);
    }

    public static class getAllOperationIssuesByMedicoIdAsync extends AsyncTask<String, Void, List<OperationIssue>> {
        public AsyncResponse delegate = null;
        private Context context;

        public getAllOperationIssuesByMedicoIdAsync(AsyncResponse delegate, Context c){
            this.context = c;
            this.delegate = delegate;
        }

        @Override
        protected List<OperationIssue> doInBackground(String... medicoIds) {
            List<OperationIssue> operationsList = new ArrayList<OperationIssue>();
            String medId = medicoIds[0];
            if(medId != null){
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

