package com.artursworld.reactiontest.model.entity;

import java.util.Date;

public class Attempt {

    private long reactionTime;
    private boolean isValid;
    private Date creationDate;
    private Date reactionGameDateId;

    public long getReactionTime() {
        return reactionTime;
    }

    public void setReactionTime(long reactionTime) {
        this.reactionTime = reactionTime;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getReactionGameDateId() {
        return reactionGameDateId;
    }

    public void setReactionGameDateId(Date reactionGameDateId) {
        this.reactionGameDateId = reactionGameDateId;
    }

    @Override
    public String toString() {
        return Attempt.class.getSimpleName() +"[reactionGameDateId=" + this.getReactionGameDateId() + ", isValid="+this.isValid+", reactionTime=" + this.reactionTime + "]";
    }
}
