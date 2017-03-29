package com.henu.swface.Datebase;

import android.provider.BaseColumns;

/**
 * Created by Administrator on 2017/3/29.
 */

public final class UserMetaData {
	public UserMetaData() {
	}
	public static abstract class UserTable implements BaseColumns{
		public static final String TABLE_NAME = "User";
		public static final String USER_NAME = "user_name";
		public static final String FACE_TOKEN1 = "face_token1";
		public static final String FACE_TOKEN2 = "face_token2";
		public static final String FACE_TOKEN3 = "face_token3";
		public static final String FACE_TOKEN4 = "face_token4";
		public static final String FACE_TOKEN5 = "face_token5";
	}

}
