package com.artursworld.reactiontest.model;

import android.content.ContentValues;
import android.content.Context;
import android.content.SyncStatusObserver;
import android.database.Cursor;
import android.util.Log;

import com.artursworld.reactiontest.entity.MedicalUser;
import com.artursworld.reactiontest.util.UtilsRG;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import ch.qos.logback.classic.android.BasicLogcatConfigurator;

public class MedicalUserManager extends EntityDbManager {

    private static final String WHERE_ID_EQUALS = DBContracts.MedicalUser.COLUMN_NAME_MEDICAL_ID + " =?";

    public MedicalUserManager(Context context) {
        super(context);
    }

    public long insert(MedicalUser medicalUser) {
        try {
            ContentValues values = new ContentValues();
            //values.put(DBContracts.MedicalUser._ID, medicalUser.getId());
            values.put(DBContracts.MedicalUser.COLUMN_NAME_MEDICAL_ID, medicalUser.getMedicalId());
            values.put(DBContracts.MedicalUser.COLUMN_NAME_CREATION_DATE, UtilsRG.dateFormat.format(medicalUser.getCreationDate()));
            values.put(DBContracts.MedicalUser.COLUMN_NAME_UPDATE_DATE, UtilsRG.dateFormat.format(medicalUser.getUpdateDate()));
            values.put(DBContracts.MedicalUser.COLUMN_NAME_BIRTH_DATE, UtilsRG.dateFormat.format(medicalUser.getBirthDate()));
            values.put(DBContracts.MedicalUser.COLUMN_NAME_GENDER, medicalUser.getGender());
            long ret = database.insertOrThrow(DBContracts.MedicalUser.TABLE_NAME, null, values);
            UtilsRG.log.info("Inserted user("+ medicalUser.getMedicalId() +") into databse successfully");
            return ret;
        }
        catch (Exception e) {
            UtilsRG.log.error("Failed to insert medicalUser: " + medicalUser.getMedicalId() + " ErrorMessage:" + e.getLocalizedMessage());
            return -1L;
        }
    }

    //TODO: medical id need real id, because of rename problem
    public long update(MedicalUser medicalUser) {
        ContentValues values = new ContentValues();
        values.put(DBContracts.MedicalUser.COLUMN_NAME_CREATION_DATE, UtilsRG.dateFormat.format(medicalUser.getCreationDate()));
        values.put(DBContracts.MedicalUser.COLUMN_NAME_UPDATE_DATE, UtilsRG.dateFormat.format(medicalUser.getUpdateDate()));
        values.put(DBContracts.MedicalUser.COLUMN_NAME_BIRTH_DATE, UtilsRG.dateFormat.format(medicalUser.getBirthDate()));
        values.put(DBContracts.MedicalUser.COLUMN_NAME_GENDER, medicalUser.getGender());

        long result = database.update(DBContracts.MedicalUser.TABLE_NAME, values,
                WHERE_ID_EQUALS,
                new String[] { String.valueOf(medicalUser.getMedicalId()) });
        Log.i("Update Result:", "=" + result);
        return result;

    }

    public List<MedicalUser> getUserByMedicoId(String medicoId){
        List<MedicalUser> medicalUserList = new ArrayList<MedicalUser>();
        Cursor cursor = database.query(DBContracts.MedicalUser.TABLE_NAME,
                new String[] { DBContracts.MedicalUser.COLUMN_NAME_MEDICAL_ID,
                        DBContracts.MedicalUser.COLUMN_NAME_CREATION_DATE,
                        DBContracts.MedicalUser.COLUMN_NAME_UPDATE_DATE,
                        DBContracts.MedicalUser.COLUMN_NAME_BIRTH_DATE,
                        DBContracts.MedicalUser._ID,
                        DBContracts.MedicalUser.COLUMN_NAME_GENDER }, null, null, null, null, null);

        while (cursor.moveToNext()) {
            MedicalUser medicalUser = new MedicalUser();
            medicalUser.setMedicalId(cursor.getString(0));
            try {
                medicalUser.setCreationDate(UtilsRG.dateFormat.parse(cursor.getString(1)));
                medicalUser.setUpdateDate(UtilsRG.dateFormat.parse(cursor.getString(2)));
                medicalUser.setBirthDate(UtilsRG.dateFormat.parse(cursor.getString(3)));
                medicalUser.setID(cursor.getInt(4));
            } catch (Exception e) {
                // TODO: error handling
                System.out.println("Failure at getMedicalUsers(): " +e.getLocalizedMessage());
            }
            medicalUser.setGender(cursor.getString(5));

            medicalUserList.add(medicalUser);
        }
        return medicalUserList;
    }

    public long renameMedicalUserByName(MedicalUser medicalUser, String newName) {
        ContentValues values = new ContentValues();
        values.put(DBContracts.MedicalUser.COLUMN_NAME_UPDATE_DATE, UtilsRG.dateFormat.format(medicalUser.getUpdateDate()));
        values.put(DBContracts.MedicalUser.COLUMN_NAME_GENDER, medicalUser.getGender());
        //TODO: reamane
        long result = database.update(DBContracts.MedicalUser.TABLE_NAME, values,
                WHERE_ID_EQUALS,
                new String[] { String.valueOf( medicalUser.getMedicalId()) });
        Log.i("Update Result:", "=" + result);
        return result;

    }

    public int delete(MedicalUser medicalUser) {
        return database.delete(DBContracts.MedicalUser.TABLE_NAME,
                WHERE_ID_EQUALS, new String[] { medicalUser.getMedicalId() + "" });
    }

    public List<MedicalUser> getAllMedicalUsers() {
        List<MedicalUser> medicalUserList = new ArrayList<MedicalUser>();
        Cursor cursor = database.query(DBContracts.MedicalUser.TABLE_NAME,
                new String[] { DBContracts.MedicalUser.COLUMN_NAME_MEDICAL_ID,
                        DBContracts.MedicalUser.COLUMN_NAME_CREATION_DATE,
                        DBContracts.MedicalUser.COLUMN_NAME_UPDATE_DATE,
                        DBContracts.MedicalUser.COLUMN_NAME_BIRTH_DATE,
                        DBContracts.MedicalUser._ID,
                        DBContracts.MedicalUser.COLUMN_NAME_GENDER }, null, null, null, null, null);

        while (cursor.moveToNext()) {
            MedicalUser medicalUser = new MedicalUser();
            medicalUser.setMedicalId(cursor.getString(0));
            try {
                medicalUser.setCreationDate(UtilsRG.dateFormat.parse(cursor.getString(1)));
                medicalUser.setUpdateDate(UtilsRG.dateFormat.parse(cursor.getString(2)));
                medicalUser.setBirthDate(UtilsRG.dateFormat.parse(cursor.getString(3)));
            } catch (Exception e) {
                // TODO: error handling
                System.out.println("Failure at getMedicalUsers(): " +e.getLocalizedMessage());
            }
            medicalUser.setID(cursor.getInt(4));
            medicalUser.setGender(cursor.getString(5));

            medicalUserList.add(medicalUser);
        }
        return medicalUserList;
    }
}

