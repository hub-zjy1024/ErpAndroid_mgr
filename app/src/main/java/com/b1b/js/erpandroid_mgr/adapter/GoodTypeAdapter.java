package com.b1b.js.erpandroid_mgr.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.b1b.js.erpandroid_mgr.R;
import com.b1b.js.erpandroid_mgr.entity.CaigouGoodType;

import java.util.List;

/**
 Created by 张建宇 on 2017/3/30. */

public class GoodTypeAdapter extends MyBaseAdapter<CaigouGoodType> {
    public GoodTypeAdapter(List<CaigouGoodType> data, Context mContext, int itemViewId) {
        super(data, mContext, itemViewId);
    }

    @Override
    protected void findChildViews(View convertView, MyBasedHolder basedHolder) {
        GoodTypeHolder holder1 = (GoodTypeHolder) basedHolder;
        holder1.tv = (TextView) convertView.findViewById(R.id.spinner_item_tv);
    }

    @Override
    protected void onBindData(CaigouGoodType currentData, MyBasedHolder baseHolder) {
        GoodTypeHolder holder = (GoodTypeHolder) baseHolder;
        holder.tv.setText(currentData.getTypeName());
    }

    @Override
    protected MyBasedHolder getCustomHolder() {
        return new GoodTypeHolder();
    }

    private class GoodTypeHolder extends MyBasedHolder {
        TextView tv;
    }
}
