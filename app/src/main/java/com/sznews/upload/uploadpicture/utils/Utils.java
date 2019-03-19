package com.sznews.upload.uploadpicture.utils;

import com.sznews.upload.uploadpicture.model.User;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author:hytao
 * Time:2018/10/19  16:14
 * Descrrption:工具类
 */
public class Utils {

    /**
     * 生成签名信息
     * @param user User类
     * @return
     */
    public static String getSignature(User user) {
        if (user == null) {
            return "";
        }
        //StringBuffer paramBuffer = new StringBuffer();
        Map<String, String> map = new HashMap<>();
        map.put("username", URLEncoder.encode(user.getUsername()).toLowerCase());
        map.put("userpass", user.getUserpass());
        map.put("appid", user.getAppid());
        map.put("nonce", user.getNonce());
        map.put("timestamp", user.getTimestamp());
        map.put("appsecret", user.getAppsecret());
        String str = getSign(user.getAppsecret(), map);//获取加密过后的签名
        /*paramBuffer
                .append("appid").append(user.getAppid())
                .append("nonce").append(user.getNonce())
                .append("timestamp").append(user.getTimestamp())
                .append("username").append(user.getUsername())
                .append("userpass").append(user.getUserpass())
                .append("appsecret").append(user.getAppsecret());
        System.out.println(paramBuffer.toString());
        // MD5是128位长度的摘要算法，用16进制表示，一个十六进制的字符能表示4个位，所以签名后的字符串长度固定为32个十六进制字符。
        byte[] str1 = null;
        try{
            str1 = str.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }*/
        return str; //new String(Hex.encodeHex(DigestUtils.md5(str)))
    }

    /**
     * 生成签名
     * @param map
     * @return
     */
    public static String getSign(String appsecret, Map<String, String> map) {

        String result = "";
        try {
            List<Map.Entry<String, String>> infoIds = new ArrayList<Map.Entry<String, String>>(map.entrySet());
            // 对所有传入参数按照字段名的 ASCII 码从小到大排序（字典序）
            Collections.sort(infoIds, new Comparator<Map.Entry<String, String>>() {
                public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
                    return (o1.getKey()).toString().compareTo(o2.getKey());
                }
            });
            // 构造签名键值对的格式
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, String> item : infoIds) {
                if (item.getKey() != null || item.getKey() != "") {
                    String key = item.getKey();
                    String val = item.getValue();
                    if (!(val == "" || val == null)) {
                        sb.append(key + val);
                    }
                }
            }
            sb.append(appsecret);
            result = sb.toString();
            System.out.println(result);
            //进行MD5加密
            byte[] str = null;
            try{
                str = result.getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            result = new String (Hex.encodeHex(DigestUtils.md5(str)));
        } catch (Exception e) {
            return null;
        }
        return result;
    }

}
