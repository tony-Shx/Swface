package com.henu.swface.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
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
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
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
import com.henu.swface.VO.Face;
import com.henu.swface.VO.UserHasSigned;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Response;

public class FaceDetailActivity extends Activity implements View.OnClickListener {
	private ImageView imageView_face_detail_head, delete_picture, detail_picture, expandedImageView,imageView_thumView;
	private TextView textView_face_detail_name, textView_face_detail_register_time;
	private ImageButton button_face_detail_edit;
	private RecyclerView recyclerView_face_detail;
	private Toolbar toolbar;
	private LinearLayout linearLayout_contains, linearLayout_bottom;
	private List<Uri> imageList = new ArrayList<>();
	private UserHasSigned userHasSigned;
	private int position;
	private boolean edit_face = false;
	private FaceDetailAdapter faceDetailAdapter;
	private Dialog dialog;
	private static boolean isViewPicture = false;
	// 保存下当前动画类，以便可以随时结束动画
	private Animator mCurrentAnimator;
	//系统的短时长动画持续时间（单位ms）
	// 对于不易察觉的动画或者频繁发生的动画
	// 这个动画持续时间是最理想的
	private int mShortAnimationDuration;
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
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});
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
				Log.i(TAG, "startDeleteUser: 3");
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
						readyToDelete(index);
					}
				}
			});

			adapter.setOnFaceClickListener(new FaceDetailAdapter.OnFaceClickListener() {
				@Override
				public void onFaceClick(View view, int position) {
					if (position >= imageList.size()) {
						Intent intent = new Intent(FaceDetailActivity.this, AddFaceActivity.class);
						intent.putExtra("userHasSigned", userHasSigned);
						startActivity(intent);
					} else {
//						Intent intent = new Intent(FaceDetailActivity.this,ViewPictureActivity.class);
//						intent.putExtra("userHasSigned",userHasSigned);
//						intent.putExtra("faceTokenAndUrl",getFaceTokenAndUrl(position));
//						startActivity(intent);
						//执行zoom动画方法
						if (!edit_face) {
							zoomImageFromThumb(view, position);
						}
					}
				}
			});
		}
	}

	private void readyToDelete(final int index) {
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

	private void zoomImageFromThumb(final View thumbView, final int position) {
		// 如果有动画在执行，立即取消，然后执行现在这个动画
		if (mCurrentAnimator != null) {
			mCurrentAnimator.cancel();
		}
		isViewPicture = true;
		// 加载高分辨率的图片
		imageView_thumView = (ImageView) thumbView;
		String[] faceTokenAndUrl = getFaceTokenAndUrl(position).split("#");
		File file = new File(PictureUtil.getPictureStoragePath(null), faceTokenAndUrl[0] + ".jpg");
		if (file.exists()) {
			Picasso.with(this).load(file).placeholder(R.mipmap.loading).into(expandedImageView);
		} else {
			Picasso.with(this).load(faceTokenAndUrl[1]).placeholder(R.mipmap.loading).into(expandedImageView);
		}
		// 计算开始和结束位置的图片范围
		final Rect startBounds = new Rect();
		final Rect finalBounds = new Rect();
		final Point globalOffset = new Point();
		// 开始的范围就是ImageButton的范围，
		// 结束的范围是容器（FrameLayout）的范围
		// getGlobalVisibleRect(Rect)得到的是view相对于整个硬件屏幕的Rect
		// 即绝对坐标，减去偏移，获得动画需要的坐标，即相对坐标
		// getGlobalVisibleRect(Rect,Point)中，Point获得的是view在它在
		// 父控件上的坐标与在屏幕上坐标的偏移
		thumbView.getGlobalVisibleRect(startBounds);
		findViewById(R.id.container)
				.getGlobalVisibleRect(finalBounds, globalOffset);
		startBounds.offset(-globalOffset.x, -globalOffset.y);
		finalBounds.offset(-globalOffset.x, -globalOffset.y);
		// Adjust the start bounds to be the same aspect ratio as the final
		// bounds using the "center crop" technique. This prevents undesirable
		// stretching during the animation. Also calculate the start scaling
		// factor (the end scaling factor is always 1.0).
		// 下面这段逻辑其实就是保持纵横比
		float startScale;
		// 如果结束图片的宽高比比开始图片的宽高比大
		// 就是结束时“视觉上”拉宽了（压扁了）图片
		if ((float) finalBounds.width() / finalBounds.height()
				> (float) startBounds.width() / startBounds.height()) {
			// Extend start bounds horizontally
			startScale = (float) startBounds.height() / finalBounds.height();
			float startWidth = startScale * finalBounds.width();
			float deltaWidth = (startWidth - startBounds.width()) / 2;
			startBounds.left -= deltaWidth;
			startBounds.right += deltaWidth;
		} else {
			// Extend start bounds vertically
			startScale = (float) startBounds.width() / finalBounds.width();
			float startHeight = startScale * finalBounds.height();
			float deltaHeight = (startHeight - startBounds.height()) / 2;
			startBounds.top -= deltaHeight;
			startBounds.bottom += deltaHeight;
		}
		// Hide the thumbnail and show the zoomed-in view. When the animation
		// begins, it will position the zoomed-in view in the place of the
		// thumbnail.
		// 隐藏小的图片，展示大的图片。当动画开始的时候，
		// 要把大的图片发在小的图片的位置上
		//小的设置透明
		thumbView.setAlpha(0f);
		linearLayout_bottom.setAlpha(0f);
		linearLayout_bottom.setVisibility(View.VISIBLE);

		//大的可见
		expandedImageView.setVisibility(View.VISIBLE);
		// Set the pivot point for SCALE_X and SCALE_Y transformations
		// to the top-left corner of the zoomed-in view (the default
		// is the center of the view).
		expandedImageView.setPivotX(0f);
		expandedImageView.setPivotY(0f);
		// Construct and run the parallel animation of the four translation and
		// scale properties (X, Y, SCALE_X, and SCALE_Y).
		AnimatorSet set = new AnimatorSet();
		set.play(ObjectAnimator.ofFloat(expandedImageView, View.X, startBounds.left, finalBounds.left))
				.with(ObjectAnimator.ofFloat(expandedImageView, View.Y, startBounds.top, finalBounds.top))
				.with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X, startScale, 1f))
				.with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_Y, startScale, 1f))
				.with(ObjectAnimator.ofFloat(linearLayout_contains, View.ALPHA, 1.0f, 0.0f))
				.with(ObjectAnimator.ofFloat(toolbar, View.ALPHA, 1.0f, 0.0f));
		set.setDuration(400);
		set.setInterpolator(new DecelerateInterpolator());
		set.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				mCurrentAnimator = null;
				showTitleAndMenu(globalOffset);
				setViewPictureOnClick(position);
			}

			@Override
			public void onAnimationCancel(Animator animation) {
				mCurrentAnimator = null;
			}
		});
		set.start();
		mCurrentAnimator = set;
	}

	private void setViewPictureOnClick(final int position) {
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				backTo();
			}
		});
		delete_picture.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				readyToDelete(position);
			}
		});
		detail_picture.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				showPictureDetail(position);
			}
		});
	}

	private void showPictureDetail(int position) {
		String faceToken = getFaceTokenAndUrl(position).split("#")[0];
		showNormalDialog("人脸详情","正在玩命分析中...",true,new ProgressBar(this),false);
		DatabaseAdapter db = new DatabaseAdapter(this);
		Face face = db.findFaceByFaceToken_Faces(faceToken,myHandler);
		if(face!=null){
			dialog.dismiss();
			showDetailDialog(face);
		}
	}

	private void showDetailDialog(Face face) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("人脸分析详情");
		ScrollView scrollView = new ScrollView(this);
		TextView textView = new TextView(this);
		textView.setTextColor(Color.BLACK);
		textView.setText(face.toString());
		scrollView.addView(textView);
		//builder.setMessage(face.toString());
		builder.setView(scrollView);
		builder.setCancelable(true);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {
				dialogInterface.dismiss();
			}
		});
		dialog = builder.show();
		WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
		layoutParams.alpha = 0.6f;
		dialog.getWindow().setAttributes(layoutParams);
	}

	private void backTo() {
		if (mCurrentAnimator != null) {
			mCurrentAnimator.cancel();
		}
		isViewPicture = false;
		// Upon clicking the zoomed-in image, it should zoom back down
		// to the original bounds and show the thumbnail instead of
		// the expanded image.
		// 再次点击返回小的图片，就是上面扩大的反向动画。即预览完成
		// 计算开始和结束位置的图片范围
		final Rect startBounds = new Rect();
		final Rect finalBounds = new Rect();
		final Point globalOffset = new Point();
		// 开始的范围就是ImageButton的范围，
		// 结束的范围是容器（FrameLayout）的范围
		// getGlobalVisibleRect(Rect)得到的是view相对于整个硬件屏幕的Rect
		// 即绝对坐标，减去偏移，获得动画需要的坐标，即相对坐标
		// getGlobalVisibleRect(Rect,Point)中，Point获得的是view在它在
		// 父控件上的坐标与在屏幕上坐标的偏移
		expandedImageView.getGlobalVisibleRect(startBounds);
		findViewById(R.id.container)
				.getGlobalVisibleRect(finalBounds, globalOffset);
		startBounds.offset(-globalOffset.x, -globalOffset.y);
		finalBounds.offset(-globalOffset.x, -globalOffset.y);
		// Adjust the start bounds to be the same aspect ratio as the final
		// bounds using the "center crop" technique. This prevents undesirable
		// stretching during the animation. Also calculate the start scaling
		// factor (the end scaling factor is always 1.0).
		// 下面这段逻辑其实就是保持纵横比
		float startScale;
		// 如果结束图片的宽高比比开始图片的宽高比大
		// 就是结束时“视觉上”拉宽了（压扁了）图片
		if ((float) finalBounds.width() / finalBounds.height()
				> (float) startBounds.width() / startBounds.height()) {
			// Extend start bounds horizontally
			startScale = (float) startBounds.height() / finalBounds.height();
			float startWidth = startScale * finalBounds.width();
			float deltaWidth = (startWidth - startBounds.width()) / 2;
			startBounds.left -= deltaWidth;
			startBounds.right += deltaWidth;
		} else {
			// Extend start bounds vertically
			startScale = (float) startBounds.width() / finalBounds.width();
			float startHeight = startScale * finalBounds.height();
			float deltaHeight = (startHeight - startBounds.height()) / 2;
			startBounds.top -= deltaHeight;
			startBounds.bottom += deltaHeight;
		}
		final float startScaleFinal = startScale;
		// Animate the four positioning/sizing properties in parallel,
		// back to their original values.
		linearLayout_contains.setVisibility(View.VISIBLE);
		AnimatorSet set = new AnimatorSet();
//		set.play(ObjectAnimator.ofFloat(expandedImageView, View.X, startBounds.left, finalBounds.left))
//				.with(ObjectAnimator.ofFloat(expandedImageView, View.Y, startBounds.top, finalBounds.top))
//				.with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X, startScale, 1f))
//				.with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_Y, startScale, 1f))
//				.with(ObjectAnimator.ofFloat(linearLayout_contains, View.ALPHA, 1.0f, 0.0f))
//				.with(ObjectAnimator.ofFloat(toolbar, View.ALPHA, 1.0f, 0.0f));
		set.play(ObjectAnimator.ofFloat(expandedImageView, View.X, finalBounds.left,startBounds.left))
				.with(ObjectAnimator.ofFloat(expandedImageView, View.Y, finalBounds.top,startBounds.top))
				.with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X, 1f,startScaleFinal))
				.with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_Y, 1f,startScaleFinal))
				.with(ObjectAnimator.ofFloat(linearLayout_contains, View.ALPHA, 0.0f, 1.0f))
				.with(ObjectAnimator.ofFloat(linearLayout_bottom, View.ALPHA, 1.0f, 0.0f))
		.with(ObjectAnimator.ofFloat(imageView_thumView,View.ALPHA,0.0f,1.0f));
		set.setDuration(400);
		set.setInterpolator(new DecelerateInterpolator());
		set.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				expandedImageView.setVisibility(View.GONE);
				linearLayout_bottom.setVisibility(View.GONE);
				toolbar.setSubtitle(null);
				toolbar.setTitle(userHasSigned.getUser_name()+"的详细信息");
				mCurrentAnimator = null;
			}

			@Override
			public void onAnimationCancel(Animator animation) {
				linearLayout_contains.setAlpha(1f);
				expandedImageView.setVisibility(View.GONE);
				linearLayout_bottom.setVisibility(View.GONE);
				toolbar.setSubtitle(null);
				toolbar.setTitle(userHasSigned.getUser_name()+"的详细信息");
				mCurrentAnimator = null;
			}
		});
		set.start();
		mCurrentAnimator = set;
	}


	private void showTitleAndMenu(Point globalOffset) {
		linearLayout_contains.setVisibility(View.GONE);
		toolbar.setTitle(userHasSigned.getUser_name());
		toolbar.setSubtitle("注册时间：" + userHasSigned.getCreated_at());
		toolbar.setAlpha(1.0f);
		Rect finnal_toobar = new Rect();
		Rect finnal_liner_bottom = new Rect();
		toolbar.getGlobalVisibleRect(finnal_toobar);
		linearLayout_bottom.getGlobalVisibleRect(finnal_liner_bottom);
		Log.i(TAG, "showTitleAndMenu: " + finnal_liner_bottom.toString() + finnal_toobar.toString());
		finnal_toobar.offset(-globalOffset.x, -globalOffset.y);
		finnal_liner_bottom.offset(-globalOffset.x, -globalOffset.y);
		Log.i(TAG, "showTitleAndMenu: " + finnal_liner_bottom.toString() + finnal_toobar.toString());
		linearLayout_bottom.setAlpha(1.0f);
		AnimatorSet set1 = new AnimatorSet();
		set1.play(ObjectAnimator.ofFloat(toolbar, View.Y, -finnal_toobar.height(), 0.0f))
				//(1280+finnal_liner_bottom.top)/2
				.with(ObjectAnimator.ofFloat(linearLayout_bottom, View.Y, finnal_liner_bottom.bottom, finnal_liner_bottom.top));
		set1.setDuration(400);
		set1.start();
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
				Log.i(TAG, "startDeleteFace: 1");
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
				Log.i(TAG, "startDeleteFace: 2");
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
					Log.i(TAG, "handleMessage: test");
					showNormalDialog("温馨提示", "删除失败，请检查网络连接!!!", true, null, true);
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
					if (isViewPicture) {
						backTo();
					}
					initDate();
					setOnClick();
					break;
				case FinalUtil.REMOVE_USER_IO_EXCEPTION:
					dialog.dismiss();
					showNormalDialog("温馨提示", "删除失败，请检查网络连接!", true, null, true);
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
					setResult(1, intent);
					finish();
					return;
				case FinalUtil.DETECT_FAILED_IO_EXCEPTION:
					dialog.dismiss();
					showNormalDialog("温馨提示","当前网络连接失败，请稍后重试",true,null,true);
					break;
				case FinalUtil.DETECT_SUCCESS:
					Face face = (Face) msg.obj;
					dialog.dismiss();
					showDetailDialog(face);
					break;
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
		faceDetailAdapter = new FaceDetailAdapter(imageList);
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
		linearLayout_contains = (LinearLayout) findViewById(R.id.linearLayout_1);
		linearLayout_bottom = (LinearLayout) findViewById(R.id.linearLayout_bottom);
		delete_picture = (ImageView) findViewById(R.id.delete_picture);
		detail_picture = (ImageView) findViewById(R.id.detail_picture);
		expandedImageView = (ImageView) findViewById(R.id.expanded_image);


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
			return;
		}
		if (isViewPicture) {
			backTo();
			return;
		}
		super.onBackPressed();

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
