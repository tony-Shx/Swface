package com.henu.swface;

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
import com.henu.swface.VO.Face;
import com.henu.swface.VO.User;
import com.henu.swface.util.FaceRect;
import com.henu.swface.util.FaceUtil;
import com.henu.swface.util.JSONUtil;
import com.henu.swface.util.ParseResult;
import com.iflytek.cloud.FaceDetector;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.util.Accelerometer;
import com.megvii.cloud.http.CommonOperate;
import com.megvii.cloud.http.FaceOperate;
import com.megvii.cloud.http.FaceSetOperate;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * 离线视频流检测
 */
public class SignInActivity extends Activity {

	private final static String API_KEY = "lJsij4n8pYEj3bW-tSJqEhRgkdfHobC8";
	private final static String API_Secret = "i1H3kRBBzJ2Wo_1T-6RsbRmWgcHAREww";
	private final static int SIGN_IN_SUCCESS = 0x200;
	private final static int SIGN_IN_FAILED_IOEXCEPTION = 0x201;
	private final static int SIGN_IN_FAILED_FILENOTFIND = 0x202;
	private final static String TAG = SignInActivity.class.getSimpleName();
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
		mAcc = new Accelerometer(SignInActivity.this);
		mFaceDetector = FaceDetector.createDetector(SignInActivity.this, null);
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
		mToast = Toast.makeText(SignInActivity.this, "", Toast.LENGTH_SHORT);

		button_take_photos.setClickable(true);
		button_take_photos.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				button_take_photos.setClickable(false);
				Camera.PictureCallback jpeg = new Camera.PictureCallback() {
					@Override
					public void onPictureTaken(byte[] data, Camera camera) {
						try {
							String filename = "waitForSearch.jpg";
							File imageFile = new File(
									Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) +
											"/" + filename);
							FileOutputStream fos = new FileOutputStream(imageFile);
							Bitmap mBitmap = compressPicture(data);
							BufferedOutputStream bos = new BufferedOutputStream(fos);
							mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
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
				OkHttpClient client = new OkHttpClient();
				RequestBody requestBody = postBody(imageFile);
				Request request = new Request.Builder().url("https://api-cn.faceplusplus.com/facepp/v3/search")
						.post(requestBody).build();
				Response response = null;
				try {
					response = client.newCall(request).execute();
					Log.i(TAG, "searchByOuterId: " + response.body().string());
				} catch (Exception e) {
					Log.e(TAG, "searchByOuterId: " + e.getMessage());
					e.printStackTrace();
				}finally {

				}
			}
		}).start();

	}

	protected RequestBody postBody(File file) {
		// 设置请求体
		MediaType MEDIA_TYPE_JPG = MediaType.parse("image/jpg");
		RequestBody body = MultipartBody.create(MEDIA_TYPE_JPG, file);
		MultipartBody.Builder builder = new MultipartBody.Builder();
		builder.setType(MultipartBody.FORM);
		//这里是 封装上传图片参数
		builder.addFormDataPart("api_key", API_KEY);
		builder.addFormDataPart("api_secret", API_Secret);
		builder.addFormDataPart("image_file", file.getName(), body);
		SharedPreferences sp = getSharedPreferences("login", Context.MODE_PRIVATE);
		String outerId = sp.getString("username", "default");
		builder.addFormDataPart("outer_id", outerId);
		return builder.build();
	}


	private Bitmap compressPicture(byte[] data) {
		//获取拍到的图片Bitmap
		Bitmap bitmap_source = BitmapFactory.decodeByteArray(data, 0, data.length);
		// 根据旋转角度，生成旋转矩阵
		Matrix matrix = new Matrix();
		matrix.postRotate(270);
		//旋转图片
		Bitmap mBitmap1 = Bitmap.createBitmap(bitmap_source, 0, 0, bitmap_source.getWidth(), bitmap_source.getHeight(), matrix, true);
		//裁剪图片压缩图片大小为尺寸1200*900，便于传输存储
		return mBitmap1.createScaledBitmap(mBitmap1, STORAGE_WIDTH, STORAGE_HEIGHT, false);
	}

	private Handler myhandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			AlertDialog.Builder newdialog = new AlertDialog.Builder(SignInActivity.this);
			switch (msg.arg1) {
				case SIGN_IN_SUCCESS:
					newdialog.setCancelable(false);
					newdialog.setTitle("温馨提示：");
					newdialog.setMessage("恭喜，注册成功！\n点击确定返回主界面");
					newdialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							finish();
						}
					});
					dialog.cancel();
					newdialog.show();
					break;
				case SIGN_IN_FAILED_IOEXCEPTION:
					newdialog.setCancelable(false);
					newdialog.setTitle("温馨提示：");
					newdialog.setMessage("注册失败！\n请检查网络连接！！");
					newdialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							finish();
						}
					});
					dialog.cancel();
					newdialog.show();
					break;
				case SIGN_IN_FAILED_FILENOTFIND:
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
				default:
					break;
			}
		}
	};


//	private void RegisterFace(final File imageFile, final String username) {
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				//CommonOperate commonOperate = new CommonOperate(API_KEY,API_Secret,false);
//				// 参数为本地图片文件二进制数组
//				//byte[] image = getBytes(imageFile);   // readImageFile函数仅为示例
//				OkHttpClient client = new OkHttpClient();
//				RequestBody requestBody = postBody(imageFile);
//				Request request = new Request.Builder().url("https://api-cn.faceplusplus.com/facepp/v3/detect")
//						.post(requestBody).build();
//				try {
//					Response response = client.newCall(request).execute();
//					String JSON = response.body().string();
//					JSONUtil jsonUtil = new JSONUtil();
//					Face face = jsonUtil.parseDetectFaceJSON(JSON);
//					if (face == null) {
//						Message message = new Message();
//						message.arg1 = DETECT_FAILED_NOFACE;
//						myhandler.sendMessage(message);
//					} else {
//						face.setImage_path(imageFile.getAbsolutePath());
//						DatabaseAdapter db = new DatabaseAdapter(getApplicationContext());
//						db.addFace_Faces(face);
//						User user = new User();
//						user.setUser_name(username);
//						user.setFace_token1(face.getFace_token());
//						db.addUser_User(user);
//						SharedPreferences sp = getSharedPreferences("login", Context.MODE_PRIVATE);
//						String outerId = sp.getString("username","default");
//						FaceSetOperate faceSetOperate = new FaceSetOperate(API_KEY,API_Secret,false);
//						try {
//							com.megvii.cloud.http.Response response1 = faceSetOperate.createFaceSet("默认分组",outerId,null,face.getFace_token(),null,1);
//							String JSON1 = new String(response1.getContent(),"UTF-8");
//							Log.i(TAG, "faceSetOperate.createFaceSet(): "+JSON1);
//						} catch (Exception e) {
//							e.printStackTrace();
//							Log.e(TAG, "faceSetOperate.createFaceSet(): ", e);
//							Message message = new Message();
//							message.arg1 = DETECT_FAILED_IOEXCEPTION;
//							myhandler.sendMessage(message);
//							return;
//						}
//						ArrayList<Face> face_list = db.findAll_Faces();
//						ArrayList<User> user_face = db.findAllUser_User();
//						for (Face face1 : face_list) {
//							Log.v("faceList:", face1.toString());
//						}
//						for (User user1: user_face) {
//							Log.v("userList:", user1.toString());
//						}
//						Message message = new Message();
//						message.arg1 = DETECT_SUCCESS;
//						myhandler.sendMessage(message);
//					}
//				} catch (IOException e) {
//					Message message = new Message();
//					message.arg1 = DETECT_FAILED_IOEXCEPTION;
//					myhandler.sendMessage(message);
//					e.printStackTrace();
//				}
//			}
//		}).start();
//	}

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
					Log.d(TAG, "result:" + result);

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

}

