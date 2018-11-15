package com.sznews.upload.uploadpicture.utils;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;

import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;
import com.sznews.upload.uploadpicture.db.ThemeDBHelper;

import rx.schedulers.Schedulers;

public class SqlBriteProvider {
    private SqlBrite sqlBrite;
    private BriteDatabase briteDatabase;
    public SqlBriteProvider(Context context){
        this.sqlBrite=providerSqlBrite();
        this.briteDatabase=createDatabase(this.sqlBrite,providerOpenHelper(context));
    }

    /**
     * 创建SQLiteOpenHelper对象
     * @param context
     * @return
     */
    private SQLiteOpenHelper providerOpenHelper(Context context){
        return  new ThemeDBHelper(context);
    }

    /**
     *
     * 创建SqlBrite对象
     *
     * @return
     */
    private SqlBrite providerSqlBrite(){
        return  new SqlBrite.Builder().build();
    }
    /**
     * 通过SQLBrite对象和SQLiteOpenHel对象
     * @param sqlBrite
     * @param sqLiteOpenHelper
     * @return
     */
    public BriteDatabase createDatabase(SqlBrite sqlBrite,SQLiteOpenHelper sqLiteOpenHelper){
        BriteDatabase db=sqlBrite.wrapDatabaseHelper(sqLiteOpenHelper, Schedulers.io());
        return db;
    }
    /**
     * 获取到项目默认的数据库
     * @return
     */
    public BriteDatabase getBriteDatabase() {
        return briteDatabase;
    }
}
