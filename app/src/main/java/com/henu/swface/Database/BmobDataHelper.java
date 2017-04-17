package com.henu.swface.Database;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.henu.swface.Utils.FinalUtil;
import com.henu.swface.VO.UserHasSigned;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.exception.BmobException;
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

	public void updateUsername(UserHasSigned userHasSigned, final String newUsername){
		Log.i(TAG, "updateUsername: "+userHasSigned.getObjectId()+" "+newUsername);
		UserHasSigned newUserHasSigned = new UserHasSigned(context);
		newUserHasSigned.setUser_name(newUsername);
		newUserHasSigned.update(userHasSigned.getObjectId(),new UpdateListener() {
			@Override
			public void done(BmobException e) {
				if(e==null){
					Message message = new Message();
					message.arg1 = FinalUtil.UPDATE_DETAIL_SUCCESS;
					message.obj = newUsername;
					myHandler.sendMessage(message);
				}else{
					Message message = new Message();
					message.arg1 = FinalUtil.UPDATE_DETAIL_IO_EXCEPTION;
					myHandler.sendMessage(message);
				}
			}
		});
	}
}
