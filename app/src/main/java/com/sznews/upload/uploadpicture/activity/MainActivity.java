package com.sznews.upload.uploadpicture.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sznews.upload.uploadpicture.R;
import com.sznews.upload.uploadpicture.model.Result;
import com.sznews.upload.uploadpicture.url.InterfaceJsonfile;
import com.sznews.upload.uploadpicture.utils.CodeUtils;
import com.sznews.upload.uploadpicture.model.User;
import com.sznews.upload.uploadpicture.utils.Utils;

import org.xutils.view.annotation.ContentView;
import org.xutils.x;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    /*@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }*/
    private final int VERIFY_CODE_NO_NULL = 0;//获取验证码成功
    private final int LOGIN_SUCCESS = 1;//登录成功
    private final int LOGIN_FAIL = 2;//登录失败
    private final int NETWORK_ERROR = 3;//验证用户名密码时网络错误

    private ImageView imgVerify;
    private EditText username;
    private EditText password;
    private EditText verify;
    private CheckBox rememberPassword;
    private Button submit;
    private String usernameStr, passwordStr, verifyStr;
    private CodeUtils codeUtils;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0: Toast.makeText(getApplicationContext(), "登陆成功", Toast.LENGTH_SHORT).show();break;
                case 1: Toast.makeText(getApplicationContext(), "用户名或密码错误，请查证后重试！", Toast.LENGTH_SHORT).show();break;
                case -2: Toast.makeText(getApplicationContext(), "验证码失效，请重新获取后重试！", Toast.LENGTH_SHORT).show();break;
                case -3: Toast.makeText(getApplicationContext(), "验证码错误，请重新获取后重试！", Toast.LENGTH_SHORT).show();break;
                case 10: Toast.makeText(getApplicationContext(), "网络连接失败，请稍后重试！", Toast.LENGTH_SHORT).show();break;
                case 11: imgVerify.setImageBitmap((Bitmap) msg.obj);break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        imgVerify = (ImageView) findViewById(R.id.img_verify);
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        rememberPassword = (CheckBox) findViewById(R.id.cb_rememberpassword);
        //自动填写已保存的用户名，密码和保存状态
        SharedPreferences sp = this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        String usernameStr = sp.getString("username", "");
        username.setText(usernameStr);
        String passwordStr = sp.getString("password", "");
        password.setText(passwordStr);
        boolean isSave = sp.getBoolean("isSave", false);
        rememberPassword.setChecked(isSave);
        verify = (EditText) findViewById(R.id.verify);
        submit = (Button) findViewById(R.id.btn_login);
        submit.setOnClickListener(this);
        imgVerify.setOnClickListener(this);

        //获取机器码，并保存
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("deviceID", getDeviceID());
        editor.commit();
        System.out.println("+++++++++++++++++++++++++++++++++++机器码:" + getDeviceID());

        //进入页面时先自动获取验证码
        createVerifyImage();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_verify:
                //获取验证码图片
                createVerifyImage();
                break;
            case R.id.btn_login:
                /*Intent intent;
                intent = new Intent(MainActivity.this, UploadActivity.class);
                startActivity(intent);*/
                usernameStr = username.getText().toString().trim();
                passwordStr = password.getText().toString().trim();
                verifyStr = verify.getText().toString().trim();
                if (null == usernameStr || TextUtils.isEmpty(usernameStr)) {
                    Toast.makeText(this, "用户名不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (null == passwordStr || TextUtils.isEmpty(passwordStr)) {
                    Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                } /*else {
                    //验证密码格式
                    String regEx = "(?![0-9A-Z]+$)(?![0-9a-z]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{8,20}$";
                    Pattern pattern = Pattern.compile(regEx);
                    Matcher matcher = pattern.matcher(passwordStr);
                    boolean rs = matcher.matches();
                    if (rs == false) {
                        Toast.makeText(this, "密码是包含大小写字母以及数字的6到15位密码", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }*/
                //判断验证码是否为空
                if (null == verifyStr || TextUtils.isEmpty(verifyStr)) {
                    Toast.makeText(this, "请输入验证码", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //验证用户名密码和验证码
                            String result = verifyUsernameAndPassword();
                            //获取返回的登录情况
                            Gson gson = new GsonBuilder().create();
                            Result result1 = gson.fromJson(result, Result.class);
                            System.out.println("===========================" + result1.getState());
                            if (result1.getState().equals("1")) {
                                System.out.println("================================" + result1.getMsg());
                                Message msg = new Message();
                                msg.what = 1;
                                handler.sendMessage(msg);
                            } else if (result1.getState().equals("0")) {
                                System.out.println("================================登录成功！" + result1.getToken() + result1.getMsg());
                                System.out.println("================================登录成功！" + result1.getMsg());
                                SharedPreferences sp = getApplication().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putString("token", result1.getToken());
                                editor.putString("userid", result1.getMsg());
                                editor.commit();
                                Message msg = new Message();
                                msg.what = 0;
                                handler.sendMessage(msg);
                                //跳转到主页面
                                Intent intent;
                                intent = new Intent(MainActivity.this, UploadActivity.class);
                                System.out.println("跳转");
                                startActivity(intent);
                            } else if (result1.getState().equals("-2")){
                                Message msg = new Message();
                                msg.what = -2;
                                handler.sendMessage(msg);
                            } else if (result1.getState().equals("-3")){
                                Message msg = new Message();
                                msg.what = -3;
                                handler.sendMessage(msg);
                            } else {
                                System.out.println("================================登录失败,请稍后再尝试登录！");
                            }
                        }
                    }).start();
                    //保存用户信息
                    saveUserInfo();
                }
                //String code = codeUtils.getCode();
                //判断验证码的正确性，正确则提交用户信息
               /* if (verifyStr.equalsIgnoreCase(code)) {
                } else {
                    Toast.makeText(this, "验证码错误", Toast.LENGTH_SHORT).show();
                }*/
                break;
            default:
                break;
        }

    }

    //绘制验证码图片
    public void createVerifyImage() {
        SharedPreferences sp = this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        final String deviceID = sp.getString("deviceID", "");
        codeUtils = CodeUtils.getInstance();
        new Thread(new Runnable() {
            @Override
            public void run() {
                //Bitmap bitmap = codeUtils.createBitmap();
                //String code = codeUtils.getCode();
                Bitmap bitmap = codeUtils.getVerifyCodePictuer(deviceID);
                //判断验证码是否获取成功,成功则绘制验证码并显示，不成功则显示默认图片
                //if (code != null && !code.equals("")){
                if (bitmap != null){
                    Message msg = new Message();
                    msg.what = 11;
                    msg.obj = bitmap;
                    handler.sendMessage(msg);
                }
            }
        }).start();
        /*Bitmap bitmap = codeUtils.createBitmap();*/
    }

    //存储用户名、密码和保存状态
    public void saveUserInfo() {
        //默认自动存储用户名
        SharedPreferences sp = this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("username", usernameStr);
            //判断是否存储密码
            if (rememberPassword.isChecked()) {
                editor.putString("password", passwordStr);
                editor.putBoolean("isSave", true);
            } else {
            editor.putString("password", "");
            editor.putBoolean("isSave", false);
        }
        editor.commit();
    }

    //验证用户名密码
    public String verifyUsernameAndPassword() {
        //String path = "http://172.16.137.198:8080/VueServer/server";
        String path = "http://v1.sznews.com/appupload/user/loginhandle2";
        //String path = "http://172.16.140.123:10001/appuploader/user/loginhandle";
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
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            //conn.setRequestProperty("Content-Type", "application/json");
            //链接地址
            conn.connect();
            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            //发送参数
            User user = new User();
            user.setUsername(usernameStr);
            user.setUserpass(passwordStr);
            String signature = Utils.getSignature(user);
            System.out.println(signature);
            StringBuffer sb = new StringBuffer();
            SharedPreferences sp = this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
            String deviceID = sp.getString("deviceID", "");
            sb.append("{\"username\":\"").append(usernameStr).append("\"")
                    .append(",\"userpass\":\"").append(passwordStr).append("\"")
                    .append(",\"appid\":\"").append(user.getAppid()).append("\"")
                    .append(",\"signature\":\"").append(signature).append("\"")
                    .append(",\"timestamp\":\"").append(user.getTimestamp()).append("\"")
                    .append(",\"checkcode\":\"").append(verifyStr).append("\"")
                    .append(",\"guid\":\"").append(deviceID).append("\"")
                    .append(",\"nonce\":\"").append(user.getNonce()).append("\"")
                    .append(",\"appsecret\":\"").append(user.getAppsecret()).append("\"")
                    .append("}");
            System.out.println(sb.toString());
            writer.write(sb.toString());
            //刷新缓冲区使数据写入基础流，并清理当前编辑器的左右缓冲区
            writer.flush();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    conn.getInputStream()));
            lines = reader.readLine();//读取请求结果
            System.out.println("++++++++++++++++++++++++++" + lines);
            reader.close();
            conn.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
            Message msg = new Message();
            msg.what = 10;
            handler.sendMessage(msg);
        }
        return lines;
    }

    @SuppressLint("MissingPermission")
    private String getDeviceID(){
        final TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
        final String tmDevice, tmSerial, tmPhone, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
        String uniqueId = deviceUuid.toString();
        return uniqueId;
    }

}
