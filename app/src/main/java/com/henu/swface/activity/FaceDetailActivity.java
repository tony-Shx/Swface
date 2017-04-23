package com.henu.swface.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.henu.swface.Adapter.FaceDetailAdapter;
import com.henu.swface.Database.BmobDataHelper;
import com.henu.swface.Database.DatabaseAdapter;
import com.henu.swface.R;
import com.henu.swface.Utils.FaceSetUtil;
import com.henu.swface.Utils.FinalUtil;
import com.henu.swface.Utils.PictureUtil;
import com.henu.swface.Utils.RoundTransform;
import com.henu.swface.VO.UserHasSigned;
import com.squareup.picasso.Picasso;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import okhttp3.Response;

public class FaceDetailActivity extends Activity implements View.OnClickListener {
	private ImageView imageView_face_detail_head;
	private TextView textView_face_detail_name, textView_face_detail_register_time;
	private ImageButton button_face_detail_edit;
	private RecyclerView recyclerView_face_detail;
	private Toolbar toolbar;
	private List<Uri> imageList = new ArrayList<>();
	private UserHasSigned userHasSigned;
	private int position;
	private boolean edit_face = false;
	private FaceDetailAdapter faceDetailAdapter;
	private Dialog dialog;
	private static final String TAG = FaceDetailActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_face_detail);
		final Intent intent = getIntent();
		userHasSigned = (UserHasSigned) intent.getSerializableExtra("userHasSigned");
		position = intent.getIntExtra("position", -1);
		findView();
		toolbar.setNavigationIcon(R.mipmap.button_back);
		toolbar.inflateMenu(R.menu.face_detail_toobar_menu);
		toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				switch (item.getItemId()) {
					case R.id.action_delete_user:
						AlertDialog.Builder builder = new AlertDialog.Builder(FaceDetailActivity.this);
						builder.setTitle("温馨提示:");
						builder.setMessage("当前操作会删除此人所有信息，包括此人所有已注册的人脸！！确定要删除吗？");
						builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogInterface, int i) {
								showNormalDialog("温馨提示", "正在删除此人，请稍后...", false, null, false);
								new Thread(new Runnable() {
									@Override
									public void run() {
										startDeleteUser();
									}
								}).start();
							}
						});
						builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogInterface, int i) {
								dialogInterface.dismiss();
							}
						});
						dialog = builder.show();
						break;
					case R.id.action_about:
						break;
					default:
						break;
				}
				return true;
			}
		});
	}

	private void startDeleteUser() {
		if (!imageList.isEmpty()) {
			StringBuilder sb = new StringBuilder();
			sb.append(getFaceTokenAndUrl(0).split("#")[0]);
			for (int i = 1; i < imageList.size(); i++) {
				sb.append(',');
				sb.append(getFaceTokenAndUrl(i).split("#")[0]);
			}
			Response response = FaceSetUtil.removeFaceFormFaceSet(FinalUtil.API_KEY, FinalUtil.API_Secret, getOutId(), sb.toString());
			if (response == null || response.code() != 200) {
				Message message = Message.obtain();
				message.arg1 = FinalUtil.REMOVE_USER_IO_EXCEPTION;
				myHandler.sendMessage(message);
				return;
			}
			String JSON = "";
			try {
				JSON = response.body().string();
				Log.i(TAG, "startDeleteFace_JSON: " + JSON);
			} catch (IOException e) {
				Log.e(TAG, "startDeleteFace: ", e);
				e.printStackTrace();
			}
			if (JSON.isEmpty() || JSON.contains("error_message")) {
				Message message = Message.obtain();
				message.arg1 = FinalUtil.REMOVE_FACE_IO_EXCEPTION;
				myHandler.sendMessage(message);
				return;
			}
		}
		BmobDataHelper bmobDataHelper = new BmobDataHelper(FaceDetailActivity.this, myHandler);
		bmobDataHelper.deleteUser(userHasSigned.getObjectId());
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
		setOnClick();

	}

	private void setOnClick() {
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});
		button_face_detail_edit.setOnClickListener(this);
		if (faceDetailAdapter != null) {
			final FaceDetailAdapter adapter = faceDetailAdapter;
			adapter.setOnItemLongClickListener(new FaceDetailAdapter.OnItemLongClickListener() {
				@Override
				public void onItemLongClick(View view) {
					Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
					long[] pattern = {0, 100};
					Log.i(TAG, "onItemLongClick_vibrate: ");
					vibrator.vibrate(pattern, 1);
					adapter.notifyDataSetChanged();
					edit_face = true;
				}
			});
			adapter.setOnDeleteClickListener(new FaceDetailAdapter.OnDeleteClickListener() {
				@Override
				public void onDeleteClick(View view, final int index) {
					if (edit_face) {
						AlertDialog.Builder builder = new AlertDialog.Builder(FaceDetailActivity.this);
						builder.setTitle("删除提醒");
						builder.setMessage("确定要删除此照片？");
						builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogInterface, int i) {
								new Thread(new Runnable() {
									@Override
									public void run() {
										startDeleteFace(index);
									}
								}).start();
								dialogInterface.dismiss();
								showNormalDialog(null, "正在删除，请稍后...", false, new ProgressBar(FaceDetailActivity.this), false);
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
				}
			});
		}
	}

	private void startDeleteFace(int index) {
		String faceTokenAndUrl = getFaceTokenAndUrl(index);
		if (!faceTokenAndUrl.equals("")) {
			String[] face = faceTokenAndUrl.split("#");
			String out_id = getOutId();
			Response response = FaceSetUtil.removeFaceFormFaceSet(FinalUtil.API_KEY, FinalUtil.API_Secret, out_id, face[0]);
			if (response == null || response.code() != 200) {
				Message message = Message.obtain();
				message.arg1 = FinalUtil.REMOVE_FACE_IO_EXCEPTION;
				myHandler.sendMessage(message);
				return;
			}
			String JSON = "";
			try {
				JSON = response.body().string();
				Log.i(TAG, "startDeleteFace_JSON: " + JSON);
			} catch (IOException e) {
				Log.e(TAG, "startDeleteFace: ", e);
				e.printStackTrace();
			}
			if (JSON.isEmpty() || JSON.contains("error_message")) {
				Message message = Message.obtain();
				message.arg1 = FinalUtil.REMOVE_FACE_IO_EXCEPTION;
				myHandler.sendMessage(message);
				return;
			}
			BmobDataHelper db = new BmobDataHelper(this, myHandler);
			String[] attribute = getAttribute(index).split("#");
			if (face.length == 2) {
				db.deleteUserFace(userHasSigned.getObjectId(), attribute[0], attribute[1]);
			}

		} else {
			Log.i(TAG, "startDeleteFace: 删除出错！");
		}
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
				case FinalUtil.REMOVE_FACE_IO_EXCEPTION:
					dialog.dismiss();
					showNormalDialog("温馨提示", "删除失败，请检查网络连接", true, null, true);
					break;
				case FinalUtil.REMOVE_FACE_BMOB_EXCEPTION:
					dialog.dismiss();
					showNormalDialog("温馨提示", "删除失败，发生了一些未知错误，请检查网络连接后重试", false, null, true);
					break;
				case FinalUtil.REMOVE_FACE_SUCCESS:
					DatabaseAdapter db = new DatabaseAdapter(FaceDetailActivity.this);
					userHasSigned = db.findUserByObiectId(userHasSigned.getObjectId());
					db = null;
					dialog.dismiss();
//					showNormalDialog(null, "恭喜，删除人脸成功！", true, null, true);
					Toast.makeText(FaceDetailActivity.this, "删除人脸成功", Toast.LENGTH_LONG).show();
					edit_face = false;
					initDate();
					setOnClick();
				case FinalUtil.REMOVE_USER_IO_EXCEPTION:
					dialog.dismiss();
					showNormalDialog("温馨提示", "删除失败，请检查网络连接", true, null, true);
					break;
				case FinalUtil.REMOVE_USER_BMOB_EXCEPTION:
					dialog.dismiss();
					showNormalDialog("温馨提示", "删除失败，发生了一些未知错误，请检查网络连接后然后重试", false, null, true);
					break;
				case FinalUtil.REMOVE_USER_SUCCESS:
					dialog.dismiss();
					Toast.makeText(FaceDetailActivity.this, "删除成功", Toast.LENGTH_LONG).show();
					Intent intent = new Intent();
					intent.putExtra("position", position);
					setResult(1,intent);
					finish();
					return;
				default:
					break;
			}
			Log.i(TAG, "handleMessage_userHasSigned: ");
			if (userHasSigned != null) {
				Intent intent = new Intent();
				intent.putExtra("userHasSigned", userHasSigned);
				intent.putExtra("position", position);
				setResult(0, intent);
			}
		}
	};

	private void initDate() {
		toolbar.setTitle(userHasSigned.getUser_name() + "的详细信息");
		String[] face = getFaceTokenAndUrl(0).split("#");
		if (face.length == 2) {
			File imageFile = new File(PictureUtil.getPictureStoragePath(this), face[0] + ".jpg");
			if (imageFile.exists()) {
				Picasso.with(this).load(imageFile).transform(new RoundTransform()).placeholder(R.mipmap.loading).into(imageView_face_detail_head);
			} else {
				Picasso.with(this).load(face[1]).transform(new RoundTransform()).centerCrop().resize(90, 120).placeholder(R.mipmap.loading).into(imageView_face_detail_head);
			}
		} else {
			Picasso.with(this).load(R.mipmap.no_face).transform(new RoundTransform()).centerCrop().resize(90, 120).placeholder(R.mipmap.loading).into(imageView_face_detail_head);
		}
		textView_face_detail_name.setText(userHasSigned.getUser_name());
		textView_face_detail_register_time.setText(userHasSigned.getCreated_at());
		imageList.clear();
		String imageurl = userHasSigned.getFace_url1();
		if (imageurl != null && !imageurl.isEmpty() && !imageurl.equals("")) {
			Uri imageUrl1 = Uri.parse(imageurl);
			imageList.add(imageUrl1);
		}
		imageurl = userHasSigned.getFace_url2();
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
		faceDetailAdapter = new FaceDetailAdapter(imageList, userHasSigned);
		StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
		recyclerView_face_detail.setLayoutManager(layoutManager);
		if (faceDetailAdapter != null) {
			recyclerView_face_detail.setAdapter(faceDetailAdapter);
		}
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

	@Override
	protected void onStop() {
		super.onStop();

	}

	@Override
	public void onBackPressed() {
		if (edit_face) {
			if (faceDetailAdapter != null) {
				faceDetailAdapter.setOnLongClick(false);
				faceDetailAdapter.notifyDataSetChanged();
			}
			edit_face = false;
		} else {
			super.onBackPressed();
		}
	}


	private String getAttribute(int index) {
		StringBuffer sb = new StringBuffer();
		if (testNull(userHasSigned.getFace_token1())) {
			index--;
			if (index == -1) {
				return sb.append("face_token1").append('#').append("face_url1").toString();
			}
		}
		if (testNull(userHasSigned.getFace_token2())) {
			index--;
			if (index == -1) {
				return sb.append("face_token2").append('#').append("face_url2").toString();
			}
		}
		if (testNull(userHasSigned.getFace_token3())) {
			index--;
			if (index == -1) {
				return sb.append("face_token3").append('#').append("face_url3").toString();
			}
		}
		if (testNull(userHasSigned.getFace_token4())) {
			index--;
			if (index == -1) {
				return sb.append("face_token4").append('#').append("face_url4").toString();
			}
		}
		if (testNull(userHasSigned.getFace_token5())) {
			index--;
			if (index == -1) {
				return sb.append("face_token5").append('#').append("face_url5").toString();
			}
		}
		return "";
	}

	private String getFaceTokenAndUrl(int index) {
		StringBuffer sb = new StringBuffer();
		if (testNull(userHasSigned.getFace_token1())) {
			index--;
			if (index == -1) {
				return sb.append(userHasSigned.getFace_token1()).append('#').append(userHasSigned.getFace_url1()).toString();
			}
		}
		if (testNull(userHasSigned.getFace_token2())) {
			index--;
			if (index == -1) {
				return sb.append(userHasSigned.getFace_token2()).append('#').append(userHasSigned.getFace_url2()).toString();
			}
		}
		if (testNull(userHasSigned.getFace_token3())) {
			index--;
			if (index == -1) {
				return sb.append(userHasSigned.getFace_token3()).append('#').append(userHasSigned.getFace_url3()).toString();
			}
		}
		if (testNull(userHasSigned.getFace_token4())) {
			index--;
			if (index == -1) {
				return sb.append(userHasSigned.getFace_token4()).append('#').append(userHasSigned.getFace_url4()).toString();
			}
		}
		if (testNull(userHasSigned.getFace_token5())) {
			index--;
			if (index == -1) {
				return sb.append(userHasSigned.getFace_token5()).append('#').append(userHasSigned.getFace_url5()).toString();
			}
		}
		return "";
	}

	private boolean testNull(String test) {
		if (test != null && !test.isEmpty() && !test.equals("")) {
			return true;
		}
		return false;
	}

	private String getOutId() {
		//创建FaceSet集合，并将人脸加入集合
		SharedPreferences sp = getSharedPreferences("login", Context.MODE_PRIVATE);
		return sp.getString("username", "default");
	}

	private void showNormalDialog(String title, String message, boolean cancel, View v, boolean haveButton) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setCancelable(cancel);
		if (title != null) {
			builder.setTitle(title);
		} else {
			builder.setTitle("温馨提示");
		}
		builder.setMessage(message);
		if (v != null) {
			builder.setView(v);
		}
		if (haveButton) {
			builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialogInterface, int i) {
					dialog.dismiss();
				}
			});
		}
		dialog = builder.show();
	}
}
