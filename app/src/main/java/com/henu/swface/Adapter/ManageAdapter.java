package com.henu.swface.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.henu.swface.R;
import com.henu.swface.Utils.FaceUtil;
import com.henu.swface.VO.UserHasSigned;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

/**
 * Created by 宋浩祥 on 2017/4/15.
 */

public class ManageAdapter extends RecyclerView.Adapter<ManageAdapter.ViewHolder> {

    private List<UserHasSigned> userHasSignedList;

    public ManageAdapter(List<UserHasSigned> userHasSignedList) {
        this.userHasSignedList = userHasSignedList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycleview_manage_item,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Context context = holder.itemView.getContext();
        UserHasSigned user = userHasSignedList.get(position);
        File file = new File(FaceUtil.getPictureStoragePath(null),user.getFace_token1()+".jpg");
        if(file.exists()){
            Picasso.with(context).load(file).centerCrop().placeholder(R.mipmap.loading).resize(60,90).into(holder.imageView_face_item);
        }else{
            Picasso.with(context).load(user.getFace_url1()).placeholder(R.mipmap.loading).centerCrop().resize(60,90).into(holder.imageView_face_item);
        }
        holder.recyclerView_manage_item_face_name.setText(user.getUser_name());
        holder.recyclerView_manage_item_register_time.setText(user.getCreatedAt());

    }

    @Override
    public int getItemCount() {
        return userHasSignedList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView_face_item;
        TextView recyclerView_manage_item_face_name,recyclerView_manage_item_register_time;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView_face_item = (ImageView) itemView.findViewById(R.id.imageView_face_item);
            recyclerView_manage_item_face_name = (TextView) itemView.findViewById(R.id.recyclerView_manage_item_face_name);
            recyclerView_manage_item_register_time = (TextView) itemView.findViewById(R.id.recyclerView_manage_item_register_time);

        }
    }

}
