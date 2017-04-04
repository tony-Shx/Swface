package com.henu.swface.VO;

/**
 * Created by 宋浩祥 on 2017/4/4.
 */

public class FaceSignIn {
    private int time_used;
    private float thresholds3,thresholds4,thresholds5,confidence;
    private String face_token;

    public FaceSignIn() {
    }

    public int getTime_used() {
        return time_used;
    }

    public void setTime_used(int time_used) {
        this.time_used = time_used;
    }

    public float getThresholds3() {
        return thresholds3;
    }

    public void setThresholds3(double thresholds3) {
        this.thresholds3 = (float) thresholds3;
    }

    public float getThresholds4() {
        return thresholds4;
    }

    public void setThresholds4(double thresholds4) {
        this.thresholds4 = (float) thresholds4;
    }

    public float getThresholds5() {
        return thresholds5;
    }

    public void setThresholds5(double thresholds5) {
        this.thresholds5 = (float) thresholds5;
    }

    public float getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = (float) confidence;
    }

    public String getFace_token() {
        return face_token;
    }

    public void setFace_token(String face_token) {
        this.face_token = face_token;
    }

    @Override
    public String toString() {
        return "FaceSignIn{" +
                "time_used=" + time_used +
                ", thresholds3=" + thresholds3 +
                ", thresholds4=" + thresholds4 +
                ", thresholds5=" + thresholds5 +
                ", confidence=" + confidence +
                ", face_token='" + face_token + '\'' +
                '}';
    }
}
