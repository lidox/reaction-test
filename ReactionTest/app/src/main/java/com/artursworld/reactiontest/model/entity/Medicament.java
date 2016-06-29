package com.artursworld.reactiontest.model.entity;

public class Medicament {
    private long operationIssueId;
    private String name;
    private int amount;
    private String measurement;

    public long getOperationIssueId() {
        return operationIssueId;
    }

    public void setOperationIssueId(long operationIssueId) {
        this.operationIssueId = operationIssueId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getMeasurement() {
        return measurement;
    }

    public void setMeasurement(String measurement) {
        this.measurement = measurement;
    }

    @Override
    public String toString() {
        return Medicament.class.getSimpleName() +"[operationIssueId=" + this.operationIssueId + ", medicament="+this.name+" " + this.amount + " "+ this.measurement+"]";
    }
}
