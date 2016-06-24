package com.artursworld.reactiontest.model.contracts;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class DBContracts {
    // Make this private so that no one can get instance of it by accident
    private DBContracts(){
    }

    // column name definition
    public static abstract class ReactionGame implements BaseColumns {
        public static final String TABLE_NAME = "reaction_game";
        public static final String COLUMN_NAME_CREATION_DATE = "creation_date";
        public static final String COLUMN_NAME_DURATION = "duration";
        public static final String COLUMN_NAME_HITS = "hits";
        public static final String COLUMN_NAME_MISSES = "misses";
        public static final String COLUMN_NAME_MEDICAL_ID = "medical_id";
        public static final String COLUMN_NAME_REACTION_TYPE = "reaction_type";
    }

    // column name definition
    public static abstract class MedicalUserTable implements BaseColumns {
        public static final String TABLE_NAME = "medical_user";
        public static final String COLUMN_NAME_MEDICAL_ID = "medical_id";
        public static final String COLUMN_NAME_CREATION_DATE = "creation_date";
        public static final String COLUMN_NAME_UPDATE_DATE = "update_date";
        public static final String COLUMN_NAME_BIRTH_DATE = "birth_date";
        public static final String COLUMN_NAME_GENDER = "gender";
    }

    // Useful SQL query parts
    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";

    // Useful SQL queries
    public static final String CREATE_MEDICAL_USER_TABLE = "CREATE TABLE "
            + MedicalUserTable.TABLE_NAME + "("
            + MedicalUserTable._ID + INTEGER_TYPE +COMMA_SEP
            + MedicalUserTable.COLUMN_NAME_MEDICAL_ID + TEXT_TYPE +COMMA_SEP
            + MedicalUserTable.COLUMN_NAME_CREATION_DATE + " DATE, "
            + MedicalUserTable.COLUMN_NAME_UPDATE_DATE + " DATE, "
            + MedicalUserTable.COLUMN_NAME_BIRTH_DATE + " DATE, "
            + MedicalUserTable.COLUMN_NAME_GENDER + TEXT_TYPE + COMMA_SEP
            + "PRIMARY KEY ("+MedicalUserTable.COLUMN_NAME_MEDICAL_ID+")"
            + ");";
    /* In case I need autoincrement on non-primary key
    INSERT INTO Log (id, rev_no, description)
    VALUES ((SELECT IFNULL(MAX(id), 0) + 1 FROM Log), 'rev_Id', 'some description')
    * */

    public static final String CREATE_REACTIONGAME_TABLE = "CREATE TABLE "
            + ReactionGame.TABLE_NAME + "("
            + ReactionGame.COLUMN_NAME_CREATION_DATE + " DATE PRIMARY KEY, "
            + ReactionGame.COLUMN_NAME_DURATION + " DOUBLE, "
            + ReactionGame.COLUMN_NAME_HITS + " INT, "
            + ReactionGame.COLUMN_NAME_MISSES + " INT, "
            + ReactionGame.COLUMN_NAME_REACTION_TYPE + " TEXT, "
            + ReactionGame.COLUMN_NAME_MEDICAL_ID + TEXT_TYPE+","
            + "FOREIGN KEY(" + ReactionGame.COLUMN_NAME_MEDICAL_ID +") "
            + "REFERENCES " + MedicalUserTable.TABLE_NAME + "(" + MedicalUserTable.COLUMN_NAME_MEDICAL_ID +") ON DELETE CASCADE);";

    // Helper class manages database creation and version management
    public static class DatabaseHelper extends SQLiteOpenHelper {

        private static final int DATABASE_VERSION = 28;
        private static final String DATABASE_NAME = "reactiongame.db";

        private static DatabaseHelper instance;

        public static synchronized DatabaseHelper getHelper(Context context) {
            if (instance == null)
                instance = new DatabaseHelper(context);
            return instance;
        }

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_MEDICAL_USER_TABLE);
            db.execSQL(CREATE_REACTIONGAME_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // There are no older versions than 1 yet.
            // Whenever you design a newer version, make sure to add some migration here.
        }
    }
}
