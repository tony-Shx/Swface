package com.henu.swface.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.henu.swface.Adapter.StickyListAdapter;
import com.henu.swface.R;
import com.henu.swface.Utils.FinalUtil;
import com.henu.swface.VO.SignLog;
import com.henu.swface.VO.UserHasSigned;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;


public class SignLogActivity extends Activity implements AdapterView.OnItemClickListener, StickyListHeadersListView.OnHeaderClickListener, StickyListHeadersListView.OnStickyHeaderChangedListener, StickyListHeadersListView.OnStickyHeaderOffsetChangedListener {
	private Toolbar toolbar;
	private StickyListHeadersListView stickyList;
	private StickyListAdapter mAdapter;
	private SwipeRefreshLayout refreshLayout;
	private Dialog dialog;
	private List<SignLog> signLogList;
	//private static List<UserHasSigned> userHasSignedList;
	private static String TAG = SignLogActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_log);
		toolbar = (Toolbar) findViewById(R.id.toolbar_sign_log);
		stickyList = (StickyListHeadersListView) findViewById(R.id.list);
		toolbar.setTitle("签到记录");
		toolbar.setNavigationIcon(R.mipmap.button_back);
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});
		toolbar.inflateMenu(R.menu.sign_log_toobar_menu);
		toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				switch (item.getItemId()){
					case R.id.action_only_signed:
						mAdapter.only_signed();
						break;
					case R.id.action_only_no_signed:
						mAdapter.only_no_signed();
						break;
					case R.id.action_all_people:
						mAdapter.all_signed();
						break;
					case R.id.action_about:

						break;
					default:
						break;

				}
				return false;
			}
		});
		stickyList.setOnItemClickListener(this);
		stickyList.setOnHeaderClickListener(this);
		stickyList.setOnStickyHeaderChangedListener(this);
		stickyList.setOnStickyHeaderOffsetChangedListener(this);
		stickyList.addHeaderView(getLayoutInflater().inflate(R.layout.list_header, null));
		stickyList.addFooterView(getLayoutInflater().inflate(R.layout.list_footer, null));
		TextView textView = new TextView(this);
		textView.setText("当前签到内容为空！");
		stickyList.setEmptyView(textView);
		stickyList.setDrawingListUnderStickyHeader(true);
		stickyList.setAreHeadersSticky(true);

		refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
		refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						refreshLayout.setRefreshing(false);
					}
				}, 1000);
			}
		});
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setCancelable(false);
		builder.setTitle("温馨提示");
		builder.setMessage("正在加载，请稍后......");
		builder.setView(new ProgressBar(this));
		dialog = builder.show();
		loadData();
	}

	private void loadData() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				BmobQuery<SignLog> bmobQuery = new BmobQuery<SignLog>();
				SharedPreferences preferences = getSharedPreferences("login", Context.MODE_PRIVATE);
				String telephone = preferences.getString("username","null");
				bmobQuery.addWhereEqualTo("telephone",telephone);
				bmobQuery.setLimit(500);
				bmobQuery.order("-createdAt");
				bmobQuery.findObjects(new FindListener<SignLog>() {
					@Override
					public void done(List<SignLog> list, BmobException e) {
						if(e==null){
							BmobQuery<UserHasSigned> bmobQuery = new BmobQuery<UserHasSigned>();
							SharedPreferences preferences = getSharedPreferences("login", Context.MODE_PRIVATE);
							final String telephone = preferences.getString("username","null");
							bmobQuery.addWhereEqualTo("telephone",telephone);
							bmobQuery.setLimit(500);
							signLogList = list;
							bmobQuery.order("username");
							bmobQuery.findObjects(new FindListener<UserHasSigned>() {
								@Override
								public void done(List<UserHasSigned> list, BmobException e) {
									Message message = Message.obtain();
									message.what = FinalUtil.LOAD_SIGN_LOG_SUCCESS;
									message.obj = list;
									myHandler.sendMessage(message);
								}
							});
						}else{
							Message message = Message.obtain();
							message.what = FinalUtil.LOAD_SIGN_LOG_IO_EXCEPTION;
							myHandler.sendMessage(message);
						}
					}
				});
			}
		}).start();
	}


	private Handler myHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what){
				case FinalUtil.LOAD_SIGN_LOG_IO_EXCEPTION:
					dialog.dismiss();
					Toast.makeText(SignLogActivity.this,"加载失败，请检查网络连接",Toast.LENGTH_LONG).show();
					break;
				case FinalUtil.LOAD_SIGN_LOG_SUCCESS:
					ArrayList<UserHasSigned> userHasSignedList = (ArrayList<UserHasSigned>) msg.obj;
					mAdapter = new StickyListAdapter(getApplicationContext(),signLogList,userHasSignedList);
					stickyList.setAdapter(mAdapter);
					dialog.dismiss();
					break;
				default:
					break;
			}
		}
	};


	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

	}

	@Override
	public void onHeaderClick(StickyListHeadersListView l, View header, int itemPosition, long headerId, boolean currentlySticky) {

	}

	@Override
	public void onStickyHeaderChanged(StickyListHeadersListView l, View header, int itemPosition, long headerId) {
		//Log.i(TAG, "onStickyHeaderChanged: "+itemPosition+" "+headerId);
	}

	@Override
	public void onStickyHeaderOffsetChanged(StickyListHeadersListView l, View header, int offset) {
		//Log.i(TAG, "onStickyHeaderOffsetChanged_offset: "+offset);
	}
}
