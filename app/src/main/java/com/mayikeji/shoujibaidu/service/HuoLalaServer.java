package com.mayikeji.shoujibaidu.service;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import com.mayikeji.shoujibaidu.utils.MyLog;
import com.mayikeji.shoujibaidu.application.ClientApplication;
import com.mayikeji.shoujibaidu.helper.UiHelper;
import com.mayikeji.shoujibaidu.ui.MainActivity;
import com.mayikeji.shoujibaidu.ui.ToastActivity;
import com.mayikeji.shoujibaidu.utils.ShareUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/11/30.
 */

public class HuoLalaServer extends QiangDanServer{

    private String packages = "com.lalamove.huolala.driver";
    private int price;
    private int price_new;
    private float dis;

    private boolean me;
    public HuoLalaServer(OnQiangDanListener listener) {
        super(listener);
    }

    private void log(String log){
        MyLog.e("HuoLalaServer",log);
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event, AccessibilityNodeInfo info) {
        int type = event.getEventType();
        switch (type) {
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                List<CharSequence> texts = event.getText();
                for (CharSequence cs : texts) {
                    Log.e("TAG", "................................." + cs);
                }
                BaseServer.mInstance.openNotification(event);
                break;
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                String className = event.getClassName().toString();
                Log.e("TAG",className);
                if (className.equals("com.lalamove.huolala.driver.HomeActivity")) {
                    me = false;
                    SystemClock.sleep(1000);
                    hasOrder(info);
                } else if (className.equals("com.lalamove.huolala.OrderDetailActivity")) {
                    if (me)
                        openPacket("抢单",info);
                } else if (className.equals("android.app.Dialog")) {
                    if (me) {
                        openPacket("确定", info);
                    }
                }else if(className.equals("com.lalamove.huolala.driver.HistoryDetailActivity")){
                    if (me){
                        commit(price_new + "","货拉拉");
                        log("货拉拉抢单完成 价格：" + price_new);
                        UiHelper.getInstance().postThreadOnMain(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(ClientApplication.getInstance(),ToastActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                ClientApplication.getInstance().startActivity(intent);
                                ClientApplication.getInstance().showNotification("蚂蚁抢单王","蚂蚁已经为您代抢到一个订单，已经停止抢单；","完成订单后请及时开启抢单功能。","");
                            }
                        },1000);
                    }
                }
                break;
        }

    }

    @Override
    public void onEvent(AccessibilityNodeInfo info) {
        hasOrder(info);
    }

    @Override
    public void startActivity() {
        if (!isTopActivity(packages)) {
            PackageManager packageManager = ClientApplication.getInstance().getPackageManager();
            Intent intent = packageManager.getLaunchIntentForPackage(packages);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_CLEAR_TOP) ;
            ClientApplication.getInstance().startActivity(intent);
        }else{
            AccessibilityNodeInfo rootInActiveWindow = BaseServer.mInstance.getRootInActiveWindow();
            hasOrder(rootInActiveWindow);
        }
    }

    @Override
    public boolean isTop() {
        return isTopActivity(packages);
    }

    //com.lalamove.huolala.driver:id/tvCount
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void hasOrder(AccessibilityNodeInfo info) {
        log("查找订单");
        if (info != null) {
            List<AccessibilityNodeInfo> infosByText = null;
//            log("findAccessibilityNodeInfosByViewId  com.lalamove.huolala.driver:id/rlMain");
            infosByText = info.findAccessibilityNodeInfosByViewId("com.lalamove.huolala.driver:id/rlMain");//com.lalamove.huolala.driver:id/rl
            List<AccessibilityNodeInfo> tem = new ArrayList<>();
            log("查找到订单数量 = " + infosByText.size());
            while (true) {
                if (infosByText.size() == 0) {
                    return;
                }
                try {
                    for (AccessibilityNodeInfo i : infosByText) {
//                        log("i.findAccessibilityNodeInfosByViewId(\"com.lalamove.huolala.driver:id/tvCount\");");
                        List<AccessibilityNodeInfo> counts = i.findAccessibilityNodeInfosByViewId("com.lalamove.huolala.driver:id/tvCount");
                        log("counts.size = " + counts.size());
                        if (counts.size() == 0) {
                            if (openOrder(i) == 0) {
                                return;
                            }else {
                                tem.add(i);
                            }
                        }
                    }
                }catch (Exception e){
                    log(e.getMessage());
                    Toast.makeText(ClientApplication.getInstance(),"未知错误",Toast.LENGTH_SHORT).show();
                }
                infosByText.removeAll(tem);
                tem.clear();
                SystemClock.sleep(300);
            }
        }
    }

    private int openOrder(AccessibilityNodeInfo i){
        log("检测订单是否符合设定");
        List<AccessibilityNodeInfo> tvPrice = i.findAccessibilityNodeInfosByViewId("com.lalamove.huolala.driver:id/tvPrice");
        List<AccessibilityNodeInfo> tvDistance = i.findAccessibilityNodeInfosByViewId("com.lalamove.huolala.driver:id/tvDistance");
        //com.lalamove.huolala.driver:id/tvCategory
        if (!ShareUtils.getBoolean(MainActivity.context,"sw_yy",false)) {
            List<AccessibilityNodeInfo> tvCategory = i.findAccessibilityNodeInfosByViewId("com.lalamove.huolala.driver:id/tvCategory");//明天预约
            if (tvCategory.get(0).getText().toString().indexOf("即时") == -1){
                Log.e("TAG","不抢预约单");
                return -4;
            }
        }
//        tvCategory.size()
        if (!ShareUtils.getBoolean(MainActivity.context,"sw_bz",false)) {
            List<AccessibilityNodeInfo> tvRemark = i.findAccessibilityNodeInfosByViewId("com.lalamove.huolala.driver:id/tvRemark");//备注
            if (tvRemark.size() > 0) {
                Log.e("TAG","不抢备注单");
                return -3;
            }
        }
        price = 0;
        dis = 0;
        try {
            String s = tvDistance.get(0).getText().toString();
            log(s);
            if (s.indexOf("公里") != -1){
                s = s.replace("公里","");
                dis = Float.valueOf(s);
            }else{
                s = s.replace("米","");
                dis = Float.valueOf(s) / 1000.0f;
            }
        }catch (Exception e){
            log(e.getMessage());
        }
        log("获取到距离：" + dis);
        if(dis == 0){
            return -2;
        }
        try {
            String s = tvPrice.get(0).getText().toString();
            log(s);
            String[] split = s.split("\\+");
            for (String s1:split) {
                Integer valueOf = Integer.valueOf(s1.trim().replace("￥", "").trim());
                price += valueOf;
            }
        } catch (Exception e) {
            log(e.getMessage());
        }
        int[] diss = getDis();
        int[] prices = getPrice();
        log("最小价格：" + prices[0]  +"   最大价格：" + prices[1] + "   价格：" + price);
        log("最小距离：" + diss[0]  +"   最大距离：" + diss[1] + "   距离：" + dis);

        if (price < prices[0] || price > prices[1]) {
            log("价格不对，不抢");
            return -1;
        }
        if (dis < diss[0] || dis > diss[1]){
            log("距离不对，不抢");
            return -1;
        }
        me = true;
        price_new = price;
        log("开始抢单");
        i.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        return 0;
    }

    /**
     * 查找到
     */
    @SuppressLint("NewApi")
    private void openPacket(String string, AccessibilityNodeInfo info) {
        log("openPacket：" + string);
        if (info != null) {
            List<AccessibilityNodeInfo> list = info
                    .findAccessibilityNodeInfosByText(string);
            if (list.size() == 2) {
                list.get(1).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                return;
            }
            for (AccessibilityNodeInfo n : list) {
//                log("openPacket" + string);
                n.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        }
    }

    @Override
    public String getPackageName() {
        return packages;
    }
}
