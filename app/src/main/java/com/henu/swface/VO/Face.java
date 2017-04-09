package com.henu.swface.VO;

import cn.bmob.v3.BmobObject;

/**
 * Created by 宋浩祥 on 2017/3/7.
 */

public class Face {

	private String image_id,request_id,gender,glass,ethnicity,face_token,image_path;
	private int id,time_used,age,face_rectangle_width,face_rectangle_top,face_rectangle_left, face_rectangle_high;
	private float lNormalGlassEyeOpen, lNoGlassEyeClose,left_occlusion, leftNoGlassEyeOpen
	, lNormalGlassEyeClose,left_dark_glasses, rNormalGlassEyeOpen, rNoGlassEyeClose,
			right_occlusion, rNoGlassEyeOpen, rNormalGlassEyeClose,right_dark_glasses,
			headpose_yaw_angle,headpose_pitch_angle,headpose_roll_angle,blurness,smile,facequality;

	public Face() {
	}

	@Override
	public String toString() {
		return "Face{" +
				"image_id='" + image_id + '\'' +
				", request_id='" + request_id + '\'' +
				", gender='" + gender + '\'' +
				", glass='" + glass + '\'' +
				", ethnicity='" + ethnicity + '\'' +
				", face_token='" + face_token + '\'' +
				", image_path='" + image_path + '\'' +
				", id=" + id +
				", time_used=" + time_used +
				", age=" + age +
				", face_rectangle_width=" + face_rectangle_width +
				", face_rectangle_top=" + face_rectangle_top +
				", face_rectangle_left=" + face_rectangle_left +
				", face_rectangle_high=" + face_rectangle_high +
				", lNormalGlassEyeOpen=" + lNormalGlassEyeOpen +
				", lNoGlassEyeClose=" + lNoGlassEyeClose +
				", left_occlusion=" + left_occlusion +
				", leftNoGlassEyeOpen=" + leftNoGlassEyeOpen +
				", lNormalGlassEyeClose=" + lNormalGlassEyeClose +
				", left_dark_glasses=" + left_dark_glasses +
				", rNormalGlassEyeOpen=" + rNormalGlassEyeOpen +
				", rNoGlassEyeClose=" + rNoGlassEyeClose +
				", right_occlusion=" + right_occlusion +
				", rNoGlassEyeOpen=" + rNoGlassEyeOpen +
				", rNormalGlassEyeClose=" + rNormalGlassEyeClose +
				", right_dark_glasses=" + right_dark_glasses +
				", headpose_yaw_angle=" + headpose_yaw_angle +
				", headpose_pitch_angle=" + headpose_pitch_angle +
				", headpose_roll_angle=" + headpose_roll_angle +
				", blurness=" + blurness +
				", smile=" + smile +
				", facequality=" + facequality +
				'}';
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}


	public String getImage_path() {
		return image_path;
	}

	public void setImage_path(String image_path) {
		this.image_path = image_path;
	}

	public String getFace_token() {
		return face_token;
	}

	public void setFace_token(String face_token) {
		this.face_token = face_token;
	}

	public String getImage_id() {
		return image_id;
	}

	public void setImage_id(String image_id) {
		this.image_id = image_id;
	}

	public String getRequest_id() {
		return request_id;
	}

	public void setRequest_id(String request_id) {
		this.request_id = request_id;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getGlass() {
		return glass;
	}

	public void setGlass(String glass) {
		this.glass = glass;
	}

	public String getEthnicity() {
		return ethnicity;
	}

	public void setEthnicity(String ethnicity) {
		this.ethnicity = ethnicity;
	}

	public int getTime_used() {
		return time_used;
	}

	public void setTime_used(int time_used) {
		this.time_used = time_used;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public int getFace_rectangle_width() {
		return face_rectangle_width;
	}

	public void setFace_rectangle_width(int face_rectangle_width) {
		this.face_rectangle_width = face_rectangle_width;
	}

	public int getFace_rectangle_top() {
		return face_rectangle_top;
	}

	public void setFace_rectangle_top(int face_rectangle_top) {
		this.face_rectangle_top = face_rectangle_top;
	}

	public int getFace_rectangle_left() {
		return face_rectangle_left;
	}

	public void setFace_rectangle_left(int face_rectangle_left) {
		this.face_rectangle_left = face_rectangle_left;
	}

	public int getFace_rectangle_high() {
		return face_rectangle_high;
	}

	public void setFace_rectangle_high(int face_rectangle_high) {
		this.face_rectangle_high = face_rectangle_high;
	}

	public float getlNormalGlassEyeOpen() {
		return lNormalGlassEyeOpen;
	}

	public void setlNormalGlassEyeOpen(double lNormalGlassEyeOpen) {
		this.lNormalGlassEyeOpen = (float) lNormalGlassEyeOpen;
	}

	public float getlNoGlassEyeClose() {
		return lNoGlassEyeClose;
	}

	public void setlNoGlassEyeClose(double lNoGlassEyeClose) {
		this.lNoGlassEyeClose = (float) lNoGlassEyeClose;
	}

	public float getLeft_occlusion() {
		return left_occlusion;
	}

	public void setLeft_occlusion(double left_occlusion) {
		this.left_occlusion = (float) left_occlusion;
	}

	public float getLeftNoGlassEyeOpen() {
		return leftNoGlassEyeOpen;
	}

	public void setLeftNoGlassEyeOpen(double leftNoGlassEyeOpen) {
		this.leftNoGlassEyeOpen = (float) leftNoGlassEyeOpen;
	}

	public float getlNormalGlassEyeClose() {
		return lNormalGlassEyeClose;
	}

	public void setlNormalGlassEyeClose(double lNormalGlassEyeClose) {
		this.lNormalGlassEyeClose = (float) lNormalGlassEyeClose;
	}

	public float getLeft_dark_glasses() {
		return left_dark_glasses;
	}

	public void setLeft_dark_glasses(double left_dark_glasses) {
		this.left_dark_glasses = (float) left_dark_glasses;
	}

	public float getrNormalGlassEyeOpen() {
		return rNormalGlassEyeOpen;
	}

	public void setrNormalGlassEyeOpen(double rNormalGlassEyeOpen) {
		this.rNormalGlassEyeOpen = (float) rNormalGlassEyeOpen;
	}

	public float getrNoGlassEyeClose() {
		return rNoGlassEyeClose;
	}

	public void setrNoGlassEyeClose(double rNoGlassEyeClose) {
		this.rNoGlassEyeClose = (float) rNoGlassEyeClose;
	}

	public float getRight_occlusion() {
		return right_occlusion;
	}

	public void setRight_occlusion(double right_occlusion) {
		this.right_occlusion = (float) right_occlusion;
	}

	public float getrNoGlassEyeOpen() {
		return rNoGlassEyeOpen;
	}

	public void setrNoGlassEyeOpen(double rNoGlassEyeOpen) {
		this.rNoGlassEyeOpen = (float) rNoGlassEyeOpen;
	}

	public float getrNormalGlassEyeClose() {
		return rNormalGlassEyeClose;
	}

	public void setrNormalGlassEyeClose(double rNormalGlassEyeClose) {
		this.rNormalGlassEyeClose = (float) rNormalGlassEyeClose;
	}

	public float getRight_dark_glasses() {
		return right_dark_glasses;
	}

	public void setRight_dark_glasses(double right_dark_glasses) {
		this.right_dark_glasses = (float) right_dark_glasses;
	}

	public float getHeadpose_yaw_angle() {
		return headpose_yaw_angle;
	}

	public void setHeadpose_yaw_angle(double headpose_yaw_angle) {
		this.headpose_yaw_angle = (float) headpose_yaw_angle;
	}

	public float getHeadpose_pitch_angle() {
		return headpose_pitch_angle;
	}

	public void setHeadpose_pitch_angle(double headpose_pitch_angle) {
		this.headpose_pitch_angle = (float)headpose_pitch_angle;
	}

	public float getHeadpose_roll_angle() {
		return headpose_roll_angle;
	}

	public void setHeadpose_roll_angle(double headpose_roll_angle) {
		this.headpose_roll_angle = (float)headpose_roll_angle;
	}

	public float getBlurness() {
		return blurness;
	}

	public void setBlurness(double blurness) {
		this.blurness = (float) blurness;
	}

	public float getSmile() {
		return smile;
	}

	public void setSmile(double smile) {
		this.smile = (float) smile;
	}

	public float getFacequality() {
		return facequality;
	}

	public void setFacequality(double facequality) {
		this.facequality = (float) facequality;
	}
}
