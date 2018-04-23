package com.artursworld.reactiontest.model.entity;

import android.app.Activity;

import com.artursworld.reactiontest.R;
import com.artursworld.reactiontest.controller.helper.Type;
import com.artursworld.reactiontest.controller.helper.Type.GameTypes;
import com.artursworld.reactiontest.controller.helper.Type.TestTypes;
import com.artursworld.reactiontest.controller.util.App;
import com.artursworld.reactiontest.controller.util.UtilsRG;
import com.artursworld.reactiontest.model.persistence.manager.TrialManager;

import java.util.Date;
import java.util.List;

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
    private int patientsAlertnessFactor;
    private double brainTemperature;

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

    /**
     * Get reaction times from database
     *
     * @return the reaction times from database
     */
    public double[] getReactionTimesByDB() {
        TrialManager trialDB = new TrialManager(App.getAppContext());
        List<Integer> rtList = trialDB.getAllReactionTimesList(getCreationDateFormatted());
        double[] reactionTimes = new double[rtList.size()];
        for (int i = 0; i < rtList.size(); i++) {
            reactionTimes[i] = rtList.get(i) / 1000.;
        }
        return reactionTimes;
    }

    public double getBrainTemperature() {
        return brainTemperature;
    }

    public void setBrainTemperature(double brainTemperature) {
        this.brainTemperature = brainTemperature;
    }

    public int getPatientsAlertnessFactor() {
        return patientsAlertnessFactor;
    }

    public void setPatientsAlertnessFactor(int patientsAlertnessFactor) {
        this.patientsAlertnessFactor = patientsAlertnessFactor;
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
        StringBuilder game = new StringBuilder();
        String COMMA = ", ";
        game.append("ReactionGame[OperationIssue: " + operationIssueID + COMMA);
        game.append("GameType: " + gameType + COMMA);
        game.append("TestType: " + testType + COMMA);
        game.append("PateintsAlertness: " + patientsAlertnessFactor + COMMA);
        game.append("AverageReactionTime: " + averageReactionTime + COMMA);
        game.append("BrainTemperature: " + brainTemperature + COMMA);
        game.append("TimeStamp: " + getTimeStamp() + "]");
        return game.toString();
    }

    @Override
    public String getTimeLineLabel(Activity activity) {
        StringBuilder ret = new StringBuilder();
        ret.append(activity.getResources().getString(R.string.reaction_test_with_following_time));
        ret.append(": " + getAverageReactionTimeFormatted());
        ret.append(" " + activity.getResources().getString(R.string.at) + " " + UtilsRG.timeFormat.format(updateDate));
        ret.append(" " + activity.getResources().getString(R.string.oclock));

        if (this.brainTemperature > 0) {
            ret.append(", brain temperature: " + this.brainTemperature);
        }

        return ret.toString();
    }

    @Override
    public Date getTimeStamp() {
        return updateDate;
    }

    public String getAverageReactionTimeFormatted() {
        String ret = averageReactionTime + "";
        if (ret.length() > 4) {
            return ret.substring(0, 5);
        }
        return ret;
    }

    @Override
    public int compareTo(ITimeLineItem another) {
        return getTimeStamp().compareTo(another.getTimeStamp());
    }
}
