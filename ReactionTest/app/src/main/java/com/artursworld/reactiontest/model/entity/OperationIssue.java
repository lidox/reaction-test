package com.artursworld.reactiontest.model.entity;

import java.util.Date;

public class OperationIssue {
    private long _ID;
    private Date intubationDate;
    private Date wakeUpDate;
    private double narcosisDuration;
    private String displayName;
    private MedicalUser medicalUser;

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

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public MedicalUser getMedicalUser() {
        return medicalUser;
    }

    public void setMedicalUser(MedicalUser medicalUser) {
        this.medicalUser = medicalUser;
    }

    @Override
    public String toString() {
        return OperationIssue.class.getSimpleName() +"[displayName=" + this.displayName + ", medicalUserId=" + this.medicalUser.getMedicalId() + "]";
    }
}
