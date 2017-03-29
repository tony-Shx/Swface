package com.henu.swface.Datebase;

import android.provider.BaseColumns;

/**
 * Created by 宋浩祥 on 2017/3/7.
 */

public final class FaceMetaData {
    private FaceMetaData() {
    }




    public static abstract class FaceTable implements BaseColumns{
        public static final String TABLE_NAME = "Faces";
        public static final String IMAGE_ID = "image_id";
        public static final String REQUEST_ID = "request_id";
        public static final String GENDER = "gender";
        public static final String GLASS = "glass";
        public static final String ETHNICITY = "ethnicity";
        public static final String TIME_USED = "time_used";
        public static final String AGE = "age";
        public static final String FACE_RECTANGLE_WIDTH = "face_rectangle_width";
        public static final String FACE_RECTANGLE_TOP = "face_rectangle_top";
        public static final String FACE_RECTANGLE_LEFT = "face_rectangle_left";
        public static final String FACE_RECTANGLE_HEIGHT = "face_rectangle_height";
        public static final String LEFT_NORMAL_GLASS_EYE_OPEN = "left_normal_glass_eye_open";
        public static final String LEFT_NO_GLASS_EYE_CLOSE = "left_no_glass_eye_close";
        public static final String LEFT_OCCLUSION = "left_occlusion";
        public static final String LEFT_NO_GLASS_EYE_OPEN = "left_no_glass_eye_open";
        public static final String LEFT_NORMAL_GLASS_EYE_CLOSE ="left_normal_glass_eye_close";
        public static final String LEFT_DARK_GLASSES = "left_dark_glasses";
        public static final String RIGHT_NORMAL_GLASS_EYE_OPEN = "right_normal_glass_eye_open";
        public static final String RIGHT_NO_GLASS_EYE_CLOSE = "right_no_glass_eye_close";
        public static final String RIGHT_OCCLUSION = "right_occlusion";
        public static final String RIGHT_NO_GLASS_EYE_OPEN = "right_no_glass_eye_open";
        public static final String RIGHT_NORMAL_GLASS_EYE_CLOSE = "right_normal_glass_eye_close";
        public static final String RIGHT_DARK_GLASSES = "right_dark_glasses";
        public static final String HEADPOSE_YAW_ANGLE = "headpose_yaw_angle";
        public static final String HEADPOSE_PITCH_ANGLE = "headpose_pitch_angle";
        public static final String HEADPOSE_ROLL_ANGLE = "headpose_roll_angle";
        public static final String BLURNESS = "blurness";
        public static final String SMILE = "smile";
        public static final String FACEQUALITY = "facequality";
        public static final String FACE_TOKEN = "face_token";
        public static final String IMAGE_PATH = "image_path";

    }
}
