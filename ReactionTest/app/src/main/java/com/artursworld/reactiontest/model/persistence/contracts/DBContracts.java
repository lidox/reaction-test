package com.artursworld.reactiontest.model.persistence.contracts;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/*
* Contains all database tables
*/
public class DBContracts {
    
    private DBContracts(){}

    // column name definition
    public static abstract class MedicalUserTable implements BaseColumns {
        public static final String TABLE_NAME = "medical_user";
        public static final String COLUMN_NAME_MEDICAL_ID = "medical_id";
        public static final String COLUMN_NAME_CREATION_DATE = "creation_date";
        public static final String COLUMN_NAME_UPDATE_DATE = "update_date";
        public static final String COLUMN_NAME_BIRTH_DATE = "birth_date";
        public static final String COLUMN_NAME_GENDER = "gender";
        public static final String COLUMN_NAME_BMI = "bmi";
    }

    public static abstract class OperationIssueTable implements BaseColumns {
        public static final String TABLE_NAME = "operation_issue";
        public static final String OPERATION_DATE = "operationDate";
        public static final String INTUBATION_TIME = "intubation_time";
        public static final String WAKE_UP_TIME = "wake_up_time";
        public static final String NARCOSIS_DURATION = "narcosis_duration";
        public static final String OPERATION_ISSUE_NAME = "operation_issue_name";
        public static final String MEDICAL_USER_ID = "medical_user_id"; //foreign key
        public static final String CREATION_DATE = "creation_date";
        public static final String UPDATE_DATE = "update_date";
    }

    public static abstract class ReactionGame implements BaseColumns {
        public static final String TABLE_NAME = "reaction_game";
        public static final String COLUMN_NAME_CREATION_DATE = "creation_date"; // primary key
        public static final String COLUMN_NAME_UPDATE_DATE = "update_date";
        public static final String COLUMN_NAME_DURATION = "duration";
        public static final String COLUMN_NAME_AVERAGE_REACTION_TIME = "average_reaction_time";
        public static final String COLUMN_NAME_INVALID_TRIAL_COUNT = "invalid_trial_count";
        public static final String COLUMN_NAME_GAME_TYPE = "game_type"; // GO-Game, GO-NO-GO-Game
        public static final String COLUMN_NAME_REACTIONTEST_TYPE = "reaction_test_type"; // Pre-,In-,Post Operation or Trial
        public static final String COLUMN_NAME_OPERATION_ISSUE_NAME = "operation_issue_name"; //foreign key
    }

    public static abstract class TrialTable{
        public static final String TABLE_NAME = "trial";
        public static final String REACTION_TIME = "reaction_time";
        public static final String IS_VALID = "is_valid";
        public static final String CREATION_DATE = "creation_date";
        public static final String PK_REACTIONGAME_CREATION_DATE = "reaction_game_creation_date";
    }

    public static abstract class MedicamentTable{
        public static final String TABLE_NAME = "medicament";
        public static final String CREATION_DATE_PK = "creation_date";
        public static final String NAME = "name";
        public static final String DOSAGE = "dosage";
        public static final String UNIT = "unit";
        public static final String OPERATION_ISSUE_NAME_FK = "operation_issue_name";
        public static final String TIMESTAMP = "timestamp";
    }

    public static abstract class InOpEventTable{
        public static final String TABLE_NAME = "in_op_event";
        public static final String ADDITIONAL_NOTE = "note";
        public static final String TYPE = "type"; // audio, note, intubation
        public static final String TIMESTAMP = "timestamp"; // Primary Key
        public static final String OPERATION_ISSUE = "operation_issue"; //Foreign Key
    }

    // Useful SQL query parts
    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String DATE_TYPE = " DATE";
    private static final String DOUBLE_TYPE = " DOUBLE";
    private static final String COMMA_SEP = ",";

    // Create SQL queries
    public static final String CREATE_MEDICAL_USER_TABLE = "CREATE TABLE "
            + MedicalUserTable.TABLE_NAME + "("
            + MedicalUserTable._ID + INTEGER_TYPE +COMMA_SEP
            + MedicalUserTable.COLUMN_NAME_MEDICAL_ID + TEXT_TYPE +COMMA_SEP
            + MedicalUserTable.COLUMN_NAME_CREATION_DATE + " DATE, "
            + MedicalUserTable.COLUMN_NAME_UPDATE_DATE + " DATE, "
            + MedicalUserTable.COLUMN_NAME_BIRTH_DATE + " DATE, "
            + MedicalUserTable.COLUMN_NAME_GENDER + TEXT_TYPE + COMMA_SEP
            + MedicalUserTable.COLUMN_NAME_BMI + DOUBLE_TYPE + COMMA_SEP
            + "PRIMARY KEY ("+MedicalUserTable.COLUMN_NAME_MEDICAL_ID+")"
            + ");";
    /* In case I need autoincrement on non-primary key
    INSERT INTO Log (id, rev_no, description)
    VALUES ((SELECT IFNULL(MAX(id), 0) + 1 FROM Log), 'rev_Id', 'some description')
    * */

    public static final String CREATE_OPERATION_ISSUE_TABLE = "CREATE TABLE "
            + OperationIssueTable.TABLE_NAME + "("
            + OperationIssueTable.OPERATION_DATE + DATE_TYPE + COMMA_SEP
            + OperationIssueTable.INTUBATION_TIME + DATE_TYPE + COMMA_SEP //TODO: delete
            + OperationIssueTable.WAKE_UP_TIME + DATE_TYPE + COMMA_SEP//TODO: delete
            + OperationIssueTable.NARCOSIS_DURATION + DATE_TYPE + COMMA_SEP//TODO: delete
            + OperationIssueTable.OPERATION_ISSUE_NAME + TEXT_TYPE + " PRIMARY KEY"+COMMA_SEP
            + OperationIssueTable.MEDICAL_USER_ID + TEXT_TYPE + COMMA_SEP
            + OperationIssueTable.CREATION_DATE + DATE_TYPE + COMMA_SEP
            + OperationIssueTable.UPDATE_DATE + DATE_TYPE + COMMA_SEP
            + "FOREIGN KEY(" + OperationIssueTable.MEDICAL_USER_ID +") "
            + "REFERENCES " + MedicalUserTable.TABLE_NAME + "(" + MedicalUserTable.COLUMN_NAME_MEDICAL_ID +") ON DELETE CASCADE);";

    public static final String CREATE_REACTIONGAME_TABLE = "CREATE TABLE "
            + ReactionGame.TABLE_NAME + "("
            + ReactionGame.COLUMN_NAME_CREATION_DATE + " DATE PRIMARY KEY, "
            + ReactionGame.COLUMN_NAME_UPDATE_DATE + DATE_TYPE + COMMA_SEP
            + ReactionGame.COLUMN_NAME_DURATION + DOUBLE_TYPE + COMMA_SEP
            + ReactionGame.COLUMN_NAME_AVERAGE_REACTION_TIME + DOUBLE_TYPE + COMMA_SEP
            + ReactionGame.COLUMN_NAME_GAME_TYPE + TEXT_TYPE + COMMA_SEP
            + ReactionGame.COLUMN_NAME_INVALID_TRIAL_COUNT + INTEGER_TYPE + COMMA_SEP
            + ReactionGame.COLUMN_NAME_REACTIONTEST_TYPE + TEXT_TYPE + COMMA_SEP
            + ReactionGame.COLUMN_NAME_OPERATION_ISSUE_NAME + TEXT_TYPE + COMMA_SEP
            + "FOREIGN KEY(" + ReactionGame.COLUMN_NAME_OPERATION_ISSUE_NAME +") "
            + "REFERENCES " + OperationIssueTable.TABLE_NAME + "(" + OperationIssueTable.OPERATION_ISSUE_NAME +") ON DELETE CASCADE);";

    public static final String CREATE_TRIAL_TABLE = "CREATE TABLE "
            + TrialTable.TABLE_NAME + "("
            + TrialTable.CREATION_DATE + DATE_TYPE + COMMA_SEP
            + TrialTable.IS_VALID + INTEGER_TYPE + " DEFAULT 0 NOT NULL CHECK("+TrialTable.IS_VALID+" IN (0,1)) " + COMMA_SEP
            + TrialTable.REACTION_TIME + DOUBLE_TYPE + COMMA_SEP
            + TrialTable.PK_REACTIONGAME_CREATION_DATE + DATE_TYPE + COMMA_SEP
            + "FOREIGN KEY(" + TrialTable.PK_REACTIONGAME_CREATION_DATE +") "
            + "REFERENCES " + ReactionGame.TABLE_NAME + "(" + ReactionGame.COLUMN_NAME_CREATION_DATE +") ON UPDATE CASCADE ON DELETE CASCADE);";

    public static final String CREATE_MEDICAMENT_TABLE = "CREATE TABLE "
            + MedicamentTable.TABLE_NAME + "("
            + MedicamentTable.DOSAGE + INTEGER_TYPE + COMMA_SEP
            + MedicamentTable.NAME + TEXT_TYPE + COMMA_SEP
            + MedicamentTable.TIMESTAMP + DATE_TYPE + COMMA_SEP
            + MedicamentTable.UNIT + TEXT_TYPE + COMMA_SEP
            + MedicamentTable.OPERATION_ISSUE_NAME_FK + TEXT_TYPE + COMMA_SEP
            + "FOREIGN KEY(" + MedicamentTable.OPERATION_ISSUE_NAME_FK +") "
            + "REFERENCES " + OperationIssueTable.TABLE_NAME + "(" + OperationIssueTable.OPERATION_ISSUE_NAME +") ON UPDATE CASCADE ON DELETE CASCADE);";

    public static final String CREATE_IN_OP_EVENT_TABLE = "CREATE TABLE "
            + InOpEventTable.TABLE_NAME + "("
            + InOpEventTable.TIMESTAMP + " DATE PRIMARY KEY, "
            + InOpEventTable.ADDITIONAL_NOTE + TEXT_TYPE + COMMA_SEP
            + InOpEventTable.TYPE + TEXT_TYPE + COMMA_SEP
            + InOpEventTable.OPERATION_ISSUE + TEXT_TYPE + COMMA_SEP
            + "FOREIGN KEY(" + InOpEventTable.OPERATION_ISSUE +") "
            + "REFERENCES " + OperationIssueTable.TABLE_NAME + "(" + OperationIssueTable.OPERATION_ISSUE_NAME +") ON UPDATE CASCADE ON DELETE CASCADE);";


    // Helper class manages database creation and version management
    public static class DatabaseHelper extends SQLiteOpenHelper {

        private static final int DATABASE_VERSION = 42;
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
            db.execSQL(CREATE_OPERATION_ISSUE_TABLE);
            db.execSQL(CREATE_REACTIONGAME_TABLE);
            db.execSQL(CREATE_TRIAL_TABLE);
            db.execSQL(CREATE_MEDICAMENT_TABLE);
            db.execSQL(CREATE_IN_OP_EVENT_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // Whenever you design a newer version, make sure to add some migration here.
        }
    }
}
