package com.sznews.upload.uploadpicture.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.sznews.upload.uploadpicture.R;
import com.sznews.upload.uploadpicture.adapter.PictureAdapter;
import com.sznews.upload.uploadpicture.model.Picture;
import com.zyao89.view.zloading.ZLoadingDialog;
import com.zyao89.view.zloading.Z_TYPE;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.sznews.upload.uploadpicture.utils.DrawableToString.DrawableToString;

@ContentView(R.layout.activity_newbuild)
public class NewBuildActivity extends AppCompatActivity{

    private PictureAdapter pictureAdapter;
    //图片录入信息
    private Spinner spinner1;
    private Spinner spinner2;
    private EditText editText1;
    private TextView textView1;
    private EditText editText2;
    private TextView textView2;
    private EditText editText3;
    private String spinnerselect1;
    private String spinnerselect2;
    private String title;
    private String datetime;
    private String source;
    private String explain;
    //上下文对象
    private Context context;
    //调用系统拍照
    private static final int RESULT_CAPTURE_CODE = 100;
    //调用系统相册-选择图片
    private static final int RESULT_IMAGE_CODE = 200;
    //ListView中图片内容
    private List<Picture> PictureList = new ArrayList<>();
    //图片EXIF信息JSONObject
    private JSONObject object1 =new JSONObject();
    //图片信息JSONObject
    private JSONObject object2 =new JSONObject();
    //图片EXIF信息JSONArray
    private JSONArray array1 =new JSONArray();
    //图片录入信息JSONArray
    private JSONArray array2 =new JSONArray();
    //图片地址
    private String path;
    //添加按钮点击次数
    int addnum = 0;

    @ViewInject(R.id.newbuild_quit)
    private Button quit;
    @ViewInject(R.id.upload)
    private Button upload;
    @ViewInject(R.id.lv_pic)
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);

        initView();

        quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                title = editText1.getText().toString().trim();
                datetime = textView1.getText().toString().trim();
                source = editText2.getText().toString().trim();
                explain = editText3.getText().toString().trim();

//                if(null == title || TextUtils.isEmpty(title)){
//                    Toast.makeText(NewBuildActivity.this, "请输入标题", Toast.LENGTH_SHORT).show();
//                    return;
//                }else if(null == source || TextUtils.isEmpty(source)){
//                    Toast.makeText(NewBuildActivity.this, "请输入来源", Toast.LENGTH_SHORT).show();
//                    return;
//                }else if(null == explain || TextUtils.isEmpty(explain)){
//                    Toast.makeText(NewBuildActivity.this, "请输入说明", Toast.LENGTH_SHORT).show();
//                    return;
//                }else if(addnum == 0 || PictureList.size() == 0){
//                    Toast.makeText(NewBuildActivity.this, "请添加图片", Toast.LENGTH_SHORT).show();
//                    return;
//                }else if(!PictureTitleIsEmpty(PictureList)){
//                    Toast.makeText(NewBuildActivity.this, "请添加图片标题", Toast.LENGTH_SHORT).show();
//                    return;
//                }
                Intent intent = new Intent(NewBuildActivity.this, UploadActivity.class);
                intent.putExtra("id",1);
                startActivity(intent);
            }
        });
    }

    private void initView() {
        View vHead= getLayoutInflater().inflate(R.layout.newbuild_head,listView, false);
        Button add = vHead.findViewById(R.id.add_button);
        spinner1 = vHead.findViewById(R.id.category1);
        spinner2 = vHead.findViewById(R.id.category2);
        editText1 = vHead.findViewById(R.id.theme);
        textView1 = vHead.findViewById(R.id.date);
        editText2 = vHead.findViewById(R.id.source);
        textView2 = vHead.findViewById(R.id.username);
        editText3 = vHead.findViewById(R.id.explain);

        //头布局放入ListView中
        listView.addHeaderView(vHead);

        spinner1.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerselect1 = NewBuildActivity.this.getResources().getStringArray(R.array.data1)[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner2.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerselect2 = NewBuildActivity.this.getResources().getStringArray(R.array.data2)[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //获取用户名
        SharedPreferences sp = this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        String usernameStr = sp.getString("username", "");
        textView2.setText(usernameStr);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// HH:mm:ss
        //获取当前时间
        Date date = new Date(System.currentTimeMillis());
        textView1.setText(simpleDateFormat.format(date));

        Drawable drawable = getResources().getDrawable(R.drawable.upload);
        String defaultpath = DrawableToString(drawable);
        PictureList.add(new Picture(defaultpath));

        pictureAdapter = new PictureAdapter1(NewBuildActivity.this,PictureList);
        listView.setAdapter(pictureAdapter);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //动态获取权限
                verifyStoragePermissions(NewBuildActivity.this);

                //清除之前选择的图片信息
                object1 =new JSONObject();
                object2 =new JSONObject();
                array1 = new JSONArray();
                array2 = new JSONArray();

                title = editText1.getText().toString().trim();
                datetime = textView1.getText().toString().trim();
                source = editText2.getText().toString().trim();
                explain = editText3.getText().toString().trim();

                try {
                    object2.put("category1",spinnerselect1);
                    object2.put("category",spinnerselect2);
                    object2.put("title",title);
                    object2.put("datetime",datetime);
                    object2.put("source",source);
                    object2.put("explain",explain);
                    array2.put(object2);
                    System.out.println(array2.toString());
                }catch (Exception e){

                }

                //在这里跳转到手机系统相册里面
                Intent intent = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, RESULT_IMAGE_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //获取图片路径
        if (requestCode == RESULT_IMAGE_CODE && resultCode == Activity.RESULT_OK && data != null) {
            //获取内容解析者对象
            try {
                Bitmap mBitmap = BitmapFactory.decodeStream(
                        getContentResolver().openInputStream(data.getData()));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            Uri selectedImage = data.getData();
            String[] filePathColumns = {MediaStore.Images.Media.DATA};
            Cursor c = getContentResolver().query(selectedImage, filePathColumns, null, null, null);
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(filePathColumns[0]);
            path = c.getString(columnIndex);//返回已选图片地址
            System.out.println("已选图片地址：" + path);
            if(addnum == 0){//第一次将默认图片替换，Listview如果没有默认第一项图片无法正常添加新的item
                PictureList.clear();
                PictureList.add(new Picture(path));
                pictureAdapter.notifyDataSetChanged();
                addnum = 1;
            }else {
                PictureList.add(new Picture(path));//将选择图片添加到下面列表
                pictureAdapter.notifyDataSetChanged();
                System.out.println("");
            }
//            GetExifInfo(path);//获取EXIF信息
//            System.out.println(array1.toString());
            c.close();
        }
    }

    //获取EXIF信息
    public void GetExifInfo(String path){

        try {
            ExifInterface exifInterface = new ExifInterface(
                    path);
            String FFNumber = exifInterface
                    .getAttribute(ExifInterface.TAG_APERTURE);//TAG_APERTURE：光圈值。
            String FDateTime = exifInterface
                    .getAttribute(ExifInterface.TAG_DATETIME);//TAG_DATETIME：拍摄时间，取决于设备设置的时间。
            String FExposureTime = exifInterface
                    .getAttribute(ExifInterface.TAG_EXPOSURE_TIME);//TAG_EXPOSURE_TIME：曝光时间。
            String FFlash = exifInterface
                    .getAttribute(ExifInterface.TAG_FLASH);//TAG_FLASH：闪光灯。
            String FFocalLength = exifInterface
                    .getAttribute(ExifInterface.TAG_FOCAL_LENGTH);//TAG_FOCAL_LENGTH：焦距。
            String FImageLength = exifInterface
                    .getAttribute(ExifInterface.TAG_IMAGE_LENGTH);//TAG_IMAGE_LENGTH：图片高度。
            String FImageWidth = exifInterface
                    .getAttribute(ExifInterface.TAG_IMAGE_WIDTH);//TAG_IMAGE_WIDTH：图片宽度。
            String FISOSpeedRatings = exifInterface
                    .getAttribute(ExifInterface.TAG_ISO);//TAG_ISO：ISO。
            String FMake = exifInterface
                    .getAttribute(ExifInterface.TAG_MAKE);//TAG_MAKE：设备品牌。
            String FModel = exifInterface
                    .getAttribute(ExifInterface.TAG_MODEL);//TAG_MODEL：设备型号，整形表示，在ExifInterface中有常量对应表示。
            String FOrientation = exifInterface
                    .getAttribute(ExifInterface.TAG_ORIENTATION);//TAG_ORIENTATION：旋转角度，整形表示，在ExifInterface中有常量对应表示。
            String FWhiteBalance = exifInterface
                    .getAttribute(ExifInterface.TAG_WHITE_BALANCE);//TAG_WHITE_BALANCE：白平衡。
                                                                   //TAG_GPS_LATITUDE 纬度
                                                                   //TAG_GPS_LATITUDE_REF 纬度参考
            try {
                object1.put("FFNumber",FFNumber);
                object1.put("FDateTime",FDateTime);
                object1.put("FExposureTime",FExposureTime);
                object1.put("FFlash",FFlash);
                object1.put("FFocalLength",FFocalLength);
                object1.put("FImageLength",FImageLength);
                object1.put("FImageWidth",FImageWidth);
                object1.put("FISOSpeedRatings",FISOSpeedRatings);
                object1.put("FMake",FMake);
                object1.put("FModel",FModel);
                object1.put("FOrientation",FOrientation);
                object1.put("FWhiteBalance",FWhiteBalance);
                array1.put(object1);
            }catch (Exception e){

            }
        }catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    //上传文件方法
    public void upload(View v, final String path) {
        //String url = "http://172.16.137.198:8080/VueServer/upload";
        if (path != null && !path.equals("")) {
            String url = "http://v1.sznews.com/jtpic/uploads/streamupload";
            RequestParams params = new RequestParams(url);
            params.setMultipart(true);
            params.addBodyParameter("file", new File(path));
            try{
                String s = URLEncoder.encode(path.substring(path.lastIndexOf("/") + 1), "utf-8");
                params.addBodyParameter("EXIFInfo", array1.toString());
                params.addBodyParameter("pictureInfo", array2.toString());
            }catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            //设置上传提示框
            final ZLoadingDialog dialog = new ZLoadingDialog(NewBuildActivity.this);
            dialog.setLoadingBuilder(Z_TYPE.STAR_LOADING)//设置类型
                    .setLoadingColor(Color.BLACK)//颜色
                    .setHintText("上传中...")
                    .setCanceledOnTouchOutside(false)
                    .show();

            x.http().post(params, new Callback.CommonCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    System.out.println("===============上传成功"  + result);
                    dialog.dismiss();//上传成功后关闭提示框
                    Toast.makeText(NewBuildActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
                    finish();
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    System.out.println("===============上传失败");
                    ex.printStackTrace();
                    dialog.dismiss();//上传成功后关闭提示框
                    Toast.makeText(NewBuildActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancelled(CancelledException cex) {
                }

                @Override
                public void onFinished() {
                }
            });
        } else {
            Toast.makeText(this, "请选择图片，再上传", Toast.LENGTH_SHORT).show();
        }
    }

    //动态获取SD卡权限
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE" };
    public static void verifyStoragePermissions(Activity activity) {

        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class PictureAdapter1 extends PictureAdapter {
        public PictureAdapter1(Context context, List<Picture> lt) {
            super(context, lt);
        }
    }

    //判断图片小标题是否输为空
    public boolean PictureTitleIsEmpty(List<Picture> pictures){
        for(int i=0;pictures.size()>i;i++){
            if(pictures.get(i).getTitle() == null){
                return false;
            }else {
                return true;
            }
        }
        return true;
    }
}
