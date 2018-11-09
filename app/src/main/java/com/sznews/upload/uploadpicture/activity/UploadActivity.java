package com.sznews.upload.uploadpicture.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.sznews.upload.uploadpicture.R;
import com.sznews.upload.uploadpicture.fragment.HomepageFragment;
import com.sznews.upload.uploadpicture.fragment.UploadlistFragment;

public class UploadActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener{

    private RadioGroup radioGroup;
    private RadioButton homepage;
    private RadioButton uploadlist;
    //fragment参数
    private int code = 0;

    private HomepageFragment homepageFragment;
    private UploadlistFragment uploadlistFragment;

    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        fragmentManager = getSupportFragmentManager();
        radioGroup = findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(this);
        //获取第一个单选按钮，并设置其为选中状态
        homepage = (RadioButton) findViewById(R.id.radio_homepage);
        uploadlist = (RadioButton) findViewById(R.id.radio_uploadlist);
        homepage.setChecked(true);
        int id = getIntent().getIntExtra("id", 0);
        if (id == 1) {
            homepage.setChecked(false);
            uploadlist.setChecked(true);
            code = 1;
        }
    }


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        FragmentTransaction fTransaction = fragmentManager.beginTransaction();
        hideFragment(fTransaction);
        switch (checkedId) {
            case R.id.radio_homepage:
                if (homepageFragment == null) {
                    homepageFragment = new HomepageFragment();
                    fTransaction.add(R.id.Fragment, homepageFragment);
                } else {
                    fTransaction.show(homepageFragment);
                }
                break;
            case R.id.radio_uploadlist:
                if (uploadlistFragment == null) {
                    uploadlistFragment = new UploadlistFragment();
                    fTransaction.add(R.id.Fragment, uploadlistFragment);
                } else {
                    fTransaction.show(uploadlistFragment);
                }
                break;
        }
        fTransaction.commit();
    }

    //隐藏Fragment
    private void hideFragment(FragmentTransaction transaction) {
        if (homepageFragment != null) {
            transaction.hide(homepageFragment);
        }
        if (uploadlistFragment != null) {
            transaction.hide(uploadlistFragment);
        }
    }

    //fragment获取参数方法
    public int getCode(){
        return code;
    }
}
