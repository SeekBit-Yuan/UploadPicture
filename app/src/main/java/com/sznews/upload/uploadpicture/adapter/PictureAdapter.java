package com.sznews.upload.uploadpicture.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.sznews.upload.uploadpicture.R;
import com.sznews.upload.uploadpicture.model.Picture;

import java.io.File;
import java.util.List;

public class PictureAdapter extends BaseAdapter {
    private Context context;
    private List<Picture> pictureList;

    public PictureAdapter(Context context, List<Picture> pictureList) {
        this.context = context;
        this.pictureList = pictureList;
    }

    @Override
    public int getCount() {
        return pictureList.size();
    }

    @Override
    public Object getItem(int position) {
        return pictureList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
//        LayoutInflater inflater = LayoutInflater.from(context);
//        View view = inflater.inflate(R.layout.picture_item, null);
//
//        ImageView ivPic = view.findViewById(R.id.ivPic);
//        Button delete = view.findViewById(R.id.button5);
//
//        Picture picture = pictureList.get(position);
//        String pic_path = picture.getpic_path();
////        String titleText = title.getText().toString().trim();
////        System.out.println("图片小标题："+ titleText);
////        picture.setTitle(titleText);
//
//        delete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                pictureList.remove(position);
//                PictureAdapter.this.notifyDataSetChanged();
//            }
//        });
//
//        //默认占位图设置
//        RequestOptions options = new RequestOptions()
//                .placeholder(R.drawable.upload)
//                .error(R.drawable.upload)
//                .fallback(R.drawable.upload);
//
//        Glide.with(context)
//                .load(pic_path)
//                .apply(options)
//                .into(ivPic);

        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.picture_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Picture picture = pictureList.get(position);

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pictureList.remove(position);
                PictureAdapter.this.notifyDataSetChanged();
            }
        });

        String pic_path = picture.getpic_path();

        //默认占位图设置
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.upload)
                .error(R.drawable.upload)
                .fallback(R.drawable.upload);

        //导入图片
        Glide.with(context)
                .load(pic_path)
                .apply(options)
                .into(holder.ivPic);

        if (holder.title.getTag() instanceof TextWatcher) {
            holder.title.removeTextChangedListener((TextWatcher) holder.title.getTag());
        }

        holder.title.setText(picture.getTitle());

        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s)) {
                    picture.setTitle("");
                } else {
                    picture.setTitle(s.toString());
                }
            }
        };

        holder.title.addTextChangedListener(watcher);
        holder.title.setTag(watcher);

        return convertView;
    }

    private class ViewHolder {
        private ImageView ivPic;
        private EditText title;
        private Button delete;

        public ViewHolder(View convertView) {
            title = convertView.findViewById(R.id.add_content);
            ivPic = convertView.findViewById(R.id.ivPic);
            delete = convertView.findViewById(R.id.button5);
        }
    }
}
