package com.henu.swface.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.henu.swface.Database.DatabaseAdapter;
import com.henu.swface.R;
import com.henu.swface.Utils.FaceSetUtil;
import com.henu.swface.Utils.FinalUtil;
import com.henu.swface.Utils.PictureUtil;
import com.henu.swface.VO.FaceSignIn;
import com.henu.swface.VO.SignLog;
import com.henu.swface.VO.UserHasSigned;
import com.henu.swface.Utils.FaceUtil;
import com.henu.swface.Utils.JSONUtil;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import okhttp3.Response;


/**
 * 离线视频流检测
 */
public class SignInActivity extends BaseVideoActivity {


	private final static String TAG = SignInActivity.class.getSimpleName();
	private AlertDialog dialog = null;
	private Button button_take_photos;
	private ImageView testImageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_video);
		initUI();
	}


	@SuppressLint("ShowToast")
	@SuppressWarnings("deprecation")
	private void initUI() {
		testImageView = (ImageView) findViewById(R.id.testImageView);
		button_take_photos = (Button) findViewById(R.id.button_take_photos);
		button_take_photos.setClickable(true);
		button_take_photos.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				button_take_photos.setClickable(false);
				AlertDialog.Builder builder = new AlertDialog.Builder(SignInActivity.this);
				builder.setCancelable(false);
				builder.setTitle("温馨提示：");
				builder.setMessage("正在比对人脸，请稍后......");
				dialog = builder.show();
				Camera.PictureCallback jpeg = new Camera.PictureCallback() {
					@Override
					public void onPictureTaken(byte[] data, Camera camera) {
						Log.i(TAG, "onPictureTaken_data.length: " + data.length);
						String filename = "waitForSearch.jpg";
						FileOutputStream fos;
						//获取拍到的图片Bitmap
						Bitmap bitmap_source = null;
						String pictureStoragePath = PictureUtil.getPictureStoragePath(getApplicationContext());
						File imageFile = new File(pictureStoragePath, filename);
						try {
							fos = new FileOutputStream(imageFile);
							if (data.length < 35000) {
								YuvImage image = new YuvImage(nv21, ImageFormat.NV21, PREVIEW_WIDTH, PREVIEW_HEIGHT, null);   //将NV21 data保存成YuvImage
								//图像压缩
								image.compressToJpeg(
										new Rect(0, 0, image.getWidth(), image.getHeight()),
										100, fos);
								Log.i(TAG, "onPictureTaken_data.length<20000: " + data.length);
								Log.i(TAG, "onPictureTaken_nv21.length: " + nv21.length);
								bitmap_source = PictureUtil.compressFacePhoto(imageFile.getAbsolutePath());
								fos = new FileOutputStream(imageFile);
								BufferedOutputStream bos = new BufferedOutputStream(fos);
								//旋转图片
								// 根据旋转角度，生成旋转矩阵
								Matrix matrix = new Matrix();
								if(mCameraId == Camera.CameraInfo.CAMERA_FACING_FRONT){
									matrix.postRotate(270);
								}else{
									matrix.postRotate(90);
								}
								Bitmap mBitmap1 = Bitmap.createBitmap(bitmap_source, 0, 0, bitmap_source.getWidth(), bitmap_source.getHeight(), matrix, true);
								testImageView.setImageBitmap(mBitmap1);
								boolean result = mBitmap1.compress(Bitmap.CompressFormat.JPEG, 100, bos);
								Log.i(TAG, "onPictureTaken_mBitmap1.compress: " + result);
							} else {
								bitmap_source = PictureUtil.compressFacePhoto(data);
								BufferedOutputStream bos = new BufferedOutputStream(fos);
								//旋转图片
								// 根据旋转角度，生成旋转矩阵
								Matrix matrix = new Matrix();
								if(mCameraId == Camera.CameraInfo.CAMERA_FACING_FRONT){
									matrix.postRotate(270);
								}else{
									matrix.postRotate(90);
								}
								Bitmap mBitmap1 = Bitmap.createBitmap(bitmap_source, 0, 0, bitmap_source.getWidth(), bitmap_source.getHeight(), matrix, true);
								testImageView.setImageBitmap(mBitmap1);
								mBitmap1.compress(Bitmap.CompressFormat.JPEG, 70, bos);
							}
							fos.flush();
							fos.close();
							searchByOuterId(imageFile);
						} catch (Exception e) {
							System.out.println("图片保存异常" + e.getMessage());
							e.printStackTrace();
						}
					}
				};
				mCamera.takePicture(null, null, jpeg);
			}
		});
	}

	private void searchByOuterId(final File imageFile) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				FaceSignIn faceSignIn = null;
				try {
					Response response = FaceSetUtil.searchFaceset(getApplicationContext(), FinalUtil.API_KEY, FinalUtil.API_Secret, imageFile);
					if (response != null && response.code() == 200) {
						JSONUtil jsonUtil = new JSONUtil();
						faceSignIn = jsonUtil.parseSearchFaceJSON(response.body().string());
					} else {
						if (response != null) {
							Log.e(TAG, "searchFaceset: " + response.code() + response.message());
						}
						Message message = new Message();
						message.arg1 = FinalUtil.SIGN_IN_FAILED_IOEXCEPTION;
						myhandler.sendMessage(message);
						return;
					}
				} catch (Exception e) {
					Log.e(TAG, "searchByOuterId: " + e.getMessage());
					Message message = new Message();
					message.arg1 = FinalUtil.SIGN_IN_FAILED_IOEXCEPTION;
					myhandler.sendMessage(message);
					e.printStackTrace();
				}
				if (faceSignIn == null || faceSignIn.getFace_token() == null || faceSignIn.getFace_token().length() < 2) {
					Message message = new Message();
					message.arg1 = FinalUtil.SIGN_IN_FAILED_FILENOTFIND;
					myhandler.sendMessage(message);
				} else {
					DatabaseAdapter db = new DatabaseAdapter(getApplicationContext());
					final UserHasSigned userHasSigned = db.findUserByFaceToken(faceSignIn, myhandler);
					final Message message = new Message();
					if (userHasSigned.getUser_name() != null) {
						SignLog signLog = new SignLog(getApplicationContext());
						signLog.setUser_name(userHasSigned.getUser_name());
						signLog.setConfidence(faceSignIn.getConfidence());
						signLog.setTime(System.currentTimeMillis());
						signLog.setFaceToken(faceSignIn.getFace_token());
						final FaceSignIn finalFaceSignIn = faceSignIn;
						signLog.save(new SaveListener<String>() {
							@Override
							public void done(String s, BmobException e) {
								if(e==null){
									message.arg1 = FinalUtil.SIGN_IN_SUCCESS;
									Bundle bundle = new Bundle();
									bundle.putFloat("confidence", finalFaceSignIn.getConfidence());
									bundle.putFloat("thresholds3", finalFaceSignIn.getThresholds3());
									bundle.putFloat("thresholds4", finalFaceSignIn.getThresholds4());
									bundle.putFloat("thresholds5", finalFaceSignIn.getThresholds5());
									message.setData(bundle);
									message.obj = userHasSigned;
									myhandler.sendMessage(message);
								}else{
									Message message = new Message();
									message.arg1 = FinalUtil.SIGN_IN_FAILED_IOEXCEPTION;
									myhandler.sendMessage(message);
								}
							}
						});

					}
				}
			}
		}).start();

	}


	private Handler myhandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			AlertDialog.Builder newdialog = new AlertDialog.Builder(SignInActivity.this);
			switch (msg.arg1) {
				case FinalUtil.SIGN_IN_SUCCESS:
					newdialog.setCancelable(false);
					newdialog.setTitle("温馨提示：");
					final UserHasSigned userHasSigned = (UserHasSigned) msg.obj;
					Bundle date = msg.getData();
					final float confidence = date.getFloat("confidence");
					float thresholds3 = date.getFloat("thresholds3");
					float thresholds4 = date.getFloat("thresholds4");
					float thresholds5 = date.getFloat("thresholds5");
					if (confidence > thresholds5) {
						LayoutInflater inflater = getLayoutInflater();
						View v = inflater.inflate(R.layout.dialog_sign_in_success, null);
						TextView username = (TextView) v.findViewById(R.id.dialog_username);
						TextView dialog_confidence = (TextView) v.findViewById(R.id.dialog_confidence);
						username.setText(userHasSigned.getUser_name());
						dialog_confidence.setText(String.valueOf(confidence));
						newdialog.setView(v);
						newdialog.setPositiveButton("完成", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								SignLog signLog = new SignLog(getApplicationContext());
								signLog.setTime(System.currentTimeMillis());
								signLog.setUser_name(userHasSigned.getUser_name());
								signLog.setConfidence(confidence);
								DatabaseAdapter db = new DatabaseAdapter(getApplicationContext());
								db.addLog_SignLog(signLog);
								dialog.dismiss();
								finish();
							}
						});
					} else if (confidence > thresholds3) {
						LayoutInflater inflater = getLayoutInflater();
						View v = inflater.inflate(R.layout.dialog_sign_in_success, null);
						TextView username = (TextView) v.findViewById(R.id.dialog_username);
						TextView dialog_confidence = (TextView) v.findViewById(R.id.dialog_confidence);
						username.setText(userHasSigned.getUser_name());
						dialog_confidence.setText(String.valueOf(confidence));
						newdialog.setView(v);
						newdialog.setPositiveButton("完成", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								SignLog signLog = new SignLog(getApplicationContext());
								signLog.setTime(System.currentTimeMillis());
								signLog.setUser_name(userHasSigned.getUser_name());
								signLog.setConfidence(confidence);
								DatabaseAdapter db = new DatabaseAdapter(getApplicationContext());
								db.addLog_SignLog(signLog);
								dialog.dismiss();
								finish();
							}
						});
						newdialog.setNegativeButton("结果不对？重新签到", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogInterface, int i) {
								dialogInterface.dismiss();
								initUI();
								mCameraId = CameraInfo.CAMERA_FACING_FRONT;
								openCamera();
								mCamera.startPreview();
							}
						});
					} else {
						newdialog.setMessage("签到失败！未在人脸库中检测到相应人脸\n该人脸尚未注册？");
						newdialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
								finish();
							}
						});
					}
					dialog.cancel();
					newdialog.show();
					break;
				case FinalUtil.SIGN_IN_FAILED_IOEXCEPTION:
					newdialog.setCancelable(false);
					newdialog.setTitle("温馨提示：");
					newdialog.setMessage("签到失败！\n请检查网络连接！！");
					newdialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							finish();
						}
					});
					dialog.cancel();
					newdialog.show();
					break;
				case FinalUtil.SIGN_IN_FAILED_FILENOTFIND:
					newdialog.setCancelable(false);
					newdialog.setTitle("温馨提示：");
					newdialog.setMessage("签到失败！\n未检测到人脸，拍照时请保证光线充足且不要逆光！！");
					newdialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							initUI();
							mCameraId = CameraInfo.CAMERA_FACING_FRONT;
							openCamera();
							mCamera.startPreview();
						}
					});
					dialog.dismiss();
					dialog = newdialog.show();
					break;
				case FinalUtil.SIGN_IN_FAILED_NOUSER:
					newdialog.setCancelable(false);
					newdialog.setTitle("温馨提示：");
					newdialog.setMessage("签到失败！\n该人脸尚未注册，请先注册再签到");
					newdialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							finish();
						}
					});
					dialog.cancel();
					newdialog.show();
					break;
//				case FinalUtil.ADDUSERSUCCESS:
//
//					break;
				default:
					break;
			}
		}
	};


	@Override
	protected void onResume() {
		super.onResume();

	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

}

