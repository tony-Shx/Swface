package com.henu.swface.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.henu.swface.Adapter.FaceDetailAdapter;
import com.henu.swface.R;
import com.henu.swface.Utils.FaceUtil;
import com.henu.swface.Utils.RoundTransform;
import com.henu.swface.VO.UserHasSigned;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FaceDetailActivity extends Activity {
	private ImageView imageView_face_detail_head;
	private TextView textView_face_detail_name,textView_face_detail_register_time;
	private ImageButton button_face_detail_edit;
	private RecyclerView recyclerView_face_detail;
	private List<Uri> imageList = new ArrayList<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_face_detail);
		findView();
		initDate();


	}

	private void initDate() {
		Intent intent = getIntent();
		UserHasSigned userHasSigned = (UserHasSigned) intent.getSerializableExtra("userHasSigned");
		File imageFile = new File(FaceUtil.getPictureStoragePath(this),userHasSigned.getFace_token1()+".jpg");
		if(imageFile.exists()){
			Uri imageUri1 = Uri.fromFile(imageFile);
			imageList.add(imageUri1);
			Picasso.with(this).load(imageFile).transform(new RoundTransform()).centerCrop().placeholder(R.mipmap.loading).into(imageView_face_detail_head);
		}else{
			Uri imageUri1 = Uri.parse(userHasSigned.getFace_url1());
			imageList.add(imageUri1);
			Picasso.with(this).load(userHasSigned.getFace_url1()).transform(new RoundTransform()).centerCrop().resize(90,120).placeholder(R.mipmap.loading).into(imageView_face_detail_head);
		}
		textView_face_detail_name.setText(userHasSigned.getUser_name());
		textView_face_detail_register_time.setText(userHasSigned.getCreatedAt());
		String imageurl = userHasSigned.getFace_url2();
		if(imageurl!=null&&!imageurl.isEmpty()&&!imageurl.equals("")){
			Uri imageUrl2 = Uri.parse(imageurl);
			imageList.add(imageUrl2);
			imageurl = userHasSigned.getFace_url3();
			if(imageurl!=null&&!imageurl.isEmpty()&&!imageurl.equals("")){
				Uri imageUrl3 = Uri.parse(imageurl);
				imageList.add(imageUrl3);
				imageurl = userHasSigned.getFace_url4();
				if(imageurl!=null&&!imageurl.isEmpty()&&!imageurl.equals("")){
					Uri imageUrl4 = Uri.parse(imageurl);
					imageList.add(imageUrl4);
					imageurl = userHasSigned.getFace_url5();
					if(imageurl!=null&&!imageurl.isEmpty()&&!imageurl.equals("")){
						Uri imageUrl5 = Uri.parse(imageurl);
						imageList.add(imageUrl5);
					}
				}
			}
		}
		FaceDetailAdapter adapter = new FaceDetailAdapter(imageList);
		StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL);
		recyclerView_face_detail.setLayoutManager(layoutManager);
		recyclerView_face_detail.setAdapter(adapter);
	}

	private void findView() {
		imageView_face_detail_head = (ImageView) findViewById(R.id.imageView_face_detail_head);
		textView_face_detail_name = (TextView) findViewById(R.id.textView_face_detail_name);
		textView_face_detail_register_time = (TextView) findViewById(R.id.textView_face_detail_register_time);
		button_face_detail_edit = (ImageButton) findViewById(R.id.button_face_detail_edit);
		recyclerView_face_detail = (RecyclerView) findViewById(R.id.recyclerView_face_detail);
	}
}
