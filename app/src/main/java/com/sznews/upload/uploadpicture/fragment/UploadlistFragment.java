package com.sznews.upload.uploadpicture.fragment;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.sznews.upload.uploadpicture.R;
import com.sznews.upload.uploadpicture.activity.UploadActivity;
import com.sznews.upload.uploadpicture.adapter.UploadlistAdapter;
import com.sznews.upload.uploadpicture.model.UploadTheme;

import java.util.ArrayList;
import java.util.List;

import static com.sznews.upload.uploadpicture.utils.DrawableToString.DrawableToString;

public class UploadlistFragment extends Fragment {

    private UploadlistAdapter uploadlistAdapter;
    private List<UploadTheme> uploadThemeList = new ArrayList<>();
    //0表示通过UploadActivity按钮点击切换进入该fragment，1表示通过NewBuildActivity上传按钮进入，code为1时触发上传方法
    private int code = 0;
    private String state = "0";//"0"：已上传成功，"1"：正在上传，"2"：暂停上传等待中

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_uploadlist, null);

        LinearLayout mEmptyTv = view.findViewById(R.id.listView_tip);
        ListView listView = view.findViewById(R.id.uploadlist_listview);

        Drawable drawable = getResources().getDrawable(R.drawable.upload);
        String defaultpath = DrawableToString(drawable);
        if(code == 1){
            uploadThemeList.add(new UploadTheme("主题1","2018-11-08 14:30:00",defaultpath,"0"));
            uploadThemeList.add(new UploadTheme("主题2","2018-11-08 14:30:00",defaultpath,"1"));
            uploadThemeList.add(new UploadTheme("主题3","2018-11-08 14:30:00",defaultpath,"2"));
        }
        uploadlistAdapter = new UploadlistAdapter1(getActivity(),uploadThemeList);
        listView.setAdapter(uploadlistAdapter);
        listView.setEmptyView(mEmptyTv);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        code = ((UploadActivity) context).getCode();//获取activity传递过来的参数
    }

    class UploadlistAdapter1 extends UploadlistAdapter{
        public UploadlistAdapter1(Context context, List<UploadTheme> uploadList) {
            super(context, uploadList);
        }
    }
}
