package com.henu.swface.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.henu.swface.R;
import com.henu.swface.VO.UserHasSigned;
import com.henu.swface.activity.AddFaceActivity;
import com.henu.swface.activity.RegisterFaceActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by 宋浩祥 on 2017/4/15.
 */

public class FaceDetailAdapter extends RecyclerView.Adapter<FaceDetailAdapter.ViewHolder> {

	private List<Uri> imageList;
	private UserHasSigned userHasSigned;
	private int imageView_width = -1;

	public FaceDetailAdapter(List<Uri> imageList,UserHasSigned userHasSigned) {
		this.imageList = imageList;
		this.userHasSigned = userHasSigned;
	}

	@Override
	public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
		final Context context = parent.getContext();
		//获取屏幕宽度便于动态改变imageview长宽
		if (imageView_width < 0) {
			imageView_width = (parent.getWidth() - 30) / 3;
		}
		View view = LayoutInflater.from(context).inflate(R.layout.recyclerview_face_detail_item, parent, false);
		final ViewHolder viewHolder = new ViewHolder(view);
		viewHolder.imageView_face_detail.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				int position = viewHolder.getAdapterPosition();
				if (position >= imageList.size()) {
					Intent intent = new Intent(context, AddFaceActivity.class);
					intent.putExtra("userHasSigned",userHasSigned);
					context.startActivity(intent);
				} else {

				}
			}
		});
		return viewHolder;
	}


	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		Context context = holder.itemView.getContext();
		if (position < imageList.size()) {
			Uri imagePath = imageList.get(position);
			Picasso.with(context).load(imagePath).placeholder(R.mipmap.loading).into(holder.imageView_face_detail);
		} else {
			Picasso.with(context).load(R.mipmap.add_face_picture).placeholder(R.mipmap.loading).into(holder.imageView_face_detail);
		}
		if (imageView_width > 0) {
			//动态改变imageview长宽使其保持正方形
			ViewGroup.LayoutParams ps = holder.imageView_face_detail.getLayoutParams();
			ps.height = imageView_width;
			holder.imageView_face_detail.setLayoutParams(ps);
		}
	}

	@Override
	public int getItemCount() {
		if (imageList.size() < 5) {
			return imageList.size() + 1;
		} else {
			return imageList.size();
		}
	}

	static class ViewHolder extends RecyclerView.ViewHolder {
		ImageView imageView_face_detail;

		public ViewHolder(View itemView) {
			super(itemView);
			imageView_face_detail = (ImageView) itemView.findViewById(R.id.imageView_face_detail);

		}
	}

}
