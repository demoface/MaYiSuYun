package com.mayikeji.mayisuyun.service;

import android.accessibilityservice.AccessibilityService;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.mayikeji.mayisuyun.utils.StaticClass;
import com.mayikeji.mayisuyun.utils.UtilTools;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Everly on 2017/3/29.
 *
 * 货拉拉辅助类
 *
 */

public class HuoLaLaAccessibility extends AccessibilityService{


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        int event = accessibilityEvent.getEventType();
        AccessibilityNodeInfo info = getRootInActiveWindow();
        switch (event){
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                List<CharSequence> texts = accessibilityEvent.getText();
                //模拟打开通知栏消息
                UtilTools.openNotification(accessibilityEvent, texts);

                break;
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                String className = accessibilityEvent.getClassName().toString();

                //判断是否是订单页面
                if (className.equals(StaticClass.DINGDAN_PACKAGE)){
                    //TODO 判断是否继续执行
                    queryOrder(info);
                }else if (className.equals(StaticClass.QIANGDAN_PACKAGE)){
                    //TODO 判断是否继续执行
                    UtilTools.openPacket("抢单",info);

                }else if (className.equals(StaticClass.DIALOG_APP)){

                }

                break;

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void queryOrder(AccessibilityNodeInfo info) {
        if (info!= null){
            List<AccessibilityNodeInfo> infoByText = info.findAccessibilityNodeInfosByViewId(StaticClass.ORDER_QUANTITY);

            List<AccessibilityNodeInfo> tem = new ArrayList<>();
            if (infoByText.size() == 0){
                return;
            }
            for (AccessibilityNodeInfo i:infoByText){
                List<AccessibilityNodeInfo> counts = i.findAccessibilityNodeInfosByViewId(StaticClass.SHU_PACKAE);
                if (counts.size() == 0){
                    SystemClock.sleep(1000);
                    //调用信息查询
                    if (UtilTools.queryOrder(this,info) == 0){
                        return;
                    }else {
                        tem.add(info);
                    }
                }
            }
            infoByText.removeAll(tem);
            tem.clear();
            SystemClock.sleep(300);

        }
    }


    @Override
    public void onInterrupt() {

    }
}
