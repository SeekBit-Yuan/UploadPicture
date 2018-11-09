package com.sznews.upload.uploadpicture.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ThemeDBHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String NAME = "subject.db";

    public ThemeDBHelper(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建主题表subject和图片表picture
        String createSubjectTable = "create table subject (_id integer primary key autoincrement, " +
                "category1 text, category2 text, theme text, " +

                "create_date integer, soucre text, username text, " +
                "description text, theme_token text)";
        String createPictureTable = "create table picture (_id integer primary key autoincrement, " +
                "path text, title text, theme_token text, state int)";
        db.execSQL(createSubjectTable);
        db.execSQL(createPictureTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String deleteSubjectTable = "drop table if exists subject";
        String deletePictureTable = "drop table if exists picture";
        db.execSQL(deleteSubjectTable);
        db.execSQL(deletePictureTable);
        onCreate(db);
    }
}
