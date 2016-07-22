package com.artursworld.reactiontest.model.persistence.manager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

import com.artursworld.reactiontest.controller.util.UtilsRG;
import com.artursworld.reactiontest.model.entity.OperationIssue;
import com.artursworld.reactiontest.model.persistence.EntityDbManager;
import com.artursworld.reactiontest.model.persistence.contracts.DBContracts;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
                    UtilsRG.info("New trial added to reaction game("+reactionGameCreationTime+") with reaction time("+reactionTime+") successfully");
                }
                catch (Exception e){
                    UtilsRG.error("Could not insert trial into db for reaction game("+reactionGameCreationTime +")" + e.getLocalizedMessage());
                }
                return null;
            }
        }.execute();
    }

    public double getFilteredReactionTimeByReactionGameId(String reactionGameId, String filter) {
        UtilsRG.info("try to getFilteredReactionTimeByReactionGameId(" + reactionGameId + ")");
        Cursor cursor = null;
        try {
            String WHERE_CLAUSE = null;
            if(reactionGameId != null){
                WHERE_CLAUSE = DBContracts.TrialTable.PK_REACTIONGAME_CREATION_DATE + " like '" + reactionGameId + "'";
            }
            cursor = database.query(DBContracts.TrialTable.TABLE_NAME,
                new String[]{filter+"(" + DBContracts.TrialTable.REACTION_TIME + ")"},
                WHERE_CLAUSE,
                null, null, null, null);

            cursor.moveToFirst();
            return cursor.getDouble(0);
        } catch (Exception e) {
            UtilsRG.error("could not get filtered reaction time. " +e.getLocalizedMessage());
        }
        finally {
            try {
                if (cursor !=null)
                    cursor.close();
            }
            catch (Exception e){
                UtilsRG.error(e.getLocalizedMessage());
            }
        }
        return -1;
    }

    public interface AsyncResponse {
        void getFilteredReactionTimeByReactionGameIdAsync(double reactionTime);
    }

    public static class getFilteredReactionTimeByReactionGameIdAsync extends AsyncTask<String, Void, Double> {

        public AsyncResponse delegate = null;
        private Context context;

        public getFilteredReactionTimeByReactionGameIdAsync(AsyncResponse delegate, Context c){
            this.context = c;
            this.delegate = delegate;
        }

        @Override
        protected Double doInBackground(String... arguments) {
            double result = -1;
            String reactionGameId = arguments[0];
            String filter = arguments[1];
            if(reactionGameId != null){
                TrialManager dbManager = new TrialManager(context);
                result = dbManager.getFilteredReactionTimeByReactionGameId(reactionGameId, filter);
            }
            return result;
        }

        @Override
        protected void onPostExecute(Double result) {
            super.onPostExecute(result);
            delegate.getFilteredReactionTimeByReactionGameIdAsync(result);
        }
    }
}
