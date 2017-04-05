package com.mayikeji.shoujibaidu.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mayikeji.shoujibaidu.R;
import com.mayikeji.shoujibaidu.application.ClientApplication;
import com.mayikeji.shoujibaidu.bean.LoginBean;
import com.mayikeji.shoujibaidu.bean.Result;
import com.mayikeji.shoujibaidu.bean.UpdateBean;
import com.mayikeji.shoujibaidu.helper.HttpHelper;
import com.mayikeji.shoujibaidu.helper.UiHelper;
import com.mayikeji.shoujibaidu.service.DownNewApkService;
import com.mayikeji.shoujibaidu.utils.CustomDialog;
import com.mayikeji.shoujibaidu.utils.ShareUtils;
import com.zhy.autolayout.AutoLayoutActivity;

/**
 * 登陆界面
 */
public class LoginActivity extends AutoLayoutActivity implements View.OnClickListener{

    private Button btn_dl;
    private EditText et_username,et_userpass;
    private String Imei;
    private CustomDialog dialog;
    private static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 1;
    private static final int WRITE_LOCATION_REQUEST_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().hide();


        statusBar();

        UiHelper.getInstance().runOnThread(new Runnable() {

            public void run() {
                checkPermission();
            }

        });

        checkUpdate();

        initView();

        initData();


    }



    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    private void initView() {
        btn_dl = (Button) findViewById(R.id.btn_dl);

        et_username = (EditText) findViewById(R.id.et_username);
        et_userpass = (EditText) findViewById(R.id.et_userpass);

        btn_dl.setOnClickListener(this);
    }

    private void initData() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, 0);
        }
        et_username.setText(ShareUtils.getString(this,"username",""));
        et_userpass.setText(ShareUtils.getString(this,"userpass",""));
    }

    private void statusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }

    private void checkUpdate() {
        HttpHelper.getInstance().update(getVersionCode(this), new HttpHelper.CallBack<Result<UpdateBean>>() {
            @Override
            public void onResult(Result<UpdateBean> result) {
                if (result == null) {
                    Log.d("SOLON", "检测更新失败");
                    return;
                }

                if (result.getCode() == 100) {
                    UpdateBean bean = result.getData();
                    Log.d("SOLON", "" + bean.getLast() + "\n" + bean.getNow() + "\n" + bean.getUp() + "\n" + bean.getUrl());
                    if (bean.getUp().equals("1")) {
                        try {
                            showUpdateDialog(bean.getUrl(), 0);
                        } catch (Exception e) {
                        }
                    } else {
                        Log.d("SOLON", "不需要更新");
                    }
                } else {
                    UiHelper.showToast(getApplicationContext(), result.getInfo());
                }
            }
        });
    }

    private void showUpdateDialog(final String url, final int type) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("有新版本,是否更新?");

        builder.setNegativeButton("退出", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        builder.setPositiveButton("更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
//                dialogInterface.dismiss();
                ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
                progressDialog.setMessage("正在下载");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setCancelable(false);
                progressDialog.show();
                Toast.makeText(getApplicationContext(), "请稍后！", Toast.LENGTH_LONG).show();
                bingService(url, type);

            }
        });
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
        dialog.show();
    }

    public static String getVersionCode(Context con) {
        PackageManager pm = null;
        PackageInfo pi = null;
        try {
            pm = con.getPackageManager();
            if (pm == null) return "";
            pi = pm.getPackageInfo(con.getPackageName(), 0);
            if (pi == null) return "";
            String versionName = "";
            versionName = pi.versionName + "";
            return versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";

    }

    private void bingService(final String url, int type) {
        /**** 绑定更新服务 ****/
        Intent updateService = new Intent(this, DownNewApkService.class);
        updateService.putExtra("url", url);
        updateService.putExtra("type", type);
        startService(updateService);
    }

    public void checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                //申请WRITE_EXTERNAL_STORAGE权限
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
            }

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                //申请ACCESS_COARSE_LOCATION权限
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                        WRITE_LOCATION_REQUEST_CODE);
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                //申请ACCESS_COARSE_LOCATION权限
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION},
                        3);
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                    != PackageManager.PERMISSION_GRANTED) {
                //申请ACCESS_COARSE_LOCATION权限
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.ACCESS_FINE_LOCATION},
                        3);
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED) {
                //申请ACCESS_COARSE_LOCATION权限
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.ACCESS_FINE_LOCATION},
                        3);
            }
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_dl:
                if ("".endsWith(et_userpass.getText().toString()) || "".endsWith(et_username.getText().toString())) {
                    UiHelper.showToast(getApplicationContext(), "请输入正确的账号和密码");
                    return;
                }
                Imei = ((TelephonyManager) getSystemService(TELEPHONY_SERVICE))
                        .getDeviceId();
                if (dialog == null) {
                    dialog = new CustomDialog(this, 100, 100, R.layout.dialog_loding, R.style.Theme_dialog, Gravity.CENTER,R.style.pop_anim_style);
                    dialog.show();
                } else {
                    dialog.show();
                }

                HttpHelper.getInstance().login(et_username.getText().toString(), et_userpass.getText().toString(),
                        Imei, new HttpHelper.CallBack<Result<LoginBean>>() {
                    @Override
                    public void onResult(Result<LoginBean> result) {
                        if (dialog != null) {
                            dialog.dismiss();
                        }

                        if (result == null) {
                            UiHelper.showToast(getApplicationContext(), "请求失败");
                            return;
                        }

                        if (result.getCode() == 100) {
                            ClientApplication.uid = result.getData().getUid();
                            ClientApplication.status = result.getData().getStatus();
                            ShareUtils.putString(LoginActivity.this,"username",et_username.getText().toString());
                            ShareUtils.putString(LoginActivity.this,"userpass",et_userpass.getText().toString());
                            ClientApplication.getInstance().login();
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
//                            MyLog.e("Login", "登陆成功");
                            finish();
                        } else {
                            UiHelper.showToast(getApplicationContext(), result.getInfo());
                        }
                    }
                });


                finish();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
