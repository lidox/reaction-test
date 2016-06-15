package com.artursworld.reactiontest.model;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class MedicalUserDbManager {
    protected SQLiteDatabase database;
    private ReactionGameContract.ReactionGameDbHelper dbHelper;
    private Context mContext;

    public MedicalUserDbManager(Context context) {
        this.mContext = context;
        dbHelper = ReactionGameContract.ReactionGameDbHelper.getHelper(mContext);
        open();

    }

    public void open() throws SQLException {
        if(dbHelper == null)
            dbHelper = ReactionGameContract.ReactionGameDbHelper.getHelper(mContext);
        database = dbHelper.getWritableDatabase();
    }

    /*public void close() {
        dbHelper.close();
        database = null;
    }*/
}

