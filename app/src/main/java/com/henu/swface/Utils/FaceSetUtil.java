package com.henu.swface.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/4/22.
 */

public class FaceSetUtil {
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

	public static Response removeFaceFormFaceSet(String API_KEY, String API_Secret, String outer_id, String face_token){
		OkHttpClient client = new OkHttpClient();
		RequestBody requestBody = postBodyRemoveFaceFormFaceSet(API_KEY, API_Secret, outer_id,face_token);
		Request request = new Request.Builder().url("https://api-cn.faceplusplus.com/facepp/v3/faceset/removeface")
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

	private static RequestBody postBodyRemoveFaceFormFaceSet(String API_KEY, String API_Secret, String outer_id, String face_token){
		// 设置请求体
		MultipartBody.Builder builder = new MultipartBody.Builder();
		builder.setType(MultipartBody.FORM);
		builder.addFormDataPart("api_key", API_KEY);
		builder.addFormDataPart("api_secret", API_Secret);
		builder.addFormDataPart("outer_id", outer_id);
		builder.addFormDataPart("face_tokens", face_token);
		return builder.build();
	}
}
