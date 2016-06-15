package com.artursworld.reactiontest.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class MedicalUser implements Parcelable {

    private String medicalId;
    private Date creationDate;
    private Date updateDate;
    private Date birthDate;
    private String gender;

    public MedicalUser(){
        super();
        this.creationDate = new Date();
    }

    public MedicalUser(String medicalId, Date birthDate, String gender){
        this.creationDate = new Date();
        if(medicalId != null)
            this.medicalId = medicalId;

        if(birthDate != null)
            this.birthDate = birthDate;

        if(gender != null)
            this.gender = gender;
    }

    private MedicalUser(Parcel in) {
        super();
        this.medicalId = in.readString();
        this.creationDate = (Date) in.readSerializable();
        this.updateDate = (Date) in.readSerializable();
        this.birthDate = new Date(in.readLong());
        this.gender = in.readString();
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
        this.updateDate = new Date();
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
        this.updateDate = new Date();
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
        this.updateDate = new Date();
    }

    public String getMedicalId() {
        return medicalId;
    }

    public void setMedicalId(String medicalId) {
        this.medicalId = medicalId;
        this.updateDate = new Date();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getMedicalId());
        dest.writeSerializable(getCreationDate());
        dest.writeSerializable(getUpdateDate());
        dest.writeLong(getBirthDate().getTime());
        dest.writeString(getGender());
    }

    public static final Parcelable.Creator<MedicalUser> CREATOR = new Parcelable.Creator<MedicalUser>() {
        public MedicalUser createFromParcel(Parcel in) {
            return new MedicalUser(in);
        }

        public MedicalUser[] newArray(int size) {
            return new MedicalUser[size];
        }
    };

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MedicalUser other = (MedicalUser) obj;
        if (this.medicalId != other.medicalId)
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
        return "MedicalUser [id=" + this.medicalId + ", gender=" + this.getGender() + "]";
    }
}
