package com.artursworld.reactiontest.model.persistence.manager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.artursworld.reactiontest.controller.helper.Gender;
import com.artursworld.reactiontest.model.persistence.EntityDbManager;
import com.artursworld.reactiontest.model.persistence.contracts.DBContracts;
import com.artursworld.reactiontest.model.entity.MedicalUser;
import com.artursworld.reactiontest.controller.util.UtilsRG;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/*
* Manages all database issues for a user e.g. insert, update and delete
*/
public class MedicalUserManager extends EntityDbManager {

    // useful 'WHERE statement'
    private static final String WHERE_ID_EQUALS = DBContracts.MedicalUserTable.COLUMN_NAME_MEDICAL_ID + " =?";
    private static final String WHERE_CREATION_DATE_EQUALS = DBContracts.MedicalUserTable.COLUMN_NAME_CREATION_DATE + " =?";

    public MedicalUserManager(Context context) {
        super(context);
    }

    // Usefull async code for getting all existing user in db
    public interface AsyncResponse {
        void getMedicalUserList(List<MedicalUser> medicalUserResultList);
    }

    public static class getAllMedicalUsers extends AsyncTask<Void, String, List<MedicalUser>> {
        public AsyncResponse delegate = null;
        private Context context;

        public getAllMedicalUsers(AsyncResponse delegate, Context c) {
            this.context = c;
            this.delegate = delegate;
        }

        @Override
        protected List<MedicalUser> doInBackground(Void... params) {
            MedicalUserManager dbManager = new MedicalUserManager(context);
            List<MedicalUser> medicalUserList = dbManager.getAllMedicalUsers();
            return medicalUserList;
        }

        @Override
        protected void onPostExecute(List<MedicalUser> result) {
            super.onPostExecute(result);
            delegate.getMedicalUserList(result);
        }

    }

    /*
    * Inserts a user into database
    */
    public long insert(MedicalUser medicalUser) {
        if (medicalUser == null)
            return -1;

        try {
            ContentValues values = getUserContentValues(medicalUser);
            long ret = database.insertOrThrow(DBContracts.MedicalUserTable.TABLE_NAME, null, values);
            UtilsRG.log.info("Inserted user(" + medicalUser.getMedicalId() + ") into databse successfully");
            return ret;
        } catch (Exception e) {
            UtilsRG.log.error("Failed to insert medicalUser: " + medicalUser.getMedicalId() + " ErrorMessage:" + e.getLocalizedMessage());
            return -1L;
        }
    }

    /**
     * Creates ContentValues for midcal user
     *
     * @param medicalUser the user to create the values
     * @return the values of the user
     */
    @NonNull
    private ContentValues getUserContentValues(MedicalUser medicalUser) {
        ContentValues values = new ContentValues();
        if (medicalUser.getMedicalId() != null)
            values.put(DBContracts.MedicalUserTable.COLUMN_NAME_MEDICAL_ID, medicalUser.getMedicalId());

        if (medicalUser.getCreationDate() != null) {
            values.put(DBContracts.MedicalUserTable.COLUMN_NAME_CREATION_DATE, UtilsRG.dateFormat.format(medicalUser.getCreationDate()));
            values.put(DBContracts.MedicalUserTable.COLUMN_NAME_UPDATE_DATE, UtilsRG.dateFormat.format(medicalUser.getUpdateDate()));
        }

        if (medicalUser.getBirthDate() != null)
            values.put(DBContracts.MedicalUserTable.COLUMN_NAME_BIRTH_DATE, UtilsRG.dateFormat.format(medicalUser.getBirthDate()));

        if (medicalUser.getGender() != null && (!medicalUser.getGender().equals("")))
            values.put(DBContracts.MedicalUserTable.COLUMN_NAME_GENDER, medicalUser.getGender().name());

        if (medicalUser.getBmi() > 0)
            values.put(DBContracts.MedicalUserTable.COLUMN_NAME_BMI, medicalUser.getBmi());

        values.put(DBContracts.MedicalUserTable.COLUMN_MARKED_AS_DELETE, (medicalUser.isMarkedAsDeleted()) ? 1 : 0);
        return values;
    }

    //TODO: medical id need real id, because of rename problem: on update cascade
    /*
    * updates a user in database
    */
    public long update(MedicalUser medicalUser) {
        try {
            ContentValues values = getUserContentValues(medicalUser);
            String creationDate = UtilsRG.dateFormat.format(medicalUser.getCreationDate());
            //long i = database.update(DBContracts.MedicalUserTable.TABLE_NAME, values, WHERE_CREATION_DATE_EQUALS + " AND " + WHERE_ID_EQUALS, new String[]{creationDate, medicalUser.getMedicalId()});
            long i = database.update(DBContracts.MedicalUserTable.TABLE_NAME, values, WHERE_CREATION_DATE_EQUALS, new String[]{creationDate});
            if (i > 0) {
                UtilsRG.info("Updated " + medicalUser.toString());
                return 1;  // 1 for successful
            } else {
                UtilsRG.error("Failed to update " + medicalUser.toString());
                return 0;  // 0 for unsuccessful
            }
        } catch (Exception e) {
            UtilsRG.info("Exception! Could not update " + medicalUser.toString() + "! " + e.getLocalizedMessage());
            return 0;
        }
    }

    /**
     * Returns user by medical user id from database
     *
     * @param medicoId the users id
     * @return returns the user by id
     */
    public MedicalUser getUserByMedicoId(String medicoId) {
        List<MedicalUser> medicalUserList = new ArrayList<MedicalUser>();
        Cursor cursor = database.query(DBContracts.MedicalUserTable.TABLE_NAME,
                new String[]{DBContracts.MedicalUserTable.COLUMN_NAME_MEDICAL_ID,
                        DBContracts.MedicalUserTable.COLUMN_NAME_CREATION_DATE,
                        DBContracts.MedicalUserTable.COLUMN_NAME_UPDATE_DATE,
                        DBContracts.MedicalUserTable.COLUMN_NAME_BIRTH_DATE,
                        DBContracts.MedicalUserTable._ID,
                        DBContracts.MedicalUserTable.COLUMN_NAME_GENDER,
                        DBContracts.MedicalUserTable.COLUMN_MARKED_AS_DELETE,
                        DBContracts.MedicalUserTable.COLUMN_NAME_BMI},
                DBContracts.MedicalUserTable.COLUMN_NAME_MEDICAL_ID + " LIKE '" + medicoId.toString() + "'",
                null, null, null, null);

        while (cursor.moveToNext()) {
            MedicalUser medicalUser = new MedicalUser();
            medicalUser.setMedicalId(cursor.getString(0));
            try {
                medicalUser.setCreationDate(UtilsRG.dateFormat.parse(cursor.getString(1)));
                medicalUser.setUpdateDate(UtilsRG.dateFormat.parse(cursor.getString(2)));
                medicalUser.setBirthDate(UtilsRG.dateFormat.parse(cursor.getString(3)));
            } catch (Exception e) {
                UtilsRG.error("Failed to parse date at getMedUser(" + medicoId + ") by MedicalID: " + e.getLocalizedMessage());
            }
            medicalUser.setGender(Gender.findByName(cursor.getString(5).toUpperCase()));
            medicalUser.setMarkedAsDeleted((cursor.getInt(6) == 1) ? true : false);
            medicalUser.setBmi(cursor.getDouble(7));
            medicalUserList.add(medicalUser);
        }

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        if (medicalUserList.size() > 0) {
            return medicalUserList.get(0);
        } else {
            UtilsRG.error("Exception! Could not find user(" + medicoId + ") in database");
            return null;
        }

    }

    /**
     * Updates the medical user
     * @param user the user to update
     */
    public void updateMedicalUser(MedicalUser user) {

        String WHERE_CLAUSE = DBContracts.MedicalUserTable.COLUMN_NAME_CREATION_DATE + " =?";
        try {
            StringBuilder values = new StringBuilder();
            values.append(" SET " + DBContracts.MedicalUserTable.COLUMN_MARKED_AS_DELETE + " = " + (user.isMarkedAsDeleted() ? 1:0) + ",");
            if(user.getBirthDate() != null)
                values.append(DBContracts.MedicalUserTable.COLUMN_NAME_BIRTH_DATE + " = '" + UtilsRG.dateFormat.format(user.getBirthDate()) + "',");
            values.append(DBContracts.MedicalUserTable.COLUMN_NAME_BMI + " = " + user.getBmi() + ",");
            if(user.getGender() != null)
                values.append(DBContracts.MedicalUserTable.COLUMN_NAME_GENDER + " = '" + user.getGender() + "',");
            values.append(DBContracts.MedicalUserTable.COLUMN_NAME_MEDICAL_ID + " = '" + user.getMedicalId() + "',");
            values.append(DBContracts.MedicalUserTable.COLUMN_NAME_UPDATE_DATE + " = '" + UtilsRG.dateFormat.format(new Date()) + "'");

            String sqlQuery = "UPDATE " + DBContracts.MedicalUserTable.TABLE_NAME + values.toString() + " WHERE " + WHERE_CLAUSE;
            database.execSQL(sqlQuery, new String[]{UtilsRG.dateFormat.format(user.getCreationDate())});
            UtilsRG.info(sqlQuery);
            UtilsRG.info("MedicalUser(" + user.getMedicalId() + ") has been updated");
        } catch (Exception e) {
            UtilsRG.error("Exception! Could not update the MedicalUser(" + user.getMedicalId() + ") " + " " + e.getLocalizedMessage());
        }//UPDATE company SET com_id='COM5' WHERE com_id='COM4';
    }

    /**
     * Deletes user from database by user id
     *
     * @param userId users midal user id
     * @return the number of rows affected if a whereClause is passed in, 0
     * otherwise. To remove all rows and get a count pass "1" as the
     * whereClause
     */
    public int deleteUserById(String userId) {
        int resultCode = 0;

        // validation
        if (userId == null)
            return resultCode;

        if (userId.trim().equals(""))
            return resultCode;

        String WHERE_CLAUSE = DBContracts.MedicalUserTable.COLUMN_NAME_MEDICAL_ID + " =?";
        try {
            resultCode = database.delete(
                    DBContracts.MedicalUserTable.TABLE_NAME,
                    WHERE_CLAUSE,
                    new String[]{userId}
            );
            UtilsRG.info("MedicalUser(" + userId + ") has been deleted from database");
        } catch (Exception e) {
            UtilsRG.error("Exception! Could not delete MedicalUser(" + userId + ") from databse" + " " + e.getLocalizedMessage());
        }

        return resultCode;
    }

    /**
     * Marks userId as deleted by userId id
     *
     * @param userId      userId to mark as deleted
     * @param isDeleted true if mark as deleted, otherwise mark as not deleted
     */
    public void markUserAsDeletedById(String userId, boolean isDeleted) {
        int isDeletedInt = (isDeleted) ? 1 : 0;
        String WHERE_CLAUSE = DBContracts.MedicalUserTable.COLUMN_NAME_MEDICAL_ID + " =?";
        try {
            String sqlQuery = "UPDATE " + DBContracts.MedicalUserTable.TABLE_NAME + " SET " + DBContracts.MedicalUserTable.COLUMN_MARKED_AS_DELETE + " = " + isDeletedInt + " WHERE " + WHERE_CLAUSE;
            database.execSQL(sqlQuery, new String[]{userId});
            UtilsRG.info(sqlQuery);
            UtilsRG.info("MedicalUser(" + userId + ") has been marked as deleted");
        } catch (Exception e) {
            UtilsRG.error("Exception! Could not mark as deleted the MedicalUser(" + userId + ") " + " " + e.getLocalizedMessage());
        }
    }

    public List<MedicalUser> getAllMedicalUsersByMark(boolean all) {
        if(all){
            return getAllMedicalUsers();
        }
        else{
            return getMedicalUsersByMark(false);
        }
    }

    private List<MedicalUser> getMedicalUsersByMark(boolean markedAsDeleted) {
        String sortOrder = DBContracts.MedicalUserTable.COLUMN_NAME_UPDATE_DATE + " DESC";
        List<MedicalUser> medicalUserList = new ArrayList<MedicalUser>();
        String WHERE = DBContracts.MedicalUserTable.COLUMN_MARKED_AS_DELETE + "=?";
        Cursor cursor = database.query(DBContracts.MedicalUserTable.TABLE_NAME,
                new String[]{DBContracts.MedicalUserTable.COLUMN_NAME_MEDICAL_ID,
                        DBContracts.MedicalUserTable.COLUMN_NAME_CREATION_DATE,
                        DBContracts.MedicalUserTable.COLUMN_NAME_UPDATE_DATE,
                        DBContracts.MedicalUserTable.COLUMN_NAME_BIRTH_DATE,
                        DBContracts.MedicalUserTable._ID,
                        DBContracts.MedicalUserTable.COLUMN_NAME_GENDER,
                        DBContracts.MedicalUserTable.COLUMN_NAME_BMI,
                        DBContracts.MedicalUserTable.COLUMN_MARKED_AS_DELETE
                }, WHERE, new String[] {""+((markedAsDeleted) ? 1:0)}, null, null, sortOrder);

        while (cursor.moveToNext()) {
            MedicalUser medicalUser = new MedicalUser();
            medicalUser.setMedicalId(cursor.getString(0));
            try {
                medicalUser.setCreationDate(UtilsRG.dateFormat.parse(cursor.getString(1)));
            } catch (Exception e) {
                UtilsRG.info("Could not load date for medical user: " + e.getLocalizedMessage());
            }
            try {
                medicalUser.setUpdateDate(UtilsRG.dateFormat.parse(cursor.getString(2)));
            } catch (Exception e) {
                UtilsRG.info("Could not load date for medical user: " + e.getLocalizedMessage());
            }
            try {
                medicalUser.setBirthDate(UtilsRG.dateFormat.parse(cursor.getString(3)));
            } catch (Exception e) {
                UtilsRG.info("Could not load date for medical user: " + e.getLocalizedMessage());
            }

            try {
                String genderString = cursor.getString(5).toUpperCase();
                Gender gender = Gender.valueOf(genderString);
                medicalUser.setGender(gender);
            } catch (Exception e) {
                UtilsRG.info("Could not set gender because no gender set" + e.getLocalizedMessage());
            }
            medicalUser.setBmi(cursor.getDouble(6));
            medicalUser.setMarkedAsDeleted((cursor.getInt(7) == 1) ? true : false);
            medicalUserList.add(medicalUser);
        }
        UtilsRG.info(medicalUserList.size() + ". medical users has been found:");
        UtilsRG.info(medicalUserList.toString());

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return medicalUserList;
    }

    /*
    * Return all users synchronous
    */
    public List<MedicalUser> getAllMedicalUsers() {
        String sortOrder = DBContracts.MedicalUserTable.COLUMN_NAME_UPDATE_DATE + " DESC";
        List<MedicalUser> medicalUserList = new ArrayList<MedicalUser>();
        Cursor cursor = database.query(DBContracts.MedicalUserTable.TABLE_NAME,
                new String[]{DBContracts.MedicalUserTable.COLUMN_NAME_MEDICAL_ID,
                        DBContracts.MedicalUserTable.COLUMN_NAME_CREATION_DATE,
                        DBContracts.MedicalUserTable.COLUMN_NAME_UPDATE_DATE,
                        DBContracts.MedicalUserTable.COLUMN_NAME_BIRTH_DATE,
                        DBContracts.MedicalUserTable._ID,
                        DBContracts.MedicalUserTable.COLUMN_NAME_GENDER,
                        DBContracts.MedicalUserTable.COLUMN_NAME_BMI,
                        DBContracts.MedicalUserTable.COLUMN_MARKED_AS_DELETE
                }, null, null, null, null, sortOrder);

        /*
            return db.query(TABLE_NAME,         // table name
                    projection,                 // columns to return
                    null,                       // columns for WHERE
                    null,                       // values for WHERE
                    null,                       // groups
                    null,                       // filters
                    sortOrder);                 // sort order
         */

        while (cursor.moveToNext()) {
            MedicalUser medicalUser = new MedicalUser();
            medicalUser.setMedicalId(cursor.getString(0));
            try {
                medicalUser.setCreationDate(UtilsRG.dateFormat.parse(cursor.getString(1)));
            } catch (Exception e) {
                UtilsRG.info("Could not load date for medical user: " + e.getLocalizedMessage());
            }
            try {
                medicalUser.setUpdateDate(UtilsRG.dateFormat.parse(cursor.getString(2)));
            } catch (Exception e) {
                UtilsRG.info("Could not load date for medical user: " + e.getLocalizedMessage());
            }
            try {
                medicalUser.setBirthDate(UtilsRG.dateFormat.parse(cursor.getString(3)));
            } catch (Exception e) {
                UtilsRG.info("Could not load date for medical user: " + e.getLocalizedMessage());
            }

            try {
                String genderString = cursor.getString(5).toUpperCase();
                Gender gender = Gender.valueOf(genderString);
                medicalUser.setGender(gender);
            } catch (Exception e) {
                UtilsRG.info("Could not set gender because no gender set" + e.getLocalizedMessage());
            }
            medicalUser.setBmi(cursor.getDouble(6));
            medicalUser.setMarkedAsDeleted((cursor.getInt(7) == 1) ? true : false);
            medicalUserList.add(medicalUser);
        }
        UtilsRG.info(medicalUserList.size() + ". medical users has been found:");
        UtilsRG.info(medicalUserList.toString());

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return medicalUserList;
    }
}

