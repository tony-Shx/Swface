package com.henu.swface.activity;

import android.Manifest.permission;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

import com.henu.swface.Datebase.DatabaseAdapter;
import com.henu.swface.Datebase.DatabaseHelper;
import com.henu.swface.R;
import com.henu.swface.VO.Face;
import com.henu.swface.VO.UserHasSigned;
import com.henu.swface.Utils.FaceRect;
import com.henu.swface.Utils.FaceUtil;
import com.henu.swface.Utils.JSONUtil;
import com.henu.swface.Utils.ParseResult;
import com.iflytek.cloud.FaceDetector;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.util.Accelerometer;
import com.megvii.cloud.http.FaceSetOperate;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * 离线视频流检测
 */
public class VideoRecogniseActivity extends Activity {

	private final static String API_KEY = "lJsij4n8pYEj3bW-tSJqEhRgkdfHobC8";
	private final static String API_Secret = "i1H3kRBBzJ2Wo_1T-6RsbRmWgcHAREww";
	private final static int DETECT_SUCCESS = 0X110;
	private final static int DETECT_FAILED_IO_EXCEPTION = 0X111;
	private final static int DETECT_FAILED_NO_FACE = 0X112;
	private final static int DETECT_FAILED_LIMIT_EXCEEDED = 0X113;
	private static int fileUploadProgress = 0;
	private final static String TAG = VideoRecogniseActivity.class.getSimpleName();
	private SurfaceView mPreviewSurface;
	private SurfaceView mFaceSurface;
	private AlertDialog dialog = null;
	private Camera mCamera;
	private int mCameraId = CameraInfo.CAMERA_FACING_FRONT;
	// Camera nv21格式预览帧的尺寸，默认设置640*480
	private int PREVIEW_WIDTH = 640;
	private int PREVIEW_HEIGHT = 480;
	//照片存储宽高
	private int STORAGE_WIDTH = 900;
	private int STORAGE_HEIGHT = 1200;
	// 预览帧数据存储数组和缓存数组
	private byte[] nv21;
	private byte[] buffer;
	// 缩放矩阵
	private Matrix mScaleMatrix = new Matrix();
	// 加速度感应器，用于获取手机的朝向
	private Accelerometer mAcc;
	// FaceDetector对象，集成了离线人脸识别：人脸检测、视频流检测功能
	private FaceDetector mFaceDetector;
	private boolean mStopTrack;
	private Toast mToast;
	private long mLastClickTime;
	private int isAlign = 0;
	private Button button_take_photos;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		SpeechUtility.createUtility(this, "appid=58c8e767");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video);
		initUI();
		nv21 = new byte[PREVIEW_WIDTH * PREVIEW_HEIGHT * 2];
		buffer = new byte[PREVIEW_WIDTH * PREVIEW_HEIGHT * 2];
		mAcc = new Accelerometer(VideoRecogniseActivity.this);
		mFaceDetector = FaceDetector.createDetector(VideoRecogniseActivity.this, null);
	}


	private Callback mPreviewCallback = new Callback() {

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			closeCamera();
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			openCamera();
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
								   int height) {
			mScaleMatrix.setScale(width / (float) PREVIEW_HEIGHT, height / (float) PREVIEW_WIDTH);
		}
	};

	private void setSurfaceSize() {
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		int width = metrics.widthPixels;
		int height = (int) (width * PREVIEW_WIDTH / (float) PREVIEW_HEIGHT);
		LayoutParams params = new LayoutParams(width, height);
		params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		mPreviewSurface.setLayoutParams(params);
		mFaceSurface.setLayoutParams(params);
	}

	@SuppressLint("ShowToast")
	@SuppressWarnings("deprecation")
	private void initUI() {
		button_take_photos = (Button) findViewById(R.id.button_take_photos);
		mPreviewSurface = (SurfaceView) findViewById(R.id.sfv_preview);
		mFaceSurface = (SurfaceView) findViewById(R.id.sfv_face);
		mPreviewSurface.getHolder().addCallback(mPreviewCallback);
		mPreviewSurface.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		mFaceSurface.setZOrderOnTop(true);
		mFaceSurface.getHolder().setFormat(PixelFormat.TRANSLUCENT);

		// 点击SurfaceView，切换摄相头
		mFaceSurface.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 只有一个摄相头，不支持切换
				if (Camera.getNumberOfCameras() == 1) {
					showTip("只有后置摄像头，不能切换");
					return;
				}
				closeCamera();
				if (CameraInfo.CAMERA_FACING_FRONT == mCameraId) {
					mCameraId = CameraInfo.CAMERA_FACING_BACK;
				} else {
					mCameraId = CameraInfo.CAMERA_FACING_FRONT;
				}
				openCamera();
			}
		});

		// 长按SurfaceView 500ms后松开，摄相头聚集
		mFaceSurface.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						mLastClickTime = System.currentTimeMillis();
						break;
					case MotionEvent.ACTION_UP:
						if (System.currentTimeMillis() - mLastClickTime > 500) {
							mCamera.autoFocus(null);
							return true;
						}
						break;
					default:
						break;
				}
				return false;
			}
		});

		RadioGroup alignGruop = (RadioGroup) findViewById(R.id.align_mode);
		alignGruop.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				switch (arg1) {
					case R.id.detect:
						isAlign = 0;
						break;
					case R.id.align:
						isAlign = 1;
						break;
					default:
						break;
				}
			}
		});

		setSurfaceSize();
		mToast = Toast.makeText(VideoRecogniseActivity.this, "", Toast.LENGTH_SHORT);
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
			String filename = "waitForRename.jpg";
			FileOutputStream fos;
			try {
				fos = new FileOutputStream(
						new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) +
								"/" + filename));
				//获取拍到的图片Bitmap
				Bitmap bitmap_source = BitmapFactory.decodeByteArray(data, 0, data.length);
				BufferedOutputStream bos = new BufferedOutputStream(fos);
				// 根据旋转角度，生成旋转矩阵
				Matrix matrix = new Matrix();
				matrix.postRotate(270);
				//旋转图片
				Bitmap mBitmap1 = Bitmap.createBitmap(bitmap_source, 0, 0, bitmap_source.getWidth(), bitmap_source.getHeight(), matrix, true);
				//裁剪图片压缩图片大小为尺寸1200*900，便于传输存储
				Bitmap mBitmap = mBitmap1.createScaledBitmap(mBitmap1, STORAGE_WIDTH, STORAGE_HEIGHT, false);
				mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
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
		if (data != null) {
			username = data.getStringExtra("username");
		}
		if (resultCode == 1) {
			File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/waitForRename.jpg");
			if (file.exists()) {
				showNormalDialog(null, "正在注册新人脸，请保持网络通畅...", false, new ProgressBar(this), false);
				RegisterFace(file, username);
			} else {
				Toast.makeText(this, "错误代码-1，拍照文件保存失败，请检查磁盘空间！", Toast.LENGTH_LONG).show();
			}
			//mCamera.startPreview();//保存之后返回预览界面
		} else {
			File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/waitForRename.jpg");
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
				case DETECT_SUCCESS:
					dialog.dismiss();
					showNormalDialog("温馨提示：", "恭喜，注册成功！\n点击确定返回主界面", false, null, true);
					break;
				case DETECT_FAILED_IO_EXCEPTION:
					dialog.cancel();
					showNormalDialog("温馨提示：", "注册失败！\n请检查网络连接！！", false, null, true);
					break;
				case DETECT_FAILED_NO_FACE:
					AlertDialog.Builder newdialog = new AlertDialog.Builder(VideoRecogniseActivity.this);
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
				case  DETECT_FAILED_LIMIT_EXCEEDED:
					showNormalDialog("温馨提示：", "注册失败！\n当前服务器压力过大，请稍后再试！！", false, null, true);
					break;
				default:
					break;
			}
		}
	};


	private void RegisterFace(final File imageFile, final String username) {
		new Thread(new Runnable() {
			@Override
			public void run() {
					Response response = FaceUtil.detectFace(imageFile, API_KEY, API_Secret);
					Face face = null;
					int attempt = 0;
					while (response.code()!=200&&attempt<5){
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
						message.arg1 = DETECT_FAILED_NO_FACE;
						myhandler.sendMessage(message);
					} else {
						File newFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/" + face.getFace_token() + ".jpg");
						if (!imageFile.renameTo(newFile)) {
							showNormalDialog(null, "文件重命名失败，请检查磁盘空间是否充足？", false, null, true);
							return;
						}
						face.setImage_path(newFile.getAbsolutePath());
						DatabaseAdapter db = new DatabaseAdapter(getApplicationContext());
						db.addFace_Faces(face);
						UserHasSigned userHasSigned = new UserHasSigned(VideoRecogniseActivity.this);
						userHasSigned.setUser_name(username);
						userHasSigned.setFace_token1(face.getFace_token());
						//上传图片到BMOB数据库
						boolean result = updateImageFile(newFile,userHasSigned,db);
						if(!result){
							Message message = new Message();
							message.arg1 = DETECT_FAILED_IO_EXCEPTION;
							myhandler.sendMessage(message);
							return;
						}
						ArrayList<Face> face_list = db.findAll_Faces();
						ArrayList<UserHasSigned> user_HasSigned_face = db.findAllUser_User();
						for (Face face1 : face_list) {
							Log.v("faceList:", face1.toString());
						}
						for (UserHasSigned userHasSigned1 : user_HasSigned_face) {
							Log.v("userList:", userHasSigned1.toString());
						}
						attempt = 0;
						while (fileUploadProgress!=100&&attempt<150){
							attempt++;
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						if(fileUploadProgress==100){
							//创建FaceSet集合，并将人脸加入集合
							SharedPreferences sp = getSharedPreferences("login", Context.MODE_PRIVATE);
							String outerId = sp.getString("username", "default");
								attempt = 0;
								while (response.code() != 200&&attempt<5){
									response = FaceUtil.createFaceSet(API_KEY, API_Secret, "default", outerId, face.getFace_token());
									String JSON2 = null;
									try {
										JSON2 = response.body().string();
									} catch (IOException e) {
										e.printStackTrace();
									}
									Log.i(TAG, "faceSetOperate.createFaceSet(): " + JSON2);
									attempt++;
									try {
										Thread.sleep(50);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
								}
								if(response.code()==200){
									SharedPreferences preferences = getSharedPreferences("detectFace",Context.MODE_PRIVATE);
									SharedPreferences.Editor editor =  preferences.edit();
									editor.remove("lastUpdateObjectId");
									editor.commit();
									Message message = new Message();
									message.arg1 = DETECT_SUCCESS;
									myhandler.sendMessage(message);
								}else{
									clearBMOBDate();
								}
						}else{
							Message message = new Message();
							message.arg1 = DETECT_FAILED_IO_EXCEPTION;
							myhandler.sendMessage(message);
						}
					}

			}
		}).start();
	}

	private void clearBMOBDate() {
		SharedPreferences preferences = getSharedPreferences("detectFace",Context.MODE_PRIVATE);
		String objectId = preferences.getString("lastUpdateObjectId",null);
		if(objectId!=null){
			UserHasSigned userHasSigned = new UserHasSigned(this);
			userHasSigned.setObjectId(objectId);
			userHasSigned.delete(new UpdateListener() {
				@Override
				public void done(BmobException e) {
				  if(e==null){
					  Message message = new Message();
					  message.arg1 = DETECT_FAILED_LIMIT_EXCEEDED;
					  myhandler.sendMessage(message);
				  }else{
					  Message message = new Message();
					  message.arg1 = DETECT_FAILED_IO_EXCEPTION;
					  myhandler.sendMessage(message);
				  }
				}
			});
		}else{
			Message message = new Message();
			message.arg1 = DETECT_FAILED_LIMIT_EXCEEDED;
			myhandler.sendMessage(message);
		}
	}

	private boolean updateImageFile(File imageFile, final UserHasSigned userHasSigned, final DatabaseAdapter db) {
		final BmobFile file = new BmobFile(imageFile);
		final boolean[] updateSuccess = {false};
		file.uploadblock(new UploadFileListener() {
			@Override
			public void done(BmobException e) {
				if(e==null){
					Log.i(TAG, "file.uploadblock:success ");
					userHasSigned.setFace_url1(file.getFileUrl());
					if(db.addUser_User(userHasSigned)){
						updateSuccess[0] = true;
					}else{
						updateSuccess[0] = false;
					}
				}else{
					Log.e(TAG, "file.uploadblock:failed ",e );
					Message message = new Message();
					message.arg1 = DETECT_FAILED_IO_EXCEPTION;
					myhandler.sendMessage(message);
				}
			}

			@Override
			public void onProgress(Integer value) {
				super.onProgress(value);
				Log.i(TAG, "onProgress: "+value);
				fileUploadProgress = value;
			}
		});
		int attemp = 0;
		while (!updateSuccess[0]&&attemp<150){
			attemp++;
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return updateSuccess[0];
	}


	private void openCamera() {
		if (null != mCamera) {
			return;
		}

		if (!checkCameraPermission()) {
			showTip("摄像头权限未打开，请打开后再试");
			mStopTrack = true;
			return;
		}

		// 只有一个摄相头，打开后置
		if (Camera.getNumberOfCameras() == 1) {
			mCameraId = CameraInfo.CAMERA_FACING_BACK;
		}

		try {
			mCamera = Camera.open(mCameraId);
			if (CameraInfo.CAMERA_FACING_FRONT == mCameraId) {
				showTip("前置摄像头已开启，点击可切换");
			} else {
				showTip("后置摄像头已开启，点击可切换");
			}
		} catch (Exception e) {
			e.printStackTrace();
			closeCamera();
			return;
		}

		Parameters params = mCamera.getParameters();
		params.setPreviewFormat(ImageFormat.NV21);
		params.setPreviewSize(PREVIEW_WIDTH, PREVIEW_HEIGHT);
		mCamera.setParameters(params);

		// 设置显示的偏转角度，大部分机器是顺时针90度，某些机器需要按情况设置
		mCamera.setDisplayOrientation(90);
		mCamera.setPreviewCallback(new PreviewCallback() {

			@Override
			public void onPreviewFrame(byte[] data, Camera camera) {
				System.arraycopy(data, 0, nv21, 0, data.length);
			}
		});

		try {
			mCamera.setPreviewDisplay(mPreviewSurface.getHolder());
			mCamera.startPreview();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (mFaceDetector == null) {
			/**
			 * 离线视频流检测功能需要单独下载支持离线人脸的SDK
			 * 请开发者前往语音云官网下载对应SDK
			 */
			// 创建单例失败，与 21001 错误为同样原因，参考 http://bbs.xfyun.cn/forum.php?mod=viewthread&tid=9688
			showTip("创建对象失败，请确认 libmsc.so 放置正确，\n 且有调用 createUtility 进行初始化");
		}
	}

	private void closeCamera() {
		if (null != mCamera) {
			mCamera.setPreviewCallback(null);
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
		}
	}

	private boolean checkCameraPermission() {
		int status = checkPermission(permission.CAMERA, Process.myPid(), Process.myUid());
		return PackageManager.PERMISSION_GRANTED == status;

	}

	@Override
	protected void onResume() {
		super.onResume();

		if (null != mAcc) {
			mAcc.start();
		}

		mStopTrack = false;
		new Thread(new Runnable() {

			@Override
			public void run() {
				while (!mStopTrack) {
					if (null == nv21) {
						continue;
					}

					synchronized (nv21) {
						System.arraycopy(nv21, 0, buffer, 0, nv21.length);
					}

					// 获取手机朝向，返回值0,1,2,3分别表示0,90,180和270度
					int direction = Accelerometer.getDirection();
					boolean frontCamera = (CameraInfo.CAMERA_FACING_FRONT == mCameraId);
					// 前置摄像头预览显示的是镜像，需要将手机朝向换算成摄相头视角下的朝向。
					// 转换公式：a' = (360 - a)%360，a为人眼视角下的朝向（单位：角度）
					if (frontCamera) {
						// SDK中使用0,1,2,3,4分别表示0,90,180,270和360度
						direction = (4 - direction) % 4;
					}

					if (mFaceDetector == null) {
						/**
						 * 离线视频流检测功能需要单独下载支持离线人脸的SDK
						 * 请开发者前往语音云官网下载对应SDK
						 */
						// 创建单例失败，与 21001 错误为同样原因，参考 http://bbs.xfyun.cn/forum.php?mod=viewthread&tid=9688
						showTip("创建对象失败，请确认 libmsc.so 放置正确，\n 且有调用 createUtility 进行初始化");
						break;
					}

					String result = mFaceDetector.trackNV21(buffer, PREVIEW_WIDTH, PREVIEW_HEIGHT, isAlign, direction);
					//Log.d(TAG, "result:" + result);

					FaceRect[] faces = ParseResult.parseResult(result);

					Canvas canvas = mFaceSurface.getHolder().lockCanvas();
					if (null == canvas) {
						continue;
					}

					canvas.drawColor(0, PorterDuff.Mode.CLEAR);
					canvas.setMatrix(mScaleMatrix);

					if (faces == null || faces.length <= 0) {
						mFaceSurface.getHolder().unlockCanvasAndPost(canvas);
						continue;
					}

					if (null != faces && frontCamera == (CameraInfo.CAMERA_FACING_FRONT == mCameraId)) {
						for (FaceRect face : faces) {
							face.bound = FaceUtil.RotateDeg90(face.bound, PREVIEW_WIDTH, PREVIEW_HEIGHT);
							if (face.point != null) {
								for (int i = 0; i < face.point.length; i++) {
									face.point[i] = FaceUtil.RotateDeg90(face.point[i], PREVIEW_WIDTH, PREVIEW_HEIGHT);
								}
							}
							FaceUtil.drawFaceRect(canvas, face, PREVIEW_WIDTH, PREVIEW_HEIGHT,
									frontCamera, false);
						}
					} else {
						Log.d(TAG, "faces:0");
					}

					mFaceSurface.getHolder().unlockCanvasAndPost(canvas);
				}
			}
		}).start();
	}

	@Override
	protected void onPause() {
		super.onPause();
		closeCamera();
		if (null != mAcc) {
			mAcc.stop();
		}
		mStopTrack = true;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (null != mFaceDetector) {
			// 销毁对象
			mFaceDetector.destroy();
		}
	}

	private void showTip(final String str) {
		mToast.setText(str);
		mToast.show();
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

