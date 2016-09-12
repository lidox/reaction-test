package com.artursworld.reactiontest.model.persistence.manager;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.artursworld.reactiontest.controller.util.UtilsRG;
import com.artursworld.reactiontest.model.entity.InOpEvent;
import com.artursworld.reactiontest.model.entity.Medicament;
import com.artursworld.reactiontest.model.persistence.EntityDbManager;
import com.artursworld.reactiontest.model.persistence.contracts.DBContracts;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MedicamentManager extends EntityDbManager {

    String WHERE_CLAUSE_PK = DBContracts.MedicamentTable.CREATION_DATE_PK + "=?";

    public MedicamentManager(Context context) {
        super(context);
    }

    /**
     * Insert a medicament into database
     *
     * @param medicament the medicament to insert
     */
    public void insertMedicament(Medicament medicament) {
        if (medicament == null) {
            UtilsRG.error("The medicament to insert equals null. So it won't be inserted!");
            return;
        }

        ContentValues values = getEventContentValues(medicament);

        try {
            database.insertOrThrow(DBContracts.MedicamentTable.TABLE_NAME, null, values);
            UtilsRG.info("New Medicament added successfully:" + medicament.toString());
        } catch (Exception e) {
            UtilsRG.error("Could not insert new Medicament into db: " + medicament.toString() + "! " + e.getLocalizedMessage());
        }
    }

    public int updateMedicament(Medicament medicament) {
        try {
            ContentValues values = getEventContentValues(medicament);
            String creationDate = UtilsRG.dateFormat.format(medicament.getCreationDate());
            long i = database.update(DBContracts.MedicamentTable.TABLE_NAME, values, WHERE_CLAUSE_PK, new String[]{creationDate});
            UtilsRG.info("Updated " + i + ". Medicaments. " + medicament.toString());
            if(i>0)
                return 1;  // 1 for successful
            else
                return 0;  // 0 for unsuccessful
        } catch (Exception e) {
            UtilsRG.info("Exception! Could not update " + medicament.toString() + "! " + e.getLocalizedMessage());
            return 0;
        }
    }

    /**
     * Deletes a medicament from database
     *
     * @param medicament the medicament to delete
     * @return the number of rows affected if a whereClause is passed in,
     * 0 otherwise. To remove all rows and get a count pass "1" as the whereClause.
     */
    public int deleteMedicament(Medicament medicament) {
        int resultCode = 0;

        // validation
        if (medicament == null) {
            UtilsRG.error("The medicament to be deleted equals null. So it cannot be deleted!");
            return resultCode;
        }

        try {
            resultCode = database.delete(
                    DBContracts.MedicamentTable.TABLE_NAME,
                    WHERE_CLAUSE_PK,
                    new String[]{UtilsRG.dateFormat.format(medicament.getCreationDate())}
            );
            UtilsRG.info("Medicament has been deleted from database: " + medicament.toString());
        } catch (Exception e) {
            UtilsRG.error("Could not delete Medicament from database: " + medicament + "! " + e.getLocalizedMessage());
        }

        return resultCode;
    }

    /**
     * Reads all medicaments from database
     *
     * @param operationIssue the selected operation issue
     * @param sortingOrder   the medicament's sort order
     * @return a list of all medicaments for specified operation issue
     */
    public List<Medicament> getMedicamentList(String operationIssue, String sortingOrder) {
        if (operationIssue == null) {
            UtilsRG.error("Cannot get Medicament list by operationIssue, because operationIssue = null");
            return null;
        }

        String sortOrder = DBContracts.MedicamentTable.TIMESTAMP + " " + sortingOrder; //TODO: this sort order works?

        List<Medicament> medicamentList = new ArrayList<Medicament>();
        String WHERE_CLAUSE = DBContracts.MedicamentTable.OPERATION_ISSUE_NAME_FK + " like '" + operationIssue + "'";
        Cursor cursor = database.query(DBContracts.MedicamentTable.TABLE_NAME,
                new String[]{
                        DBContracts.MedicamentTable.NAME,
                        DBContracts.MedicamentTable.DOSAGE,
                        DBContracts.MedicamentTable.UNIT,
                        DBContracts.MedicamentTable.TIMESTAMP,
                        DBContracts.MedicamentTable.CREATION_DATE_PK,
                }, WHERE_CLAUSE, null, null, null, sortOrder);

        while (cursor.moveToNext()) {
            Date creationDate = null;
            Date timeStamp = null;

            try {
                creationDate = (UtilsRG.dateFormat.parse(cursor.getString(4)));
            } catch (Exception e) {
                UtilsRG.info("Could not parse the creation date of the medicament, while try to read the medicament from database: " + e.getLocalizedMessage());
            }

            try {
                timeStamp = (UtilsRG.dateFormat.parse(cursor.getString(3)));
            } catch (Exception e) {
                UtilsRG.info("Could not parse the timeStamp of the medicament, while try to read the medicament from database: " + e.getLocalizedMessage());
            }

            Medicament medicament = new Medicament(operationIssue, cursor.getString(0), cursor.getInt(1), cursor.getString(2), timeStamp);
            medicament.setCreationDate(creationDate);
            medicamentList.add(medicament);
        }
        UtilsRG.info(medicamentList.size() + ". Medicaments has been found:");
        UtilsRG.info(medicamentList.toString());

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return medicamentList;
    }

    /**
     * Get content values for database by medicament to be able to make inserts and updates
     *
     * @param medicament the medicament to insert or update
     * @return the medicaments content values
     */
    private ContentValues getEventContentValues(Medicament medicament) {
        ContentValues values = new ContentValues();

        if (medicament.getTimestamp() != null)
            values.put(DBContracts.MedicamentTable.CREATION_DATE_PK, UtilsRG.dateFormat.format(medicament.getCreationDate()));

        if (medicament.getTimestamp() != null)
            values.put(DBContracts.MedicamentTable.TIMESTAMP, UtilsRG.dateFormat.format(medicament.getTimestamp()));

        if (medicament.getOperationIssueId() != null)
            values.put(DBContracts.MedicamentTable.OPERATION_ISSUE_NAME_FK, medicament.getOperationIssueId());

        if (medicament.getName() != null)
            values.put(DBContracts.MedicamentTable.NAME, medicament.getName());

        if (medicament.getDosage() != 0)
            values.put(DBContracts.MedicamentTable.DOSAGE, medicament.getDosage());

        if (medicament.getDosage() != 0)
            values.put(DBContracts.MedicamentTable.UNIT, medicament.getUnit());
        return values;
    }

}
