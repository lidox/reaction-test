package com.artursworld.reactiontest.controller.helper;


import com.artursworld.reactiontest.R;
import com.artursworld.reactiontest.view.MyApplication;

import java.util.HashMap;
import java.util.Map;

public enum OpStatus {
    RUNNING(0, R.string.operation_started),
    FINISHED(1, R.string.operation_finished);

    private Integer resourceId = null;
    private Integer index = null;

    private static final Map<Integer, OpStatus> lookupIndex = new HashMap<Integer, OpStatus>();
    private static final Map<Integer, OpStatus> lookupResourceId = new HashMap<Integer, OpStatus>();
    private static final Map<String, OpStatus> lookupTranslation = new HashMap<String, OpStatus>();

    private OpStatus(Integer index, Integer displayTextId) {
        this.resourceId = displayTextId;
        this.index = index;
    }

    static {
        for (OpStatus status : values()) {
            lookupIndex.put(status.getIndex(), status);
            lookupResourceId.put(status.getResourceId(), status);
            lookupTranslation.put(status.toString(), status);
        }
    }

    public Integer getIndex() {
        return index;
    }

    public Integer getResourceId() {
        return resourceId;
    }

    public static OpStatus findByIndex(Integer index) {
        return lookupIndex.get(index);
    }

    public static OpStatus findByResourceId(Integer id) {
        return lookupResourceId.get(id);
    }

    public static OpStatus findByTranslationText(String text) {
        return lookupTranslation.get(text);
    }

    @Override
    public String toString() {
        return MyApplication.getInstance().getResources().getString(this.resourceId);
    }
}
