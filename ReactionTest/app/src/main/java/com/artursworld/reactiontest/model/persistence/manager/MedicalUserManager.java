package com.artursworld.reactiontest.model.persistence.manager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import com.artursworld.reactiontest.controller.helper.AsyncResponse;
import com.artursworld.reactiontest.model.persistence.EntityDbManager;
import com.artursworld.reactiontest.model.persistence.contracts.DBContracts;
import com.artursworld.reactiontest.model.entity.MedicalUser;
import com.artursworld.reactiontest.controller.util.UtilsRG;

import org.slf4j.helpers.Util;

import java.util.ArrayList;
import java.util.List;

public class MedicalUserManager extends EntityDbManager {

    private static final String WHERE_ID_EQUALS = DBContracts.MedicalUserTable.COLUMN_NAME_MEDICAL_ID + " =?";
    private Context context;

    public MedicalUserManager(Context context) {
        super(context);
        this.context = context;
    }



    public static class getItemLists extends AsyncTask<Void, String, List<MedicalUser>> {
        public AsyncResponse delegate = null;
        private Context context;
        public getItemLists(Context c){
            this.context = c;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected List<MedicalUser> doInBackground(Void... params) {
            MedicalUserManager dao = new MedicalUserManager(context);
            List<MedicalUser> fal = dao.getAllMedicalUsers();
            return fal;
        }

        @Override
        protected void onPostExecute(List<MedicalUser> result) {
            super.onPostExecute(result);
            delegate.getMedicalUserList(result);
            //initMedicalUserListView(result);
        }
    }

    public long insert(MedicalUser medicalUser) {
        //TODO: add validation

        /* Database access should be done asynchronously
        new AsyncTask<Void, Void, Cursor>() {
                @Override
                protected Cursor doInBackground(Void... params) {

                    return null;
                }

                @Override
                protected void onPostExecute(Cursor cursor) {

                }
        }.execute();
        */
        try {
            ContentValues values = new ContentValues();
            values.put(DBContracts.MedicalUserTable.COLUMN_NAME_MEDICAL_ID, medicalUser.getMedicalId());
            values.put(DBContracts.MedicalUserTable.COLUMN_NAME_CREATION_DATE, UtilsRG.dateFormat.format(medicalUser.getCreationDate()));
            values.put(DBContracts.MedicalUserTable.COLUMN_NAME_UPDATE_DATE, UtilsRG.dateFormat.format(medicalUser.getUpdateDate()));
            values.put(DBContracts.MedicalUserTable.COLUMN_NAME_BIRTH_DATE, UtilsRG.dateFormat.format(medicalUser.getBirthDate()));
            values.put(DBContracts.MedicalUserTable.COLUMN_NAME_GENDER, medicalUser.getGender());
            values.put(DBContracts.MedicalUserTable.COLUMN_NAME_BMI, medicalUser.getBmi());
            long ret = database.insertOrThrow(DBContracts.MedicalUserTable.TABLE_NAME, null, values);
            UtilsRG.log.info("Inserted user("+ medicalUser.getMedicalId() +") into databse successfully");
            return ret;
        }
        catch (Exception e) {
            UtilsRG.log.error("Failed to insert medicalUser: " + medicalUser.getMedicalId() + " ErrorMessage:" + e.getLocalizedMessage());
            return -1L;
        }
    }

    //TODO: medical id need real id, because of rename problem: on update cascade
    public long update(MedicalUser medicalUser) {
        ContentValues values = new ContentValues();
        values.put(DBContracts.MedicalUserTable.COLUMN_NAME_CREATION_DATE, UtilsRG.dateFormat.format(medicalUser.getCreationDate()));
        values.put(DBContracts.MedicalUserTable.COLUMN_NAME_UPDATE_DATE, UtilsRG.dateFormat.format(medicalUser.getUpdateDate()));
        values.put(DBContracts.MedicalUserTable.COLUMN_NAME_BIRTH_DATE, UtilsRG.dateFormat.format(medicalUser.getBirthDate()));
        values.put(DBContracts.MedicalUserTable.COLUMN_NAME_GENDER, medicalUser.getGender());

        long result = database.update(DBContracts.MedicalUserTable.TABLE_NAME, values,
                WHERE_ID_EQUALS,
                new String[] { String.valueOf(medicalUser.getMedicalId()) });
        Log.i("Update Result:", "=" + result);
        return result;

    }

    //TODO: not working
    public MedicalUser getUserByMedicoId(String medicoId){
        List<MedicalUser> medicalUserList = new ArrayList<MedicalUser>();
        Cursor cursor = database.query(DBContracts.MedicalUserTable.TABLE_NAME,
                new String[] { DBContracts.MedicalUserTable.COLUMN_NAME_MEDICAL_ID,
                        DBContracts.MedicalUserTable.COLUMN_NAME_CREATION_DATE,
                        DBContracts.MedicalUserTable.COLUMN_NAME_UPDATE_DATE,
                        DBContracts.MedicalUserTable.COLUMN_NAME_BIRTH_DATE,
                        DBContracts.MedicalUserTable._ID,
                        DBContracts.MedicalUserTable.COLUMN_NAME_GENDER },
                DBContracts.MedicalUserTable.COLUMN_NAME_MEDICAL_ID + " = " +medicoId.toString(), // KEY_HOMEID+" = "+jounalId,
                null, null, null, null);

        while (cursor.moveToNext()) {
            MedicalUser medicalUser = new MedicalUser();
            medicalUser.setMedicalId(cursor.getString(0));
            try {
                medicalUser.setCreationDate(UtilsRG.dateFormat.parse(cursor.getString(1)));
                medicalUser.setUpdateDate(UtilsRG.dateFormat.parse(cursor.getString(2)));
                medicalUser.setBirthDate(UtilsRG.dateFormat.parse(cursor.getString(3)));
                medicalUser.setID(cursor.getInt(4));
            } catch (Exception e) {
                UtilsRG.error("Failed to get MedUser("+medicoId+") by MedicalID: " +e.getLocalizedMessage());
            }
            medicalUser.setGender(cursor.getString(5));
            medicalUserList.add(medicalUser);
        }
        return medicalUserList.get(0);
    }

    //TODO: not working
    public long renameMedicalUserByName(MedicalUser medicalUser, String newName) {
        ContentValues values = new ContentValues();
        values.put(DBContracts.MedicalUserTable.COLUMN_NAME_UPDATE_DATE, UtilsRG.dateFormat.format(medicalUser.getUpdateDate()));
        values.put(DBContracts.MedicalUserTable.COLUMN_NAME_GENDER, medicalUser.getGender());
        //TODO: reamane
        long result = database.update(DBContracts.MedicalUserTable.TABLE_NAME, values,
                WHERE_ID_EQUALS,
                new String[] { String.valueOf( medicalUser.getMedicalId()) });
        Log.i("Update Result:", "=" + result);
        return result;

    }

    public int delete(MedicalUser medicalUser) {
        return database.delete(DBContracts.MedicalUserTable.TABLE_NAME,
                WHERE_ID_EQUALS, new String[] { medicalUser.getMedicalId() + "" });
    }

    public List<MedicalUser> getAllMedicalUsers() {
        String sortOrder = DBContracts.MedicalUserTable.COLUMN_NAME_UPDATE_DATE + " DESC";
        List<MedicalUser> medicalUserList = new ArrayList<MedicalUser>();
        Cursor cursor = database.query(DBContracts.MedicalUserTable.TABLE_NAME,
                new String[] { DBContracts.MedicalUserTable.COLUMN_NAME_MEDICAL_ID,
                        DBContracts.MedicalUserTable.COLUMN_NAME_CREATION_DATE,
                        DBContracts.MedicalUserTable.COLUMN_NAME_UPDATE_DATE,
                        DBContracts.MedicalUserTable.COLUMN_NAME_BIRTH_DATE,
                        DBContracts.MedicalUserTable._ID,
                        DBContracts.MedicalUserTable.COLUMN_NAME_GENDER,
                        DBContracts.MedicalUserTable.COLUMN_NAME_BMI
                }, null, null, null, null, sortOrder);

        /*
            return db.query(CatRace.TABLE_NAME, // table name
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
                medicalUser.setUpdateDate(UtilsRG.dateFormat.parse(cursor.getString(2)));
                medicalUser.setBirthDate(UtilsRG.dateFormat.parse(cursor.getString(3)));
            } catch (Exception e) {
                UtilsRG.error("Failure at getting all mediacal users: " +e.getLocalizedMessage());
            }
            medicalUser.setID(cursor.getInt(4));
            medicalUser.setGender(cursor.getString(5));
            medicalUser.setBmi(cursor.getDouble(6));
            medicalUserList.add(medicalUser);
        }
        UtilsRG.info(medicalUserList.size()+ ". medical users has been found:");
        UtilsRG.info(medicalUserList.toString());
        return medicalUserList;
    }
}

