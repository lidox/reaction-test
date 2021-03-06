package com.artursworld.reactiontest.model.entity;


import android.app.Activity;

import com.artursworld.reactiontest.R;
import com.artursworld.reactiontest.controller.util.UtilsRG;

import java.text.SimpleDateFormat;
import java.util.Date;

public class InOpEvent implements ITimeLineItem, Comparable<ITimeLineItem>{

    private Date creationDate = null;
    private Date timeStamp = null;
    private String operationIssue = null;
    private String additionalNote = null;
    private String type = null;

    public InOpEvent(String operationIssue, Date timeStamp, String type, String note) {
        this.creationDate = new Date();
        this.operationIssue = operationIssue;
        this.timeStamp = timeStamp;
        this.type = type;
        this.additionalNote = note;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getOperationIssue() {
        return operationIssue;
    }

    public void setOperationIssue(String operationIssue) {
        this.operationIssue = operationIssue;
    }

    public String getAdditionalNote() {
        if (additionalNote != null)
            return additionalNote;
        else
            return "";
    }

    public void setAdditionalNote(String additionalNote) {
        this.additionalNote = additionalNote;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        StringBuilder event = new StringBuilder();
        String COMMA = ", ";
        event.append("Event(OperationIssue: " + operationIssue + COMMA);
        event.append("Timestamp: " + timeStamp + COMMA);
        event.append("Type: " + type + COMMA);
        event.append("Note: " + additionalNote + ")");
        return event.toString();
    }

    public String getHoursAndMinutes() {
        SimpleDateFormat formatter = UtilsRG.timeFormat;
        if (this.timeStamp != null)
            return formatter.format(this.timeStamp);
        else
            return "-";
    }

    public String getAudioTimeStampt() {
        SimpleDateFormat formatter = UtilsRG.audioTimeStamp;
        if (this.timeStamp != null)
            return formatter.format(this.timeStamp);
        else
            return "-";
    }

    @Override
    public String getTimeLineLabel(Activity activity) {
        if(!getType().equals("Note"))
            return this.getType()+ ": " + this.getHoursAndMinutes() + " " +activity.getResources().getString(R.string.oclock);
        else
            return this.getAdditionalNote() + ". "+this.getType()+ ": " + this.getHoursAndMinutes() + " " +activity.getResources().getString(R.string.oclock);
    }

    @Override
    public int compareTo(ITimeLineItem another) {
        return getTimeStamp().compareTo(another.getTimeStamp());
    }

    public String toJSON() {
        StringBuilder e = new StringBuilder();
        String COMMA = ",";
        String EQUALS = ":";
        String MARKS = "\"";
        String BEGIN = "[{";
        String END = "}]";

        e.append(MARKS + "event" + MARKS + EQUALS + BEGIN);
        e.append(MARKS + "type" + MARKS + EQUALS + MARKS + getType() + MARKS + COMMA);
        e.append(MARKS + "note" + MARKS + EQUALS + MARKS + getAdditionalNote() + MARKS + COMMA);
        e.append(MARKS + "timestamp" + MARKS + EQUALS + MARKS + UtilsRG.dateFormat.format(getTimeStamp()) + MARKS);
        e.append(END);
        return e.toString();
    }
}
