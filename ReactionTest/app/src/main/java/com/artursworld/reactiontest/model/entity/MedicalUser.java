package com.artursworld.reactiontest.model.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.artursworld.reactiontest.R;
import com.artursworld.reactiontest.controller.helper.Gender;
import com.artursworld.reactiontest.controller.util.UtilsRG;

import java.util.Calendar;
import java.util.Date;

/*
* the midical user
*/
public class MedicalUser {
    private String medicoId;
    private Date creationDate;
    private Date updateDate;
    private Date birthDate;
    private Gender gender;
    private double bmi;
    private boolean isMarkedAsDeleted = false;

    public MedicalUser() {
        super();
        this.creationDate = new Date();
        this.updateDate = new Date();
    }

    public boolean isMarkedAsDeleted() {
        return isMarkedAsDeleted;
    }

    public void setMarkedAsDeleted(boolean markedAsDeleted) {
        isMarkedAsDeleted = markedAsDeleted;
    }

    public MedicalUser(String medicalId, Date birthDate, Gender gender, double bmi) {
        this.creationDate = new Date();
        this.updateDate = new Date();
        if (medicalId != null)
            this.medicoId = medicalId;

        if (birthDate != null)
            this.birthDate = birthDate;

        if (gender != null)
            this.gender = gender;

        this.setBmi(bmi);
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
        this.updateDate = new Date();
    }

    public double getBmi() {
        return bmi;
    }

    public void setBmi(double bmi) {
        this.bmi = bmi;
        this.updateDate = new Date();
    }

    public Date getBirthDate() {
        return birthDate;
    }


    public String getBirthDateAsString() {
        try {
            if (birthDate != null) {
                return UtilsRG.germanDateFormat.format(birthDate);
            }

        }
        catch (Exception e){

        }
        return "";
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

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
        this.updateDate = new Date();
    }

    public String getMedicalId() {
        return medicoId;
    }

    public void setMedicalId(String medicalId) {
        this.medicoId = medicalId;
        this.updateDate = new Date();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MedicalUser other = (MedicalUser) obj;
        if (this.medicoId != other.medicoId)
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
        StringBuilder meduser = new StringBuilder();
        meduser.append("MedicalUser [");
        meduser.append("medicalId=" + this.medicoId + ",");
        meduser.append("creationDate=" + this.creationDate + ",");
        meduser.append("updateDate=" + this.updateDate + ",");
        meduser.append("birthDate=" + this.birthDate + ",");
        meduser.append("gender=" + this.getGender() + ",");
        meduser.append("isMarkedAsDeleted=" + this.isMarkedAsDeleted());
        meduser.append("]");
        return meduser.toString();
    }

    /*
    * Returns the age of the user caculated by birthdate
    */
    public int getAge() {
        if (birthDate != null) {
            Calendar birthDate = Calendar.getInstance();
            birthDate.setTime(this.birthDate);
            Calendar today = Calendar.getInstance();
            int age = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR);

            if (today.get(Calendar.DAY_OF_YEAR) < birthDate.get(Calendar.DAY_OF_YEAR))
                age--;

            return age;
        }
        return 0;
    }

    /*
    * Returns the image by gender for the list view
    */
    public int getImage() {
        //TODO: only work with english version. Thus use enums
        if(this.gender != null){
            if (this.gender == Gender.FEMALE) {
                return R.drawable.female_icon;
            }
            return R.drawable.male_icon;
        }
        return R.drawable.male_female_icon;
    }
}
