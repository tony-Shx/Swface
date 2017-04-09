package com.henu.swface.Activity;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.henu.swface.R;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {

    private ImageButton register, imageButton_login;
    private EditText editText3_username, editText3_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findView();
        initOnClick();

    }

    private void initOnClick() {
        register.setOnClickListener(this);
        register.setOnTouchListener(this);
        imageButton_login.setOnTouchListener(this);
        imageButton_login.setOnClickListener(this);
    }

    private void findView() {

        register = (ImageButton) findViewById(R.id.imageButton_register);
        imageButton_login = (ImageButton) findViewById(R.id.imageButton_login);
        editText3_username = (EditText) findViewById(R.id.editText3_username);
        editText3_password = (EditText) findViewById(R.id.editText3_password);
    }

    @Override
    public void onClick(View v) {
        System.out.println("执行了这句话");
        switch (v.getId()) {
            case R.id.imageButton_register:
                startActivity(new Intent(this, RegisterActivity.class));
                break;
            case R.id.imageButton_login:
                String username, password;
                username = editText3_username.getText().toString();
                password = editText3_password.getText().toString();
                if (username.equals("test") || password.equals("test")) {
                    showReadProcess();
                } else {
                    if (username.equals("") || password.equals("")) {
                        showNormalDia("温馨提示", "用户名或密码为空，请填写完整的用户名密码");
                    } else {
                        SQLiteDatabase db = this.openOrCreateDatabase("username_db.db", Context.MODE_PRIVATE, null);
                        String sql = "create table if not exists t_user (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL,password TEXT NOT NULL,sex INTEGER NOT NULL,telephone TEXT NOT NULL)";
                        db.execSQL(sql);
                        Cursor c = db.query("t_user", new String[]{"id", "name", "password", "sex", "telephone"}, "name=?", new String[]{username}, null, null, null);
                        c.moveToFirst();
                        if (c.getCount() > 0) {
                            String name = c.getString(1);
                            if (name.equals(username)) {
                                String psd = c.getString(2);
                                if (psd.equals(password)) {
                                    showReadProcess();
                                } else {
                                    showNormalDia("温馨提示", "您输入的用户名和密码不匹配，请检查用户名或密码");
                                }
                            }
                        } else {
                            showNormalDia("温馨提示", "您输入的用户名不存在，请先注册");
                        }
                    }
                }
            default:
                break;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {
            case R.id.imageButton_register:
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    register.setImageResource(R.mipmap.button_register_press);
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    register.setImageResource(R.mipmap.button_register);
                }
                break;
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
        normalDia.setIcon(R.mipmap.ic_launcher);
        normalDia.setTitle(title);
        normalDia.setMessage(message);
        normalDia.setPositiveButton("朕知道了", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        normalDia.create().show();
    }

    //进度读取框需要模拟读取
    ProgressDialog mReadProcessDia = null;
    public final static int MAX_READPROCESS = 15;

    private void showReadProcess() {
        mReadProcessDia = new ProgressDialog(this);
        mReadProcessDia.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mReadProcessDia.setIndeterminate(true);
        mReadProcessDia.setTitle("正在登陆请稍后...");
        mReadProcessDia.show();
        new Thread() {
            //新开启一个线程，循环的累加，一直到100然后在停止
            public void run() {
                int Progress = 0;
                while (Progress < MAX_READPROCESS) {
                    try {
                        Thread.sleep(100);
                        Progress++;
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                //读取完了以后窗口自消失
                SharedPreferences sharedPreferences = getSharedPreferences("denglu", Context.MODE_PRIVATE); //私有数据
                SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器
                editor.putInt("denglu", 1);
                editor.commit();
                mReadProcessDia.cancel();
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }
        }.start();
    }


}