package com.mayikeji.mayisuyun.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;

import com.mayikeji.mayisuyun.R;
import com.mayikeji.mayisuyun.helper.UiHelper;
import com.mayikeji.mayisuyun.utils.SDCardUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 程序下载更新服务
 *
 * @author Administrator
 */
public class DownNewApkService extends Service {

    private String url = "";


    /**
     * 用于显示通知栏
     */
    private NotificationCompat.Builder mBuilder;
    private NotificationManager mNotificationManager;

    // 管理更新接口
    private static final int MESSAGE_NEW_BOOK_ARRIVED = 1;


    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 101:
                    mBuilder.setContentTitle("下载完成").setContentText("进度：100%").setTicker("下载完成");// 通知首次出现在通知栏，带上升动画效果的
                    mBuilder.setProgress(100, 100, false);
                    // 点击安装
                    Intent resultIntent = new Intent();
                    resultIntent.setAction(Intent.ACTION_VIEW);
                    resultIntent.setDataAndType(Uri.fromFile(new File(msg.obj.toString())), "application/vnd.android.package-archive");
                    resultIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    PendingIntent pendingIntent = PendingIntent.getActivity(DownNewApkService.this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    mBuilder.setContentIntent(pendingIntent);
                    // 发布通知
                    mNotificationManager.notify(0, mBuilder.build());
                    break;
                case -1:
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(new File(msg.obj.toString())), "application/vnd.android.package-archive");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    break;
                default:
                    mBuilder.setContentTitle("正在下载").setContentText("进度：" + msg.what + " %").setTicker("开始下载");// 通知首次出现在通知栏，带上升动画效果的
                    mBuilder.setProgress(100, msg.what, true);
                    mNotificationManager.notify(0, mBuilder.build());
                    break;
            }
        }
    };
    private int type;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initNotify();
        try {
            url = intent.getStringExtra("url");
            type = intent.getIntExtra("type", 0);
            download(url);
        }catch (Exception e){
            e.printStackTrace();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 初始化通知栏
     */
    private void initNotify() {
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(this);
        Notification notification = mBuilder.build();
        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        mBuilder.setWhen(System.currentTimeMillis())// 通知产生的时间，会在通知信息里显示
                .setContentIntent(getDefalutIntent(0))
                        // .setNumber(number)//显示数量
                .setPriority(Notification.PRIORITY_DEFAULT)// 设置该通知优先级
                        // .setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
                .setOngoing(true)// ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                        // .setDefaults(Notification.DEFAULT_VIBRATE)//
                        // 向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合：
                        // Notification.DEFAULT_ALL Notification.DEFAULT_SOUND 添加声音 //
                        // requires VIBRATE permission
                .setSmallIcon(R.mipmap.app_icon);

    }
    /**
     * @获取默认的pendingIntent,为了防止2.3及以下版本报错
     * @flags属性: 在顶部常驻:Notification.FLAG_ONGOING_EVENT 点击去除：
     * Notification.FLAG_AUTO_CANCEL
     */
    public PendingIntent getDefalutIntent(int flags) {
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, new Intent(), flags);
        return pendingIntent;
    }
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    private boolean isDownload = false;
    private synchronized void download(final String url) {
        if (isDownload)
            return;
        if (!SDCardUtils.isSDCardEnable()) {
            UiHelper.showToast(getApplicationContext(), "存储卡不可用");
            return;
        }
        isDownload = true;
        final String target = SDCardUtils.getInnerSDCardPath() + "/" + System.currentTimeMillis() + "qiangdan.apk";
        UiHelper.getInstance().runOnThread(new Runnable() {
            @Override
            public void run() {
                InputStream is = null;
                FileOutputStream ous = null;
                final File file = new File(target);
                if (!file.exists()) {
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    URL Url = new URL(url);
                    HttpURLConnection urlConnection = (HttpURLConnection) Url.openConnection();
                    is = urlConnection.getInputStream();
                    ous = new FileOutputStream(target);
                    int len;
                    int count = urlConnection.getContentLength();
                    int current = 0;
                    byte[] b = new byte[count / 100];
                    if (type == 0)
                        UiHelper.showToast(getApplicationContext(), "开始下载");
                    int offset = 0;
                    while ((len = is.read(b)) > 0) {
                        ous.write(b, 0, len);
                        current += len;
                        int i = current * 100 / count;
                        if (type == 0 && offset < i) {
                            offset = i;
                            onNewMsg(null,offset);
                        }
                    }
                    ous.close();
                    is.close();
                    UiHelper.getInstance().postThreadOnMain(new Runnable() {
                        @Override
                        public void run() {
//                            if (type == 0){
//                                onNewMsg(file.getAbsoluteFile().toString(),101);
//                                return;
//                            }
//                            onNewMsg(file.getAbsoluteFile().toString(),-1);
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setDataAndType(Uri.fromFile(new File(file.getAbsoluteFile().toString())), "application/vnd.android.package-archive");
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    });
                } catch (Exception e) {
                    if (type == 0)
                        UiHelper.showToast(getApplicationContext(), "下载失败");
                } finally {
                    if (is != null)
                        try {
                            is.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    if (ous != null)
                        try {
                            ous.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    isDownload = false;
                }

            }
        });
    }


    public void onNewMsg(String msg, int what) {
        Message msg1 = Message.obtain();
        msg1.obj = msg;
        msg1.what = what;
        mHandler.sendMessage(msg1);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
