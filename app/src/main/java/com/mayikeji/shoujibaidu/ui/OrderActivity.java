package com.mayikeji.shoujibaidu.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.mayikeji.shoujibaidu.R;
import com.mayikeji.shoujibaidu.adapter.OrderAdapter;
import com.mayikeji.shoujibaidu.adapter.RefreshRecyclerView;
import com.mayikeji.shoujibaidu.application.ClientApplication;
import com.mayikeji.shoujibaidu.bean.OrderBean;
import com.mayikeji.shoujibaidu.bean.Result;
import com.mayikeji.shoujibaidu.helper.HttpHelper;
import com.zhy.autolayout.AutoLayoutActivity;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/4/5.
 */

public class OrderActivity extends AutoLayoutActivity implements SwipeRefreshLayout.OnRefreshListener,View.OnClickListener{
    private LinearLayout layout_back;
    String DINGDAN = "　　http://mayi.utuiwu.com/index.php/api/app/get_order";
    private SwipeRefreshLayout swipeRefreshLayout;
    private RefreshRecyclerView recyclerView;
    private List<OrderBean> list;
    private int page = 1;
    private ProgressDialog progressDialog;
    private OrderAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        getSupportActionBar().hide();
        statusBar();
        initView();
        initData();


    }

    @Override
    protected void onResume() {
        super.onResume();
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                initData();
            }
        });

    }

    private void initView() {
        layout_back = (LinearLayout) findViewById(R.id.layout_back);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        recyclerView = (RefreshRecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setLoadMoreEnable(true);//允许加载更多
        recyclerView.setFooterResource(R.layout.item_footer);//设置脚布局
        recyclerView.setAdapter(adapter = new OrderAdapter());
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_UP:
                        recyclerView.notifyData();
                        break;
                }
                return false;
            }
        });

        swipeRefreshLayout.setOnRefreshListener(this);
        layout_back.setOnClickListener(this);

        recyclerView.setOnLoadMoreListener(new RefreshRecyclerView.OnLoadMoreListener() {
            @Override
            public void loadMoreListener() {
                page++;
                initData();

            }
        });
    }

    private void initData() {
        list = new ArrayList<>();

        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this) ;
            progressDialog.setMessage("正在加载");
            progressDialog.show();
        } else {
            progressDialog.show();
        }
        HttpHelper.getInstance().getOrderList(page + "", ClientApplication.uid , new HttpHelper.CallBack<Result<ArrayList<OrderBean>>>() {
            @Override
            public void onResult(Result<ArrayList<OrderBean>> result) {
                progressDialog.dismiss();

                if (result == null)
                    return;
                if (page == 1)
                    adapter.setData(result.getData());
                else
                    adapter.addData(result.getData());
//                recyclerView.notifyData();
            }
        });
        swipeRefreshLayout.setRefreshing(false);
    }

    private void statusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }


    //手动下拉刷新监听
    @Override
    public void onRefresh() {
        initData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.layout_back:
                startActivity(new Intent(this,MainActivity.class));
                finish();
                break;
        }
    }
}
