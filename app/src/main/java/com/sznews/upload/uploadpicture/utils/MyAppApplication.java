package com.sznews.upload.uploadpicture.utils;

import android.app.Application;
import org.xutils.x;

public class MyAppApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
        //x.Ext.setDebug(false); //输出debug日志，开启会影响性能
    }
}
