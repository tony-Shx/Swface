package com.henu.swface.Datebase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by 宋浩祥 on 2017/3/7.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "Swface.db";
    private static final int version = 1;
    private static final String CREATE_TABLE_FACES = "CREATE TABLE IF NOT EXISTS Faces(_id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "image_id TEXT,request_id TEXT,gender TEXT,glass TEXT,ethnicity TEXT," +
            "time_used INTEGER,age INTEGER,face_rectangle_width INTEGER,face_rectangle_top INTEGER," +
            "face_rectangle_left INTEGER,face_rectangle_height INTEGER," +
            "left_normal_glass_eye_open REAL,left_no_glass_eye_close REAL,left_occlusion REAL," +
            "left_no_glass_eye_open REAL,left_normal_glass_eye_close REAL,left_dark_glasses REAL," +
            "right_normal_glass_eye_open REAL,right_no_glass_eye_close REAL,right_occlusion REAL," +
            "right_no_glass_eye_open REAL,right_normal_glass_eye_close REAL,right_dark_glasses REAL," +
            "headpose_yaw_angle REAL,headpose_pitch_angle REAL,headpose_roll_angle REAL,blurness REAL," +
            "smile REAL,facequality REAL,face_token TEXT,image_path TEXT)";
    private static final String DROP_TABLE_FACES = "DROP TABLE IF EXISTS Faces";
    private static final String CREATE_TABLE_USER = "CREATE TABLE IF NOT EXISTS User(_id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "user_name TEXT,face_token1 TEXT,face_token2 TEXT,face_token3 TEXT,face_token4 TEXT,face_token5 TEXT)";
    private static final String DROP_TABLE_USER = "DROP TABLE IF EXISTS User";
    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_FACES);
        db.execSQL(CREATE_TABLE_USER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE_FACES);
        db.execSQL(DROP_TABLE_USER);
        db.execSQL(CREATE_TABLE_FACES);
        db.execSQL(CREATE_TABLE_USER);
    }
}
