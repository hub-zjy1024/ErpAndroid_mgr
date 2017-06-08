package com.b1b.js.erpandroid_mgr;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.b1b.js.erpandroid_mgr.service.PushService;
import com.b1b.js.erpandroid_mgr.utils.MyToast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuActivity extends AppCompatActivity {
    private ListView menuList;
    private SimpleAdapter simpleAdapter;
    private List<Map<String, Object>> listItems = new ArrayList<>();
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_layout);
        menuList = (ListView) findViewById(R.id.lv);
        simpleAdapter = new SimpleAdapter(this, listItems, R.layout.menu_items, new String[]{"title", "imageID"}, new int[]{R
                .id.menu_title, R.id.menu_img});
        simpleAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data, String textRepresentation) {
                if (view instanceof ImageView) {
                    ImageView iv = (ImageView) view;
                    //                    iv.setImageResource((Integer) data);
                    return true;
                } else if (view instanceof TextView) {
                    TextView tv = (TextView) view;
                    tv.setText((String) data);
                    return true;
                }
                return false;
            }
        });
        Intent intent = new Intent(MenuActivity.this, PushService.class);
        intent.putExtra("id", MyApp.id);
        startService(intent);
        // 为菜单项设置点击事件
        setItemOnclickListener();
        addItem();
        // 设置adapter
        menuList.setAdapter(simpleAdapter);
        simpleAdapter.notifyDataSetChanged();
    }

    private void setItemOnclickListener() {
        menuList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String, Object> item = listItems.get(position);
                String value = (String) item.get("title");
                Intent intent = new Intent();
                switch (value) {
                    case "库存发布":
                        intent.setClass(MenuActivity.this, KucunFBActivity.class);
                        startActivity(intent);
                        break;
                    case "供应商管理":
                        intent.setClass(MenuActivity.this, ProviderManagerActivity.class);
                        startActivity(intent);
                        break;
                    case "取消自动登录":
                        SharedPreferences sp = getSharedPreferences("UserInfo", 0);
                        boolean atuoLogin = sp.getBoolean("autol", false);
                        if (!atuoLogin) {
                            MyToast.showToast(MenuActivity.this, "当前已是非自动登录状态");
                        } else {
                            SharedPreferences.Editor editor = sp.edit();
                            if (editor.putBoolean("autol", false).commit()) {
                                MyToast.showToast(MenuActivity.this, "取消登录成功");
                            }
                        }
                        break;
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    // 添加菜单项
    private void addItem() {
        Map<String, Object> map = new HashMap<>();
        map.put("title", "库存发布");
        listItems.add(map);
        map = new HashMap<>();
        map.put("title", "供应商管理");
        listItems.add(map);
        map = new HashMap<>();
        map.put("title", "取消自动登录");
        listItems.add(map);
    }
}
