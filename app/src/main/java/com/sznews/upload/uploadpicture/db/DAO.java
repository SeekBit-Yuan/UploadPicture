package com.sznews.upload.uploadpicture.db;

import java.util.List;

public interface DAO<T> {
    List<T> queryAll();
    List<T>  queryAction(String selection,
                         String[] selectionArgs);
    void delite();
    void insert(T t);
    void update(T t);
}
