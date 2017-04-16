package com.henu.swface.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.henu.swface.R;
import com.henu.swface.Utils.FaceUtil;
import com.henu.swface.Utils.RoundTransform;
import com.henu.swface.VO.UserHasSigned;
import com.henu.swface.activity.FaceDetailActivity;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

/**
 * Created by 宋浩祥 on 2017/4/15.
 */

public class ManageAdapter extends RecyclerView.Adapter<ManageAdapter.ViewHolder> {

	private List<UserHasSigned> userHasSignedList;
	private int imageView_width = -1;

	public ManageAdapter(List<UserHasSigned> userHasSignedList) {
		this.userHasSignedList = userHasSignedList;
	}

	@Override
	public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
		final Context context = parent.getContext();
		//获取屏幕宽度便于动态改变imageview长宽
		if(imageView_width<0){
			imageView_width = (parent.getWidth()-60) / 3;
		}
		View view = LayoutInflater.from(context).inflate(R.layout.recycleview_manage_item, parent, false);
		final ViewHolder viewHolder = new ViewHolder(view);
		viewHolder.imageView_face_item.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent  =new Intent(context, FaceDetailActivity.class);
				int position = viewHolder.getAdapterPosition();
				intent.putExtra("userHasSigned",userHasSignedList.get(position));
				context.startActivity(intent);
			}
		});
		viewHolder.recyclerView_manage_item_face_name.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent  =new Intent(context, FaceDetailActivity.class);
				int position = viewHolder.getAdapterPosition();
				intent.putExtra("userHasSigned",userHasSignedList.get(position));
				context.startActivity(intent);
			}
		});
		return viewHolder;
	}



	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		Context context = holder.itemView.getContext();
		UserHasSigned user = userHasSignedList.get(position);
		File file = new File(FaceUtil.getPictureStoragePath(null), user.getFace_token1() + ".jpg");
		if (file.exists()) {
			Picasso.with(context).load(file).centerCrop().transform(new RoundTransform()).placeholder(R.mipmap.loading).resize(120, 180).into(holder.imageView_face_item);
		} else {
			Picasso.with(context).load(user.getFace_url1()).centerCrop().placeholder(R.mipmap.loading).resize(120, 180).transform(new RoundTransform()).into(holder.imageView_face_item);
		}
		if (imageView_width > 0) {
			//动态改变imageview长宽使其保持正方形
			ViewGroup.LayoutParams ps = holder.imageView_face_item.getLayoutParams();
			ps.height = imageView_width;
			holder.imageView_face_item.setLayoutParams(ps);
		}
		holder.recyclerView_manage_item_face_name.setText(user.getUser_name());

	}

	@Override
	public int getItemCount() {
		return userHasSignedList.size();
	}

	static class ViewHolder extends RecyclerView.ViewHolder {
		ImageView imageView_face_item;
		TextView recyclerView_manage_item_face_name;

		public ViewHolder(View itemView) {
			super(itemView);
			imageView_face_item = (ImageView) itemView.findViewById(R.id.imageView_face_item);
			recyclerView_manage_item_face_name = (TextView) itemView.findViewById(R.id.recyclerView_manage_item_face_name);

		}
	}

}
