package com.sznews.upload.uploadpicture.utils;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sznews.upload.uploadpicture.model.User;
import com.sznews.upload.uploadpicture.url.InterfaceJsonfile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

public class CodeUtils {

    private static CodeUtils mCodeUtils;
    private int mPaddingLeft, mPaddingTop;
    private Random mRandom = new Random();

    private String verifyCode = null;

    //Default Settings
    private static final int DEFAULT_FONT_SIZE = 60;//字体大小
    private static final int DEFAULT_LINE_NUMBER = 3;//多少条干扰线
    private static final int BASE_PADDING_LEFT = 20; //左边距
    private static final int RANGE_PADDING_LEFT = 30;//左边距范围值
    private static final int BASE_PADDING_TOP = 70;//上边距
    private static final int RANGE_PADDING_TOP = 15;//上边距范围值
    private static final int DEFAULT_WIDTH = 300;//默认宽度.图片的总宽
    private static final int DEFAULT_HEIGHT = 100;//默认高度.图片的总高
    private static final int DEFAULT_COLOR = 0xDF;//默认背景颜色值

    private String code;

    public static CodeUtils getInstance() {
        if (mCodeUtils == null) {
            mCodeUtils = new CodeUtils();
        }
        return mCodeUtils;
    }

    //生成验证码图片
    public Bitmap createBitmap() {
        mPaddingLeft = 0; //每次生成验证码图片时初始化
        mPaddingTop = 0;

        Bitmap bitmap = Bitmap.createBitmap(DEFAULT_WIDTH, DEFAULT_HEIGHT, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        code = createCode();

        canvas.drawColor(Color.rgb(DEFAULT_COLOR, DEFAULT_COLOR, DEFAULT_COLOR));
        Paint paint = new Paint();
        paint.setTextSize(DEFAULT_FONT_SIZE);

        //若code为空则不绘制
        if (code != null && !code.equals("")){
            for (int i = 0; i < code.length(); i++) {
                randomTextStyle(paint);
                randomPadding();
                canvas.drawText(code.charAt(i) + "", mPaddingLeft, mPaddingTop, paint);
            }
        }

        //干扰线
        for (int i = 0; i < DEFAULT_LINE_NUMBER; i++) {
            drawLine(canvas, paint);
        }

        canvas.save();//保存
        canvas.restore();
        return bitmap;
    }

    /**
     * 得到图片中的验证码字符串
     *
     * @return
     */
    public String getCode() {
        return verifyCode;
    }

    //获取验证码
    public String createCode() {
        verifyCode = getVerifyCode(InterfaceJsonfile.VerifyCode);
        Gson gson = new GsonBuilder().create();
        User user = gson.fromJson(verifyCode, User.class);
        if (user == null) {
            verifyCode = null;
            return verifyCode;
        }else {
            verifyCode = user.getScode();
//            System.out.println("=========================verifyCode：" + verifyCode);
            return verifyCode;
        }
    }

    //生成干扰线
    private void drawLine(Canvas canvas, Paint paint) {
        int color = randomColor();
        int startX = mRandom.nextInt(DEFAULT_WIDTH);
        int startY = mRandom.nextInt(DEFAULT_HEIGHT);
        int stopX = mRandom.nextInt(DEFAULT_WIDTH);
        int stopY = mRandom.nextInt(DEFAULT_HEIGHT);
        paint.setStrokeWidth(1);
        paint.setColor(color);
        canvas.drawLine(startX, startY, stopX, stopY, paint);
    }

    //随机颜色
    private int randomColor() {
        StringBuffer mBuilder = new StringBuffer();
        mBuilder.delete(0, mBuilder.length()); //使用之前首先清空内容

        String haxString;
        for (int i = 0; i < 3; i++) {
            haxString = Integer.toHexString(mRandom.nextInt(0xFF));
            if (haxString.length() == 1) {
                haxString = "0" + haxString;
            }
            mBuilder.append(haxString);
        }

        return Color.parseColor("#" + mBuilder.toString());
    }

    //随机文本样式
    private void randomTextStyle(Paint paint) {
        int color = randomColor();
        paint.setColor(color);
        paint.setFakeBoldText(mRandom.nextBoolean());  //true为粗体，false为非粗体
        float skewX = mRandom.nextInt(11) / 10;
        skewX = mRandom.nextBoolean() ? skewX : -skewX;
        paint.setTextSkewX(skewX); //float类型参数，负数表示右斜，整数左斜
//        paint.setUnderlineText(true); //true为下划线，false为非下划线
//        paint.setStrikeThruText(true); //true为删除线，false为非删除线
    }

    //随机间距
    private void randomPadding() {
        mPaddingLeft += BASE_PADDING_LEFT + mRandom.nextInt(RANGE_PADDING_LEFT);
        mPaddingTop = BASE_PADDING_TOP + mRandom.nextInt(RANGE_PADDING_TOP);
    }

    /**
     * 获取验证码
     * @param path
     * @return 获取到的验证码
     */
    private String getVerifyCode(String path) {
        try {
            URL url = new URL(path.trim());
            //打开连接
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            if(200 == urlConnection.getResponseCode()){
                //得到输入流
                InputStream is =urlConnection.getInputStream();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int len = 0;
                while(-1 != (len = is.read(buffer))){
                    baos.write(buffer,0,len);
                    baos.flush();
                }
                return baos.toString("utf-8");
            }
        }  catch (IOException e) {
            e.printStackTrace();
        } /*catch (Exception e) {
            e.printStackTrace();
            System.out.println("==========================");
        }*/
        return null;
    }

    /**
     * 获取验证码
     * @return 获取到的验证码图片
     */
    public Bitmap getVerifyCodePictuer() {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL("http://v1.sznews.com/appupload/User/GetCheckCode?guid=1234567890").openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("GET");
            if(conn.getResponseCode() == 200){
                InputStream inputStream = conn.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                inputStream.close();
                System.out.println("++++++++++++++++++++++++++++++++++++获取图片成功");
                return bitmap;
            }
            conn.disconnect();
            System.out.println("++++++++++++++++++++++++++++++++++++获取图片失败1");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("++++++++++++++++++++++++++++++++++++获取图片失败2");
        } finally {

        }
        return null;
    }
}
