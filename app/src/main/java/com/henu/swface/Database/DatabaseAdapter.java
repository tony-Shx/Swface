package com.henu.swface.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.henu.swface.Utils.FaceUtil;
import com.henu.swface.Utils.FinalUtil;
import com.henu.swface.Utils.JSONUtil;
import com.henu.swface.VO.Face;
import com.henu.swface.VO.FaceSignIn;
import com.henu.swface.VO.SignLog;
import com.henu.swface.VO.UserHasSigned;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import okhttp3.Response;

/**
 * Created by 宋浩祥 on 2017/3/7.
 */

public class DatabaseAdapter {
	private static final String TAG = "DatabaseAdapter";
	private static final String Sql_findUserByFaceToken = "SELECT user_name FROM UserHasSigned WHERE face_token1 = ? OR face_token2 = ? OR face_token3 = ? OR face_token4 = ? OR face_token5 = ?;";
	private Context context;
	private DatabaseHelper databaseHelper;


	public DatabaseAdapter(Context context) {
		this.context = context;
		databaseHelper = new DatabaseHelper(context);
		//第一：默认初始化
		Bmob.initialize(context, "6e64317b6509bb503f7d9ab8404bf54c");
	}

	public void addFace_Faces(final Face face) {
		SQLiteDatabase db = databaseHelper.getWritableDatabase();
		//将数据按照键值对存入ContentValues
		ContentValues values = new ContentValues();
		values.put(FaceMetaData.FaceTable.REQUEST_ID, face.getRequest_id());
		values.put(FaceMetaData.FaceTable.GENDER, face.getGender());
		values.put(FaceMetaData.FaceTable.GLASS, face.getGlass());
		values.put(FaceMetaData.FaceTable.ETHNICITY, face.getEthnicity());
		values.put(FaceMetaData.FaceTable.TIME_USED, face.getTime_used());
		values.put(FaceMetaData.FaceTable.AGE, face.getAge());
		values.put(FaceMetaData.FaceTable.FACE_RECTANGLE_WIDTH, face.getFace_rectangle_width());
		values.put(FaceMetaData.FaceTable.FACE_RECTANGLE_TOP, face.getFace_rectangle_top());
		values.put(FaceMetaData.FaceTable.FACE_RECTANGLE_LEFT, face.getFace_rectangle_left());
		values.put(FaceMetaData.FaceTable.FACE_RECTANGLE_HEIGHT, face.getFace_rectangle_high());
		values.put(FaceMetaData.FaceTable.LEFT_NORMAL_GLASS_EYE_OPEN, face.getlNormalGlassEyeOpen());
		values.put(FaceMetaData.FaceTable.LEFT_NO_GLASS_EYE_CLOSE, face.getlNoGlassEyeClose());
		values.put(FaceMetaData.FaceTable.LEFT_OCCLUSION, face.getLeft_occlusion());
		values.put(FaceMetaData.FaceTable.LEFT_NO_GLASS_EYE_OPEN, face.getLeftNoGlassEyeOpen());
		values.put(FaceMetaData.FaceTable.LEFT_NORMAL_GLASS_EYE_CLOSE, face.getlNormalGlassEyeClose());
		values.put(FaceMetaData.FaceTable.LEFT_DARK_GLASSES, face.getLeft_dark_glasses());
		values.put(FaceMetaData.FaceTable.RIGHT_NORMAL_GLASS_EYE_OPEN, face.getrNormalGlassEyeOpen());
		values.put(FaceMetaData.FaceTable.RIGHT_NO_GLASS_EYE_CLOSE, face.getrNoGlassEyeClose());
		values.put(FaceMetaData.FaceTable.RIGHT_OCCLUSION, face.getRight_occlusion());
		values.put(FaceMetaData.FaceTable.RIGHT_NO_GLASS_EYE_OPEN, face.getrNoGlassEyeOpen());
		values.put(FaceMetaData.FaceTable.RIGHT_NORMAL_GLASS_EYE_CLOSE, face.getrNormalGlassEyeClose());
		values.put(FaceMetaData.FaceTable.RIGHT_DARK_GLASSES, face.getRight_dark_glasses());
		values.put(FaceMetaData.FaceTable.HEADPOSE_YAW_ANGLE, face.getHeadpose_yaw_angle());
		values.put(FaceMetaData.FaceTable.HEADPOSE_PITCH_ANGLE, face.getHeadpose_pitch_angle());
		values.put(FaceMetaData.FaceTable.HEADPOSE_ROLL_ANGLE, face.getHeadpose_roll_angle());
		values.put(FaceMetaData.FaceTable.BLURNESS, face.getBlurness());
		values.put(FaceMetaData.FaceTable.SMILE, face.getSmile());
		values.put(FaceMetaData.FaceTable.FACEQUALITY, face.getFacequality());
		values.put(FaceMetaData.FaceTable.FACE_TOKEN, face.getFace_token());
		values.put(FaceMetaData.FaceTable.IMAGE_PATH, face.getImage_path());
		//执行插入操作
		db.insert(FaceMetaData.FaceTable.TABLE_NAME, FaceMetaData.FaceTable.HEADPOSE_ROLL_ANGLE, values);
		db.close();
		System.out.println("插入数据成功！");

	}

	public void delete_Faces(int id) {
		SQLiteDatabase db = databaseHelper.getWritableDatabase();
		String[] args = {String.valueOf(id)};
		db.delete(FaceMetaData.FaceTable.TABLE_NAME, "image_id=?", args);
		db.close();
	}

	public void update_Faces(Face face) {

	}

	public Face findFaceByFaceToken_Faces(final String faceToken, final Handler myHandler){
		SQLiteDatabase db = databaseHelper.getReadableDatabase();
		String[] selectionArgs = {String.valueOf(faceToken)};
		Cursor cursor = db.query(FaceMetaData.FaceTable.TABLE_NAME, null, "face_token=?", selectionArgs, null, null, null);
		Face face = null;
		while (cursor.moveToNext()) {
			face = new Face();
			face.setId(cursor.getInt(cursor.getColumnIndex("id")));
			face.setRequest_id(cursor.getString(cursor.getColumnIndex("request_id")));
			face.setGender(cursor.getString(cursor.getColumnIndex("gender")));
			face.setGlass(cursor.getString(cursor.getColumnIndex("glass")));
			face.setEthnicity(cursor.getString(cursor.getColumnIndex("ethnicity")));
			face.setTime_used(cursor.getInt(cursor.getColumnIndex("time_used")));
			face.setAge(cursor.getInt(cursor.getColumnIndex("age")));
			face.setFace_rectangle_width(cursor.getInt(cursor.getColumnIndex("face_rectangle_width")));
			face.setFace_rectangle_top(cursor.getInt(cursor.getColumnIndex("face_rectangle_top")));
			face.setFace_rectangle_left(cursor.getInt(cursor.getColumnIndex("face_rectangle_left")));
			face.setFace_rectangle_high(cursor.getInt(cursor.getColumnIndex("face_rectangle_height")));
			face.setlNormalGlassEyeOpen(cursor.getFloat(cursor.getColumnIndex("left_normal_glass_eye_open")));
			face.setlNoGlassEyeClose(cursor.getFloat(cursor.getColumnIndex("left_no_glass_eye_close")));
			face.setLeft_occlusion(cursor.getFloat(cursor.getColumnIndex("left_occlusion")));
			face.setLeftNoGlassEyeOpen(cursor.getFloat(cursor.getColumnIndex("left_no_glass_eye_open")));
			face.setlNormalGlassEyeClose(cursor.getFloat(cursor.getColumnIndex("left_normal_glass_eye_close")));
			face.setLeft_dark_glasses(cursor.getFloat(cursor.getColumnIndex("left_dark_glasses")));
			face.setrNormalGlassEyeOpen(cursor.getFloat(cursor.getColumnIndex("right_normal_glass_eye_open")));
			face.setrNoGlassEyeClose(cursor.getFloat(cursor.getColumnIndex("right_no_glass_eye_close")));
			face.setRight_occlusion(cursor.getFloat(cursor.getColumnIndex("right_occlusion")));
			face.setrNoGlassEyeOpen(cursor.getFloat(cursor.getColumnIndex("right_no_glass_eye_open")));
			face.setrNormalGlassEyeClose(cursor.getFloat(cursor.getColumnIndex("right_normal_glass_eye_close")));
			face.setRight_dark_glasses(cursor.getFloat(cursor.getColumnIndex("right_dark_glasses")));
			face.setHeadpose_yaw_angle(cursor.getFloat(cursor.getColumnIndex("headpose_yaw_angle")));
			face.setHeadpose_pitch_angle(cursor.getFloat(cursor.getColumnIndex("headpose_pitch_angle")));
			face.setHeadpose_roll_angle(cursor.getFloat(cursor.getColumnIndex("headpose_roll_angle")));
			face.setBlurness(cursor.getFloat(cursor.getColumnIndex("blurness")));
			face.setSmile(cursor.getFloat(cursor.getColumnIndex("smile")));
			face.setFacequality(cursor.getFloat(cursor.getColumnIndex("facequality")));
			face.setFace_token(cursor.getString(cursor.getColumnIndex("face_token")));
			face.setImage_path(cursor.getString(cursor.getColumnIndex("image_path")));
		}
		cursor.close();
		if(face==null){
			new Thread(new Runnable() {
				@Override
				public void run() {
					Response response = FaceUtil.detectFace(faceToken,FinalUtil.API_KEY,FinalUtil.API_Secret);
					JSONUtil jsonUtil = new JSONUtil();
					if(response==null||response.code()!=200){
						Log.i(TAG, "FaceUtil.detectFace: 1"+response.code());
						Message message = Message.obtain();
						message.arg1 = FinalUtil.DETECT_FAILED_IO_EXCEPTION;
						myHandler.sendMessage(message);
					}else{
						Log.i(TAG, "FaceUtil.detectFace: 2");
						try {
							Face face = jsonUtil.parseDetectFaceJSON(response.body().string());
							if(face!=null){
								addFace_Faces(face);
								Message message = Message.obtain();
								message.obj = face;
								message.arg1 = FinalUtil.DETECT_SUCCESS;
								myHandler.sendMessage(message);
							}else{
								Message message = Message.obtain();
								message.arg1 = FinalUtil.DETECT_FAILED_IO_EXCEPTION;
								myHandler.sendMessage(message);
							}
						} catch (IOException e) {
							e.printStackTrace();
						}

					}
				}
			}).start();
		}
		return face;
	}

	public Face findById_Faces(int id) {
		SQLiteDatabase db = databaseHelper.getReadableDatabase();
		String[] selectionArgs = {String.valueOf(id)};
		Cursor cursor = db.query(FaceMetaData.FaceTable.TABLE_NAME, null, "_id=?", selectionArgs, null, null, null);
		Face face = null;
		while (cursor.moveToNext()) {
			face = new Face();
			face.setId(cursor.getInt(cursor.getColumnIndex("id")));
			face.setRequest_id(cursor.getString(cursor.getColumnIndex("request_id")));
			face.setGender(cursor.getString(cursor.getColumnIndex("gender")));
			face.setGlass(cursor.getString(cursor.getColumnIndex("glass")));
			face.setEthnicity(cursor.getString(cursor.getColumnIndex("ethnicity")));
			face.setTime_used(cursor.getInt(cursor.getColumnIndex("time_used")));
			face.setAge(cursor.getInt(cursor.getColumnIndex("age")));
			face.setFace_rectangle_width(cursor.getInt(cursor.getColumnIndex("face_rectangle_width")));
			face.setFace_rectangle_top(cursor.getInt(cursor.getColumnIndex("face_rectangle_top")));
			face.setFace_rectangle_left(cursor.getInt(cursor.getColumnIndex("face_rectangle_left")));
			face.setFace_rectangle_high(cursor.getInt(cursor.getColumnIndex("face_rectangle_height")));
			face.setlNormalGlassEyeOpen(cursor.getFloat(cursor.getColumnIndex("left_normal_glass_eye_open")));
			face.setlNoGlassEyeClose(cursor.getFloat(cursor.getColumnIndex("left_no_glass_eye_close")));
			face.setLeft_occlusion(cursor.getFloat(cursor.getColumnIndex("left_occlusion")));
			face.setLeftNoGlassEyeOpen(cursor.getFloat(cursor.getColumnIndex("left_no_glass_eye_open")));
			face.setlNormalGlassEyeClose(cursor.getFloat(cursor.getColumnIndex("left_normal_glass_eye_close")));
			face.setLeft_dark_glasses(cursor.getFloat(cursor.getColumnIndex("left_dark_glasses")));
			face.setrNormalGlassEyeOpen(cursor.getFloat(cursor.getColumnIndex("right_normal_glass_eye_open")));
			face.setrNoGlassEyeClose(cursor.getFloat(cursor.getColumnIndex("right_no_glass_eye_close")));
			face.setRight_occlusion(cursor.getFloat(cursor.getColumnIndex("right_occlusion")));
			face.setrNoGlassEyeOpen(cursor.getFloat(cursor.getColumnIndex("right_no_glass_eye_open")));
			face.setrNormalGlassEyeClose(cursor.getFloat(cursor.getColumnIndex("right_normal_glass_eye_close")));
			face.setRight_dark_glasses(cursor.getFloat(cursor.getColumnIndex("right_dark_glasses")));
			face.setHeadpose_yaw_angle(cursor.getFloat(cursor.getColumnIndex("headpose_yaw_angle")));
			face.setHeadpose_pitch_angle(cursor.getFloat(cursor.getColumnIndex("headpose_pitch_angle")));
			face.setHeadpose_roll_angle(cursor.getFloat(cursor.getColumnIndex("headpose_roll_angle")));
			face.setBlurness(cursor.getFloat(cursor.getColumnIndex("blurness")));
			face.setSmile(cursor.getFloat(cursor.getColumnIndex("smile")));
			face.setFacequality(cursor.getFloat(cursor.getColumnIndex("facequality")));
			face.setFace_token(cursor.getString(cursor.getColumnIndex("face_token")));
			face.setImage_path(cursor.getString(cursor.getColumnIndex("image_path")));
		}
		cursor.close();
		return face;
	}

	public ArrayList<Face> findAll_Faces() {
		SQLiteDatabase db = databaseHelper.getReadableDatabase();
		Cursor cursor = db.query(FaceMetaData.FaceTable.TABLE_NAME, null, null, null, null, null, null);
		ArrayList<Face> faceArrayList = new ArrayList<>();
		while (cursor.moveToNext()) {
			Face face = new Face();
			face.setId(cursor.getInt(cursor.getColumnIndex("id")));
			face.setRequest_id(cursor.getString(cursor.getColumnIndex("request_id")));
			face.setGender(cursor.getString(cursor.getColumnIndex("gender")));
			face.setGlass(cursor.getString(cursor.getColumnIndex("glass")));
			face.setEthnicity(cursor.getString(cursor.getColumnIndex("ethnicity")));
			face.setTime_used(cursor.getInt(cursor.getColumnIndex("time_used")));
			face.setAge(cursor.getInt(cursor.getColumnIndex("age")));
			face.setFace_rectangle_width(cursor.getInt(cursor.getColumnIndex("face_rectangle_width")));
			face.setFace_rectangle_top(cursor.getInt(cursor.getColumnIndex("face_rectangle_top")));
			face.setFace_rectangle_left(cursor.getInt(cursor.getColumnIndex("face_rectangle_left")));
			face.setFace_rectangle_high(cursor.getInt(cursor.getColumnIndex("face_rectangle_height")));
			face.setlNormalGlassEyeOpen(cursor.getFloat(cursor.getColumnIndex("left_normal_glass_eye_open")));
			face.setlNoGlassEyeClose(cursor.getFloat(cursor.getColumnIndex("left_no_glass_eye_close")));
			face.setLeft_occlusion(cursor.getFloat(cursor.getColumnIndex("left_occlusion")));
			face.setLeftNoGlassEyeOpen(cursor.getFloat(cursor.getColumnIndex("left_no_glass_eye_open")));
			face.setlNormalGlassEyeClose(cursor.getFloat(cursor.getColumnIndex("left_normal_glass_eye_close")));
			face.setLeft_dark_glasses(cursor.getFloat(cursor.getColumnIndex("left_dark_glasses")));
			face.setrNormalGlassEyeOpen(cursor.getFloat(cursor.getColumnIndex("right_normal_glass_eye_open")));
			face.setrNoGlassEyeClose(cursor.getFloat(cursor.getColumnIndex("right_no_glass_eye_close")));
			face.setRight_occlusion(cursor.getFloat(cursor.getColumnIndex("right_occlusion")));
			face.setrNoGlassEyeOpen(cursor.getFloat(cursor.getColumnIndex("right_no_glass_eye_open")));
			face.setrNormalGlassEyeClose(cursor.getFloat(cursor.getColumnIndex("right_normal_glass_eye_close")));
			face.setRight_dark_glasses(cursor.getFloat(cursor.getColumnIndex("right_dark_glasses")));
			face.setHeadpose_yaw_angle(cursor.getFloat(cursor.getColumnIndex("headpose_yaw_angle")));
			face.setHeadpose_pitch_angle(cursor.getFloat(cursor.getColumnIndex("headpose_pitch_angle")));
			face.setHeadpose_roll_angle(cursor.getFloat(cursor.getColumnIndex("headpose_roll_angle")));
			face.setBlurness(cursor.getFloat(cursor.getColumnIndex("blurness")));
			face.setSmile(cursor.getFloat(cursor.getColumnIndex("smile")));
			face.setFacequality(cursor.getFloat(cursor.getColumnIndex("facequality")));
			face.setFace_token(cursor.getString(cursor.getColumnIndex("face_token")));
			face.setImage_path(cursor.getString(cursor.getColumnIndex("image_path")));
			faceArrayList.add(face);
		}
		cursor.close();
		return faceArrayList;
	}


	public void addUser_User(final UserHasSigned userHasSigned, final Handler myHandler) {
		SQLiteDatabase db = databaseHelper.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put(UserMetaData.UserTable.OBJECT_ID, userHasSigned.getObjectId());
		contentValues.put(UserMetaData.UserTable.USER_NAME, userHasSigned.getUser_name());
		if (userHasSigned.getFace_url1() != null && !userHasSigned.getFace_url1().isEmpty()) {
			contentValues.put(UserMetaData.UserTable.FACE_URL1, userHasSigned.getFace_url1());
		}
		if (userHasSigned.getFace_url2() != null && !userHasSigned.getFace_url2().isEmpty()) {
			contentValues.put(UserMetaData.UserTable.FACE_URL2, userHasSigned.getFace_url2());
		}
		if (userHasSigned.getFace_url3() != null && !userHasSigned.getFace_url3().isEmpty()) {
			contentValues.put(UserMetaData.UserTable.FACE_URL3, userHasSigned.getFace_url3());
		}
		if (userHasSigned.getFace_url4() != null && !userHasSigned.getFace_url4().isEmpty()) {
			contentValues.put(UserMetaData.UserTable.FACE_URL4, userHasSigned.getFace_url4());
		}
		if (userHasSigned.getFace_url5() != null && !userHasSigned.getFace_url5().isEmpty()) {
			contentValues.put(UserMetaData.UserTable.FACE_URL5, userHasSigned.getFace_url5());
		}
		if (userHasSigned.getFace_token1() != null && !userHasSigned.getFace_token1().isEmpty()) {
			contentValues.put(UserMetaData.UserTable.FACE_TOKEN1, userHasSigned.getFace_token1());
		}
		if (userHasSigned.getFace_token2() != null && !userHasSigned.getFace_token2().isEmpty()) {
			contentValues.put(UserMetaData.UserTable.FACE_TOKEN2, userHasSigned.getFace_token2());
		}
		if (userHasSigned.getFace_token3() != null && !userHasSigned.getFace_token3().isEmpty()) {
			contentValues.put(UserMetaData.UserTable.FACE_TOKEN3, userHasSigned.getFace_token3());
		}
		if (userHasSigned.getFace_token4() != null && !userHasSigned.getFace_token4().isEmpty()) {
			contentValues.put(UserMetaData.UserTable.FACE_TOKEN4, userHasSigned.getFace_token4());
		}
		if (userHasSigned.getFace_token5() != null && !userHasSigned.getFace_token5().isEmpty()) {
			contentValues.put(UserMetaData.UserTable.FACE_TOKEN5, userHasSigned.getFace_token5());
		}
		if (userHasSigned.getCreatedAt() != null && !userHasSigned.getCreatedAt().isEmpty()) {
			contentValues.put(UserMetaData.UserTable.CREATED_AT, userHasSigned.getCreatedAt());
		}
		if (userHasSigned.getUpdatedAt() != null && !userHasSigned.getUpdatedAt().isEmpty()) {
			contentValues.put(UserMetaData.UserTable.UPDATED_AT, userHasSigned.getUpdatedAt());
		}
		db.insert(UserMetaData.UserTable.TABLE_NAME, UserMetaData.UserTable.USER_NAME, contentValues);
		db.close();
		Log.i(TAG, "addUser_User: success!");

		if (myHandler != null) {
			Message message = new Message();
			message.arg1 = FinalUtil.UPDATE_PICTURE_SUCCESS;
			Bundle bundle = new Bundle();
			bundle.putString("objectId", userHasSigned.getObjectId());
			bundle.putString("faceToken", userHasSigned.getFace_token1());
			message.setData(bundle);
			myHandler.sendMessage(message);
		}
	}

	public ArrayList<UserHasSigned> findAllUser_User() {
		SQLiteDatabase db = databaseHelper.getReadableDatabase();
		Cursor cursor = db.query(UserMetaData.UserTable.TABLE_NAME, null, null, null, null, null, null);
		ArrayList<UserHasSigned> list = new ArrayList<>();
		while (cursor.moveToNext()) {
			UserHasSigned userHasSigned = new UserHasSigned(context);
			userHasSigned.setObjectId(cursor.getString(cursor.getColumnIndex("object_id")));
			userHasSigned.setUser_name(cursor.getString(cursor.getColumnIndex("user_name")));
			userHasSigned.setFace_token1(cursor.getString(cursor.getColumnIndex("face_token1")));
			userHasSigned.setFace_token2(cursor.getString(cursor.getColumnIndex("face_token2")));
			userHasSigned.setFace_token3(cursor.getString(cursor.getColumnIndex("face_token3")));
			userHasSigned.setFace_token4(cursor.getString(cursor.getColumnIndex("face_token4")));
			userHasSigned.setFace_token5(cursor.getString(cursor.getColumnIndex("face_token5")));
			userHasSigned.setFace_url1(cursor.getString(cursor.getColumnIndex("face_url1")));
			userHasSigned.setFace_url2(cursor.getString(cursor.getColumnIndex("face_url2")));
			userHasSigned.setFace_url3(cursor.getString(cursor.getColumnIndex("face_url3")));
			userHasSigned.setFace_url4(cursor.getString(cursor.getColumnIndex("face_url4")));
			userHasSigned.setFace_url5(cursor.getString(cursor.getColumnIndex("face_url5")));
			userHasSigned.setUpdated_at(cursor.getString(cursor.getColumnIndex("updated_at")));
			userHasSigned.setCreated_at(cursor.getString(cursor.getColumnIndex("created_at")));
			list.add(userHasSigned);
		}
		cursor.close();
		return list;
	}

	public UserHasSigned findUserByObiectId(String objectId) {
		SQLiteDatabase db = databaseHelper.getReadableDatabase();
		String selection = UserMetaData.UserTable.OBJECT_ID + "=?";
		String[] args = {objectId};
		Cursor cursor = db.query(UserMetaData.UserTable.TABLE_NAME, null, selection, args, null, null, null);
		UserHasSigned userHasSigned = new UserHasSigned(context);
		while (cursor.moveToNext()) {
			userHasSigned.setUser_name(cursor.getString(cursor.getColumnIndex("user_name")));
			userHasSigned.setFace_token1(cursor.getString(cursor.getColumnIndex("face_token1")));
			userHasSigned.setFace_token2(cursor.getString(cursor.getColumnIndex("face_token2")));
			userHasSigned.setFace_token3(cursor.getString(cursor.getColumnIndex("face_token3")));
			userHasSigned.setFace_token4(cursor.getString(cursor.getColumnIndex("face_token4")));
			userHasSigned.setFace_token5(cursor.getString(cursor.getColumnIndex("face_token5")));
			userHasSigned.setFace_url1(cursor.getString(cursor.getColumnIndex("face_url1")));
			userHasSigned.setFace_url2(cursor.getString(cursor.getColumnIndex("face_url2")));
			userHasSigned.setFace_url3(cursor.getString(cursor.getColumnIndex("face_url3")));
			userHasSigned.setFace_url4(cursor.getString(cursor.getColumnIndex("face_url4")));
			userHasSigned.setFace_url5(cursor.getString(cursor.getColumnIndex("face_url5")));
			userHasSigned.setObjectId(cursor.getString(cursor.getColumnIndex("object_id")));
			userHasSigned.setUpdated_at(cursor.getString(cursor.getColumnIndex("updated_at")));
			userHasSigned.setCreated_at(cursor.getString(cursor.getColumnIndex("created_at")));
		}
		return userHasSigned;
	}

	public UserHasSigned findUserByFaceToken(final FaceSignIn faceSignIn, final Handler myHandler) {
		String facetoken = faceSignIn.getFace_token();
		SQLiteDatabase db = databaseHelper.getReadableDatabase();
		String[] arge = {facetoken, facetoken, facetoken, facetoken, facetoken};
		Cursor cursor = db.rawQuery(Sql_findUserByFaceToken, arge);
		final UserHasSigned userHasSigned = new UserHasSigned(context);
		while (cursor.moveToNext()) {
			userHasSigned.setUser_name(cursor.getString(cursor.getColumnIndex("user_name")));
		}
		cursor.close();
		db.close();
		if (userHasSigned.getUser_name() == null) {
			BmobDataHelper bmobDataHelper = new BmobDataHelper(context, myHandler);
			bmobDataHelper.findUserByFaceToken(faceSignIn);
		}
		Log.i(TAG, "findUserByFaceToken: " + userHasSigned.getUser_name());
		return userHasSigned;
	}

	public void addUserFace_User(UserHasSigned userHasSigned) {
		SQLiteDatabase db = databaseHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		if (userHasSigned.getFace_url1() != null) {
			values.put(UserMetaData.UserTable.FACE_URL1, userHasSigned.getFace_url1());
			values.put(UserMetaData.UserTable.FACE_TOKEN1, userHasSigned.getFace_token1());
		}
		if (userHasSigned.getFace_url2() != null) {
			values.put(UserMetaData.UserTable.FACE_URL2, userHasSigned.getFace_url2());
			values.put(UserMetaData.UserTable.FACE_TOKEN2, userHasSigned.getFace_token2());
		}
		if (userHasSigned.getFace_url3() != null) {
			values.put(UserMetaData.UserTable.FACE_URL3, userHasSigned.getFace_url3());
			values.put(UserMetaData.UserTable.FACE_TOKEN3, userHasSigned.getFace_token3());
		}
		if (userHasSigned.getFace_url4() != null) {
			values.put(UserMetaData.UserTable.FACE_URL4, userHasSigned.getFace_url4());
			values.put(UserMetaData.UserTable.FACE_TOKEN4, userHasSigned.getFace_token4());
		}
		if (userHasSigned.getFace_url5()!=null){
			values.put(UserMetaData.UserTable.FACE_URL5, userHasSigned.getFace_url5());
			values.put(UserMetaData.UserTable.FACE_TOKEN5, userHasSigned.getFace_token5());
		}
		if(userHasSigned.getUpdatedAt()!=null){
			values.put(UserMetaData.UserTable.UPDATED_AT,userHasSigned.getUpdatedAt());
		}
		String whereClause = UserMetaData.UserTable.OBJECT_ID + "=?";
		String[] args = {userHasSigned.getObjectId()};
		db.update(UserMetaData.UserTable.TABLE_NAME, values, whereClause, args);
		Log.i(TAG, "addUserFace_User: success!");
	}


	public void updateUserFaceName_User(UserHasSigned userHasSigned) {
		SQLiteDatabase db = databaseHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(UserMetaData.UserTable.USER_NAME, userHasSigned.getUser_name());
		if(userHasSigned.getUpdatedAt()!=null){
			values.put(UserMetaData.UserTable.UPDATED_AT,userHasSigned.getUpdatedAt());
		}
		String whereClause = UserMetaData.UserTable.OBJECT_ID + "=?";
		String[] args = {userHasSigned.getObjectId()};
		db.update(UserMetaData.UserTable.TABLE_NAME, values, whereClause, args);
		Log.i(TAG, "updateUserFaceName_User: success!");
	}

	public void deleteUserFace_User(String objectId,String faceToken,String faceUrl,Handler myHandler){
		SQLiteDatabase db = databaseHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(faceToken, "");
		values.put(faceUrl,"");
		String whereClause = UserMetaData.UserTable.OBJECT_ID + "=?";
		String[] args = {objectId};
		db.update(UserMetaData.UserTable.TABLE_NAME,values,whereClause,args);
		Log.i(TAG, "daleteUserFace_User: success!");
		Message message = Message.obtain();
		message.arg1 = FinalUtil.REMOVE_FACE_SUCCESS;
		myHandler.sendMessage(message);
	}

	public void deleteUser_User(String objectId,Handler myHandler){
		SQLiteDatabase db = databaseHelper.getWritableDatabase();
		String whereClause = UserMetaData.UserTable.OBJECT_ID + "=?";
		String[] args = {objectId};
		db.delete(UserMetaData.UserTable.TABLE_NAME,whereClause,args);
		Log.i(TAG, "deleteUser_User: success!");
		Message message = Message.obtain();
		message.arg1 = FinalUtil.REMOVE_USER_SUCCESS;
		myHandler.sendMessage(message);
	}


	public void addLog_SignLog(SignLog log) {
		SQLiteDatabase db = databaseHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("user_name", log.getUser_name());
		values.put("confidence", log.getConfidence());
		values.put("time", log.getTime());
		db.insert(SignLogData.SignLogTable.TABLE_NAME, SignLogData.SignLogTable.TIME, values);
		db.close();
		Log.i(TAG, "addLog_SignLog: success");
	}

}
