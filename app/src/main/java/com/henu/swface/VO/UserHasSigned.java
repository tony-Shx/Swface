package com.henu.swface.VO;

import android.content.Context;
import android.content.SharedPreferences;

import com.henu.swface.activity.VideoRecogniseActivity;

import cn.bmob.v3.BmobObject;

/**
 * Created by Administrator on 2017/3/29.
 */

public class UserHasSigned extends BmobObject{
	private String user_name, face_token1, face_token2, face_token3, face_token4, face_token5,telephone;

	public UserHasSigned(Context context) {
		SharedPreferences preferences = context.getSharedPreferences("login",Context.MODE_PRIVATE);
		telephone = preferences.getString("username","default");
	}

	public String getTelephone() {
		return telephone;
	}

	public UserHasSigned(String user_name, String face_token1, String face_token2, String face_token3, String face_token4, String face_token5) {
		this.user_name = user_name;
		this.face_token1 = face_token1;
		this.face_token2 = face_token2;
		this.face_token3 = face_token3;
		this.face_token4 = face_token4;
		this.face_token5 = face_token5;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public String getFace_token1() {
		return face_token1;
	}

	public void setFace_token1(String face_token1) {
		this.face_token1 = face_token1;
	}

	public String getFace_token2() {
		return face_token2;
	}

	public void setFace_token2(String face_token2) {
		this.face_token2 = face_token2;
	}

	public String getFace_token3() {
		return face_token3;
	}

	public void setFace_token3(String face_token3) {
		this.face_token3 = face_token3;
	}

	public String getFace_token4() {
		return face_token4;
	}

	public void setFace_token4(String face_token4) {
		this.face_token4 = face_token4;
	}

	public String getFace_token5() {
		return face_token5;
	}

	public void setFace_token5(String face_token5) {
		this.face_token5 = face_token5;
	}

	@Override
	public String toString() {
		return "UserHasSigned{" +
				"user_name='" + user_name + '\'' +
				", face_token1='" + face_token1 + '\'' +
				", face_token2='" + face_token2 + '\'' +
				", face_token3='" + face_token3 + '\'' +
				", face_token4='" + face_token4 + '\'' +
				", face_token5='" + face_token5 + '\'' +
				'}';
	}
}
