package com.artursworld.reactiontest.model.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.artursworld.reactiontest.controller.util.UtilsRG;

import java.util.Date;

/*
* The reaction game to test the users reaction
*/
public class ReactionGame implements Parcelable{

    private Date creationDate;
    private Date updateDate;
    private double duration;
    private double averageReactionTime;
    private String gameType; // GO-Game, GO-NO-GO-Game TODO: create string class for this issue
    private String reactionTestType; // Pre-,In-,Post Operation or Trial TODO: create string class for this issue
    private long operationIssueID;

    public ReactionGame() {
        super();
        this.creationDate = new Date();
    }

    public ReactionGame(long operationIssueId) {
        super();
        this.creationDate = new Date();
        this.operationIssueID = operationIssueId;
    }

    private ReactionGame(Parcel in) {
        super();
        this.creationDate = (Date) in.readSerializable();
        this.updateDate = (Date) in.readSerializable();
        this.duration = in.readDouble();
        this.averageReactionTime = in.readDouble();
        this.gameType = in.readString();
        this.reactionTestType = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(getCreationDate());
        dest.writeSerializable(getUpdateDate());
        dest.writeDouble(getDuration());
        dest.writeDouble(getAverageReactionTime());
        dest.writeString(getGameType());
        dest.writeString(getReactionTestType());
    }

    public static final Parcelable.Creator<ReactionGame> CREATOR = new Parcelable.Creator<ReactionGame>() {
        public ReactionGame createFromParcel(Parcel in) {
            return new ReactionGame(in);
        }

        public ReactionGame[] newArray(int size) {
            return new ReactionGame[size];
        }
    };

    public Date getCreationDate() {
        return creationDate;
    }

    public String getCreationDateFormatted() {
        return UtilsRG.dateFormat.format(getCreationDate());
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public long getOperationIssueID() {
        return operationIssueID;
    }

    public void setOperationIssueID(long operationIssueID) {
        this.operationIssueID = operationIssueID;
    }

    public String getReactionTestType() {
        return reactionTestType;
    }

    public void setReactionTestType(String reactionTestType) {
        this.reactionTestType = reactionTestType;
    }

    public String getGameType() {
        return gameType;
    }

    public void setGameType(String gameType) {
        this.gameType = gameType;
    }

    public double getAverageReactionTime() {
        return averageReactionTime;
    }

    public void setAverageReactionTime(double averageReactionTime) {
        this.averageReactionTime = averageReactionTime;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ReactionGame other = (ReactionGame) obj;
        if (this.creationDate != other.creationDate)
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        int id = (int) (creationDate.getTime()/1000);
        result = prime * result + id;
        return result;
    }

    @Override
    public String toString() {
        return "ReactionGame [creationDate=" + this.creationDate + ", operationIssueId=" + this.operationIssueID + "]";
        /*
            private double duration;
            private int hits;
            private int misses;
            private String reationType;
            private MedicalUser medicalUser
        * */
    }
}
