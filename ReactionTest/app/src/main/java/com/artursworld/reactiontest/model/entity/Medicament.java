package com.artursworld.reactiontest.model.entity;

import com.artursworld.reactiontest.controller.util.UtilsRG;

import java.util.Date;

/*
* The medicament which a user got during an operation issue
*/
public class Medicament {

    private String operationIssueId = null;
    private String name = null;
    private int dosage = 0;
    private String unit = null;
    private Date timestamp = null;
    private Date creationDate = null;

    public Medicament(String operationIssueId, String name, int dosage, String unit, Date timeStamp){
        this.operationIssueId = operationIssueId;
        this.name = name;
        this.dosage = dosage;
        this.unit = unit;
        this.timestamp = timeStamp;
        this.creationDate = new Date();
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getOperationIssueId() {
        return operationIssueId;
    }

    public void setOperationIssueId(String operationIssueId) {
        this.operationIssueId = operationIssueId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDosage() {
        return dosage;
    }

    public void setDosage(int dosage) {
        this.dosage = dosage;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        StringBuilder event = new StringBuilder();
        String COMMA = ", ";
        event.append(Medicament.class.getSimpleName()+"[");
        event.append("name: " + name + COMMA);
        event.append("dosage: " + dosage + COMMA);
        event.append("unit: " + unit + COMMA);
        event.append("creationDate_PK: " + creationDate + COMMA);
        event.append("timestamp: " + timestamp + COMMA);
        event.append("operationIssueId_FK: " + operationIssueId + "]");
        return event.toString();
    }

    public String getTime() {
        try {
            return UtilsRG.timeFormat.format(this.timestamp);
        }
        catch (Exception e){
            UtilsRG.error("Could not parse date to string for medicament: " +e.getLocalizedMessage());
            return null;
        }
    }

    public String getDate() {
        try {
            return UtilsRG.dateOnlyFormat.format(this.timestamp);
        }
        catch (Exception e){
            UtilsRG.error("Could not parse date to string for medicament: " +e.getLocalizedMessage());
            return null;
        }
    }
}
