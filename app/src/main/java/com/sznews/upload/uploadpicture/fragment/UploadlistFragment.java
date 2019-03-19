package com.sznews.upload.uploadpicture.fragment;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sznews.upload.uploadpicture.R;
import com.sznews.upload.uploadpicture.activity.UploadActivity;
import com.sznews.upload.uploadpicture.adapter.UploadlistAdapter;
import com.sznews.upload.uploadpicture.model.Picture;
import com.sznews.upload.uploadpicture.model.Result;
import com.sznews.upload.uploadpicture.model.UploadTheme;
import com.sznews.upload.uploadpicture.model.User;
import com.sznews.upload.uploadpicture.url.InterfaceJsonfile;
import com.sznews.upload.uploadpicture.db.ThemeDBHelper;
import com.sznews.upload.uploadpicture.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;

import rx.Subscription;
import rx.internal.operators.OnSubscribeDelaySubscriptionOther;

public class UploadlistFragment extends Fragment {

    private UploadlistAdapter uploadlistAdapter;
    private List<UploadTheme> uploadThemeList = new ArrayList<>();

    //待上传主题
    private List<UploadTheme> ThemeList = new ArrayList<>();
    //所有图片
    private List<Picture> PictureListAll = new ArrayList<>();

    //0表示通过UploadActivity按钮点击切换进入该fragment，1表示通过getActivity()上传按钮进入，code为1时触发上传方法
    private int code = 0;//是否从新建上传任务跳转判断码，0：不是；1：是
    private SQLiteDatabase db;
    private StringBuilder sb;//字符串连接
    //未完成主题id集合
    private List<String> ThemeId = new ArrayList<String>();
    private List<String> PictureId = new ArrayList<String>();
    //该图片是否上传完成，0：已完成,-1：未开始，1：未完成
    private int isSuccess = 1;
    //该主题是否上传完成，0：已完成
    private int isSuccess1 = 1;
    //    //图片EXIF信息JSONObject
//    private JSONObject object1 = new JSONObject();
//    //图片信息JSONObject
//    private JSONObject object2 = new JSONObject();
    //图片EXIF信息JSONArray
    private JSONArray array1 = new JSONArray();
    //图片录入信息JSONArray
    private JSONArray array2 = new JSONArray();

    //    private BriteDatabase db;
    private Subscription suscription;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0: uploadlistAdapter.notifyDataSetChanged();break;//更新上传列表数据
            }

        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_uploadlist, null);

//        this.db = BaseApplication.getAppContext().getSqlBriteProvider().getBriteDatabase();
//        query();

        //实例化创建数据库和数据表
        ThemeDBHelper themeDBHelper = new ThemeDBHelper(getActivity());
        db = themeDBHelper.getWritableDatabase();

        uploadThemeList = new ArrayList<>();

        //参数依次是:表名，列名，where约束条件，where中占位符提供具体的值，指定group by的列，进一步约束
        //查询结果加入到List<UploadTheme>中以供上传列表展示
        Cursor cursor = db.query("subject", null, null, null, null, null, null);
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
                int dutyid = cursor.getInt(cursor.getColumnIndex("dutyid"));
                int sum = cursor.getInt(cursor.getColumnIndex("total"));
                int num = cursor.getInt(cursor.getColumnIndex("uploaded"));
                int state = cursor.getInt(cursor.getColumnIndex("state"));//"0"：已上传成功，"1"：正在上传，"2"：暂停上传等待中
                uploadThemeList.add(new UploadTheme(theme, date, path, username, author, userid, description, category1, category2, dutyid, sum, num, state));
                if (state != 0) {
                    ThemeList.add(new UploadTheme(theme, date, path, username, author, userid, description, category1, category2, dutyid, sum, num, state));
                }
            } while (cursor.moveToNext());
        }
        cursor.close();

        LinearLayout mEmptyTv = view.findViewById(R.id.listView_tip);
        ListView listView = view.findViewById(R.id.uploadlist_listview);

        uploadlistAdapter = new UploadlistAdapter1(getActivity(), uploadThemeList);
        listView.setAdapter(uploadlistAdapter);
        listView.setEmptyView(mEmptyTv);//上传列表为空占位布局

        //code！=0代表是从新建主题跳转过来，默认数据库已有主题全部上传完成，直接上传新建的图片和主题
        if (code != 0) {
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    UploadFromNewbuild(uploadThemeList,code);
//                }
//            }).start();
            UploadFromNewbuild(uploadThemeList,code);
        }

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        code = ((UploadActivity) context).getCode();//获取activity传递过来的参数
    }

    class UploadlistAdapter1 extends UploadlistAdapter {
        public UploadlistAdapter1(Context context, List<UploadTheme> uploadList) {
            super(context, uploadList);
        }
    }

    //从新建主题跳转过来触发的上传方法
    public void UploadFromNewbuild(List<UploadTheme> uploadThemeList, final int code){

        //待上传图片
        List<Picture> PictureList = new ArrayList<>();
        //图片EXIF信息JSONObject
        JSONObject object1 = new JSONObject();
        //图片信息JSONObject
        final JSONObject object2 = new JSONObject();

        //上传主题列表数量
        int n = uploadThemeList.size();
        //将新建任务图片加到PictureList中
        Cursor cursor1 = db.query("picture", null, "dutyid=?", new String[]{code + ""}, null, null, null);
        if (cursor1.moveToFirst()) {
            do {
                int picid = cursor1.getInt(cursor1.getColumnIndex("_id"));
                String path = cursor1.getString(cursor1.getColumnIndex("path"));
                String title = cursor1.getString(cursor1.getColumnIndex("title"));
                int dutyid = cursor1.getInt(cursor1.getColumnIndex("dutyid"));
                int state = cursor1.getInt(cursor1.getColumnIndex("state"));//"0"：已上传成功，"1"：正在上传，"2"：暂停上传等待中
                PictureList.add(new Picture(picid, title, path, dutyid, state));
            } while (cursor1.moveToNext());
        }
        cursor1.close();
        //上传PictureList中所有图片
        for (int i = 0; PictureList.size() > i; i++) {
            //上传单张图片
            upload(PictureList.get(i).getPic_path(), PictureList.get(i).getDutyid(), PictureList.get(i).getTitle(), object1, PictureList, i, n, object2);

//            if(isSuccess == 0){
//                object1 = new JSONObject();
//                ContentValues values0 = new ContentValues();
//                values0.put("state", 0);
//                //改变已上传图片状态
//                db.update("picture", values0, "_id = ?", new String[]{PictureList.get(i).getPicid() + ""});
//                uploadThemeList.get(n-1).setNum(i + 1);
//                ContentValues values = new ContentValues();
//                values.put("uploaded", i + 1);
//                //改变已上传完成数量值
//                db.update("subject", values, "dutyid = ?", new String[]{code + ""});
//                isSuccess = -1;
//                //uploadlistAdapter.notifyDataSetChanged();//更新上传列表数据
//                System.out.println("num:" + uploadThemeList.get(n-1).getNum() + ",sum:" + uploadThemeList.get(n-1).getSum());
//                if (uploadThemeList.get(n-1).getNum() == uploadThemeList.get(n-1).getSum()) {
//                    try {
//                        object2.put("dutyid", uploadThemeList.get(n-1).getDutyid());
//                        object2.put("dutyname", uploadThemeList.get(n-1).getTheme());
//                        object2.put("username", uploadThemeList.get(n-1).getUsername());
//                        object2.put("uid", uploadThemeList.get(n-1).getUserid());
//                        object2.put("author", uploadThemeList.get(n-1).getUsername());
//                        object2.put("description", uploadThemeList.get(n-1).getDescription());
//                        object2.put("primaryClassification", uploadThemeList.get(n-1).getCategory1());
//                        object2.put("secondaryClassification", uploadThemeList.get(n-1).getCategory2());
//                        System.out.println("Theme:" + object2);
//                    } catch (JSONException e) {}
//                    //上传主题信息
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            String result = UploadTheme(object2);
//                            //获取返回的上传情况
//                            Gson gson = new GsonBuilder().create();
//                            Result result1 = gson.fromJson(result, Result.class);
//                            if (result1.getState().equals("1")) {
//                                System.out.println("================================主题信息上传失败！");
//                                isSuccess1 = -1;
//                                Toast.makeText(getActivity(), "主题信息上传失败", Toast.LENGTH_SHORT).show();
//                            } else if (result1.getState().equals("0")) {
//                                System.out.println("================================主题信息上传成功！");
//                                isSuccess1 = 0;
//                                ContentValues values1 = new ContentValues();
//                                values1.put("state", 0);
//                                //修改主题是否上传完成状态，"0"：已上传成功，"1"：未上传完成
//                                db.update("subject", values1, "dutyid = ?", new String[]{code + ""});
//                                //uploadlistAdapter.notifyDataSetChanged();//更新上传列表数据
//                                Message msg = new Message();
//                                msg.what = 0;
//                                handler.sendMessage(msg);
//                                Interface(object2);
//                            }
//                        }
//                    }).start();
//                }
//            } else if (isSuccess == -1) {
//                Toast.makeText(getActivity(), "上传失败!!!", Toast.LENGTH_SHORT).show();
//                ContentValues values = new ContentValues();
//                values.put("state", 2);
//                //修改主题是否上传完成状态，"0"：已上传成功，"1"：未上传完成，"2"：上传任务暂停
//                db.update("subject", values, "dutyid = ?", new String[]{code + ""});
//                uploadlistAdapter.notifyDataSetChanged();//更新上传列表数据
//                break;
//            }
        }

        if (isSuccess1 == 0){
            Toast.makeText(getActivity(), "上传成功!!!", Toast.LENGTH_SHORT).show();
        }else if (isSuccess1 == -1) {
            ContentValues values = new ContentValues();
            values.put("state", 2);
            //修改主题是否上传完成状态，"0"：已上传成功，"1"：未上传完成，"2"：上传任务暂停
            db.update("subject", values, "dutyid = ?", new String[]{code + ""});
            uploadlistAdapter.notifyDataSetChanged();//更新上传列表数据

        }
    }

    //上传图片文件方法
    public void upload(final String path, int dutyid, String title, final JSONObject object1, final List<Picture> PictureList, final int i, final int n, final JSONObject object2) {
        SharedPreferences sp = getActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        String token = sp.getString("token", "");
        String username = sp.getString("username", "");
        //String url = "http://172.16.137.198:8080/VueServer/upload";
        if (path != null && !path.equals("")) {
            String url = InterfaceJsonfile.UPLOAD;

            RequestParams params = new RequestParams(url);
            params.setMultipart(true);
            params.addBodyParameter("file", new File(path));
            try {
                GetExifInfo(path, object1);
                String s = URLEncoder.encode(path.substring(path.lastIndexOf("/") + 1), "utf-8");
                params.addBodyParameter("dutyid", String.valueOf(dutyid));
                params.addBodyParameter("title", title);
                params.addBodyParameter("token", token);
                params.addBodyParameter("username", username);
                params.addBodyParameter("exif_camera", object1.getString("exif_camera"));//	照相机型号
                params.addBodyParameter("exif_mfrs", object1.getString("exif_mfrs"));// 制造商
                params.addBodyParameter("exif_shottime", object1.getString("exif_shottime"));// 拍摄时间
                params.addBodyParameter("exif_focus", object1.getString("exif_focus"));// 焦距
                params.addBodyParameter("exif_fnumber", object1.getString("exif_fnumber"));// 光圈值
                params.addBodyParameter("exif_mode", object1.getString("exif_mode"));// 测光模式
                params.addBodyParameter("exif_iso", object1.getString("exif_iso"));// 感光度
                params.addBodyParameter("exif_fl", object1.getString("exif_fl"));// 闪光灯
                params.addBodyParameter("exif_ep", object1.getString("exif_ep"));// 曝光程序
                params.addBodyParameter("exif_et", object1.getString("exif_et"));// 曝光时间
                params.addBodyParameter("exif_ec", object1.getString("exif_ec"));// 曝光补偿
                params.addBodyParameter("exif_wb", object1.getString("exif_wb"));// 白平衡

                System.out.println("dutyid:" + String.valueOf(dutyid) + ",title:" + title + ",object:" + object1);

                x.http().post(params, new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {

                        int savePathIndex = result.indexOf("savePath");
                        if (savePathIndex == -1){
                            isSuccess = -1;
                        } else {
                            isSuccess = 0;
                            System.out.println("===============单张图片上传成功"  + result);
                            UploadStatus(object1, PictureList, i, n, object2);
                        }

                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        System.out.println("===============单张图片上传失败");
                        ex.printStackTrace();
                        Toast.makeText(getActivity(), "图片上传失败", Toast.LENGTH_SHORT).show();
                        isSuccess = -1;

                    }

                    @Override
                    public void onCancelled(CancelledException cex) {
                    }

                    @Override
                    public void onFinished() {
                    }
                });

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else {
            Toast.makeText(getActivity(), "请选择图片，再上传", Toast.LENGTH_SHORT).show();
        }
    }

    //判断图片上传进度以及上传主题
    public void UploadStatus(JSONObject object1, List<Picture> PictureList, int i, int n, final JSONObject object2){
        SharedPreferences sp = getActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        String token = sp.getString("token", "");
        String username = sp.getString("username", "");

        object1 = new JSONObject();
        ContentValues values0 = new ContentValues();
        values0.put("state", 0);
        //改变已上传图片状态
        db.update("picture", values0, "_id = ?", new String[]{PictureList.get(i).getPicid() + ""});
        uploadThemeList.get(n-1).setNum(i + 1);
        ContentValues values = new ContentValues();
        values.put("uploaded", i + 1);
        //改变已上传完成数量值
        db.update("subject", values, "dutyid = ?", new String[]{code + ""});
        isSuccess = -1;
        //uploadlistAdapter.notifyDataSetChanged();//更新上传列表数据
        System.out.println("num:" + uploadThemeList.get(n-1).getNum() + ",sum:" + uploadThemeList.get(n-1).getSum());
        if (uploadThemeList.get(n-1).getNum() == uploadThemeList.get(n-1).getSum()) {
            try {
                object2.put("dutyid", uploadThemeList.get(n-1).getDutyid());
                object2.put("dutyname", uploadThemeList.get(n-1).getTheme());
                object2.put("username", username);
                object2.put("uid", uploadThemeList.get(n-1).getUserid());
                object2.put("author", uploadThemeList.get(n-1).getUsername());
                object2.put("description", uploadThemeList.get(n-1).getDescription());
                object2.put("primaryClassification", uploadThemeList.get(n-1).getCategory1());
                object2.put("secondaryClassification", uploadThemeList.get(n-1).getCategory2());
                System.out.println("Theme:" + object2);
            } catch (JSONException e) {}
            //上传主题信息
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String result = UploadTheme(object2);
                    //获取返回的上传情况
                    Gson gson = new GsonBuilder().create();
                    Result result1 = gson.fromJson(result, Result.class);
                    if (result1.getState().equals("1")) {
                        System.out.println("================================主题信息上传失败！");
                        isSuccess1 = -1;
                        Toast.makeText(getActivity(), "主题信息上传失败", Toast.LENGTH_SHORT).show();
                    } else if (result1.getState().equals("0")) {
                        System.out.println("================================主题信息上传成功！");
                        isSuccess1 = 0;
                        ContentValues values1 = new ContentValues();
                        values1.put("state", 0);
                        //修改主题是否上传完成状态，"0"：已上传成功，"1"：未上传完成
                        db.update("subject", values1, "dutyid = ?", new String[]{code + ""});
                        //uploadlistAdapter.notifyDataSetChanged();//更新上传列表数据
                        Message msg = new Message();
                        msg.what = 0;
                        handler.sendMessage(msg);
                        Interface(object2);
                    }
                }
            }).start();
        }
    }

    //主题上传
    public String UploadTheme(JSONObject object2) {
        SharedPreferences sp = getActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        String token = sp.getString("token", "");
        String path = InterfaceJsonfile.UPLOAD_DUTY;
        URL url;
        String lines = "";
        try {
            url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");// 提交模式
            //是否允许输入输出
            conn.setDoInput(true);
            conn.setDoOutput(true);
            //设置请求头里面的数据，以下设置用于解决http请求code415的问题
            conn.setRequestProperty("Content-Type", "application/json");
            //链接地址
            conn.connect();
            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            //发送参数
            StringBuffer sb = new StringBuffer();
            sb.append("{\"dutyid\":\"").append(object2.getString("dutyid")).append("\"")
                    .append(",\"dutyname\":\"").append(object2.getString("dutyname")).append("\"")
                    .append(",\"username\":\"").append(object2.getString("username")).append("\"")
                    .append(",\"token\":\"").append(token).append("\"")
                    .append(",\"userid\":\"").append(object2.getString("uid")).append("\"")
                    .append(",\"author\":\"").append(object2.getString("author")).append("\"")
                    .append(",\"description\":\"").append(object2.getString("description")).append("\"")
                    .append(",\"primaryClassification\":\"").append(object2.getString("primaryClassification")).append("\"")
                    .append(",\"secondaryClassification\":\"").append(object2.getString("secondaryClassification")).append("\"")
                    .append("}");
            writer.write(sb.toString());
            //清理当前编辑器的左右缓冲区，并使缓冲区数据写入基础流
            writer.flush();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    conn.getInputStream()));
            lines = reader.readLine();//读取请求结果
            System.out.println("lines:"+lines);
            reader.close();
            conn.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return lines;
    }

    //上传完成触发接口
    public String Interface(JSONObject object2) {
        SharedPreferences sp = getActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        String token = sp.getString("token", "");
        String path = InterfaceJsonfile.InterfaceUrl;
        URL url;
        String lines = "";
        try {
            url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");// 提交模式
            //是否允许输入输出
            conn.setDoInput(true);
            conn.setDoOutput(true);
            //设置请求头里面的数据，以下设置用于解决http请求code415的问题
            conn.setRequestProperty("Content-Type", "application/json");
            //链接地址
            conn.connect();
            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            //发送参数
            StringBuffer sb = new StringBuffer();
            sb.append("{\"dutyid\":\"").append(object2.getString("dutyid")).append("\"")
                    .append(",\"username\":\"").append(object2.getString("username")).append("\"")
                    .append(",\"token\":\"").append(token).append("\"")
                    .append("}");
            writer.write(sb.toString());
            //清理当前编辑器的左右缓冲区，并使缓冲区数据写入基础流
            writer.flush();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    conn.getInputStream()));
            lines = reader.readLine();//读取请求结果
            System.out.println("lines:"+lines);
            reader.close();
            conn.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return lines;
    }

    //获取EXIF信息
    public JSONObject GetExifInfo(String path, JSONObject object1) {

        try {
            ExifInterface exifInterface = new ExifInterface(
                    path);
            String FModel = exifInterface
                    .getAttribute(ExifInterface.TAG_MODEL);//TAG_MODEL：设备型号，整形表示，在ExifInterface中有常量对应表示。
            String FMake = exifInterface
                    .getAttribute(ExifInterface.TAG_MAKE);//TAG_MAKE：设备品牌。
            String FDateTime = exifInterface
                    .getAttribute(ExifInterface.TAG_DATETIME);//TAG_DATETIME：拍摄时间，取决于设备设置的时间。
            String FFocalLength = exifInterface
                    .getAttribute(ExifInterface.TAG_FOCAL_LENGTH);//TAG_FOCAL_LENGTH：焦距。
            String FFNumber = exifInterface
                    .getAttribute(ExifInterface.TAG_APERTURE);//TAG_APERTURE：光圈值。
            String FMETERING_MODE = exifInterface
                    .getAttribute(ExifInterface.TAG_METERING_MODE);//TAG_ISO：测距模式。
            String FISOSpeedRatings = exifInterface
                    .getAttribute(ExifInterface.TAG_ISO);//TAG_ISO：感光度。
            String FFlash = exifInterface
                    .getAttribute(ExifInterface.TAG_FLASH);//TAG_FLASH：闪光灯。
            String FEXPOSURE_PROGRAM = exifInterface
                    .getAttribute(ExifInterface.TAG_EXPOSURE_PROGRAM);//TAG_EXPOSURE_TIME：曝光程序。
            String FExposureTime = exifInterface
                    .getAttribute(ExifInterface.TAG_EXPOSURE_TIME);//TAG_EXPOSURE_TIME：曝光时间。
            String FEXPOSURE_BIAS_VALUE = exifInterface
                    .getAttribute(ExifInterface.TAG_EXPOSURE_BIAS_VALUE);//TAG_EXPOSURE_TIME：曝光补偿。
            String FWhiteBalance = exifInterface
                    .getAttribute(ExifInterface.TAG_WHITE_BALANCE);//TAG_WHITE_BALANCE：白平衡。
            if (FModel == null || FModel.length() == 0) {
                FModel = "-";
            }
            if (FMake == null || FMake.length() == 0) {
                FMake = "-";
            }
            if (FDateTime == null || FDateTime.length() == 0) {
                FDateTime = "-";
            }
            if (FFocalLength == null || FFocalLength.length() == 0) {
                FFocalLength = "-";
            }
            if (FFNumber == null || FFNumber.length() == 0) {
                FFNumber = "-";
            }
            if (FMETERING_MODE == null || FMETERING_MODE.length() == 0) {
                FMETERING_MODE = "-";
            }
            if (FISOSpeedRatings == null || FISOSpeedRatings.length() == 0) {
                FISOSpeedRatings = "-";
            }
            if (FFlash == null || FFlash.length() == 0) {
                FFlash = "-";
            }
            if (FEXPOSURE_PROGRAM == null || FEXPOSURE_PROGRAM.length() == 0) {
                FEXPOSURE_PROGRAM = "-";
            }
            if (FExposureTime == null || FExposureTime.length() == 0) {
                FExposureTime = "-";
            }
            if (FEXPOSURE_BIAS_VALUE == null || FEXPOSURE_BIAS_VALUE.length() == 0) {
                FEXPOSURE_BIAS_VALUE = "-";
            }
            if (FWhiteBalance == null || FWhiteBalance.length() == 0) {
                FWhiteBalance = "-";
            }

            object1.put("exif_camera", FModel);
            object1.put("exif_mfrs", FMake);
            object1.put("exif_shottime", FDateTime);
            object1.put("exif_focus", FFocalLength);
            object1.put("exif_fnumber", FFNumber);
            object1.put("exif_mode", FMETERING_MODE);
            object1.put("exif_iso", FISOSpeedRatings);
            object1.put("exif_fl", FFlash);
            object1.put("exif_ep", FEXPOSURE_PROGRAM);
            object1.put("exif_et", FExposureTime);
            object1.put("exif_ec", FEXPOSURE_BIAS_VALUE);
            object1.put("exif_wb", FWhiteBalance);
            System.out.println("EXIF0:" + object1);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return object1;
    }
}
