package com.sznews.upload.uploadpicture.utils;

import android.app.Application;

import com.sznews.upload.uploadpicture.db.DAO;
import com.sznews.upload.uploadpicture.model.UploadTheme;
import com.sznews.upload.uploadpicture.db.UploadThemeDAO;

public class BaseApplication extends Application {
    private static BaseApplication  appContext;
    private DAO<UploadTheme> UploadThemeDAO;
    private SqlBriteProvider sqlBriteProvider;
    @Override
    public void onCreate() {
        super.onCreate();
        initConfig();

    }

    /**
     *初始化配置
     */
    private void initConfig() {
        appContext=this;
        this.UploadThemeDAO=new UploadThemeDAO(this);
        this.sqlBriteProvider=new SqlBriteProvider(this);
    }

    public static BaseApplication getAppContext() {
        return appContext;
    }

    public DAO<UploadTheme> getUploadThemeDAO() {
        return UploadThemeDAO;
    }

    public SqlBriteProvider getSqlBriteProvider() {
        return sqlBriteProvider;
    }
}
