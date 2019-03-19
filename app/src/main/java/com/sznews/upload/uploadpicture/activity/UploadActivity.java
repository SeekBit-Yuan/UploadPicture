package com.sznews.upload.uploadpicture.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

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

//    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        FragmentManager fragmentManager;
        fragmentManager = getSupportFragmentManager();
        radioGroup = findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(this);
        //获取第一个单选按钮，并设置其为选中状态
        homepage = (RadioButton) findViewById(R.id.radio_homepage);
        uploadlist = (RadioButton) findViewById(R.id.radio_uploadlist);
        homepage.setChecked(true);
        int id = getIntent().getIntExtra("id", 0);
        if (id != 0) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.hide(homepageFragment);
            uploadlistFragment = new UploadlistFragment();
            transaction.add(R.id.Fragment, uploadlistFragment);
            transaction.commit();
            uploadlist.setChecked(true);
            code = id;
//            System.out.println("code:"+code);
        }
    }


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        FragmentManager fragmentManager = getSupportFragmentManager();
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

    //1.重写onBackPressed方法组织super即可实现禁止返回上一层页面
    public void onBackPressed(){
        //super.onBackPressed();
    }

    private long firstTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        /**
         * event.getRepeatCount() 重复次数,点后退键的时候，为了防止点得过快，触发两次后退事件，故做此设置。
         *
         * 建议保留这个判断，增强程序健壮性。
         */
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            return false;
        }

        // TODO Auto-generated method stub
        switch(keyCode)
        {
            case KeyEvent.KEYCODE_BACK:
                long secondTime = System.currentTimeMillis();
                if (secondTime - firstTime > 2000) {                                         //如果两次按键时间间隔大于2秒，则不退出
                    Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                    firstTime = secondTime;//更新firstTime
                    return true;
                } else {                                                    //两次按键小于2秒时，退出应用
                    System.exit(0);
                }
                break;
        }
        return super.onKeyDown(keyCode, event);
    }
}
