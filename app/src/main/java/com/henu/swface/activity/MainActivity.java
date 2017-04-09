package com.henu.swface.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.henu.swface.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
	private Button button_register,button_sign_in;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initUI();
	}

	private void initUI() {
		button_register = (Button) this.findViewById(R.id.button_register);
		button_sign_in = (Button) this.findViewById(R.id.button_sign_in);
		button_register.setOnClickListener(this);
		button_sign_in.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.button_register:
				Intent intent = new Intent(this,VideoRecogniseActivity.class);
				startActivity(intent);
				break;
			case R.id.button_sign_in:
				startActivity(new Intent(this,SignInActivity.class));
				break;
			default:
				break;
		}
	}
}
