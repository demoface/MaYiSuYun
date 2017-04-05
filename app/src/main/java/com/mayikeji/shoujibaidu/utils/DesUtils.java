package com.mayikeji.shoujibaidu.utils;

import android.util.Base64;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 * Created by Administrator on 2016/12/11.
 */

public class DesUtils {

    private static final String key = "@#sd45df" ;

    /***
     * decode by Base64
     */
    public static byte[] decodeBase64(String input) {
//        Class clazz;
//        try {
//            clazz = Class.forName("com.sun.org.apache.xerces.internal.impl.dv.util.Base64");
//            Method mainMethod = clazz.getMethod("decode", String.class);
//            mainMethod.setAccessible(true);
//            Object retObj = mainMethod.invoke(null, input);
//            return (byte[]) retObj;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }

        return Base64.decode(input , Base64.DEFAULT) ;

    }

    public static byte[] decrypt(byte[] src, String password) {
        // DES算法要求有一个可信任的随机数源
        SecureRandom random = new SecureRandom();
        // 创建一个DESKeySpec对象
        DESKeySpec desKey;
        try {
            desKey = new DESKeySpec(password.getBytes());
            // 创建一个密匙工厂
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            // 将DESKeySpec对象转换成SecretKey对象
            SecretKey securekey = keyFactory.generateSecret(desKey);
            // Cipher对象实际完成解密操作
            Cipher cipher = Cipher.getInstance("DES/ECB/NoPadding");
            // 用密匙初始化Cipher对象
            cipher.init(Cipher.DECRYPT_MODE, securekey, random);
            // 真正开始解密操作
            return cipher.doFinal(src);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }

    }

    public static String encrypt(String input) {
        byte[] data = encrypt(input.getBytes() , key) ;
        return Base64.encodeToString(data , Base64.DEFAULT) ;
    }

    /**
     * 加密
     *
     * @param datasource byte[]
     * @param password   String
     * @return byte[]
     */
    public static byte[] encrypt(byte[] datasource, String password) {
        try {
            SecureRandom random = new SecureRandom();
            DESKeySpec desKey = new DESKeySpec(password.getBytes());
            //创建一个密匙工厂，然后用它把DESKeySpec转换成
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey securekey = keyFactory.generateSecret(desKey);
            //Cipher对象实际完成加密操作
            Cipher cipher = Cipher.getInstance("DES");
            //用密匙初始化Cipher对象
            cipher.init(Cipher.ENCRYPT_MODE, securekey, random);
            //现在，获取数据并加密
            //正式执行加密操作
            return cipher.doFinal(datasource);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }
}
