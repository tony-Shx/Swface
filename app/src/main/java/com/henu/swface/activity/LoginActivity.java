package com.henu.swface.activity;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;

import com.henu.swface.R;
import com.henu.swface.VO.UserLogin;

import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {

	private ImageButton imageButton_login;
	private EditText editText3_username, editText3_password;
	private CheckBox checkBox_remember_password;
	private static final String TAG = "LoginActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		//第一：默认初始化
		Bmob.initialize(this, "6e64317b6509bb503f7d9ab8404bf54c");
		findView();
		initOnClick();
		initDate();
	}

	private void initDate() {
		SharedPreferences sharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE);
		Boolean isLogin = sharedPreferences.getBoolean("isLogin", false);
		if (isLogin) {
			startActivity(new Intent(this, MainActivity.class));
			finish();
		} else {
			String username = sharedPreferences.getString("username", "");
			editText3_username.setText(username);
		}

	}

	private void initOnClick() {
		imageButton_login.setOnTouchListener(this);
		imageButton_login.setOnClickListener(this);
	}

	private void findView() {
		imageButton_login = (ImageButton) findViewById(R.id.imageButton_login);
		editText3_username = (EditText) findViewById(R.id.editText3_username);
		editText3_password = (EditText) findViewById(R.id.editText3_password);
		checkBox_remember_password = (CheckBox) findViewById(R.id.checkBox_remember_password);
	}

	@Override
	public void onClick(View v) {
		System.out.println("执行了这句话");
		switch (v.getId()) {
			case R.id.imageButton_login:
				String username, password;
				username = editText3_username.getText().toString();
				password = editText3_password.getText().toString();
				if (username.length() != 11 || !username.startsWith("1")) {
					showNormalDia("温馨提示", "请输入合法的手机号");
				} else if (password.length() < 6 || password.length() > 20) {
					showNormalDia("温馨提示", "密码为6-20位，不可过短或过长");
				} else {
					showReadProcess();
					startSearchUser(username, password);

				}
			default:
				break;
		}
	}

	private void startSearchUser(final String username, final String password) {
		BmobQuery<UserLogin> query = new BmobQuery<>();
		query.addWhereEqualTo("username", username);
		query.setLimit(1);
		query.findObjects(new FindListener<UserLogin>() {
			@Override
			public void done(List<UserLogin> list, BmobException e) {
				if (e == null && !list.isEmpty()) {
					startLogin(list.get(0));
				} else if (e == null) {
					startRegister(username, password);
				} else {
					showNormalDia("温馨提示", "登录失败！请检查网络连接");
					Log.e(TAG, "query.findObjects: ", e);
				}
			}
		});
	}

	private void startRegister(final String username, final String password) {
		UserLogin userLogin = new UserLogin();
		userLogin.setUsername(username);
		userLogin.setPassword(password);
		userLogin.signUp(new SaveListener<UserLogin>() {
			@Override
			public void done(UserLogin userLogin, BmobException e) {
				if (e == null) {
					startLogin(userLogin);
				} else {
					showNormalDia("温馨提示", "注册失败！请检查网络连接");
					Log.e(TAG, "userLogin.signUp: ", e);
				}
			}
		});
	}

	private void startLogin(UserLogin userLogin) {
		userLogin.setUsername(editText3_username.getText().toString());
		userLogin.setPassword(editText3_password.getText().toString());
		userLogin.login(new SaveListener<UserLogin>() {
			@Override
			public void done(UserLogin userLogin, BmobException e) {
				if (e == null) {
					startNextActivity(userLogin);
				} else {
					showNormalDia("温馨提示", "登录失败！请检查用户名或密码");
					Log.e(TAG, "userLogin.login: ", e);
				}
			}
		});
	}

	private void startNextActivity(UserLogin userLogin) {
		SharedPreferences sharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE); //私有数据
		SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器
		editor.putString("username", userLogin.getUsername());
		if (checkBox_remember_password.isChecked()) {
			editor.putBoolean("isLogin", true);
		}
		editor.commit();
		mReadProcessDia.cancel();
		startActivity(new Intent(LoginActivity.this, MainActivity.class));
		finish();
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (v.getId()) {
			case R.id.imageButton_login:
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					imageButton_login.setImageResource(R.mipmap.button_login_press);
				}
				if (event.getAction() == MotionEvent.ACTION_UP) {
					imageButton_login.setImageResource(R.mipmap.button_login);
				}
				break;
			default:
				break;
		}
		return false;
	}

	/*普通的对话框*/
	private void showNormalDia(String title, String message) {
		//AlertDialog.Builder normalDialog=new AlertDialog.Builder(getApplicationContext());
		AlertDialog.Builder normalDia = new AlertDialog.Builder(this);
		//normalDia.setIcon(R.mipmap.ic_launcher);
		normalDia.setTitle(title);
		normalDia.setMessage(message);
		normalDia.setPositiveButton("朕知道了", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		normalDia.create().show();
	}

	ProgressDialog mReadProcessDia = null;

	//显示加载对话框
	private void showReadProcess() {
		mReadProcessDia = new ProgressDialog(this);
		mReadProcessDia.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mReadProcessDia.setIndeterminate(true);
		mReadProcessDia.setTitle("玩命加载中...");
		mReadProcessDia.setMessage("正在登陆请稍后...");
		mReadProcessDia.show();
	}


}