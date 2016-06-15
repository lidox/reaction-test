package com.artursworld.reactiontest.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class ReactionGame implements Parcelable{

    private Date creationDate;
    private double duration;
    private int hits;
    private int misses;
    private String reationType;
    private MedicalUser medicalUser;

    public ReactionGame() {
        super();
    }

    private ReactionGame(Parcel in) {
        super();
        this.creationDate = (Date) in.readSerializable();
        this.duration = in.readDouble();
        this.hits = in.readInt();
        this.misses = in.readInt();
        this.reationType = in.readString();
        this.medicalUser = in.readParcelable(MedicalUser.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(getCreationDate());
        dest.writeDouble(getDuration());
        dest.writeInt(getHits());
        dest.writeInt(getMisses());
        dest.writeString(getReationType());
        dest.writeParcelable(getMedicalUser(), flags);
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

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public int getHits() {
        return hits;
    }

    public void setHits(int hits) {
        this.hits = hits;
    }

    public int getMisses() {
        return misses;
    }

    public void setMisses(int misses) {
        this.misses = misses;
    }

    public String getReationType() {
        return reationType;
    }

    public void setReationType(String reationType) {
        this.reationType = reationType;
    }

    public MedicalUser getMedicalUser() {
        return medicalUser;
    }

    public void setMedicalUser(MedicalUser medicalUser) {
        this.medicalUser = medicalUser;
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
}
