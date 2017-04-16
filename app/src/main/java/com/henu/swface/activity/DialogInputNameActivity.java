package com.henu.swface.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.henu.swface.R;

public class DialogInputNameActivity extends Activity implements View.OnClickListener {
	private Button button_ok, button_cancel;
	private EditText editText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dialog_input_name);
		button_ok = (Button) findViewById(R.id.button_dialog_input_ok);
		button_cancel = (Button) findViewById(R.id.button_dialog_input_cancel);
		editText = (EditText) findViewById(R.id.editText_dialog_username);
		button_ok.setOnClickListener(this);
		button_cancel.setOnClickListener(this);
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.button_dialog_input_ok:
				String username = editText.getText().toString();
				if (null == username || username.isEmpty() || username.equals("")) {
					Toast.makeText(this,"注册的人脸姓名不能为空！",Toast.LENGTH_LONG).show();
				} else {
					Intent intent = new Intent();
					intent.putExtra("username", username);
					setResult(1, intent);
					finish();
				}
				break;
			case R.id.button_dialog_input_cancel:
				setResult(0);
				finish();
				break;
			default:
				break;
		}
	}
}
