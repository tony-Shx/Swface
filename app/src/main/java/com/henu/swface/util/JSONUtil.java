package com.henu.swface.util;

import com.henu.swface.VO.Face;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2017/3/24.
 */

public class JSONUtil {

	public JSONUtil() {
	}

	public Face parseDetectFaceJSON(String JSON){
		System.out.println("!!!!!!!!!"+JSON);
		Face face = new Face();
		try {
			JSONObject jsonObject = new JSONObject(JSON);
			face.setImage_id(jsonObject.get("image_id").toString());
			face.setRequest_id(jsonObject.get("request_id").toString());
			face.setTime_used(jsonObject.getInt("time_used"));
			JSONArray faceArray = jsonObject.getJSONArray("faces");
			if(faceArray==null){
				return null;
			}
			JSONObject faceObject = faceArray.getJSONObject(0);
			face.setFace_rectangle_width(faceObject.getJSONObject("face_rectangle").getInt("width"));
			face.setFace_rectangle_top(faceObject.getJSONObject("face_rectangle").getInt("top"));
			face.setFace_rectangle_left(faceObject.getJSONObject("face_rectangle").getInt("left"));
			face.setFace_rectangle_height(faceObject.getJSONObject("face_rectangle").getInt("height"));
			JSONObject attributesObject = faceObject.getJSONObject("attributes");
			face.setGender(attributesObject.getJSONObject("gender").get("value").toString());
			face.setAge(attributesObject.getJSONObject("age").getInt("value"));
			face.setGlass(attributesObject.getJSONObject("glass").get("value").toString());
			face.setHeadpose_yaw_angle(attributesObject.getJSONObject("headpose").getDouble("yaw_angle"));
			face.setHeadpose_pitch_angle(attributesObject.getJSONObject("headpose").getDouble("pitch_angle"));
			face.setHeadpose_roll_angle(attributesObject.getJSONObject("headpose").getDouble("roll_angle"));
			face.setSmile(attributesObject.getJSONObject("smile").getDouble("value"));
			face.setFacequality(attributesObject.getJSONObject("facequality").getDouble("value"));
			face.setEthnicity(attributesObject.getJSONObject("ethnicity").get("value").toString());
			face.setFace_token(faceObject.get("face_token").toString());
			JSONObject eyestatusObject = attributesObject.getJSONObject("eyestatus");
			face.setLeft_normal_glass_eye_open(eyestatusObject.getJSONObject("left_eye_status").getDouble("normal_glass_eye_open"));
			face.setLeft_no_glass_eye_close(eyestatusObject.getJSONObject("left_eye_status").getDouble("no_glass_eye_close"));
			face.setLeft_occlusion(eyestatusObject.getJSONObject("left_eye_status").getDouble("occlusion"));
			face.setLeft_no_glass_eye_open(eyestatusObject.getJSONObject("left_eye_status").getDouble("no_glass_eye_open"));
			face.setLeft_normal_glass_eye_close(eyestatusObject.getJSONObject("left_eye_status").getDouble("normal_glass_eye_close"));
			face.setLeft_dark_glasses(eyestatusObject.getJSONObject("left_eye_status").getDouble("dark_glasses"));
			face.setRight_normal_glass_eye_open(eyestatusObject.getJSONObject("right_eye_status").getDouble("normal_glass_eye_open"));
			face.setRight_no_glass_eye_close(eyestatusObject.getJSONObject("right_eye_status").getDouble("no_glass_eye_close"));
			face.setRight_occlusion(eyestatusObject.getJSONObject("right_eye_status").getDouble("occlusion"));
			face.setRight_no_glass_eye_open(eyestatusObject.getJSONObject("right_eye_status").getDouble("no_glass_eye_open"));
			face.setRight_normal_glass_eye_close(eyestatusObject.getJSONObject("right_eye_status").getDouble("normal_glass_eye_close"));
			face.setRight_dark_glasses(eyestatusObject.getJSONObject("right_eye_status").getDouble("dark_glasses"));
			face.setBlurness(attributesObject.getJSONObject("blur").getJSONObject("blurness").getDouble("value"));
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		return face;
	}



}
