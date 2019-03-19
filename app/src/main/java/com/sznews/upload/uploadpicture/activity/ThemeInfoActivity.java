package com.sznews.upload.uploadpicture.activity;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import com.sznews.upload.uploadpicture.R;
import com.sznews.upload.uploadpicture.db.ThemeDBHelper;
import com.sznews.upload.uploadpicture.model.UploadTheme;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@ContentView(R.layout.activity_themeinfo)
public class ThemeInfoActivity extends Activity {

    @ViewInject(R.id.themeinfo_quit)
    private Button quit;
    @ViewInject(R.id.themeinfo_category1)
    private TextView category1;
    @ViewInject(R.id.themeinfo_category2)
    private TextView category2;
    @ViewInject(R.id.themeinfo_theme)
    private TextView theme;
    @ViewInject(R.id.themeinfo_date)
    private TextView date;
    @ViewInject(R.id.themeinfo_username)
    private TextView username;
    @ViewInject(R.id.themeinfo_explain)
    private TextView explain;
    private GridView mGridView;

    private SQLiteDatabase db;
    private List<UploadTheme> uploadThemeList = new ArrayList<>();
    private List<String> pathList = new ArrayList<>();
    private List<String> titleList = new ArrayList<>();
    List<Map<String , Object>> list = new LinkedList<>();
    private int dutyid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);

        dutyid = getIntent().getIntExtra("dutyid", 0);

        initView(dutyid);

        quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void initView(int dutyid){
        final int id = dutyid;
        //实例化创建数据库和数据表
        ThemeDBHelper themeDBHelper = new ThemeDBHelper(ThemeInfoActivity.this);
        db = themeDBHelper.getWritableDatabase();
        //将新建任务图片加到PictureList中
        Cursor cursor = db.query("subject", null, "dutyid=?", new String[]{dutyid + ""}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String theme = cursor.getString(cursor.getColumnIndex("theme"));
                String date = cursor.getString(cursor.getColumnIndex("create_date"));
                String path = cursor.getString(cursor.getColumnIndex("path"));
                String username = cursor.getString(cursor.getColumnIndex("username"));
                String author = cursor.getString(cursor.getColumnIndex("author"));
                int userid = cursor.getColumnIndex("uid");
                String description = cursor.getString(cursor.getColumnIndex("description"));
                String category1 = cursor.getString(cursor.getColumnIndex("category1"));
                String category2 = cursor.getString(cursor.getColumnIndex("category2"));
                dutyid = cursor.getInt(cursor.getColumnIndex("dutyid"));
                int sum = cursor.getInt(cursor.getColumnIndex("total"));
                int num = cursor.getInt(cursor.getColumnIndex("uploaded"));
                int state = cursor.getColumnIndex("state");//"0"：已上传成功，"1"：正在上传，"2"：暂停上传等待中
                uploadThemeList.add(new UploadTheme(theme, date, path, username, author, userid, description, category1, category2, dutyid, sum, num, state));
            } while (cursor.moveToNext());
        }
        cursor.close();

        category1.setText(uploadThemeList.get(0).getCategory1());
        category2.setText(uploadThemeList.get(0).getCategory2());
        theme.setText(uploadThemeList.get(0).getTheme());
        date.setText(uploadThemeList.get(0).getDate());
        username.setText(uploadThemeList.get(0).getUsername());
        explain.setText(uploadThemeList.get(0).getDescription());

        new Thread(new Runnable() {
            @Override
            public void run() {
                Cursor cursor1 = db.query("picture", null, "dutyid=?", new String[]{id + ""}, null, null, null);
                if (cursor1.moveToFirst()) {
                    do {
                        String path = cursor1.getString(cursor1.getColumnIndex("path"));
                        String title = cursor1.getString(cursor1.getColumnIndex("title"));
                        int state = cursor1.getInt(cursor1.getColumnIndex("state"));//"0"：已上传成功，"1"：正在上传，"2"：暂停上传等待中
                        pathList.add(path);
                        titleList.add(title);
                    } while (cursor1.moveToNext());
                }
                cursor1.close();
                mGridView = (GridView) findViewById(R.id.themeinfo_gridview);
                for (int i = 0;titleList.size() > i;i++){
                    Map<String, Object> pic = new HashMap<String, Object>();
                    pic.put("title", titleList.get(i));
                    pic.put("path", pathList.get(i));
                    list.add(pic);
                }
                SimpleAdapter adapter = new SimpleAdapter(ThemeInfoActivity.this, list , R.layout.picture ,
                        new String[]{"title" , "path"} , new int[]{R.id.themeinfo_pictitle , R.id.themeinfo_pic});
                mGridView.setAdapter(adapter);
            }
        }).start();
    }
}
