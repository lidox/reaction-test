package com.artursworld.reactiontest.controller.export;


import com.artursworld.reactiontest.model.entity.ReactionGame;

import java.util.List;

public class UserStats {

    private String name;
    private int age;
    private String gender;
    private List<ReactionGame> reactionGameList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public List<ReactionGame> getReactionGameList() {
        return reactionGameList;
    }

    public void setReactionGameList(List<ReactionGame> reactionGameList) {
        this.reactionGameList = reactionGameList;
    }

    /*
    {name:"marcus", gender: "male", age: 24, games:[
    {
        datetime: "28.9.2016 11:49",
        times: [290,300,312,299],
        type: "pre"
    },
    {
        datetime: "28.9.2016 12:05",
        times: [290,300,312,299],
        type: "in"
    },
]}

     */

}
