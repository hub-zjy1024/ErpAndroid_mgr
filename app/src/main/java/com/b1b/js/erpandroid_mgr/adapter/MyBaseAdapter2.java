package com.b1b.js.erpandroid_mgr.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 Created by 张建宇 on 2017/2/23. */

public abstract class MyBaseAdapter2<T, H extends MyBaseAdapter2.MyBasedHolder> extends BaseAdapter {
    protected List<T> data;
    protected Context mContext;
    protected int itemViewId;

    public MyBaseAdapter2(List<T> data, Context mContext, int itemViewId) {
        this.data = data;
        this.mContext = mContext;
        this.itemViewId = itemViewId;
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        H holder = getCustomHolder();
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(itemViewId, parent, false);
            findChildViews(convertView, holder);
            convertView.setTag(holder);
        } else {
            holder = (H) convertView.getTag();
        }
        T currentT = data.get(position);
        if (currentT != null) {
            onBindData(currentT, holder);
        }
        return convertView;
    }

    @Override
    public int getCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class MyBasedHolder {

    }

    protected abstract void findChildViews(View convertView, H holder);

    protected abstract void onBindData(T currentData, H mHolder);

    protected abstract H getCustomHolder();

}
