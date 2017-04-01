package com.mayikeji.mayisuyun.utils;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

/**
 * Created by Administrator on 2017/3/30.
 */

public class UtilTools {

    //货拉拉模拟打开通知栏消息
    public static void openNotification(AccessibilityEvent accessibilityEvent, List<CharSequence> texts) {
        if (!texts.isEmpty()){
            for (CharSequence cs : texts) {
                String text = cs.toString();
                if (text.contains("订单")){
                    if (accessibilityEvent.getParcelableData()!= null &&
                            accessibilityEvent.getParcelableData() instanceof Notification){
                        Notification notification = (Notification) accessibilityEvent.getParcelableData();
                        PendingIntent pendingIntent = notification.contentIntent;

                        try {
                            pendingIntent.send();
                        } catch (PendingIntent.CanceledException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    //货拉拉订单查询

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static int queryOrder(Context context, AccessibilityNodeInfo info){

        if (!ShareUtils.getBoolean(context, "sw_yy", false)) {
            List<AccessibilityNodeInfo> yuyue = info.findAccessibilityNodeInfosByViewId(StaticClass.STATE_YUYUE);
            if (yuyue.get(0).getText().toString().indexOf("即时") == -1){
                return -4;
            }
        }

        if (!ShareUtils.getBoolean(context,"sw_bz",false)){
            List<AccessibilityNodeInfo> beizhu = info.findAccessibilityNodeInfosByViewId(StaticClass.STATE_BEIZHU);
            if (beizhu.size()>0){
                return -3;
            }
        }

        List<AccessibilityNodeInfo> tvDis = info.findAccessibilityNodeInfosByViewId(StaticClass.KM);
        float dis ;
        String s = tvDis.get(0).getText().toString();
        if (s.indexOf("公里") != -1){
            s = s.replace("公里","");
            dis = Float.valueOf(s);
        }else{
            s = s.replace("米","");
            dis = Float.valueOf(s) / 1000.0f;
        }

        float min = Integer.getInteger(ShareUtils.getString(context,"min_dis",""));
        float max = Integer.getInteger(ShareUtils.getString(context,"max_dis",""));

        if (dis < min || dis > max){
            return -2;
        }

        List<AccessibilityNodeInfo> tvPrice = info.findAccessibilityNodeInfosByViewId(StaticClass.PRICE);
        String jg = tvPrice.get(0).getText().toString();

        String[] split = s.split("\\+");
        for (String s1:split) {
            Integer valueOf = Integer.valueOf(s1.trim().replace("￥", "").trim());
            jg += valueOf;
        }
        float min_price = Integer.getInteger(ShareUtils.getString(context,"min_price",""));
        float max_price = Integer.getInteger(ShareUtils.getString(context,"max_price",""));

        if (dis < min_price || dis > max_price){
            return -2;
        }
        info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        return 0;
    }

    //货拉拉抢单
    public static void openPacket(String string, AccessibilityNodeInfo info) {
        if (info != null) {
            List<AccessibilityNodeInfo> list = info
                    .findAccessibilityNodeInfosByText(string);
            if (list.size() == 2) {
                list.get(1).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                return;
            }
            for (AccessibilityNodeInfo n : list) {

                n.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        }
    }
}
