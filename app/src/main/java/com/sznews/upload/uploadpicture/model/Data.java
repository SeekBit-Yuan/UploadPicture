package com.sznews.upload.uploadpicture.model;

import android.provider.BaseColumns;

public class Data implements BaseColumns {
    /**
     * 数据库信息
     */
    public static final String SQLITE_NAME="subject.db";
    public static final int SQLITE_VERSON=1;
    /**
     * 信息表，及其字段
     */
    public static final String TABLE_NAME="subject";
    public static final String COLUMN_CATEGORY1="category1";
    public static final String COLUMN_CATEGORY2="category2";
    public static final String COLUMN_THEME="theme";
    public static final String COLUMN_DATE="create_date";
    public static final String COLUMN_SOURCE="soucre";
    public static final String COLUMN_USERNAME="username";
    public static final String COLUMN_DESCRIPTION="description";
    public static final String COLUMN_PATH="path";
    public static final String COLUMN_TOKEN="theme_token";
    public static final String COLUMN_STATE="state";

    /**
     * 时间字段的格式
     */
    public static final String DATE_FORMAT="YYYY-MM-DD";
    /**
     * 时间字段的降序，采用date函数比较
     */
    public static final String ORDER_BY="date("+COLUMN_DATE+") desc";
}
