package com.mayikeji.mayisuyun.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Everly on 2017/1/23.
 */

public class ShareUtils {

    public static final String NAME = "config";

    //string
    public static void putString(Context mContext,String key,String values){
          SharedPreferences sp = mContext.getSharedPreferences(NAME,Context.MODE_PRIVATE);
          sp.edit().putString(key,values).commit();
      }

    public static String getString(Context mContext,String key,String defValue){
        SharedPreferences sp = mContext.getSharedPreferences(NAME,Context.MODE_PRIVATE);
        return sp.getString(key, defValue);
    }

    //int
    public static void putInt(Context mContext,String key,int values){
        SharedPreferences sp = mContext.getSharedPreferences(NAME,Context.MODE_PRIVATE);
        sp.edit().putInt(key,values).commit();
    }


    public static int getInt(Context mContext, String key, int defValue){
        SharedPreferences sp = mContext.getSharedPreferences(NAME,Context.MODE_PRIVATE);
        return sp.getInt(key, defValue);
    }

    //Boolean
    public static void putBoolean(Context mContext,String key,Boolean values){
        SharedPreferences sp = mContext.getSharedPreferences(NAME,Context.MODE_PRIVATE);
        sp.edit().putBoolean(key,values).commit();
    }


    public static Boolean getBoolean(Context mContext, String key, Boolean defValue){
        SharedPreferences sp = mContext.getSharedPreferences(NAME,Context.MODE_PRIVATE);
        return sp.getBoolean(key, defValue);
    }

    //删除单个
    public static void deleShare(Context mContext,String key){
        SharedPreferences sp = mContext.getSharedPreferences(NAME,Context.MODE_PRIVATE);
        sp.edit().remove(key).commit();
    }

    //删除全部
    public static void deleAll(Context mContext){
        SharedPreferences sp = mContext.getSharedPreferences(NAME,Context.MODE_PRIVATE);
        sp.edit().clear().commit();
    }
}
