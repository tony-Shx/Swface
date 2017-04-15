package com.henu.swface.activity;

import java.io.IOException;
import android.Manifest.permission;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
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
import android.os.Process;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;
import com.henu.swface.R;
import com.henu.swface.Utils.FaceRect;
import com.henu.swface.Utils.FaceUtil;
import com.henu.swface.Utils.ParseResult;
import com.iflytek.cloud.FaceDetector;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.util.Accelerometer;

/**
 * Created by 宋浩祥 on 2017/4/15.
 */

public class BaseVideoActivity extends Activity {
        private final static String TAG = BaseVideoActivity.class.getSimpleName();
        private SurfaceView mPreviewSurface;
        private SurfaceView mFaceSurface;
        protected Camera mCamera;
        protected int mCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
        // Camera nv21格式预览帧的尺寸，默认设置640*480
        protected int PREVIEW_WIDTH = 640;
        protected int PREVIEW_HEIGHT = 480;
        // 预览帧数据存储数组和缓存数组
        protected byte[] nv21;
        protected byte[] buffer;
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

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            SpeechUtility.createUtility(this, "appid=58c8e767");
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_video);
            initUI();
            nv21 = new byte[PREVIEW_WIDTH * PREVIEW_HEIGHT * 2];
            buffer = new byte[PREVIEW_WIDTH * PREVIEW_HEIGHT * 2];
            mAcc = new Accelerometer(BaseVideoActivity.this);
            mFaceDetector = FaceDetector.createDetector(BaseVideoActivity.this, null);
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
                mScaleMatrix.setScale(width/(float)PREVIEW_HEIGHT, height/(float)PREVIEW_WIDTH);
            }
        };

        private void setSurfaceSize() {
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);

            int width = metrics.widthPixels;
            int height = (int) (width * PREVIEW_WIDTH / (float)PREVIEW_HEIGHT);
            RelativeLayout.LayoutParams params = new LayoutParams(width, height);
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP);

            mPreviewSurface.setLayoutParams(params);
            mFaceSurface.setLayoutParams(params);
        }

        @SuppressLint("ShowToast")
        @SuppressWarnings("deprecation")
        private void initUI() {
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
            mToast = Toast.makeText(BaseVideoActivity.this, "", Toast.LENGTH_SHORT);
        }

        protected void openCamera() {
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

            if(mFaceDetector == null) {
                /**
                 * 离线视频流检测功能需要单独下载支持离线人脸的SDK
                 * 请开发者前往语音云官网下载对应SDK
                 */
                // 创建单例失败，与 21001 错误为同样原因，参考 http://bbs.xfyun.cn/forum.php?mod=viewthread&tid=9688
                showTip( "创建对象失败，请确认 libmsc.so 放置正确，\n 且有调用 createUtility 进行初始化" );
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
                        boolean frontCamera = (Camera.CameraInfo.CAMERA_FACING_FRONT == mCameraId);
                        // 前置摄像头预览显示的是镜像，需要将手机朝向换算成摄相头视角下的朝向。
                        // 转换公式：a' = (360 - a)%360，a为人眼视角下的朝向（单位：角度）
                        if (frontCamera) {
                            // SDK中使用0,1,2,3,4分别表示0,90,180,270和360度
                            direction = (4 - direction)%4;
                        }

                        if(mFaceDetector == null) {
                            /**
                             * 离线视频流检测功能需要单独下载支持离线人脸的SDK
                             * 请开发者前往语音云官网下载对应SDK
                             */
                            // 创建单例失败，与 21001 错误为同样原因，参考 http://bbs.xfyun.cn/forum.php?mod=viewthread&tid=9688
                            showTip( "创建对象失败，请确认 libmsc.so 放置正确，\n 且有调用 createUtility 进行初始化" );
                            break;
                        }

                        String result = mFaceDetector.trackNV21(buffer, PREVIEW_WIDTH, PREVIEW_HEIGHT, isAlign, direction);
                        //Log.d(TAG, "result:"+result);

                        FaceRect[] faces = ParseResult.parseResult(result);

                        Canvas canvas = mFaceSurface.getHolder().lockCanvas();
                        if (null == canvas) {
                            continue;
                        }

                        canvas.drawColor(0, PorterDuff.Mode.CLEAR);
                        canvas.setMatrix(mScaleMatrix);

                        if( faces == null || faces.length <=0 ) {
                            mFaceSurface.getHolder().unlockCanvasAndPost(canvas);
                            continue;
                        }

                        if (null != faces && frontCamera == (Camera.CameraInfo.CAMERA_FACING_FRONT == mCameraId)) {
                            for (FaceRect face: faces) {
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
            if( null != mFaceDetector ){
                // 销毁对象
                mFaceDetector.destroy();
            }
        }

        private void showTip(final String str) {
            mToast.setText(str);
            mToast.show();
        }

    }
