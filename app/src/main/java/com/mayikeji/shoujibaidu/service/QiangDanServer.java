package com.mayikeji.shoujibaidu.service;

import android.app.ActivityManager;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.mayikeji.shoujibaidu.application.ClientApplication;
import com.mayikeji.shoujibaidu.bean.Result;
import com.mayikeji.shoujibaidu.helper.HttpHelper;
import com.mayikeji.shoujibaidu.ui.MainActivity;
import com.mayikeji.shoujibaidu.utils.ShareUtils;

import java.util.List;

/**
 * author : solon
 * date: on 16/12/14.
 */

public abstract class QiangDanServer {

    public OnQiangDanListener mListener ;

    public abstract void onAccessibilityEvent(AccessibilityEvent event, AccessibilityNodeInfo rootInActiveWindow);
    public abstract void onEvent(AccessibilityNodeInfo rootInActiveWindow);
    public abstract void startActivity();
    public abstract boolean isTop() ;
    public abstract String getPackageName() ;
    public int[] getPrice() {
        return new int[]{ShareUtils.getInt(MainActivity.context,"min_price",0), ShareUtils.getInt(MainActivity.context,"max_price",0)};
    }

    public int[] getDis() {
        return new int[]{ShareUtils.getInt(MainActivity.context,"min_dis",0), ShareUtils.getInt(MainActivity.context,"max_dis",0)};
    }

    public QiangDanServer(OnQiangDanListener listener) {
        mListener = listener ;
    }

    public void commit(String price , String source ) {
        if (BaseServer.servers.size() > 1) {
            BaseServer.isRunning = false;
        }
        HttpHelper.getInstance().commit(price, source, ClientApplication.uid, new HttpHelper.CallBack<Result<String>>() {
            @Override
            public void onResult(Result<String> result) {
                if (result == null) {
                    Log.d("TAG" , "请求失败") ;
                    if (!getPackageName().equals("com.cxyw.suyun.ui")) {
                        Intent intent = new Intent("server_message");
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("result", true);
                        intent.putExtras(bundle);
                        LocalBroadcastManager.getInstance(MainActivity.context).sendBroadcast(intent);
                    }

                    return;
                }

                if (result.getCode() == 100) {
                    Log.d("TAG" , "提交成功") ;
                } else {
                    Log.d("TAG" , "提交失败") ;
                }

                //应客户要求 58速运 抢到订单 不停止抢单 手动控制停止  货啦拉抢到订单就停止
                if (BaseServer.servers.size() > 1 || getPackageName().equals("com.lalamove.huolala.driver")) {
                    Intent intent = new Intent("server_message");
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("result", true);
                    intent.putExtras(bundle);
                    LocalBroadcastManager.getInstance(MainActivity.context).sendBroadcast(intent);
//                mListener.onGetOrder();
                }


            }
        });
    }

    public boolean isBackground(Context context , String packName) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(packName)) {
                if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {
                    Log.i("后台", appProcess.processName);
                    return true;
                }else{
                    Log.i("前台", appProcess.processName);
                    return false;
                }
            }
        }
        return false;
    }
    public static String getTopActivityName(){
        AccessibilityNodeInfo rootInActiveWindow = BaseServer.mInstance.getRootInActiveWindow();
        CharSequence className = rootInActiveWindow.getPackageName();
        return className.toString();
    }
    public static boolean isTopActivity(String package_name){
        String topActivityName = getTopActivityName();
        if (topActivityName.contains(package_name)){
            return true;
        }
        return false;
    }
    private static String getLauncherTopApp() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            ActivityManager activityManager = (ActivityManager) ClientApplication.getInstance().getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> appTasks = activityManager.getRunningTasks(1);
            if (null != appTasks && !appTasks.isEmpty()) {
                return appTasks.get(0).topActivity.getPackageName();
            }
        } else {
            long endTime = System.currentTimeMillis();
            long beginTime = endTime - 10000;
            UsageStatsManager sUsageStatsManager = null;
            if (sUsageStatsManager == null) {
                sUsageStatsManager = (UsageStatsManager) ClientApplication.getInstance().getSystemService(Context.USAGE_STATS_SERVICE);
            }
            String result = "";
            UsageEvents.Event event = new UsageEvents.Event();
            UsageEvents usageEvents = sUsageStatsManager.queryEvents(beginTime, endTime);
            while (usageEvents.hasNextEvent()) {
                usageEvents.getNextEvent(event);
                if (event.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                    result = event.getPackageName();
                }
            }
            if (!android.text.TextUtils.isEmpty(result)) {
                return result;
            }
        }
        return "";
    }
}
