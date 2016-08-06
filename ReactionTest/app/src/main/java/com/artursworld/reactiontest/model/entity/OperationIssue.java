package com.artursworld.reactiontest.model.entity;

import java.util.Date;

/*
* A user can be included into multiple operations. So for each opration an issue must be provided
*/
public class OperationIssue {
    private long _ID;
    private Date intubationDate;
    private Date wakeUpDate;
    private Date creationDate;
    private Date updateDate;
    private double narcosisDuration;
    private String displayName;
    private String medicalUserId;

    public long get_ID() {
        return _ID;
    }

    public void set_ID(long _ID) {
        this._ID = _ID;
    }

    public Date getIntubationDate() {
        return intubationDate;
    }

    public void setIntubationDate(Date intubationDate) {
        this.intubationDate = intubationDate;
    }

    public Date getWakeUpDate() {
        return wakeUpDate;
    }

    public void setWakeUpDate(Date wakeUpDate) {
        this.wakeUpDate = wakeUpDate;
    }

    public double getNarcosisDuration() {
        return narcosisDuration;
    }

    public void setNarcosisDuration(double narcosisDuration) {
        this.narcosisDuration = narcosisDuration;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getMedicalUserId() {
        return medicalUserId;
    }

    public void setMedicalUserId(String medicalUserId) {
        this.medicalUserId = medicalUserId;
    }

    @Override
    public String toString() {
        return OperationIssue.class.getSimpleName() +"[displayName=" + this.displayName + ", medicalUserId=" + this.medicalUserId + "]";
    }
}
