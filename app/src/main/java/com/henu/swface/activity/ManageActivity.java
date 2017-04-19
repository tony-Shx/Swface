package com.henu.swface.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.henu.swface.Adapter.ManageAdapter;
import com.henu.swface.Database.DatabaseAdapter;
import com.henu.swface.R;
import com.henu.swface.VO.UserHasSigned;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class ManageActivity extends Activity {

    private RecyclerView recyclerView_manage;
    private Dialog dialog;
    private Toolbar toolbar;
    private static final String TAG = ManageActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage);
        //第一：默认初始化
        Bmob.initialize(this, "6e64317b6509bb503f7d9ab8404bf54c");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("温馨提示：");
        builder.setMessage("正在玩命查询，请稍后......");
        builder.setView(new ProgressBar(this));
        dialog = builder.show();
        dialog.setCancelable(false);
        recyclerView_manage = (RecyclerView) findViewById(R.id.recyclerView_manage);
        toolbar = (Toolbar) findViewById(R.id.toolbar_manage);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL);
        recyclerView_manage.setLayoutManager(layoutManager);
        initData();
    }

    private void initData() {
        toolbar.setNavigationIcon(R.mipmap.button_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        toolbar.setTitle("已注册用户");
        toolbar.inflateMenu(R.menu.base_toolbar_menu);
        BmobQuery<UserHasSigned> query = new BmobQuery<>();
        SharedPreferences sharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE);
        String telephone = sharedPreferences.getString("username","");
        query.addWhereEqualTo("telephone",telephone);
        query.setLimit(50);
        query.findObjects(new FindListener<UserHasSigned>() {
            @Override
            public void done(List<UserHasSigned> list, BmobException e) {
                if(e==null){
                    ManageAdapter adapter = new ManageAdapter(list);
                    recyclerView_manage.setAdapter(adapter);
                    DatabaseAdapter db = new DatabaseAdapter(ManageActivity.this);
                    ArrayList<UserHasSigned> mList = db.findAllUser_User();
                    for(UserHasSigned userHasSigned:list){
                        if(!mList.contains(userHasSigned)){
                            Log.i(TAG, "mList.contains(userHasSigned): false");
                            db.addUser_User(userHasSigned,null);
                        }
                    }
                    mList = db.findAllUser_User();
                    for(UserHasSigned userHasSigned:mList){
                        Log.i(TAG, "done: "+userHasSigned.toString());
                    }
                    dialog.dismiss();
                }else{
                    Log.e(TAG, "query.findObjects: ", e);
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(),"查询出错，请检查网络连接",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
