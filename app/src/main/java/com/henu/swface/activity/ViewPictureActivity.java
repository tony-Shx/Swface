package com.henu.swface.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.henu.swface.R;
import com.henu.swface.Utils.PictureUtil;
import com.henu.swface.VO.UserHasSigned;
import com.squareup.picasso.Picasso;

import java.io.File;

public class ViewPictureActivity extends Activity {
	private LinearLayout linearLayout_image;
	private ImageView imageView_face;
	// 保存下当前动画类，以便可以随时结束动画
	private Animator mCurrentAnimator;
	//系统的短时长动画持续时间（单位ms）
	// 对于不易察觉的动画或者频繁发生的动画
	// 这个动画持续时间是最理想的
	private int mShortAnimationDuration;
	private String faceTokenAndUrl;
	private UserHasSigned userHasSigned;
	private Toolbar toolbar;
	private final static String TAG = ViewPictureActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_picture);
		Intent intent = getIntent();
		userHasSigned = (UserHasSigned) intent.getSerializableExtra("userHasSigned");
		faceTokenAndUrl = intent.getStringExtra("faceTokenAndUrl");
		imageView_face = (ImageView) findViewById(R.id.imageView_face);
		toolbar = (Toolbar) findViewById(R.id.toolbar_view_picture);
		toolbar.setNavigationIcon(R.mipmap.button_back);
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});
		toolbar.setTitle(userHasSigned.getUser_name()+"的人脸照片");
		toolbar.inflateMenu(R.menu.base_toolbar_menu);

		startViewPicture();
	}

	private void startViewPicture() {
		String[] face = faceTokenAndUrl.split("#");
		File file = new File(PictureUtil.getPictureStoragePath(null), face[0] + ".jpg");
		if (file.exists()) {
			Picasso.with(this).load(file).placeholder(R.mipmap.loading).into(imageView_face);
		} else {
			Picasso.with(this).load(face[1]).placeholder(R.mipmap.loading).into(imageView_face);
		}
		//zoomImage();
	}

//	private void zoomImage() {
//		Log.i(TAG, "zoomImage: ");
//		// 如果有动画在执行，立即取消，然后执行现在这个动画
//		if (mCurrentAnimator != null) {
//			mCurrentAnimator.cancel();
//		}
//		// 计算开始和结束位置的图片范围
//		final Rect startBounds = new Rect();
//		final Rect finalBounds = new Rect();
//		final Point globalOffset = new Point();
//
//		// 开始的范围就是ImageButton的范围，
//		// 结束的范围是容器（FrameLayout）的范围
//		// getGlobalVisibleRect(Rect)得到的是view相对于整个硬件屏幕的Rect
//		// 即绝对坐标，减去偏移，获得动画需要的坐标，即相对坐标
//		// getGlobalVisibleRect(Rect,Point)中，Point获得的是view在它在
//// 父控件上的坐标与在屏幕上坐标的偏移
//		view.getGlobalVisibleRect(startBounds);
//		linearLayout_image.setVisibility(View.VISIBLE);
//		int right = getApplicationContext().getResources().getDisplayMetrics().widthPixels;
//		int bottom = getApplicationContext().getResources().getDisplayMetrics().heightPixels;
//		finalBounds.set(0,0,right,bottom);
////		linearLayout_image.getGlobalVisibleRect(finalBounds, globalOffset);
////		startBounds.offset(-globalOffset.x, -globalOffset.y);
////		finalBounds.offset(-globalOffset.x, -globalOffset.y);
//		Log.i(TAG, "zoomImage: "+startBounds.toString()+finalBounds.toString()+globalOffset.toString());
//		// 下面这段逻辑其实就是保持纵横比
//		float startScale;
//		// 如果结束图片的宽高比比开始图片的宽高比大
//		// 就是结束时“视觉上”拉宽了（压扁了）图片
//		if ((float) finalBounds.width() / finalBounds.height() > (float) startBounds.width() / startBounds.height()) {
//			// Extend start bounds horizontally
//			startScale = (float) startBounds.height() / finalBounds.height();
//			float startWidth = startScale * finalBounds.width();
//			float deltaWidth = (startWidth - startBounds.width()) / 2;
//			startBounds.left -= deltaWidth;
//			startBounds.right += deltaWidth;
//		} else {
//			// Extend start bounds vertically
//			startScale = (float) startBounds.width() / finalBounds.width();
//			float startHeight = startScale * finalBounds.height();
//			float deltaHeight = (startHeight - startBounds.height()) / 2;
//			startBounds.top -= deltaHeight;
//			startBounds.bottom += deltaHeight;
//		}
//		// 隐藏小的图片，展示大的图片。当动画开始的时候，
//		// 要把大的图片发在小的图片的位置上
//		//小的设置透明
//		//thumbView.setAlpha(0f);
//		//大的可见
//		imageView_face.setVisibility(View.VISIBLE);
//		imageView_face.setPivotX(0f);
//		imageView_face.setPivotY(0f);
//
//		// Construct and run the parallel animation of the four translation and
//		// scale properties (X, Y, SCALE_X, and SCALE_Y).
//		AnimatorSet set = new AnimatorSet();
//		set.play(ObjectAnimator.ofFloat(imageView_face, View.X, startBounds.left, finalBounds.left))
//				.with(ObjectAnimator.ofFloat(imageView_face, View.Y, startBounds.top, finalBounds.top))
//				.with(ObjectAnimator.ofFloat(imageView_face, View.SCALE_X, startScale, 1f))
//				.with(ObjectAnimator.ofFloat(imageView_face, View.SCALE_Y, startScale, 1f));
//		set.setDuration(1000);
//		set.setInterpolator(new DecelerateInterpolator());
//		set.addListener(new AnimatorListenerAdapter() {
//			@Override
//			public void onAnimationEnd(Animator animation) {
//				mCurrentAnimator = null;
//				Log.i(TAG, "onAnimationEnd: ");
//			}
//
//			@Override
//			public void onAnimationCancel(Animator animation) {
//				mCurrentAnimator = null;
//				Log.i(TAG, "onAnimationEnd: ");
//			}
//
//			@Override
//			public void onAnimationStart(Animator animation) {
//				super.onAnimationStart(animation);
//				Log.i(TAG, "onAnimationStart: ");
//			}
//		});
//		set.start();
//		mCurrentAnimator = set;
//	}
}
