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

    public double getBestReactionTimeByReactionGameId(String reactionGameId) {
        UtilsRG.info("try to getBestReactionTimeByReactionGameId(" + reactionGameId + ")");
        Cursor cursor = database.query(DBContracts.OperationIssueTable.TABLE_NAME,
                new String[]{"MIN(" + DBContracts.TrialTable.REACTION_TIME + ")"},
                DBContracts.TrialTable.PK_REACTIONGAME_CREATION_DATE + " like '" + reactionGameId + "'",
                null, null, null, null);
        try {
            cursor.moveToFirst();
            return cursor.getDouble(0);
        } catch (Exception e) {
            UtilsRG.error("could not get best reaction time. " +e.getLocalizedMessage());
        }
        finally {
            try {
                cursor.close();
            }
            catch (Exception e){
                UtilsRG.error(e.getLocalizedMessage());
            }
        }
        return -1;
    }

    public interface AsyncResponse {
        void getBestReactionTimeByReactionGameIdAsync(double bestReactionTime);
    }

    public static class getBestReactionTimeByReactionGameIdAsync extends AsyncTask<String, Void, Double> {

        public AsyncResponse delegate = null;
        private Context context;

        public getBestReactionTimeByReactionGameIdAsync(AsyncResponse delegate, Context c){
            this.context = c;
            this.delegate = delegate;
        }

        @Override
        protected Double doInBackground(String... reactionGameIds) {
            double result = -1;
            String reactionGameId = reactionGameIds[0];
            if(reactionGameId != null){
                TrialManager dbManager = new TrialManager(context);
                result = dbManager.getBestReactionTimeByReactionGameId(reactionGameId);
            }
            return result;
        }

        @Override
        protected void onPostExecute(Double result) {
            super.onPostExecute(result);
            delegate.getBestReactionTimeByReactionGameIdAsync(result);
        }
    }

}
