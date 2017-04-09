package com.henu.swface.Activity;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;

import com.henu.swface.R;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {

    private EditText  editText_password_register, editText_password_again_register, editText2_telephone_register;
    private ImageButton tijiao_register;
    private boolean dialogIsShow = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        findView();
        tijiao_register.setOnTouchListener(this);
        tijiao_register.setOnClickListener(this);
    }

    private void findView() {
        editText_password_register = (EditText) findViewById(R.id.editText_password_register);
        editText2_telephone_register = (EditText) findViewById(R.id.editText2_telephone_register);
        tijiao_register = (ImageButton) findViewById(R.id.tijiao_register);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tijiao_register:
                if (editText_password_register.getText().toString().equals("") || editText_password_again_register.getText().toString().equals("")) {
                        showNormalDia("温馨提示", "密码不能为空\n请输入密码");
                } else if (editText2_telephone_register.getText().toString().length() < 11) {
                                    showNormalDia("温馨提示", "输入的手机号少于11位，请重新输入");
                } else {

                       }
                break;
            default:
                break;
        }

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {
            case R.id.tijiao_register:
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    tijiao_register.setImageResource(R.mipmap.button_tijiao_press);
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    tijiao_register.setImageResource(R.mipmap.button_tijiao);
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
                dialogIsShow = false;
            }
        });
//        normalDia.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//            }
//        });
        if (!dialogIsShow) {
            normalDia.create().show();
            dialogIsShow = true;
        }
    }

}
