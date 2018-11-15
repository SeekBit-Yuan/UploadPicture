package com.sznews.upload.uploadpicture.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sznews.upload.uploadpicture.R;
import com.sznews.upload.uploadpicture.adapter.PictureAdapter;
import com.sznews.upload.uploadpicture.model.Picture;
import com.sznews.upload.uploadpicture.model.Result;
import com.sznews.upload.uploadpicture.url.InterfaceJsonfile;
import com.sznews.upload.uploadpicture.db.ThemeDBHelper;
import com.zyao89.view.zloading.ZLoadingDialog;
import com.zyao89.view.zloading.Z_TYPE;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
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
    private ArrayAdapter<String> adapter1, adapter2;
    private EditText editText1;
    private TextView textView1;
    private TextView editText2;
    private TextView textView2;
    private EditText editText3;
    private String spinnerselect1;
    private String spinnerselect2;
    private String title;
    private String datetime;
    private String source;
    private String username;
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
    //主题ID
    private int dutyid;
    //SQLite
    private SQLiteDatabase db;

    //添加按钮点击次数
    int addnum = 0;

    //第一个分类
    int num;

    //userid
    int uid = 0;

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
                username = textView2.getText().toString().trim();
                explain = editText3.getText().toString().trim();

                if(null == title || TextUtils.isEmpty(title)){
                    Toast.makeText(NewBuildActivity.this, "请输入标题", Toast.LENGTH_SHORT).show();
                    return;
                }else if(null == source || TextUtils.isEmpty(source)){
                    Toast.makeText(NewBuildActivity.this, "请输入来源", Toast.LENGTH_SHORT).show();
                    return;
                }else if(null == explain || TextUtils.isEmpty(explain)){
                    Toast.makeText(NewBuildActivity.this, "请输入说明", Toast.LENGTH_SHORT).show();
                    return;
                }else if(addnum == 0 || PictureList.size() == 0){
                    Toast.makeText(NewBuildActivity.this, "请添加图片", Toast.LENGTH_SHORT).show();
                    return;
                }else if(!PictureTitleIsEmpty(PictureList)){
                    Toast.makeText(NewBuildActivity.this, "请添加图片标题", Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String result = GetDutyid();
                            //获取返回的登录情况
                            Gson gson = new GsonBuilder().create();
                            Result result1 = gson.fromJson(result, Result.class);

                            if (result1.getState().equals("1")) {
                                System.out.println("dutyid获取失败");
                            } else if (result1.getState().equals("0")) {
                                dutyid = Integer.parseInt(result1.getDutyid());
//                            System.out.println("dutyid:"+dutyid);
                                if(dutyid != 0){
                                    //插入数据库
                                    ThemeDBHelper themeDBHelper = new ThemeDBHelper(NewBuildActivity.this);
                                    db = themeDBHelper.getWritableDatabase();

                                    //插入主题信息
                                    ContentValues values = new ContentValues();
                                    values.put("category1", spinnerselect1);
                                    values.put("category2", spinnerselect2);
                                    values.put("theme", title);
                                    values.put("create_date", datetime);
                                    values.put("soucre", source);
                                    values.put("username", username);
                                    values.put("uid", uid);
                                    values.put("description", explain);
                                    values.put("path", PictureList.get(0).getPic_path());
                                    values.put("total",PictureList.size());
                                    values.put("uploaded",0);
                                    values.put("dutyid", dutyid);
                                    values.put("state", 1);
                                    //参数依次是：表名，强行插入null值得数据列的列名，一行记录的数据
                                    db.insert("subject", null, values);

                                    //插入图片信息
                                    for(int i = 0;PictureList.size() > i;i++){
                                        ContentValues values1 = new ContentValues();
                                        values1.put("path", PictureList.get(i).getPic_path());
                                        values1.put("title", PictureList.get(i).getTitle());
                                        values1.put("dutyid", dutyid);
                                        values1.put("state", 1);
                                        db.insert("picture", null, values1);
                                    }

                                    //通过跳转回UploadActivity再切换到UploadlistFragment
                                    Intent intent = new Intent(NewBuildActivity.this, UploadActivity.class);
                                    intent.putExtra("id",dutyid);
                                    startActivity(intent);
                                }
                            }
                        }
                    }).start();
                }
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

        final String[] category1 = { "政治", "时政" ,"经济", "军事","体育", "科技", "社会" ,"生活", "民生","传媒业", "服务业" ,"教育", "商业","司法", "游戏" ,"综合竞技", "地产","工业", "金融" ,"理财", "农业","娱乐", "电商" ,"国际关系", "旅游","汽车", "文化" ,"运输物流", "动漫","健康", "美食" ,"人文", "医疗卫生","其他"};
        final String[][] category2 = {
                { "人事任免", "应急预警", "政务动态", "政策法规", "政策解读", "理论", "统计公开"},
                { "中国革命与建设问题", "国家元首、地区长官","国家概况、地区概况", "政治运动、政治事件", "时局", "统一战线组织", "议会" },
                { "中国区域经济", "企业管理", "企业类型", "双边经济关系", "国际区域经济与经济一体化", "国际经济关系","宏观经济", "对外经济援助", "工商行政管理","经济会议", "经济体制与所有制", "经济博览会","经济发展规划", "经济对外开放", "经济理论研究","经济社会发展战略与发展模式", "经济管理", "经济结构"},
                { "军事史", "军事百科","军事评论", "国内军情","国防部", "国际军情"},
                { "中国足球", "体育动态", "国际足球", "彩票"},
                { "业界动态", "互联网", "创业", "手机","数码", "智能硬件", "科学", "通信" },
                { "公益", "城建", "奇闻趣事", "灾害", "环保"},
                { "奢侈品", "宠物", "情感", "时装", "星座", "美容" },
                { "人力资源", "人口、计划生育", "劳动、就业", "恋爱、婚姻、家庭", "民族、种族", "社会、劳动其它", "社会事业", "社会建设", "社会生活", "社会群体、社会阶层、社会团体", "社会行为、社会关系", "社会问题" },
                { "传媒市场", "传媒科技", "出版、印刷、发行业", "广告业", "广播、电视业", "报刊业", "新媒体", "新闻业", "网络媒体", "通讯社" },
                { "专业技术服务业", "住宿服务业", "保安服务业", "修理服务业", "商务服务业", "外包服务", "服务业", "服务业、旅游业其它", "生活服务业", "租赁业", "餐饮业" },
                { "中小学教育", "农村教育", "培训考试", "婴幼儿教育", "教育动态", "教育扶贫", "民办教育", "留学教育", "职业教育", "高等教育" },
                { "产权交易", "商业流通业", "商品交易会","商品价格与流通费用", "商品市场", "商品流通","对外贸易", "海关", " 物资" },
                { "法律", "法治" },
                { "其他", "手游", "电竞", "网游", "行业资讯" },
                { "体育奖", "体育比赛项目", "体育记录", "体育运动会", "其他"},
                { "家具", "建筑", "房产" },
                { "交通运输设备业", "化学原料及化学制品业", "化学纤维业", "医药业", "地热与热力", "家用电器业","工业经济学研究", "机械业", "水利", "水务", "清洁能源", "煤炭产业", "电力产业", "电气机械及电工器材业", "石油、天然气产业", "矿业", "纺织及服装、鞋帽业", "能源", "能源、水务、水利其它", "节能", "金属冶炼及压延加工业", "食品、饮料业" },
                { "投资、融资", "财政", "货币", "金融业", "银行业", "非银行金融业" },
                { "保险业", "信托业", "债券市场", "基金市场", "外汇、黄金", "期货市场", "股票市场", "证券业", "金融衍生品市场" },
                { "三农问题", "农业、农村其它", "农业与农村经济", "农业基本建设", "农业改革与农村改革", "农业服务业","农业生产", "农产品加工", "农作物灾害与防治", "农林牧渔科技", "林业", "渔业", "畜牧业" },
                { "娱乐资讯", "娱评", "演出", "电影", "电视剧", "音乐" },
                { "公司人物", "电商动态", "行业观察" },
                { "国际会议", "国际关系", "国际性组织", "国际性问题", "国际日", "国际条约、协定", "对华反应", "对外关系", "对外关系、国际关系其它", "对外关系沿革、现状", "对外战略、政策", "涉外机构与涉外事务", "联合国", "领土问题" },
                { "攻略", "资讯动态" },
                { "导购", "新车", "行业动态", "评测", "养护"},
                { "世界遗产", "人文与社会科学", "传统文化传承", "博物馆业", "图书馆业", "文化", "文化场馆、纪念地", "档案业", "民俗","语言文字" },
                { "交通运输", "快递业", "桥梁、隧道", "水路运输业", "物流业", "航空运输业", "道路运输业", "邮政业", "铁路运输业" },
                { "动态", "动画", "周边","漫画"},
                { "健身", "养生", "育儿" },
                { "美食资讯", "食品健康", "食谱" },
                { "哲学", "宗教", "文学", "艺术" },
                { "中国少数民族医药", "传统医药", "公共卫生", "其他", "医学科学研究", "医药、卫生其它", "医药卫生体制", "医药卫生管理", "生殖与性健康", "疾病与治疗", "药典"},
                { "其他" },
        };

        adapter1 = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, category1);
        adapter2 = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, category2[0]);
        spinner1.setAdapter(adapter1);
        spinner2.setAdapter(adapter2);

        spinner1.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                spinnerselect1 = NewBuildActivity.this.getResources().getStringArray(R.array.data1)[position];
                adapter2 = new ArrayAdapter<String>(NewBuildActivity.this,android.R.layout.simple_spinner_dropdown_item, category2[position]);
                spinner2.setAdapter(adapter2);
                spinnerselect1 = category1[position];
                num = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner2.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                spinnerselect2 = NewBuildActivity.this.getResources().getStringArray(R.array.data2)[position];
                spinnerselect2 = category2[num][position];
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
        //获取uid
        uid = Integer.parseInt(sp.getString("uid", ""));

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
                username = textView2.getText().toString().trim();
                explain = editText3.getText().toString().trim();

                try {
                    object2.put("category1",spinnerselect1);
                    object2.put("category",spinnerselect2);
                    object2.put("title",title);
                    object2.put("datetime",datetime);
                    object2.put("source",source);
                    object2.put("explain",explain);
                    array2.put(object2);
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

//    //请求dutyid
//    public void GetDutyid(){
//        JSONObject object = new JSONObject();
//        JSONArray array = new JSONArray();
//        try {
//            object.put("username",username);
//            object.put("userid",String.valueOf(uid));
//            array.put(object);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        RequestParams params = new RequestParams("http://v1.sznews.com/appupload/Duty/CreateDuty");
//        params.addQueryStringParameter("username",object.toString());
////        params.addQueryStringParameter("userid",String.valueOf(uid));
//        x.http().post(params, new Callback.CommonCallback<String>() {
//            @Override
//            public void onSuccess(String result) {
//                dutyid = Integer.parseInt(result);
//                System.out.println("dutyid:"+dutyid);
//            }
//
//            @Override
//            public void onError(Throwable ex, boolean isOnCallback) {
//                Toast.makeText(NewBuildActivity.this, "请求失败", Toast.LENGTH_SHORT).show();
//                System.out.println("ex:"+ ex);
//            }
//
//            @Override
//            public void onCancelled(CancelledException cex) {
//            }
//
//            @Override
//            public void onFinished() {
//            }
//        });
//    }

    //获取dutyid
    public String GetDutyid() {
        String path = InterfaceJsonfile.DutyID;
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
            StringBuffer sb = new StringBuffer();
            sb.append("{\"username\":\"").append(username).append("\"")
                    .append(",\"userid\":\"").append(String.valueOf(uid)).append("\"")
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
            System.out.println("IOException:"+e);
        }
        return lines;
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
