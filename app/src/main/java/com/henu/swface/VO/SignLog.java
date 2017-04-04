package com.henu.swface.VO;

/**
 * Created by 宋浩祥 on 2017/4/4.
 */

public class SignLog {
    private String user_name;
    private long time;
    private float confidence;

    public SignLog() {
    }

    public SignLog(String user_name, long time, float confidence) {
        this.user_name = user_name;
        this.time = time;
        this.confidence = confidence;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public float getConfidence() {
        return confidence;
    }

    public void setConfidence(float confidence) {
        this.confidence = confidence;
    }
}
