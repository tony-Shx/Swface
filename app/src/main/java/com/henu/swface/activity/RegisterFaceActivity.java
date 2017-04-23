package com.henu.swface.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.henu.swface.Database.BmobDataHelper;
import com.henu.swface.Database.DatabaseAdapter;
import com.henu.swface.R;
import com.henu.swface.Utils.FaceSetUtil;
import com.henu.swface.Utils.FaceUtil;
import com.henu.swface.Utils.FinalUtil;
import com.henu.swface.Utils.JSONUtil;
import com.henu.swface.Utils.PictureUtil;
import com.henu.swface.VO.Face;
import com.henu.swface.VO.UserHasSigned;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import okhttp3.Response;


/**
 * 离线视频流检测
 */
public class RegisterFaceActivity extends BaseVideoActivity {

	private final static String API_KEY = "lJsij4n8pYEj3bW-tSJqEhRgkdfHobC8";
	private final static String API_Secret = "i1H3kRBBzJ2Wo_1T-6RsbRmWgcHAREww";
	private static int fileUploadProgress = 0;
	private final static String TAG = RegisterFaceActivity.class.getSimpleName();
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
				mCamera.takePicture(null, null, jpeg);
			}
		});
	}

	Camera.PictureCallback jpeg = new Camera.PictureCallback() {
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			Log.i(TAG, "onPictureTaken_data.length: " + data.length);
			String filename = "waitForRename.jpg";
			FileOutputStream fos;
			//获取拍到的图片Bitmap
			Bitmap bitmap_source = null;
			String pictureStoragePath = PictureUtil.getPictureStoragePath(getApplicationContext());
			File f = new File(pictureStoragePath, filename);
			try {
				fos = new FileOutputStream(f);
				if (data.length < 35000) {
					YuvImage image = new YuvImage(nv21, ImageFormat.NV21, PREVIEW_WIDTH, PREVIEW_HEIGHT, null);   //将NV21 data保存成YuvImage
					//图像压缩
					image.compressToJpeg(
							new Rect(0, 0, image.getWidth(), image.getHeight()),
							100, fos);
					Log.i(TAG, "onPictureTaken_data.length<20000: " + data.length);
					Log.i(TAG, "onPictureTaken_nv21.length: " + nv21.length);
					bitmap_source = PictureUtil.compressFacePhoto(f.getAbsolutePath());
					fos = new FileOutputStream(f);
					BufferedOutputStream bos = new BufferedOutputStream(fos);
					//旋转图片
					// 根据旋转角度，生成旋转矩阵
					Matrix matrix = new Matrix();
					matrix.postRotate(270);
					Bitmap mBitmap1 = Bitmap.createBitmap(bitmap_source, 0, 0, bitmap_source.getWidth(), bitmap_source.getHeight(), matrix, true);
					testImageView.setImageBitmap(mBitmap1);
					boolean result = mBitmap1.compress(Bitmap.CompressFormat.JPEG, 100, bos);
					bos.close();
					Log.i(TAG, "onPictureTaken_mBitmap1.compress: " + result);
				} else {
					bitmap_source = PictureUtil.compressFacePhoto(data);
					BufferedOutputStream bos = new BufferedOutputStream(fos);
					//旋转图片
					// 根据旋转角度，生成旋转矩阵
					Matrix matrix = new Matrix();
					matrix.postRotate(270);
					Bitmap mBitmap1 = Bitmap.createBitmap(bitmap_source, 0, 0, bitmap_source.getWidth(), bitmap_source.getHeight(), matrix, true);
					testImageView.setImageBitmap(mBitmap1);
					mBitmap1.compress(Bitmap.CompressFormat.JPEG, 70, bos);
					bos.close();
				}
				//fos.write(data);
				fos.flush();
				fos.close();

				Log.i(TAG, "onPictureTaken: 图片保存成功");
				Intent intent = new Intent(getApplicationContext(), DialogInputNameActivity.class);
				startActivityForResult(intent, 0);
			} catch (Exception e) {
				System.out.println("图片保存异常" + e.getMessage());
				e.printStackTrace();
			}
		}
	};


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		String username = null;
		String pictureStoragePath = PictureUtil.getPictureStoragePath(getApplicationContext());
		if (data != null) {
			username = data.getStringExtra("username");
		}
		if (resultCode == 1) {
			File file = new File(pictureStoragePath, "waitForRename.jpg");
			if (file.exists()) {
				showNormalDialog(null, "正在注册新人脸，请保持网络通畅...", false, new ProgressBar(this), false);
				RegisterFace(file, username);
			} else {
				Toast.makeText(this, "错误代码-1，拍照文件保存失败，请检查磁盘空间！", Toast.LENGTH_LONG).show();
			}
			//mCamera.startPreview();//保存之后返回预览界面
		} else {
			File file = new File(pictureStoragePath + "/waitForRename.jpg");
			if (file.exists()) {
				if (file.delete()) {
					Toast.makeText(this, "已取消保存人脸", Toast.LENGTH_LONG).show();
					initUI();
					mCameraId = CameraInfo.CAMERA_FACING_FRONT;
					openCamera();
					mCamera.startPreview();
				} else {
					Toast.makeText(this, "请检查磁盘空间，人脸删除失败", Toast.LENGTH_LONG).show();
				}
			}
		}
	}

	private Handler myhandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.arg1) {
				case FinalUtil.DETECT_SUCCESS:
					dialog.dismiss();
					showNormalDialog("温馨提示：", "恭喜，注册成功！\n点击确定返回主界面", false, null, true);
					break;
				case FinalUtil.DETECT_FAILED_IO_EXCEPTION:
					dialog.cancel();
					showNormalDialog("温馨提示：", "注册失败！\n请检查网络连接！！", false, null, true);
					break;
				case FinalUtil.DETECT_FAILED_NO_FACE:
					AlertDialog.Builder newdialog = new AlertDialog.Builder(RegisterFaceActivity.this);
					newdialog.setCancelable(false);
					newdialog.setTitle("温馨提示：");
					newdialog.setMessage("注册失败！\n未检测到人脸，拍照时请保证光线充足且不要逆光！！");
					newdialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							initUI();
							mCameraId = CameraInfo.CAMERA_FACING_FRONT;
							openCamera();
							mCamera.startPreview();
						}
					});
					dialog.cancel();
					newdialog.show();
					break;
				case FinalUtil.DETECT_FAILED_LIMIT_EXCEEDED:
					showNormalDialog("温馨提示：", "注册失败！\n当前服务器压力过大，请稍后再试！！", false, null, true);
					break;
				case FinalUtil.UPDATE_PICTURE_SUCCESS:
					Bundle data = msg.getData();
					String faceToken = data.getString("faceToken");
					String objectId = data.getString("objectId");
					createFaceSet(faceToken, objectId);
					break;
				default:
					break;
			}
		}
	};

	private void createFaceSet(final String faceToken, final String objectID) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				//创建FaceSet集合，并将人脸加入集合
				SharedPreferences sp = getSharedPreferences("login", Context.MODE_PRIVATE);
				String outerId = sp.getString("username", "default");
				int attempt = 0;
				Response response1 = FaceSetUtil.createFaceSet(API_KEY, API_Secret, "default", outerId, faceToken);
				while (response1 != null && response1.code() != 200 && attempt < 10) {
					response1 = FaceSetUtil.createFaceSet(API_KEY, API_Secret, "default", outerId, faceToken);
					attempt++;
					Log.e(TAG, "createFaceSet_attempt: " + attempt);
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				if (response1 != null && response1.code() == 200) {
					String JSON2 = null;
					try {
						JSON2 = response1.body().string();
					} catch (IOException e) {
						e.printStackTrace();
					}

					Log.i(TAG, "faceSetOperate.createFaceSet(): " + JSON2);
					if (JSON2.contains("\"face_added\": 1")) {
						Message message = new Message();
						message.arg1 = FinalUtil.DETECT_SUCCESS;
						myhandler.sendMessage(message);
					}else{
						clearBMOBDate(objectID);
					}
				} else {
					clearBMOBDate(objectID);
				}
			}
		}).start();
	}

	private void RegisterFace(final File imageFile, final String username) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				Response response = FaceUtil.detectFace(imageFile, API_KEY, API_Secret);
				if (null == response) {
					Message message = new Message();
					message.arg1 = FinalUtil.DETECT_FAILED_IO_EXCEPTION;
					myhandler.sendMessage(message);
					return;
				}
				Face face = null;
				int attempt = 0;
				while (response.code() != 200 && attempt < 5) {
					response = FaceUtil.detectFace(imageFile, API_KEY, API_Secret);
					attempt++;
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						Log.e(TAG, "Thread.sleep: ", e);
						e.printStackTrace();
					}
				}
				String JSON = null;
				try {
					JSON = response.body().string();
				} catch (IOException e) {
					e.printStackTrace();
				}
				JSONUtil jsonUtil = new JSONUtil();
				face = jsonUtil.parseDetectFaceJSON(JSON);
				if (face == null) {
					Message message = new Message();
					message.arg1 = FinalUtil.DETECT_FAILED_NO_FACE;
					myhandler.sendMessage(message);
				} else {
					File newFile = new File(PictureUtil.getPictureStoragePath(getApplicationContext()), face.getFace_token() + ".jpg");
					if (!imageFile.renameTo(newFile)) {
						showNormalDialog(null, "文件重命名失败，请检查磁盘空间是否充足？", false, null, true);
						return;
					}
					face.setImage_path(newFile.getAbsolutePath());
					DatabaseAdapter db = new DatabaseAdapter(getApplicationContext());
					db.addFace_Faces(face);
					UserHasSigned userHasSigned = new UserHasSigned(RegisterFaceActivity.this);
					userHasSigned.setUser_name(username);
					userHasSigned.setFace_token1(face.getFace_token());
					//上传图片到BMOB数据库
					updateImageFile(newFile, userHasSigned, db);
				}
			}
		}).start();
	}

	private void clearBMOBDate(String objectID) {
		if (objectID != null) {
			UserHasSigned userHasSigned = new UserHasSigned(this);
			userHasSigned.setObjectId(objectID);
			userHasSigned.delete(new UpdateListener() {
				@Override
				public void done(BmobException e) {
					if (e == null) {
						Message message = new Message();
						message.arg1 = FinalUtil.DETECT_FAILED_LIMIT_EXCEEDED;
						myhandler.sendMessage(message);
					} else {
						Message message = new Message();
						message.arg1 = FinalUtil.DETECT_FAILED_IO_EXCEPTION;
						myhandler.sendMessage(message);
					}
				}
			});
		} else {
			Message message = new Message();
			message.arg1 = FinalUtil.DETECT_FAILED_LIMIT_EXCEEDED;
			myhandler.sendMessage(message);
		}
	}

	private void updateImageFile(File imageFile, final UserHasSigned userHasSigned, final DatabaseAdapter db) {
		final BmobFile file = new BmobFile(imageFile);
		file.uploadblock(new UploadFileListener() {
			@Override
			public void done(BmobException e) {
				if (e == null) {
					Log.i(TAG, "file.uploadblock:success ");
					userHasSigned.setFace_url1(file.getFileUrl());
					Log.i(TAG, "file.uploadblock:if ");
					dialog.setMessage("上传图片成功！");
					dialog.show();
					new Thread(new Runnable() {
						@Override
						public void run() {
							BmobDataHelper bmobDataHelper = new BmobDataHelper(RegisterFaceActivity.this, myhandler);
							bmobDataHelper.addUserHasSign(userHasSigned);
						}
					}).start();
				} else {
					Log.e(TAG, "file.uploadblock:failed ", e);
					Message message = new Message();
					message.arg1 = FinalUtil.DETECT_FAILED_IO_EXCEPTION;
					myhandler.sendMessage(message);
				}
			}

			@Override
			public void onProgress(Integer value) {
				super.onProgress(value);
				Log.i(TAG, "onProgress: " + value);
				fileUploadProgress = value;
			}
		});

	}


	@Override
	protected void onResume() {
		super.onResume();
		button_take_photos.setClickable(true);
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
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
					finish();
				}
			});
		}
		dialog = builder.show();
	}

}

