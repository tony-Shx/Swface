package com.henu.swface.Datebase;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.henu.swface.VO.Face;
import com.henu.swface.VO.FaceSignIn;
import com.henu.swface.VO.SignLog;
import com.henu.swface.VO.UserHasSigned;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by 宋浩祥 on 2017/3/7.
 */

public class DatabaseAdapter {
	private static final String TAG = "DatabaseAdapter";

	private static final String Sql_findUserByFaceToken = "SELECT user_name FROM UserHasSigned WHERE face_token1 = ? OR face_token2 = ? OR face_token3 = ? OR face_token4 = ? OR face_token5 = ?;";
	private Context context;
	private DatabaseHelper databaseHelper;
	private final static int SIGN_IN_SUCCESS = 0x200;
	private static final int ADDUSERSUCCESS = 0x204;
	private final static int SIGN_IN_FAILED_NOUSER = 0x203;

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
		db.insert(FaceMetaData.FaceTable.TABLE_NAME, FaceMetaData.FaceTable.IMAGE_ID, values);
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

	public Face findById_Faces(int id) {
		SQLiteDatabase db = databaseHelper.getReadableDatabase();
		String[] selectionArgs = {String.valueOf(id)};
		Cursor cursor = db.query(FaceMetaData.FaceTable.TABLE_NAME, null, "_id=?", selectionArgs, null, null, null);
		Face face = null;
		while (cursor.moveToNext()) {
			face = new Face();
			face.setId(cursor.getInt(cursor.getColumnIndex("id")));
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


	public boolean addUser_User(final UserHasSigned userHasSigned) {
		final boolean[] saveSuccess = {false};
		if (userHasSigned.getObjectId() == null) {
			userHasSigned.save(new SaveListener<String>() {
				@Override
				public void done(String s, BmobException e) {
					if (e == null) {
						saveSuccess[0] = true;
						Log.i(TAG, "userHasSigned.save: success");
						SharedPreferences sharedPreferences = context.getSharedPreferences("detectFace", Context.MODE_PRIVATE);
						SharedPreferences.Editor editor = sharedPreferences.edit();
						editor.putString("lastUpdateObjectId", s);
						editor.commit();
					} else {
						saveSuccess[0] = false;
						Log.e(TAG, "userHasSigned.save: failed" + e);
					}
				}
			});
		}
		if (userHasSigned.getObjectId() == null) {
			int attemp = 0;
			while (!saveSuccess[0] && attemp < 150) {
				attemp++;
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		if (saveSuccess[0] || userHasSigned.getObjectId() != null) {
			SQLiteDatabase db = databaseHelper.getWritableDatabase();
			ContentValues contentValues = new ContentValues();
			contentValues.put(UserMetaData.UserTable.USER_NAME, userHasSigned.getUser_name());
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
			db.insert(UserMetaData.UserTable.TABLE_NAME, UserMetaData.UserTable.USER_NAME, contentValues);
			db.close();
			Log.i("addUser_User: ", "Success!");
			return true;
		} else {
			return false;
		}

	}

	public ArrayList<UserHasSigned> findAllUser_User() {
		SQLiteDatabase db = databaseHelper.getReadableDatabase();
		Cursor cursor = db.query(UserMetaData.UserTable.TABLE_NAME, null, null, null, null, null, null);
		ArrayList<UserHasSigned> list = new ArrayList<>();
		while (cursor.moveToNext()) {
			UserHasSigned userHasSigned = new UserHasSigned(context);
			userHasSigned.setUser_name(cursor.getString(cursor.getColumnIndex("user_name")));
			userHasSigned.setFace_token1(cursor.getString(cursor.getColumnIndex("face_token1")));
			userHasSigned.setFace_token2(cursor.getString(cursor.getColumnIndex("face_token2")));
			userHasSigned.setFace_token3(cursor.getString(cursor.getColumnIndex("face_token3")));
			userHasSigned.setFace_token4(cursor.getString(cursor.getColumnIndex("face_token4")));
			userHasSigned.setFace_token5(cursor.getString(cursor.getColumnIndex("face_token5")));
			list.add(userHasSigned);
		}
		cursor.close();
		return list;
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
			Log.i(TAG, "findUserByFaceToken: StartBmobSearch" + facetoken);
			BmobQuery<UserHasSigned> query1 = new BmobQuery<>();
			query1.addWhereEqualTo("face_token1", facetoken);
			BmobQuery<UserHasSigned> query2 = new BmobQuery<>();
			query2.addWhereEqualTo("face_token2", facetoken);
			BmobQuery<UserHasSigned> query3 = new BmobQuery<>();
			query3.addWhereEqualTo("face_token3", facetoken);
			BmobQuery<UserHasSigned> query4 = new BmobQuery<>();
			query4.addWhereEqualTo("face_token4", facetoken);
			BmobQuery<UserHasSigned> query5 = new BmobQuery<>();
			query5.addWhereEqualTo("face_token5", facetoken);
			List<BmobQuery<UserHasSigned>> list = new ArrayList<>();
			list.add(query1);
			list.add(query2);
			list.add(query3);
			list.add(query4);
			list.add(query5);
			BmobQuery<UserHasSigned> mainquery = new BmobQuery<>();
			mainquery.or(list);
			mainquery.setLimit(1);
			mainquery.findObjects(new FindListener<UserHasSigned>() {
				@Override
				public void done(List<UserHasSigned> list, BmobException e) {
					if (e == null && !list.isEmpty()) {
						UserHasSigned userHasSigned1 = list.get(0);
						if(addUser_User(userHasSigned1)){
							Message message=new Message();
							message.arg1 = SIGN_IN_SUCCESS;
							if (faceSignIn.getConfidence() > faceSignIn.getThresholds5()) {
								Bundle bundle = new Bundle();
								bundle.putFloat("confidence", faceSignIn.getConfidence());
								bundle.putFloat("thresholds3", faceSignIn.getThresholds3());
								bundle.putFloat("thresholds4", faceSignIn.getThresholds4());
								bundle.putFloat("thresholds5", faceSignIn.getThresholds5());
								message.setData(bundle);
							}
							userHasSigned.setUser_name(userHasSigned1.getUser_name());
							message.obj = userHasSigned;
							myHandler.sendMessage(message);
						}else{
							Message message = new Message();
							message.arg1 =SIGN_IN_FAILED_NOUSER;
							myHandler.sendMessage(message);
						}
					}
				}
			});
		}
		Log.i(TAG, "findUserByFaceToken: " + userHasSigned.getUser_name());
		return userHasSigned;
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
