package com.mayikeji.mayisuyun.ui;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.mayikeji.mayisuyun.R;
import com.zhy.autolayout.AutoLayoutActivity;

/**
 * 登陆界面
 */
public class LoginActivity extends AutoLayoutActivity implements View.OnClickListener{

    private Button btn_dl;
    private EditText et_username,et_userpass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().hide();
        statusBar();

        initView();



    }

    private void statusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }

    private void initView() {
        btn_dl = (Button) findViewById(R.id.btn_dl);

        et_username = (EditText) findViewById(R.id.et_username);
        et_userpass = (EditText) findViewById(R.id.et_userpass);

        btn_dl.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_dl:
                startActivity(new Intent(this,MainActivity.class));
                finish();
                break;
        }
    }
}
