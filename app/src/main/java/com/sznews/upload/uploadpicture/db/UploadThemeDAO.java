package com.sznews.upload.uploadpicture.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.sznews.upload.uploadpicture.db.DAO;
import com.sznews.upload.uploadpicture.db.ThemeDBHelper;
import com.sznews.upload.uploadpicture.model.Data;
import com.sznews.upload.uploadpicture.model.UploadTheme;
import com.sznews.upload.uploadpicture.utils.ValuesTransform;

import java.util.ArrayList;
import java.util.List;

public class UploadThemeDAO implements DAO<UploadTheme> {
    private Context context;
    private ThemeDBHelper themeDBHelper;

    public UploadThemeDAO(Context context) {
        this.context = context;
        this.themeDBHelper = new ThemeDBHelper(this.context);
    }

    @Override
    public List<UploadTheme> queryAll() {
        return queryAction(null,null);
    }

    @Override
    public List<UploadTheme> queryAction(String selection,
                                     String[] selectionArgs) {
        SQLiteDatabase sqLiteDatabase = null;
        Cursor cursor = null;
        List<UploadTheme> list = null;
        try {
            sqLiteDatabase = this.themeDBHelper.getReadableDatabase();
            cursor = sqLiteDatabase.query(Data.TABLE_NAME, null, selection, selectionArgs, null, null, Data.ORDER_BY);
            list=new ArrayList<>();
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    list.add(ValuesTransform.transformUploadTheme(cursor));
                }while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (sqLiteDatabase != null) {
                sqLiteDatabase.close();
            }
        }
        return list;
    }

    @Override
    public void delite() {

    }

    @Override
    public void insert(UploadTheme UploadTheme) {
        SQLiteDatabase sqLiteDatabase = null;
        try {
            sqLiteDatabase = this.themeDBHelper.getWritableDatabase();
            sqLiteDatabase.insert(Data.TABLE_NAME,null,ValuesTransform.transformContentValues(UploadTheme));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (sqLiteDatabase != null) {
                sqLiteDatabase.close();
            }
        }
    }

    @Override
    public void update(UploadTheme UploadTheme) {

    }
}
