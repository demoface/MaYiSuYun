package com.mayikeji.shoujibaidu.adapter;

import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mayikeji.shoujibaidu.R;
import com.mayikeji.shoujibaidu.application.ClientApplication;
import com.mayikeji.shoujibaidu.bean.OrderBean;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/12/19.
 */

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.Holder>{
    private ArrayList<OrderBean> mData;

    public void setData( ArrayList<OrderBean> data){
        mData = data;
        notifyDataSetChanged();
    }
    @Override
    public OrderAdapter.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(ClientApplication.getInstance()).inflate(R.layout.i_order_main, parent, false);
        Holder holder = new Holder(inflate);
        return holder;
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        holder.top.setVisibility(View.VISIBLE);
        holder.bottom.setVisibility(View.VISIBLE);
        if (position == 0){
            holder.top.setVisibility(View.INVISIBLE);
        }
        if (position == getItemCount() - 1)
            holder.bottom.setVisibility(View.INVISIBLE);
        OrderBean orderBean = mData.get(position);
        holder.textViewCompat.setText(orderBean.getCtime() + "   成功获得"+ orderBean.getSource() + orderBean.getPrice()+"元订单");
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    public void addData(ArrayList<OrderBean> data) {
        if (mData == null)
            mData = data;
        mData.addAll(data);
        notifyDataSetChanged();
    }

    class Holder extends RecyclerView.ViewHolder{
        AppCompatTextView textViewCompat;
        View top,bottom;
        public Holder(View itemView) {
            super(itemView);
            textViewCompat = (AppCompatTextView) itemView.findViewById(R.id.text);
            top = itemView.findViewById(R.id.top);
            bottom = itemView.findViewById(R.id.bottom);
        }
    }
}
