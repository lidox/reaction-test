package com.artursworld.reactiontest.controller.importer;


import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class JsonUser {

    private String name = null;
    private String gender = null;
    private List<JsonGame> gameList = new ArrayList<>();
    private int age = 0;

    public void addGame(JsonGame game){
        gameList.add(game);
    }

    public Date getBirthDate(){
        return getDateByAge(this.getAge());
    }

    @NonNull
    private Date getDateByAge(int age) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) - age);
        calendar.set(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTime();
    }


    public class JsonGame {
        private Date datetime= null;
        private List<Double> times = new ArrayList<Double>();
        private String type = null;

        public void addTime(Double time){
            times.add(time);
        }

        public Date getDatetime() {
            return datetime;
        }

        public void setDatetime(Date datetime) {
            this.datetime = datetime;
        }

        public List<Double> getTimes() {
            return times;
        }

        public void setTimes(List<Double> times) {
            this.times = times;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<JsonGame> getGAMES() {
        return gameList;
    }

    public void setGAMES(List<JsonGame> GAMES) {
        this.gameList = GAMES;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
