package com.sznews.upload.uploadpicture.utils;

import com.sznews.upload.uploadpicture.model.User;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;


import java.io.UnsupportedEncodingException;

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
        StringBuffer paramBuffer = new StringBuffer();
        /*for (String key : keys) {
            paramBuffer.append(key).append(params.get(key) == null ? "" : params.get(key));
        }*/
        paramBuffer.append("username").append(user.getUsername())
        .append("userpass").append(user.getUsername())
        .append("timestamp").append(user.getTimestamp())
        .append("nonce").append(user.getNonce());
        System.out.println(paramBuffer.toString());
        // MD5是128位长度的摘要算法，用16进制表示，一个十六进制的字符能表示4个位，所以签名后的字符串长度固定为32个十六进制字符。
        byte[] str = null;
        try{
            str = paramBuffer.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return new String(Hex.encodeHex(DigestUtils.md5(str)));
    }

}
