package com.artursworld.reactiontest.controller.helper;


import android.content.Context;

import com.artursworld.reactiontest.model.entity.MedicalUser;

import java.util.List;

public interface AsyncResponse {
    void getMedicalUserList(List<MedicalUser> result);
}
