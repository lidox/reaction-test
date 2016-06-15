package com.artursworld.reactiontest.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.artursworld.reactiontest.entity.MedicalUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MedicalUserManager extends MedicalUserDbManager {

    private static final SimpleDateFormat formatter = new SimpleDateFormat(
            "yyyy-MM-dd", Locale.GERMAN);
    private static final String WHERE_ID_EQUALS = ReactionGameContract.MedicalUser.COLUMN_NAME_MEDICAL_ID
            + " =?";

    public MedicalUserManager(Context context) {
        super(context);
    }

    public long insert(MedicalUser medicalUser) {
        try {
            ContentValues values = new ContentValues();
            values.put(ReactionGameContract.MedicalUser.COLUMN_NAME_MEDICAL_ID, medicalUser.getMedicalId());
            //TODO: insert dates to database
            //values.put(ReactionGameContract.MedicalUser.COLUMN_NAME_CREATION_DATE, medicalUser.getCreationDate());
            //values.put(ReactionGameContract.MedicalUser.COLUMN_NAME_UPDATE_DATE, medicalUser.getUpdateDate());
            // TODO: validation before put
            if (medicalUser.getBirthDate() != null)
                values.put(ReactionGameContract.MedicalUser.COLUMN_NAME_BIRTH_DATE, formatter.format(medicalUser.getBirthDate()));
            values.put(ReactionGameContract.MedicalUser.COLUMN_NAME_GENDER, medicalUser.getGender());
            return database.insert(ReactionGameContract.MedicalUser.TABLE_NAME, null, values);
        }
        catch (Exception e) {
            // TODO: this try catch does not work. search online for error handling
            System.out.println("Failed to insert: " + e.getLocalizedMessage());
        }
        return -1L;
    }

    public long update(MedicalUser medicalUser) {
        ContentValues values = new ContentValues();
        // TODO: update all attributes
        values.put(ReactionGameContract.MedicalUser.COLUMN_NAME_GENDER, medicalUser.getGender());

        long result = database.update(ReactionGameContract.MedicalUser.TABLE_NAME, values,
                WHERE_ID_EQUALS,
                new String[] { String.valueOf(medicalUser.getMedicalId()) });
        Log.i("Update Result:", "=" + result);
        return result;

    }

    public int delete(MedicalUser medicalUser) {
        return database.delete(ReactionGameContract.MedicalUser.TABLE_NAME,
                WHERE_ID_EQUALS, new String[] { medicalUser.getMedicalId() + "" });
    }

    public List<MedicalUser> getMedicalUsers() {
        List<MedicalUser> medicalUserList = new ArrayList<MedicalUser>();
        // TODO: add all columns to read
        Cursor cursor = database.query(ReactionGameContract.MedicalUser.TABLE_NAME,
                new String[] { ReactionGameContract.MedicalUser.COLUMN_NAME_MEDICAL_ID,
                        ReactionGameContract.MedicalUser.COLUMN_NAME_GENDER }, null, null, null, null,
                null);

        while (cursor.moveToNext()) {
            MedicalUser medicalUser = new MedicalUser();
            medicalUser.setMedicalId(cursor.getString(0));
            medicalUser.setGender(cursor.getString(1));
            // TODO: set all columns
            medicalUserList.add(medicalUser);
        }
        return medicalUserList;
    }

    /*
    public void loadDepartments() {
        Department department = new Department("Development");
        Department department1 = new Department("R and D");
        Department department2 = new Department("Human Resource");
        Department department3 = new Department("Financial");
        Department department4 = new Department("Marketing");
        Department department5 = new Department("Sales");

        List<Department> departments = new ArrayList<Department>();
        departments.add(department);
        departments.add(department1);
        departments.add(department2);
        departments.add(department3);
        departments.add(department4);
        departments.add(department5);
        for (Department dept : departments) {
            ContentValues values = new ContentValues();
            values.put(DataBaseHelper.NAME_COLUMN, dept.getName());
            database.insert(DataBaseHelper.DEPARTMENT_TABLE, null, values);
        }
    }
    */
}

