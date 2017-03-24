package com.henu.swface.Datebase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by 宋浩祥 on 2017/3/7.
 */

public class DatebaseAdapter {

    private DatabaseHelper databaseHelper;
    public DatebaseAdapter(Context context) {
        databaseHelper = new DatabaseHelper(context);
    }

    public void add(Face face){
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        //将数据按照键值对存入ContentValues
        ContentValues values = new ContentValues();
        values.put(FaceMetaData.FaceTable.IMAGE_ID, face.getImage_id());
        values.put(FaceMetaData.FaceTable.REQUEST_ID, face.getRequest_id());
        values.put(FaceMetaData.FaceTable.GENDER, face.getGender());
        values.put(FaceMetaData.FaceTable.GLASS, face.getGlass());
        values.put(FaceMetaData.FaceTable.ETHNICITY, face.getEthnicity());
        values.put(FaceMetaData.FaceTable.TIME_USED, face.getTime_used());
        values.put(FaceMetaData.FaceTable.AGE, face.getAge());
        values.put(FaceMetaData.FaceTable.FACE_RECTANGLE_WIDTH, face.getFace_rectangle_width());
        values.put(FaceMetaData.FaceTable.FACE_RECTANGLE_TOP, face.getFace_rectangle_top());
        values.put(FaceMetaData.FaceTable.FACE_RECTANGLE_LEFT, face.getFace_rectangle_left());
        values.put(FaceMetaData.FaceTable.FACE_RECTANGLE_HEIGHT, face.getFace_rectangle_height());
        values.put(FaceMetaData.FaceTable.LEFT_NORMAL_GLASS_EYE_OPEN, face.getLeft_normal_glass_eye_open());
        values.put(FaceMetaData.FaceTable.LEFT_NO_GLASS_EYE_CLOSE, face.getLeft_no_glass_eye_close());
        values.put(FaceMetaData.FaceTable.LEFT_OCCLUSION, face.getLeft_occlusion());
        values.put(FaceMetaData.FaceTable.LEFT_NO_GLASS_EYE_OPEN, face.getLeft_no_glass_eye_open());
        values.put(FaceMetaData.FaceTable.LEFT_NORMAL_GLASS_EYE_CLOSE, face.getLeft_normal_glass_eye_close());
        values.put(FaceMetaData.FaceTable.LEFT_DARK_GLASSES, face.getLeft_dark_glasses());
        values.put(FaceMetaData.FaceTable.RIGHT_NORMAL_GLASS_EYE_OPEN, face.getRight_normal_glass_eye_open());
        values.put(FaceMetaData.FaceTable.RIGHT_NO_GLASS_EYE_CLOSE, face.getRight_no_glass_eye_close());
        values.put(FaceMetaData.FaceTable.RIGHT_OCCLUSION, face.getRight_occlusion());
        values.put(FaceMetaData.FaceTable.RIGHT_NO_GLASS_EYE_OPEN, face.getRight_no_glass_eye_open());
        values.put(FaceMetaData.FaceTable.RIGHT_NORMAL_GLASS_EYE_CLOSE, face.getRight_normal_glass_eye_close());
        values.put(FaceMetaData.FaceTable.RIGHT_DARK_GLASSES, face.getRight_dark_glasses());
        values.put(FaceMetaData.FaceTable.HEADPOSE_YAW_ANGLE, face.getHeadpose_yaw_angle());
        values.put(FaceMetaData.FaceTable.HEADPOSE_PITCH_ANGLE, face.getHeadpose_pitch_angle());
        values.put(FaceMetaData.FaceTable.HEADPOSE_ROLL_ANGLE, face.getHeadpose_roll_angle());
        values.put(FaceMetaData.FaceTable.BLURNESS, face.getBlurness());
        values.put(FaceMetaData.FaceTable.SMILE, face.getSmile());
        values.put(FaceMetaData.FaceTable.FACEQUALITY,face.getFacequality());
        //执行插入操作
        db.insert(FaceMetaData.FaceTable.TABLE_NAME, FaceMetaData.FaceTable.IMAGE_ID,values);
        db.close();
        System.out.println("插入数据成功！");
    }
    public void delete(int id){
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        String[] args = {String.valueOf(id)};
        db.delete(FaceMetaData.FaceTable.TABLE_NAME,"image_id=?",args);
        db.close();
    }

    public void update(Face face){

    }

    public Face findById(int id){
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String[] selectionArgs = {String.valueOf(id)};
        Cursor cursor = db.query(FaceMetaData.FaceTable.TABLE_NAME,null,"_id=?",selectionArgs,null,null,null);
        Face face = null;
        while (cursor.moveToNext()){
            face =  new Face();
            face.set_id(cursor.getInt(cursor.getColumnIndex("_id")));
            face.setImage_id(cursor.getString(cursor.getColumnIndex("image_id")));
            face.setRequest_id(cursor.getString(cursor.getColumnIndex("request_id")));
            face.setGender(cursor.getString(cursor.getColumnIndex("gender")));
            face.setGlass(cursor.getString(cursor.getColumnIndex("glass")));
            face.setEthnicity(cursor.getString(cursor.getColumnIndex("ethnicity")));
            face.setTime_used(cursor.getInt(cursor.getColumnIndex("time_used")));
            face.setAge(cursor.getInt(cursor.getColumnIndex("age")));
            face.setFace_rectangle_width(cursor.getInt(cursor.getColumnIndex("face_rectangle_width")));
            face.setFace_rectangle_top(cursor.getInt(cursor.getColumnIndex("face_rectangle_top")));
            face.setFace_rectangle_left(cursor.getInt(cursor.getColumnIndex("face_rectangle_left")));
            face.setFace_rectangle_height(cursor.getInt(cursor.getColumnIndex("face_rectangle_height")));
            face.setLeft_normal_glass_eye_open(cursor.getFloat(cursor.getColumnIndex("left_normal_glass_eye_open")));
            face.setLeft_no_glass_eye_close(cursor.getFloat(cursor.getColumnIndex("left_no_glass_eye_close")));
            face.setLeft_occlusion(cursor.getFloat(cursor.getColumnIndex("left_occlusion")));
            face.setLeft_no_glass_eye_open(cursor.getFloat(cursor.getColumnIndex("left_no_glass_eye_open")));
            face.setLeft_normal_glass_eye_close(cursor.getFloat(cursor.getColumnIndex("left_normal_glass_eye_close")));
            face.setLeft_dark_glasses(cursor.getFloat(cursor.getColumnIndex("left_dark_glasses")));
            face.setRight_normal_glass_eye_open(cursor.getFloat(cursor.getColumnIndex("right_normal_glass_eye_open")));
            face.setRight_no_glass_eye_close(cursor.getFloat(cursor.getColumnIndex("right_no_glass_eye_close")));
            face.setRight_occlusion(cursor.getFloat(cursor.getColumnIndex("right_occlusion")));
            face.setRight_no_glass_eye_open(cursor.getFloat(cursor.getColumnIndex("right_no_glass_eye_open")));
            face.setRight_normal_glass_eye_close(cursor.getFloat(cursor.getColumnIndex("right_normal_glass_eye_close")));
            face.setRight_dark_glasses(cursor.getFloat(cursor.getColumnIndex("right_dark_glasses")));
            face.setHeadpose_yaw_angle(cursor.getFloat(cursor.getColumnIndex("headpose_yaw_angle")));
            face.setHeadpose_pitch_angle(cursor.getFloat(cursor.getColumnIndex("headpose_pitch_angle")));
            face.setHeadpose_roll_angle(cursor.getFloat(cursor.getColumnIndex("headpose_roll_angle")));
            face.setBlurness(cursor.getFloat(cursor.getColumnIndex("blurness")));
            face.setSmile(cursor.getFloat(cursor.getColumnIndex("smile")));
            face.setFacequality(cursor.getFloat(cursor.getColumnIndex("facequality")));
        }
        return face;
    }

    public ArrayList<Face> findAll(){
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.query(FaceMetaData.FaceTable.TABLE_NAME,null,null,null,null,null,null);
        ArrayList<Face> faceArrayList = new ArrayList<>();
        while (cursor.moveToNext()){
            Face face =  new Face();
            face.set_id(cursor.getInt(cursor.getColumnIndex("_id")));
            face.setImage_id(cursor.getString(cursor.getColumnIndex("image_id")));
            face.setRequest_id(cursor.getString(cursor.getColumnIndex("request_id")));
            face.setGender(cursor.getString(cursor.getColumnIndex("gender")));
            face.setGlass(cursor.getString(cursor.getColumnIndex("glass")));
            face.setEthnicity(cursor.getString(cursor.getColumnIndex("ethnicity")));
            face.setTime_used(cursor.getInt(cursor.getColumnIndex("time_used")));
            face.setAge(cursor.getInt(cursor.getColumnIndex("age")));
            face.setFace_rectangle_width(cursor.getInt(cursor.getColumnIndex("face_rectangle_width")));
            face.setFace_rectangle_top(cursor.getInt(cursor.getColumnIndex("face_rectangle_top")));
            face.setFace_rectangle_left(cursor.getInt(cursor.getColumnIndex("face_rectangle_left")));
            face.setFace_rectangle_height(cursor.getInt(cursor.getColumnIndex("face_rectangle_height")));
            face.setLeft_normal_glass_eye_open(cursor.getFloat(cursor.getColumnIndex("left_normal_glass_eye_open")));
            face.setLeft_no_glass_eye_close(cursor.getFloat(cursor.getColumnIndex("left_no_glass_eye_close")));
            face.setLeft_occlusion(cursor.getFloat(cursor.getColumnIndex("left_occlusion")));
            face.setLeft_no_glass_eye_open(cursor.getFloat(cursor.getColumnIndex("left_no_glass_eye_open")));
            face.setLeft_normal_glass_eye_close(cursor.getFloat(cursor.getColumnIndex("left_normal_glass_eye_close")));
            face.setLeft_dark_glasses(cursor.getFloat(cursor.getColumnIndex("left_dark_glasses")));
            face.setRight_normal_glass_eye_open(cursor.getFloat(cursor.getColumnIndex("right_normal_glass_eye_open")));
            face.setRight_no_glass_eye_close(cursor.getFloat(cursor.getColumnIndex("right_no_glass_eye_close")));
            face.setRight_occlusion(cursor.getFloat(cursor.getColumnIndex("right_occlusion")));
            face.setRight_no_glass_eye_open(cursor.getFloat(cursor.getColumnIndex("right_no_glass_eye_open")));
            face.setRight_normal_glass_eye_close(cursor.getFloat(cursor.getColumnIndex("right_normal_glass_eye_close")));
            face.setRight_dark_glasses(cursor.getFloat(cursor.getColumnIndex("right_dark_glasses")));
            face.setHeadpose_yaw_angle(cursor.getFloat(cursor.getColumnIndex("headpose_yaw_angle")));
            face.setHeadpose_pitch_angle(cursor.getFloat(cursor.getColumnIndex("headpose_pitch_angle")));
            face.setHeadpose_roll_angle(cursor.getFloat(cursor.getColumnIndex("headpose_roll_angle")));
            face.setBlurness(cursor.getFloat(cursor.getColumnIndex("blurness")));
            face.setSmile(cursor.getFloat(cursor.getColumnIndex("smile")));
            face.setFacequality(cursor.getFloat(cursor.getColumnIndex("facequality")));
            faceArrayList.add(face);
        }
        return faceArrayList;
    }



}
