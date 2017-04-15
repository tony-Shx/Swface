package com.henu.swface.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FaceUtil {
	public final static int REQUEST_PICTURE_CHOOSE = 1;
	public final static int REQUEST_CAMERA_IMAGE = 2;
	public final static int REQUEST_CROP_IMAGE = 3;
	public final static int STORAGE_WIDTH = 480;
	public final static int STORAGE_HEIGHT = 800;

	/***
	 * 裁剪图片
	 * @param activity Activity
	 * @param uri 图片的Uri
	 */
	public static void cropPicture(Activity activity, Uri uri) {
		Intent innerIntent = new Intent("com.android.camera.action.CROP");
		innerIntent.setDataAndType(uri, "image/*");
		innerIntent.putExtra("crop", "true");// 才能出剪辑的小方框，不然没有剪辑功能，只能选取图片
		innerIntent.putExtra("aspectX", 1); // 放大缩小比例的X
		innerIntent.putExtra("aspectY", 1);// 放大缩小比例的X   这里的比例为：   1:1
		innerIntent.putExtra("outputX", 320);  //这个是限制输出图片大小
		innerIntent.putExtra("outputY", 320);
		innerIntent.putExtra("return-data", true);
		// 切图大小不足输出，无黑框
		innerIntent.putExtra("scale", true);
		innerIntent.putExtra("scaleUpIfNeeded", true);
		File imageFile = new File(getImagePath(activity.getApplicationContext()));
		innerIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile));
		innerIntent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		activity.startActivityForResult(innerIntent, REQUEST_CROP_IMAGE);
	}

	/**
	 * 保存裁剪的图片的路径
	 *
	 * @return
	 */
	public static String getImagePath(Context context) {
		String path;

		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			path = context.getFilesDir().getAbsolutePath();
		} else {
			path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/msc/";
		}

		if (!path.endsWith("/")) {
			path += "/";
		}

		File folder = new File(path);
		if (folder != null && !folder.exists()) {
			folder.mkdirs();
		}
		path += "ifd.jpg";
		return path;
	}

	/**
	 * 读取图片属性：旋转的角度
	 *
	 * @param path 图片绝对路径
	 * @return degree 旋转角度
	 */
	public static int readPictureDegree(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
				case ExifInterface.ORIENTATION_ROTATE_90:
					degree = 90;
					break;
				case ExifInterface.ORIENTATION_ROTATE_180:
					degree = 180;
					break;
				case ExifInterface.ORIENTATION_ROTATE_270:
					degree = 270;
					break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}

	/**
	 * 旋转图片
	 *
	 * @param angle  旋转角度
	 * @param bitmap 原图
	 * @return bitmap 旋转后的图片
	 */
	public static Bitmap rotateImage(int angle, Bitmap bitmap) {
		// 图片旋转矩阵
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		// 得到旋转后的图片
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
				bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		return resizedBitmap;
	}

	/**
	 * 在指定画布上将人脸框出来
	 *
	 * @param canvas      给定的画布
	 * @param face        需要绘制的人脸信息
	 * @param width       原图宽
	 * @param height      原图高
	 * @param frontCamera 是否为前置摄像头，如为前置摄像头需左右对称
	 * @param DrawOriRect 可绘制原始框，也可以只画四个角
	 */
	static public void drawFaceRect(Canvas canvas, FaceRect face, int width, int height, boolean frontCamera, boolean DrawOriRect) {
		if (canvas == null) {
			return;
		}

		Paint paint = new Paint();
		paint.setColor(Color.rgb(255, 203, 15));
		int len = (face.bound.bottom - face.bound.top) / 8;
		if (len / 8 >= 2) paint.setStrokeWidth(len / 8);
		else paint.setStrokeWidth(2);

		Rect rect = face.bound;

		if (frontCamera) {
			int top = rect.top;
			rect.top = width - rect.bottom;
			rect.bottom = width - top;
		}

		if (DrawOriRect) {
			paint.setStyle(Style.STROKE);
			canvas.drawRect(rect, paint);
		} else {
			int drawl = rect.left - len;
			int drawr = rect.right + len;
			int drawu = rect.top - len;
			int drawd = rect.bottom + len;

			canvas.drawLine(drawl, drawd, drawl, drawd - len, paint);
			canvas.drawLine(drawl, drawd, drawl + len, drawd, paint);
			canvas.drawLine(drawr, drawd, drawr, drawd - len, paint);
			canvas.drawLine(drawr, drawd, drawr - len, drawd, paint);
			canvas.drawLine(drawl, drawu, drawl, drawu + len, paint);
			canvas.drawLine(drawl, drawu, drawl + len, drawu, paint);
			canvas.drawLine(drawr, drawu, drawr, drawu + len, paint);
			canvas.drawLine(drawr, drawu, drawr - len, drawu, paint);
		}

		if (face.point != null) {
			for (Point p : face.point) {
				if (frontCamera) {
					p.y = width - p.y;
				}
				canvas.drawPoint(p.x, p.y, paint);
			}
		}
	}

	/**
	 * 将矩形随原图顺时针旋转90度
	 *
	 * @param r      待旋转的矩形
	 * @param width  输入矩形对应的原图宽
	 * @param height 输入矩形对应的原图高
	 * @return 旋转后的矩形
	 */
	static public Rect RotateDeg90(Rect r, int width, int height) {
		int left = r.left;
		r.left = height - r.bottom;
		r.bottom = r.right;
		r.right = height - r.top;
		r.top = left;
		return r;
	}

	/**
	 * 将点随原图顺时针旋转90度
	 *
	 * @param p      待旋转的点
	 * @param width  输入点对应的原图宽
	 * @param height 输入点对应的原图宽
	 * @return 旋转后的点
	 */
	static public Point RotateDeg90(Point p, int width, int height) {
		int x = p.x;
		p.x = height - p.y;
		p.y = x;
		return p;
	}

	public static Bitmap adjustPhotoRotation(Bitmap bm, final int orientationDegree)
	{
		Matrix m = new Matrix();
		m.setRotate(orientationDegree, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
		float targetX, targetY;
		if (orientationDegree == 90) {
			targetX = bm.getHeight();
			targetY = 0;
		} else {
			targetX = bm.getHeight();
			targetY = bm.getWidth();
		}

		final float[] values = new float[9];
		m.getValues(values);

		float x1 = values[Matrix.MTRANS_X];
		float y1 = values[Matrix.MTRANS_Y];

		m.postTranslate(targetX - x1, targetY - y1);
		Bitmap bm1 = Bitmap.createBitmap(bm.getHeight(), bm.getWidth(), Bitmap.Config.ARGB_8888);
		Paint paint = new Paint();
		Canvas canvas = new Canvas(bm1);
		canvas.drawBitmap(bm, m, paint);

		return bm1;
	}

	public static String getPictureStoragePath(Context context){
		String extr = Environment.getExternalStorageDirectory().toString();
		File mFolder = new File(extr + "/Swface");
		if (!mFolder.exists()) {
			mFolder.mkdir();
		}

		return mFolder.getAbsolutePath();
		//return context.getFilesDir().getAbsolutePath();
	}

	public static void compressFacePhoto(String imagePath, File storageFile) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(imagePath, options);
		options.inSampleSize = calculateInSampleSize(options, STORAGE_WIDTH, STORAGE_HEIGHT);
		options.inJustDecodeBounds = false;
		Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(storageFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		BufferedOutputStream bos = null;
		if (fos != null) {
			bos = new BufferedOutputStream(fos);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 70, bos);
			try {
				fos.flush();
				fos.close();
				bos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static Bitmap compressFacePhoto(String imagePath) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(imagePath, options);
		options.inSampleSize = calculateInSampleSize(options, STORAGE_WIDTH, STORAGE_HEIGHT);
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(imagePath, options);
	}

	public static Bitmap compressFacePhoto(byte[] data) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeByteArray(data,0,data.length,options);
		options.inSampleSize = calculateInSampleSize(options, STORAGE_WIDTH, STORAGE_HEIGHT);
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeByteArray(data,0,data.length,options);
	}

	//计算图片的缩放值
	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			final int heightRatio = Math.round((float) height / (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}
		return inSampleSize;
	}

	public static int getNumCores() {
		class CpuFilter implements FileFilter {
			@Override
			public boolean accept(File pathname) {
				if (Pattern.matches("cpu[0-9]", pathname.getName())) {
					return true;
				}
				return false;
			}
		}
		try {
			File dir = new File("/sys/devices/system/cpu/");
			File[] files = dir.listFiles(new CpuFilter());
			return files.length;
		} catch (Exception e) {
			e.printStackTrace();
			return 1;
		}
	}

	/**
	 * 保存Bitmap至本地
	 */
	public static void saveBitmapToFile(Context context, Bitmap bmp) {
		String file_path = getImagePath(context);
		File file = new File(file_path);
		FileOutputStream fOut;
		try {
			fOut = new FileOutputStream(file);
			bmp.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
			fOut.flush();
			fOut.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Response detectFace(File imageFile, String API_KEY, String API_Secret) {
		OkHttpClient client = new OkHttpClient();
		//封装请求体
		RequestBody requestBody = postBodyDetectFace(imageFile, API_KEY, API_Secret);
		Request request = new Request.Builder().url("https://api-cn.faceplusplus.com/facepp/v3/detect")
				.post(requestBody).build();
		Response response = null;
		try {
			response = client.newCall(request).execute();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return response;
	}

	public static Response createFaceSet(String API_KEY, String API_Secret, String display_name, String outer_id, String face_token) {
		OkHttpClient client = new OkHttpClient();
		//封装请求体
		RequestBody requestBody = postBodyCreateFaceSet(API_KEY, API_Secret, display_name, outer_id, face_token);
		Request request = new Request.Builder().url("https://api-cn.faceplusplus.com/facepp/v3/faceset/create")
				.post(requestBody).build();
		Response response = null;
		try {
			response = client.newCall(request).execute();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return response;
	}

	public static Response searchFaceset(Context context, String API_KEY, String API_Secret, File imageFile) {
		OkHttpClient client = new OkHttpClient();
		RequestBody requestBody = postBodySearchFaceSet(context, API_KEY, API_Secret, imageFile);
		Request request = new Request.Builder().url("https://api-cn.faceplusplus.com/facepp/v3/search")
				.post(requestBody).build();
		Response response = null;
		try {
			int attemp = 0;
			do {
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				response = client.newCall(request).execute();
				attemp++;
			} while (response.code() != 200 && attemp < 10);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return response;
	}

	private static RequestBody postBodyDetectFace(File file, String API_KEY, String API_Secret) {
		// 设置请求体
		MediaType MEDIA_TYPE_JPG = MediaType.parse("image/jpg");
		RequestBody body = MultipartBody.create(MEDIA_TYPE_JPG, file);
		MultipartBody.Builder builder = new MultipartBody.Builder();
		builder.setType(MultipartBody.FORM);
		builder.addFormDataPart("api_key", API_KEY);
		builder.addFormDataPart("api_secret", API_Secret);
		builder.addFormDataPart("image_file", file.getName(), body);
		//builder.addFormDataPart("return_landmark", "1");
		builder.addFormDataPart("return_attributes", "gender,age,smiling,headpose,facequality,blur,eyestatus,ethnicity");
		return builder.build();
	}

	private static RequestBody postBodyCreateFaceSet(String API_KEY, String API_Secret, String display_name, String outer_id, String face_token) {
		// 设置请求体
		MultipartBody.Builder builder = new MultipartBody.Builder();
		builder.setType(MultipartBody.FORM);
		builder.addFormDataPart("api_key", API_KEY);
		builder.addFormDataPart("api_secret", API_Secret);
		builder.addFormDataPart("display_name", display_name);
		builder.addFormDataPart("outer_id", outer_id);
		builder.addFormDataPart("face_tokens", face_token);
		builder.addFormDataPart("force_merge", "1");
		return builder.build();
	}

	protected static RequestBody postBodySearchFaceSet(Context context, String API_KEY, String API_Secret, File file) {
		// 设置请求体
		MediaType MEDIA_TYPE_JPG = MediaType.parse("image/jpg");
		RequestBody body = MultipartBody.create(MEDIA_TYPE_JPG, file);
		MultipartBody.Builder builder = new MultipartBody.Builder();
		builder.setType(MultipartBody.FORM);
		//这里是 封装上传图片参数
		builder.addFormDataPart("api_key", API_KEY);
		builder.addFormDataPart("api_secret", API_Secret);
		builder.addFormDataPart("image_file", file.getName(), body);
		SharedPreferences sp = context.getSharedPreferences("login", Context.MODE_PRIVATE);
		String outerId = sp.getString("username", "default");
		builder.addFormDataPart("outer_id", outerId);
		return builder.build();
	}
}
