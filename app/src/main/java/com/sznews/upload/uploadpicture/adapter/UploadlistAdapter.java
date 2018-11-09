package com.sznews.upload.uploadpicture.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.sznews.upload.uploadpicture.R;
import com.sznews.upload.uploadpicture.activity.ThemeInfoActivity;
import com.sznews.upload.uploadpicture.model.Picture;
import com.sznews.upload.uploadpicture.model.UploadTheme;

import java.util.List;

public class UploadlistAdapter extends BaseAdapter {
    private Context context;
    private List<UploadTheme> uploadList;

    public UploadlistAdapter(Context context, List<UploadTheme> uploadList) {
        this.context = context;
        this.uploadList = uploadList;
    }

    @Override
    public int getCount() {
        return uploadList.size();
    }

    @Override
    public Object getItem(int position) {
        return uploadList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.uploadlist_item, null);

        ImageView ivPic = view.findViewById(R.id.uploadlist_pic);
        TextView theme = view.findViewById(R.id.uploadlist_theme);
        TextView date = view.findViewById(R.id.uploadlist_date);
        final ImageView play = view.findViewById(R.id.uploadlist_button);
        final TextView state = view.findViewById(R.id.uploadlist_statetext);

        UploadTheme uploadTheme = uploadList.get(position);
        String path = uploadTheme.getPath();
        theme.setText(uploadTheme.getTheme());
        date.setText(uploadTheme.getDate());

        if (uploadTheme.getState() == "0") {
            play.setImageDrawable(context.getResources().getDrawable(R.drawable.finish));
            play.setClickable(false);
            state.setText("已完成");
            //点击跳转详情页查看
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ThemeInfoActivity.class);
                    intent.putExtra("code", "0");
                    context.startActivity(intent);
                }
            });
        } else if (uploadTheme.getState() == "1") {
            play.setImageDrawable(context.getResources().getDrawable(R.drawable.uploading));
            play.setClickable(false);
            state.setText("上传中");
        } else if (uploadTheme.getState() == "2") {
            play.setImageDrawable(context.getResources().getDrawable(R.drawable.play));
            state.setText("已暂停");
            play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    play.setImageDrawable(context.getResources().getDrawable(R.drawable.uploading));
                    state.setText("上传中");
                }
            });
        }

//        //点击跳转详情页查看
//        view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(context, ThemeInfoActivity.class);
//                intent.putExtra("code", "0");
//                context.startActivity(intent);
//            }
//        });

        //默认占位图设置
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.upload)
                .error(R.drawable.upload)
                .fallback(R.drawable.upload);

        Glide.with(context)
                .load(path)
                .apply(options)
                .into(ivPic);

        return view;
    }


    @Override
    public boolean isEnabled(int position) {
        return false;
    }
}
