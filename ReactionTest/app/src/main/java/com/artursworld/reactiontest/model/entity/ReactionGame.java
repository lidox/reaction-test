package com.artursworld.reactiontest.model.entity;

import android.app.Activity;
import android.os.Parcel;
import android.os.Parcelable;

import com.artursworld.reactiontest.R;
import com.artursworld.reactiontest.controller.helper.Type;
import com.artursworld.reactiontest.controller.helper.Type.GameTypes;
import com.artursworld.reactiontest.controller.helper.Type.TestTypes;
import com.artursworld.reactiontest.controller.util.UtilsRG;

import junit.framework.Test;

import java.sql.Types;
import java.util.Date;

/*
* The reaction game to test the users reaction
*/
public class ReactionGame implements ITimeLineItem {

    private Date creationDate;
    private Date updateDate;
    private double duration;
    private float averageReactionTime;
    private GameTypes gameType;
    private TestTypes testType;
    private String operationIssueID;

    public ReactionGame() {
        super();
        this.creationDate = new Date();
    }

    public ReactionGame(String operationIssue, String gameType, String testType) {
        super();
        this.creationDate = new Date();
        this.updateDate = creationDate;
        this.operationIssueID = operationIssue;
        this.gameType = Type.getGameType(gameType);
        this.testType = Type.getTestType(testType);
    }

    public GameTypes getGameType() {
        return gameType;
    }

    public void setGameType(GameTypes gameType) {
        this.gameType = gameType;
    }

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

    public TestTypes getTestType() {
        return testType;
    }

    public void setTestType(TestTypes testType) {
        this.testType = testType;
    }

    public String getOperationIssueID() {
        return operationIssueID;
    }

    public void setOperationIssueID(String operationIssueID) {
        this.operationIssueID = operationIssueID;
    }

    public double getAverageReactionTime() {
        return averageReactionTime;
    }

    public void setAverageReactionTime(float averageReactionTime) {
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
        int id = (int) (creationDate.getTime() / 1000);
        result = prime * result + id;
        return result;
    }

    @Override
    public String toString() {
        StringBuilder event = new StringBuilder();
        String COMMA = ", ";
        event.append("ReactionGame[OperationIssue: " + operationIssueID + COMMA);
        event.append("GameType: " + gameType + COMMA);
        event.append("TestType: " + testType + COMMA);
        event.append("AverageReactionTime: " + averageReactionTime + "]");
        event.append("TimeStamp: " + getTimeStamp() + "]");
        return event.toString();
    }

    @Override
    public String getTimeLineLabel(Activity activity) {
        StringBuilder ret = new StringBuilder();
        ret.append(activity.getResources().getString(R.string.reaction_test_with_following_time));
        ret.append(": " + getAverageReactionTimeFormatted());
        ret.append(" " + activity.getResources().getString(R.string.at) +" "+ UtilsRG.timeFormat.format(updateDate));
        ret.append(" " + activity.getResources().getString(R.string.oclock));
        return ret.toString() ;
    }

    @Override
    public Date getTimeStamp() {
        return updateDate;
    }

    public String getAverageReactionTimeFormatted() {
        String ret = averageReactionTime +"";
        if(ret.length() > 4){
            return  ret.substring(0,5);
        }
        return ret;
    }

    @Override
    public int compareTo(ITimeLineItem another) {
        return getTimeStamp().compareTo(another.getTimeStamp());
    }
}
