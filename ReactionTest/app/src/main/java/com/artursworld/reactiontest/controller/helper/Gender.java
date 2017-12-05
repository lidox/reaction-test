package com.artursworld.reactiontest.controller.helper;

import com.artursworld.reactiontest.R;
import com.artursworld.reactiontest.controller.util.App;

import java.util.HashMap;
import java.util.Map;

public enum Gender {
    MALE(0, R.string.male),
    FEMALE(1, R.string.female);

    private Integer resourceId;
    private Integer index;

    private static final Map<Integer, Gender> lookupIndex = new HashMap<Integer, Gender>();
    private static final Map<Integer, Gender> lookupResourceId = new HashMap<Integer, Gender>();
    private static final Map<String, Gender> lookupTranslation = new HashMap<String, Gender>();
    private static final Map<String, Gender> lookupByName = new HashMap<String, Gender>();

    static {
        for (Gender g : values()) {
            lookupIndex.put(g.getIndex(), g);
            lookupResourceId.put(g.getResourceId(), g);
            lookupTranslation.put(g.toString(), g);
            lookupByName.put(g.name(), g);
        }
    }

    private Gender(Integer index, Integer displayText) {
        this.resourceId = displayText;
        this.index = index;
    }

    public Integer getIndex() {
        return this.index;
    }

    public Integer getResourceId() {
        return this.resourceId;
    }

    public static Gender findByIndex(Integer index) {
        return lookupIndex.get(index);
    }

    public static Gender findByResourceId(Integer id) {
        return lookupResourceId.get(id);
    }

    public static Gender findByTranslationText(String text) {
        return lookupTranslation.get(text);
    }

    public static Gender findByName(String name) {
        name = name.toUpperCase();
        return lookupByName.get(name);
    }

    @Override
    public String toString() {
        return App.getInstance().getResources().getString(this.resourceId);
    }

}
