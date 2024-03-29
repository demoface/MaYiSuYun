package com.mayikeji.shoujibaidu.utils;

import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;

import com.mayikeji.shoujibaidu.application.ClientApplication;
import com.mayikeji.shoujibaidu.helper.HttpHelper;
import com.mayikeji.shoujibaidu.helper.UiHelper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/** 
 * 带日志文件输入的，又可控开关的日志调试 
 *  
 * @author CarlJay
 * @version 1.0 
 * @data 2012-2-20 
 */  
public class MyLog {  
    private static Boolean MYLOG_SWITCH=true; // 日志文件总开关  
    private static Boolean MYLOG_WRITE_TO_FILE=true;// 日志写入文件开关  
    private static char MYLOG_TYPE='v';// 输入日志类型，w代表只输出告警信息等，v代表输出所有信息  
    private static String MYLOG_PATH_SDCARD_DIR = Environment.getExternalStorageDirectory().getPath();// 日志文件在sdcard中的路径
    private static int SDCARD_LOG_FILE_SAVE_DAYS = 0;// sd卡中日志文件的最多保存天数  
    private static String MYLOGFILEName = "HongBaoLog.txt";// 本类输出的日志文件名称
    private static SimpleDateFormat myLogSdf = new SimpleDateFormat(  
            "yyyy-MM-dd HH:mm:ss");// 日志的输出格式  
    private static SimpleDateFormat logfile = new SimpleDateFormat("yyyy-MM-dd");// 日志文件格式  
  
    public static void w(String tag, Object msg) { // 警告信息  
        log(tag, msg.toString(), 'w');  
    }  
  
    public static void e(String tag, Object msg) { // 错误信息  
        log(tag, msg.toString(), 'e');  
    }  
  
    public static void d(String tag, Object msg) {// 调试信息  
        log(tag, msg.toString(), 'd');  
    }  
  
    public static void i(String tag, Object msg) {//  
        log(tag, msg.toString(), 'i');  
    }  
  
    public static void v(String tag, Object msg) {  
        log(tag, msg.toString(), 'v');  
    }  
  
    public static void w(String tag, String text) {  
        log(tag, text, 'w');  
    }  
  
    public static void e(String tag, String text) {  
        log(tag, text, 'e');  
    }  
  
    public static void d(String tag, String text) {  
        log(tag, text, 'd');  
    }  
  
    public static void i(String tag, String text) {  
        log(tag, text, 'i');  
    }  
  
    public static void v(String tag, String text) {  
        log(tag, text, 'v');  
    }  
  
    /** 
     * 根据tag, msg和等级，输出日志 
     *  
     * @param tag 
     * @param msg 
     * @param level 
     * @return void 
     * @since v 1.0 
     */  
    private static void log(String tag, String msg, char level) {  
        if (MYLOG_SWITCH) {  
            if ('e' == level && ('e' == MYLOG_TYPE || 'v' == MYLOG_TYPE)) { // 输出错误信息  
                Log.e(tag, msg);  
            } else if ('w' == level && ('w' == MYLOG_TYPE || 'v' == MYLOG_TYPE)) {  
                Log.w(tag, msg);  
            } else if ('d' == level && ('d' == MYLOG_TYPE || 'v' == MYLOG_TYPE)) {  
                Log.d(tag, msg);  
            } else if ('i' == level && ('d' == MYLOG_TYPE || 'v' == MYLOG_TYPE)) {  
                Log.i(tag, msg);  
            } else {  
                Log.v(tag, msg);  
            }  
            if (MYLOG_WRITE_TO_FILE)  
                writeLogtoFile(String.valueOf(level), tag, msg);  
        }  
    }  
  
    /** 
     * 打开日志文件并写入日志 
     *  
     * @return 
     * **/  
    private static void writeLogtoFile(String mylogtype, String tag, String text) {// 新建或打开日志文件
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String date = sDateFormat.format(new Date());
        Date nowtime = new Date();
        String versionName = "未获取到";
        try {
            String pkName = ClientApplication.getInstance().getPackageName();
            versionName = ClientApplication.getInstance().getPackageManager().getPackageInfo(
                    pkName, 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String needWriteMessage = myLogSdf.format(nowtime) + "  "+ android.os.Build.MODEL + ","
                + android.os.Build.VERSION.RELEASE
                + "--" + versionName + "：" + text + "\n";
        //网络日志
//        HttpHelper.getInstance().log();
        //本地日志
        File file = new File(MYLOG_PATH_SDCARD_DIR,date + MYLOGFILEName);
        try {
            FileWriter filerWriter = new FileWriter(file, true);//后面这个参数代表是不是要接上文件中原来的数据，不进行覆盖
            BufferedWriter bufWriter = new BufferedWriter(filerWriter);
            bufWriter.write(needWriteMessage);
            bufWriter.newLine();
            bufWriter.close();
            filerWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void uploadLog(){
        UiHelper.getInstance().runOnThread(new Runnable() {
            @Override
            public void run() {
                BufferedReader reader = null;
                try {
                    SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    String date = sDateFormat.format(new Date());
                    File file = new File(MYLOG_PATH_SDCARD_DIR,date + MYLOGFILEName);

                    reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                    String l;
                    StringBuffer sb = new StringBuffer();
                    while ((l = reader.readLine()) != null) {
                        sb.append(l);
                    }
                    String post = "content=" + sb.toString();
                    String tel = ClientApplication.sp.getString("username", "");
                    post = post + "&mobile=" + tel;
                    String data = OkHttpUtils.getInstance().sendPost(HttpHelper.BaseUrl+"index.php/api/app/record_log_new/mobile/",post) ;
                    if (data != null) {
                        UiHelper.showToast(ClientApplication.getInstance(),"提交完成");
                        file.delete();
                    }
                    Log.e("TAG",data);
                } catch (Exception e) {
                    UiHelper.showToast(ClientApplication.getInstance(),"暂无日志");
                    e.printStackTrace();
                }finally {
                    try {
                        if (reader!= null)
                            reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
    /** 
     * 删除制定的日志文件 
     * */  
    public static void delFile() {// 删除日志文件  
        String needDelFiel = logfile.format(getDateBefore());  
        File file = new File(MYLOG_PATH_SDCARD_DIR, needDelFiel + MYLOGFILEName);  
        if (file.exists()) {  
            file.delete();  
        }  
    }  
  
    /** 
     * 得到现在时间前的几天日期，用来得到需要删除的日志文件名 
     * */  
    private static Date getDateBefore() {  
        Date nowtime = new Date();  
        Calendar now = Calendar.getInstance();  
        now.setTime(nowtime);  
        now.set(Calendar.DATE, now.get(Calendar.DATE)  
                - SDCARD_LOG_FILE_SAVE_DAYS);  
        return now.getTime();  
    }  
  
}  