package com.henu.swface.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.henu.swface.R;

import java.io.File;
import java.io.FileOutputStream;

public class DialogInputNameActivity extends Activity implements View.OnClickListener {
	private Button button_ok,button_cancel;
	private EditText editText;
	private byte[] data;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dialog_input_name);
		data = getIntent().getByteArrayExtra("data");
		button_ok = (Button) findViewById(R.id.button_dialog_input_ok);
		button_cancel = (Button) findViewById(R.id.button_dialog_input_cancel);
		editText = (EditText) findViewById(R.id.editText_dialog_username);
		button_ok.setOnClickListener(this);
		button_cancel.setOnClickListener(this);

	}


	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.button_dialog_input_ok:
				String username = editText.getText().toString();
				Intent intent = new Intent();
				intent.putExtra("username",username);
				setResult(1,intent);
				finish();
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
