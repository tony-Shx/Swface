package com.henu.swface.Utils;

/**
 * Created by Administrator on 2017/4/16.
 */

public final class FinalUtil {
	//face++的api_key
	public final static String API_KEY = "lJsij4n8pYEj3bW-tSJqEhRgkdfHobC8";
	public final static String API_Secret = "i1H3kRBBzJ2Wo_1T-6RsbRmWgcHAREww";

	public final static int DETECT_SUCCESS = 0X100;
	public final static int DETECT_FAILED_IO_EXCEPTION = 0X101;
	public final static int DETECT_FAILED_NO_FACE = 0X102;
	public final static int DETECT_FAILED_LIMIT_EXCEEDED = 0X103;

	public final static int UPDATE_PICTURE_SUCCESS = 0X120;
	public final static int UPDATE_PICTURE_EXCEPTION = 0X121;

	public static final int SIGN_IN_SUCCESS = 0x200;
	public final static int SIGN_IN_FAILED_IOEXCEPTION = 0x201;
	public final static int SIGN_IN_FAILED_FILENOTFIND = 0x202;
	public final static int SIGN_IN_FAILED_NOUSER = 0x203;
	public final static int ADDUSERSUCCESS = 0x210;

	public final static int UPDATE_DETAIL_SUCCESS = 0X300;
	public final static int UPDATE_DETAIL_IO_EXCEPTION = 0X301;

	public final static int ADD_FACE_SUCCESS = 0X400;
	public final static int ADD_FACE_IO_EXCEPTION = 0X401;
	public final static int ADD_FACE_TO_FACESET_SUCCESS = 0X410;
	public final static int ADD_FACE_TO_FACESET_EXCEPTION = 0X411;

	public final static int SYN_DATA_SUCCESS = 0x500;
	public final static int SYN_DATA_FAILED = 0x501;

	public final static int REMOVE_FACE_SUCCESS = 0X600;
	public final static int REMOVE_FACE_IO_EXCEPTION = 0X601;
	public final static int REMOVE_FACE_BMOB_EXCEPTION = 0X602;

}
