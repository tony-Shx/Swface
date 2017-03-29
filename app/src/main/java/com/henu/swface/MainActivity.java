package com.henu.swface;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
	private Button button_register,button_sign_in;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initUI();
		SharedPreferences sp = getSharedPreferences("login", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString("username","17839223557");
		editor.commit();
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
				Intent intent = new Intent(this,VideoRecognise.class);
				startActivity(intent);
				break;
			case R.id.button_sign_in:

				break;
			default:
				break;
		}
	}
}
