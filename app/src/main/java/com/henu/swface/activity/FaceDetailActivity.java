package com.henu.swface.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.henu.swface.Adapter.FaceDetailAdapter;
import com.henu.swface.Database.BmobDataHelper;
import com.henu.swface.Database.DatabaseAdapter;
import com.henu.swface.R;
import com.henu.swface.Utils.FaceUtil;
import com.henu.swface.Utils.FinalUtil;
import com.henu.swface.Utils.RoundTransform;
import com.henu.swface.VO.UserHasSigned;
import com.iflytek.cloud.thirdparty.L;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FaceDetailActivity extends Activity implements View.OnClickListener {
	private ImageView imageView_face_detail_head;
	private TextView textView_face_detail_name, textView_face_detail_register_time;
	private ImageButton button_face_detail_edit;
	private RecyclerView recyclerView_face_detail;
	private Toolbar toolbar;
	private List<Uri> imageList = new ArrayList<>();
	private UserHasSigned userHasSigned;
	private int position;
	private static final String TAG = FaceDetailActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_face_detail);
		Intent intent = getIntent();
		userHasSigned = (UserHasSigned) intent.getSerializableExtra("userHasSigned");
		position = intent.getIntExtra("position",-1);
		findView();
		toolbar.setNavigationIcon(R.mipmap.button_back);
		toolbar.inflateMenu(R.menu.base_toolbar_menu);
		setOnClick();
	}

	@Override
	protected void onResume() {
		super.onResume();
		DatabaseAdapter db = new DatabaseAdapter(this);
		UserHasSigned userHasSignedTemp = db.findUserByObiectId(userHasSigned.getObjectId());
		if (userHasSignedTemp != null) {
			userHasSigned.setFace_token1(userHasSignedTemp.getFace_token1());
			userHasSigned.setFace_token2(userHasSignedTemp.getFace_token2());
			userHasSigned.setFace_token3(userHasSignedTemp.getFace_token3());
			userHasSigned.setFace_token4(userHasSignedTemp.getFace_token4());
			userHasSigned.setFace_token5(userHasSignedTemp.getFace_token5());
			userHasSigned.setFace_url1(userHasSignedTemp.getFace_url1());
			userHasSigned.setFace_url2(userHasSignedTemp.getFace_url2());
			userHasSigned.setFace_url3(userHasSignedTemp.getFace_url3());
			userHasSigned.setFace_url4(userHasSignedTemp.getFace_url4());
			userHasSigned.setFace_url5(userHasSignedTemp.getFace_url5());
			userHasSignedTemp = null;
		}
		initDate();

	}

	private void setOnClick() {
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});
		button_face_detail_edit.setOnClickListener(this);
	}

	private void initDate() {
		toolbar.setTitle(userHasSigned.getUser_name() + "的详细信息");
		File imageFile = new File(FaceUtil.getPictureStoragePath(this), userHasSigned.getFace_token1() + ".jpg");
		imageList.clear();
		if (imageFile.exists()) {
			Uri imageUri1 = Uri.fromFile(imageFile);
			imageList.add(imageUri1);
			Picasso.with(this).load(imageFile).transform(new RoundTransform()).placeholder(R.mipmap.loading).into(imageView_face_detail_head);
		} else {
			Uri imageUri1 = Uri.parse(userHasSigned.getFace_url1());
			imageList.add(imageUri1);
			Picasso.with(this).load(userHasSigned.getFace_url1()).transform(new RoundTransform()).centerCrop().resize(90, 120).placeholder(R.mipmap.loading).into(imageView_face_detail_head);
		}
		textView_face_detail_name.setText(userHasSigned.getUser_name());
		textView_face_detail_register_time.setText(userHasSigned.getCreated_at());
		String imageurl = userHasSigned.getFace_url2();
		if (imageurl != null && !imageurl.isEmpty() && !imageurl.equals("")) {
			Uri imageUrl2 = Uri.parse(imageurl);
			imageList.add(imageUrl2);
		}
		imageurl = userHasSigned.getFace_url3();
		if (imageurl != null && !imageurl.isEmpty() && !imageurl.equals("")) {
			Uri imageUrl3 = Uri.parse(imageurl);
			imageList.add(imageUrl3);
		}
		imageurl = userHasSigned.getFace_url4();
		if (imageurl != null && !imageurl.isEmpty() && !imageurl.equals("")) {
			Uri imageUrl4 = Uri.parse(imageurl);
			imageList.add(imageUrl4);
		}
		imageurl = userHasSigned.getFace_url5();
		if (imageurl != null && !imageurl.isEmpty() && !imageurl.equals("")) {
			Uri imageUrl5 = Uri.parse(imageurl);
			imageList.add(imageUrl5);
		}
		FaceDetailAdapter adapter = new FaceDetailAdapter(imageList, userHasSigned);
		StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
		recyclerView_face_detail.setLayoutManager(layoutManager);
		recyclerView_face_detail.setAdapter(adapter);
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.i(TAG, "onPause: ");
	}

	private void findView() {
		imageView_face_detail_head = (ImageView) findViewById(R.id.imageView_face_detail_head);
		textView_face_detail_name = (TextView) findViewById(R.id.textView_face_detail_name);
		textView_face_detail_register_time = (TextView) findViewById(R.id.textView_face_detail_register_time);
		button_face_detail_edit = (ImageButton) findViewById(R.id.button_face_detail_edit);
		recyclerView_face_detail = (RecyclerView) findViewById(R.id.recyclerView_face_detail);
		toolbar = (Toolbar) findViewById(R.id.toolbar_face_detail);

	}

	@Override
	public void onClick(View view) {
		Log.i(TAG, "onClick: " + view.getId());
		switch (view.getId()) {
			case R.id.button_face_detail_edit:
				showEditDialog();
				break;
			default:
				break;
		}
	}

	private void showEditDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("请输入姓名：");
		final EditText editText = new EditText(this);
		editText.setText(userHasSigned.getUser_name());
		builder.setView(editText);
		builder.setCancelable(true);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {
				String newUsername = editText.getText().toString();
				if (newUsername != null && !newUsername.isEmpty() && !newUsername.equals("")) {
					if (userHasSigned.getUser_name().equals(newUsername)) {
						Toast.makeText(getApplicationContext(), "用户名相同，未提交修改！", Toast.LENGTH_LONG).show();
					} else {
						BmobDataHelper db = new BmobDataHelper(FaceDetailActivity.this, myHandler);
						db.updateUsername(userHasSigned, newUsername);
					}
					dialogInterface.dismiss();
				} else {
					Toast.makeText(getApplicationContext(), "用户名不能为空！", Toast.LENGTH_LONG).show();
				}
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {
				dialogInterface.dismiss();
			}
		});
		builder.show();
	}

	private Handler myHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			switch (msg.arg1) {
				case FinalUtil.UPDATE_DETAIL_SUCCESS:
					Toast.makeText(getApplicationContext(), "重命名成功", Toast.LENGTH_LONG).show();
					String username = userHasSigned.getUser_name();
					textView_face_detail_name.setText(username);
					toolbar.setTitle(username + "的详细信息");
					break;
				case FinalUtil.UPDATE_DETAIL_IO_EXCEPTION:
					Toast.makeText(getApplicationContext(), "网络异常，重命名失败", Toast.LENGTH_LONG).show();
					break;
				default:
					break;
			}
			Intent intent = new Intent();
			intent.putExtra("userHasSigned",userHasSigned);
			intent.putExtra("position",position);
			setResult(0,intent);
		}
	};

	@Override
	protected void onStop() {
		super.onStop();

	}

}
