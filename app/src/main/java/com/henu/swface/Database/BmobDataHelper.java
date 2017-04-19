package com.henu.swface.Database;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.henu.swface.Utils.FinalUtil;
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
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by Administrator on 2017/4/17.
 */

public class BmobDataHelper {
	private Context context;
	private Handler myHandler;
	private static final String TAG = BmobDataHelper.class.getSimpleName();

	public BmobDataHelper(Context context, Handler myHandler) {
		this.context = context;
		this.myHandler = myHandler;
		//第一：默认初始化
		Bmob.initialize(context, "6e64317b6509bb503f7d9ab8404bf54c");
	}

	public void addUserHasSign(final UserHasSigned userHasSigned) {
		userHasSigned.save(new SaveListener<String>() {
			@Override
			public void done(String s, BmobException e) {
				if (e == null) {
					Log.i(TAG, "userHasSigned.save: success");
					DatabaseAdapter db = new DatabaseAdapter(context);
					userHasSigned.setObjectId(s);
					db.addUser_User(userHasSigned, myHandler);
					Log.i(TAG, "addUser_User: Success!");
				} else {
					Log.e(TAG, "userHasSigned.save: failed" + e);
					Message message = new Message();
					message.arg1 = FinalUtil.DETECT_FAILED_IO_EXCEPTION;
					myHandler.sendMessage(message);
				}
			}
		});
	}

	public void addUserFace(final UserHasSigned userHasSigned, final String objectId) {
		userHasSigned.update(objectId, new UpdateListener() {
			@Override
			public void done(BmobException e) {
				if (e == null) {
					userHasSigned.setObjectId(objectId);
					DatabaseAdapter db = new DatabaseAdapter(context);
					db.addUserFace_User(userHasSigned);
					Message message = Message.obtain();
					message.arg1 = FinalUtil.ADD_FACE_SUCCESS;
					myHandler.sendMessage(message);
				} else {
					Message message = Message.obtain();
					message.arg1 = FinalUtil.ADD_FACE_IO_EXCEPTION;
					myHandler.sendMessage(message);
				}
			}
		});
	}

	public void findUserByFaceToken(final FaceSignIn faceSignIn) {
		String facetoken = faceSignIn.getFace_token();
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
					final UserHasSigned userHasSigned1 = list.get(0);
					SignLog signLog = new SignLog(context);
					signLog.setUser_name(userHasSigned1.getUser_name());
					signLog.setConfidence(faceSignIn.getConfidence());
					signLog.setTime(System.currentTimeMillis());
					signLog.setFaceToken(userHasSigned1.getFace_token1());
					signLog.save(new SaveListener<String>() {
						@Override
						public void done(String s, BmobException e) {
							if (e == null) {
								Message message = new Message();
								message.arg1 = FinalUtil.SIGN_IN_SUCCESS;
								Bundle bundle = new Bundle();
								bundle.putFloat("confidence", faceSignIn.getConfidence());
								bundle.putFloat("thresholds3", faceSignIn.getThresholds3());
								bundle.putFloat("thresholds4", faceSignIn.getThresholds4());
								bundle.putFloat("thresholds5", faceSignIn.getThresholds5());
								message.setData(bundle);
								message.obj = userHasSigned1;
								myHandler.sendMessage(message);
							} else {
								Message message = new Message();
								message.arg1 = FinalUtil.SIGN_IN_FAILED_IOEXCEPTION;
								myHandler.sendMessage(message);
							}
						}
					});
				} else {
					Message message = new Message();
					message.arg1 = FinalUtil.SIGN_IN_FAILED_NOUSER;
					myHandler.sendMessage(message);
				}
			}
		});
	}

	public void updateUsername(final UserHasSigned userHasSigned, final String newUsername) {
		Log.i(TAG, "updateUsername: " + userHasSigned.getObjectId() + " " + newUsername);
		UserHasSigned newUserHasSigned = new UserHasSigned(context);
		newUserHasSigned.setUser_name(newUsername);
		newUserHasSigned.update(userHasSigned.getObjectId(), new UpdateListener() {
			@Override
			public void done(BmobException e) {
				if (e == null) {
					Log.i(TAG, "newUserHasSigned.update: success");
					DatabaseAdapter db = new DatabaseAdapter(context);
					userHasSigned.setUser_name(newUsername);
					db.updateUserFaceName_User(userHasSigned);
					Message message = new Message();
					message.arg1 = FinalUtil.UPDATE_DETAIL_SUCCESS;
					message.obj = newUsername;
					myHandler.sendMessage(message);
				} else {
					Log.e(TAG, "newUserHasSigned.update: ", e);
					Message message = new Message();
					message.arg1 = FinalUtil.UPDATE_DETAIL_IO_EXCEPTION;
					myHandler.sendMessage(message);
				}
			}
		});
	}
}
