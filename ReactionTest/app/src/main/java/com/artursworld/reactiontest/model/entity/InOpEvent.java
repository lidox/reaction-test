package com.artursworld.reactiontest.model.entity;


import java.util.Date;

public class InOpEvent {

    private Date timeStamp = null;
    private String operationIssue = null;
    private String additionalNote = null;
    private String type = null;

    public InOpEvent(String operationIssue, Date timeStamp, String type, String note) {
        this.operationIssue = operationIssue;
        this.timeStamp = timeStamp;
        this.type = type;
        this.additionalNote = note;
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
        return additionalNote;
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
}
