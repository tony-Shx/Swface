package com.henu.swface;

import android.app.Application;
import android.util.Log;

import cn.bmob.v3.Bmob;

/**
 * Created by Administrator on 2017/4/20.
 */

public class MyApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		//第一：默认初始化
		Bmob.initialize(this, "6e64317b6509bb503f7d9ab8404bf54c");
		Log.i("onCreate: ","bmob!!!!");
	}
}
