package com.mayikeji.shoujibaidu.application;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;

import com.mayikeji.shoujibaidu.R;
import com.mayikeji.shoujibaidu.helper.UiHelper;
import com.mayikeji.shoujibaidu.service.BaseServer;
import com.mayikeji.shoujibaidu.ui.MainActivity;

/**
 * Created by 豪杰 on 2016/11/28.
 */
public class ClientApplication extends Application {
    private static final int TIME = 21600;//秒
//    private static final int TIME = 10;//秒
    public static SharedPreferences sp;

    private static ClientApplication mInstance ;
    private NotificationManager myManager;

    public static ClientApplication getInstance(){
        return mInstance;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        sp = getSharedPreferences("config",MODE_PRIVATE);
        myManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

    }
    public void login(){
        UiHelper.getInstance().postThreadOnMain(new Runnable() {
            @Override
            public void run() {
                Intent i = getBaseContext().getPackageManager()
                        .getLaunchIntentForPackage(getBaseContext().getPackageName());
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                BaseServer.isRunning = false;
                startActivity(i);
            }
        },TIME * 1000);
    }
    public static String uid ;
    public static String status ;

    public void showNotification(String title,String  content,String sub,String ticker){

        //3.定义一个PendingIntent，点击Notification后启动一个Activity
        PendingIntent pi = PendingIntent.getActivity(
                this,
                100,
                new Intent(this, MainActivity.class),
                PendingIntent.FLAG_CANCEL_CURRENT
        );

        //2.通过Notification.Builder来创建通知
        Notification.Builder myBuilder = new Notification.Builder(this);
        myBuilder.setContentTitle(title)
                .setContentText(content)
                .setSubText(sub)
                .setTicker(ticker)
                //设置状态栏中的小图片，尺寸一般建议在24×24，这个图片同样也是在下拉状态栏中所显示
                .setSmallIcon(R.mipmap.ic_launcher)
//                .setLargeIcon(LargeBitmap)
                //设置默认声音和震动
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                .setAutoCancel(true)//点击后取消
                .setWhen(System.currentTimeMillis())//设置通知时间
                .setPriority(Notification.PRIORITY_HIGH)//高优先级
//                .setVisibility(Notification.VISIBILITY_PUBLIC)
                //android5.0加入了一种新的模式Notification的显示等级，共有三种：
                //VISIBILITY_PUBLIC  只有在没有锁屏时会显示通知
                //VISIBILITY_PRIVATE 任何情况都会显示通知
                //VISIBILITY_SECRET  在安全锁和没有锁屏的情况下显示通知
                .setContentIntent(pi);  //3.关联PendingIntent
        Notification myNotification = myBuilder.build();
        //4.通过通知管理器来发起通知，ID区分通知
        myManager.notify(1, myNotification);
    }
}