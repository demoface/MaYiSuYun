package com.mayikeji.mayisuyun.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.mayikeji.mayisuyun.R;
import com.mayikeji.mayisuyun.utils.ShareUtils;
import com.zhy.autolayout.AutoLayoutActivity;

/**
 * Created by Everly on 2017/3/29.
 * <p>
 * 设置界面
 */

public class SettingActivity extends AutoLayoutActivity implements View.OnClickListener {

    private Switch sw_hll, sw_yhhd, sw_58, sw_zjs, sw_bz, sw_yy;
    private EditText min_price, max_price, min_dis, max_dis;
    private Button btn_sub;
    private CheckBox check_xm,check_zm,check_xh,check_dh;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        initView();

        sw_hll.setChecked(ShareUtils.getBoolean(this, "sw_hll", sw_hll.isChecked()));
        sw_yhhd.setChecked(ShareUtils.getBoolean(this, "sw_yhhd", sw_yhhd.isChecked()));
        sw_58.setChecked(ShareUtils.getBoolean(this, "sw_58", sw_58.isChecked()));
        sw_zjs.setChecked(ShareUtils.getBoolean(this, "sw_zjs", sw_zjs.isChecked()));
        sw_bz.setChecked(ShareUtils.getBoolean(this, "sw_bz", sw_bz.isChecked()));
        sw_yy.setChecked(ShareUtils.getBoolean(this, "sw_yy", sw_yy.isChecked()));

        check_xm.setChecked(ShareUtils.getBoolean(this, "check_xm", check_xm.isChecked()));
        check_zm.setChecked(ShareUtils.getBoolean(this, "check_zm", check_zm.isChecked()));
        check_xh.setChecked(ShareUtils.getBoolean(this, "check_xh", check_xh.isChecked()));
        check_dh.setChecked(ShareUtils.getBoolean(this, "check_dh", check_dh.isChecked()));


        min_price.setText(ShareUtils.getString(getApplicationContext(), "min_price", ""));
        max_price.setText(ShareUtils.getString(getApplicationContext(), "max_price", ""));

        min_dis.setText(ShareUtils.getString(getApplicationContext(), "min_dis", ""));
        max_dis.setText(ShareUtils.getString(getApplicationContext(), "max_dis", ""));

        intent = new Intent(this,MainActivity.class);
    }


    @Override
    protected void onResume() {
        super.onResume();


    }

    private void initView() {
        sw_hll = (Switch) findViewById(R.id.sw_hll);
        sw_yhhd = (Switch) findViewById(R.id.sw_yhhd);
        sw_58 = (Switch) findViewById(R.id.sw_58);
        sw_zjs = (Switch) findViewById(R.id.sw_zjs);

        sw_bz = (Switch) findViewById(R.id.sw_bz);
        sw_yy = (Switch) findViewById(R.id.sw_yy);

        min_price = (EditText) findViewById(R.id.min_price);
        max_price = (EditText) findViewById(R.id.max_price);

        min_dis = (EditText) findViewById(R.id.min_dis);
        max_dis = (EditText) findViewById(R.id.max_dis);

        btn_sub = (Button) findViewById(R.id.btn_sub);

        check_xm = (CheckBox) findViewById(R.id.btn_xm);
        check_zm = (CheckBox) findViewById(R.id.btn_zm);
        check_xh = (CheckBox) findViewById(R.id.btn_xh);
        check_dh = (CheckBox) findViewById(R.id.btn_dh);

        sw_hll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    ShareUtils.putBoolean(SettingActivity.this, "sw_hll", isChecked);
                } else {
                    ShareUtils.putBoolean(SettingActivity.this, "sw_hll", isChecked);
                }
            }
        });

        sw_yhhd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    ShareUtils.putBoolean(SettingActivity.this, "sw_yhhd", isChecked);
                } else {
                    ShareUtils.putBoolean(SettingActivity.this, "sw_yhhd", isChecked);
                }
            }
        });

        sw_58.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    ShareUtils.putBoolean(SettingActivity.this, "sw_58", isChecked);
                } else {
                    ShareUtils.putBoolean(SettingActivity.this, "sw_58", isChecked);
                }
            }
        });

        sw_zjs.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    ShareUtils.putBoolean(SettingActivity.this, "sw_zjs", isChecked);
                } else {
                    ShareUtils.putBoolean(SettingActivity.this, "sw_zjs", isChecked);
                }
            }
        });

        sw_bz.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    ShareUtils.putBoolean(SettingActivity.this, "sw_bz", isChecked);
                } else {
                    ShareUtils.putBoolean(SettingActivity.this, "sw_bz", isChecked);
                }
            }
        });

        sw_yy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    ShareUtils.putBoolean(SettingActivity.this, "sw_yy", isChecked);
                } else {
                    ShareUtils.putBoolean(SettingActivity.this, "sw_yy", isChecked);
                }
            }
        });


        check_xm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    ShareUtils.putBoolean(SettingActivity.this, "check_xm", isChecked);
                    buttonView.setBackgroundColor(Color.parseColor("#f98e42"));
                }else {
                    ShareUtils.putBoolean(SettingActivity.this, "check_xm", isChecked);
                    buttonView.setBackgroundColor(Color.parseColor("#e2e2e2"));
                }
            }
        });

        check_zm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    ShareUtils.putBoolean(SettingActivity.this, "check_zm", isChecked);
                    buttonView.setBackgroundColor(Color.parseColor("#f98e42"));
                }else {
                    ShareUtils.putBoolean(SettingActivity.this, "check_zm", isChecked);
                    buttonView.setBackgroundColor(Color.parseColor("#e2e2e2"));
                }
            }
        });

        check_xh.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    ShareUtils.putBoolean(SettingActivity.this, "check_xh", isChecked);
                    buttonView.setBackgroundColor(Color.parseColor("#f98e42"));
                }else {
                    ShareUtils.putBoolean(SettingActivity.this, "check_xh", isChecked);
                    buttonView.setBackgroundColor(Color.parseColor("#e2e2e2"));
                }
            }
        });

        check_dh.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    ShareUtils.putBoolean(SettingActivity.this, "check_dh", isChecked);
                    buttonView.setBackgroundColor(Color.parseColor("#f98e42"));
                    ShareUtils.putString(SettingActivity.this,"check_dhString","大货");
                }else {
                    ShareUtils.putBoolean(SettingActivity.this, "check_dh", isChecked);
                    buttonView.setBackgroundColor(Color.parseColor("#e2e2e2"));
                    ShareUtils.putString(SettingActivity.this,"check_dhString","");
                }
            }
        });

        btn_sub.setOnClickListener(this);


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sub:


                if (!TextUtils.isEmpty(min_price.getText().toString()) && !TextUtils.isEmpty(max_price.getText().toString()) &&
                        !TextUtils.isEmpty(min_dis.getText().toString()) && !TextUtils.isEmpty(max_dis.getText().toString())) {

                    ShareUtils.putString(getApplicationContext(), "min_price", min_price.getText().toString());
                    ShareUtils.putString(getApplicationContext(), "max_price", max_price.getText().toString());

                    ShareUtils.putString(getApplicationContext(), "min_dis", min_dis.getText().toString());
                    ShareUtils.putString(getApplicationContext(), "max_dis", max_dis.getText().toString());

                    startActivity(intent);
                    finish();

                } else {
                    Toast.makeText(this, "价格区间或公里区间不能为空", Toast.LENGTH_SHORT).show();
                }


                break;
        }
    }
}
