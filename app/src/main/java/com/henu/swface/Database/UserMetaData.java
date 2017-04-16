package com.henu.swface.Database;

import android.provider.BaseColumns;

/**
 * Created by Administrator on 2017/3/29.
 */

public final class UserMetaData {
	public UserMetaData() {
	}
	public static abstract class UserTable implements BaseColumns{
		public static final String TABLE_NAME = "UserHasSigned";
		public static final String USER_NAME = "user_name";
		public static final String FACE_TOKEN1 = "face_token1";
		public static final String FACE_TOKEN2 = "face_token2";
		public static final String FACE_TOKEN3 = "face_token3";
		public static final String FACE_TOKEN4 = "face_token4";
		public static final String FACE_TOKEN5 = "face_token5";
		public static final String FACE_URL1 = "face_url1";
		public static final String FACE_URL2 = "face_url2";
		public static final String FACE_URL3 = "face_url3";
		public static final String FACE_URL4 = "face_url4";
		public static final String FACE_URL5 = "face_url5";
	}

}
