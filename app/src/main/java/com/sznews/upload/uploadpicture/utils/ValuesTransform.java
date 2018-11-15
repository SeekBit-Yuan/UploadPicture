package com.sznews.upload.uploadpicture.utils;

import android.content.ContentValues;
import android.database.Cursor;

import com.sznews.upload.uploadpicture.model.Data;
import com.sznews.upload.uploadpicture.model.UploadTheme;

/**
 * 用途：
 * 各种对象转换的工具类
 */
public class ValuesTransform {
    /**
     * 从Cursor生成UploadTheme对象
     * @param cursor
     * @return
     */
    public static UploadTheme transformUploadTheme(Cursor cursor){
        UploadTheme uploadTheme = new UploadTheme();
        uploadTheme.setTheme(cursor.getString(cursor.getColumnIndex("theme")));
        uploadTheme.setDate(cursor.getString(cursor.getColumnIndex("create_date")));
        uploadTheme.setPath(cursor.getString(cursor.getColumnIndex("path")));
        uploadTheme.setState(cursor.getInt(cursor.getColumnIndex("state")));
        return  uploadTheme;
    }

    /**
     * 从UploadTheme生成ContentValues
     * @param uploadTheme
     * @return
     */
    public static ContentValues transformContentValues(UploadTheme uploadTheme){
        ContentValues contentValues=new ContentValues();
        contentValues.put(Data.COLUMN_THEME,uploadTheme.getTheme());
        contentValues.put(Data.COLUMN_DATE,uploadTheme.getDate());
        contentValues.put(Data.COLUMN_PATH,uploadTheme.getPath());
        contentValues.put(Data.COLUMN_STATE,uploadTheme.getState());
        return  contentValues;
    }

}
