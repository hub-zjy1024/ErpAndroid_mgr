package com.b1b.js.erpandroid_mgr;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.b1b.js.erpandroid_mgr.entity.ProviderInfo;
import com.b1b.js.erpandroid_mgr.entity.ProviderSpecialPartNo;
import com.b1b.js.erpandroid_mgr.utils.WebserviceUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class ProviderDetailActivity extends AppCompatActivity {

    private ArrayAdapter<ProviderSpecialPartNo> adapter;
    private static final int ERROR_NETWORK = 4;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    if (dataSet.size() > 0) {
                        adapter.notifyDataSetChanged();
                    }
                    break;
                case 1:
//                    AlertDialog.Builder builder = new AlertDialog.Builder
//                            (ProviderDetailActivity.this);
//                    builder.setTitle("提示");
//                    builder.setNegativeButton("是", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            Intent intent = new Intent(ProviderDetailActivity.this,
//                                    EditProvider.class);
//                            startActivity(intent);
//                        }
//                    });
//                    builder.setPositiveButton("否", null);
//                    builder.setMessage("当前供应商暂无特色产品，是否新增");
//                    builder.show();
                    break;
                case 2:
                    adapter.notifyDataSetChanged();
                    AlertDialog.Builder errorBuilder3 = new AlertDialog.Builder
                            (ProviderDetailActivity.this);
                    errorBuilder3.setTitle("提示");
                    errorBuilder3.setMessage("删除成功");
                    errorBuilder3.show();
                    break;
                case 3:
                    AlertDialog.Builder errorBuilder2 = new AlertDialog.Builder
                            (ProviderDetailActivity.this);
                    errorBuilder2.setTitle("提示");
                    errorBuilder2.setMessage("删除失败");
                    errorBuilder2.show();
                    break;
                case ERROR_NETWORK:
                    AlertDialog.Builder errorBuilder = new AlertDialog.Builder
                            (ProviderDetailActivity.this);
                    errorBuilder.setTitle("提示");
                    errorBuilder.setMessage("连接服务器失败，请检查网络");
                    errorBuilder.show();
                    break;

            }
        }
    };
    private ArrayList<ProviderSpecialPartNo> dataSet;

    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_detail);
        Button btnAdd = (Button) findViewById(R.id.activity_provider_detail_btn_add);
        TextView tv = (TextView) findViewById(R.id.activity_provider_detail_tv);
        lv = (ListView) findViewById(R.id.activity_provider_detail_lv);
        final Intent lastIntent = getIntent();
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProviderDetailActivity.this, EditProvider
                        .class);
                ProviderInfo info = (ProviderInfo) lastIntent.getSerializableExtra
                        ("data");
                intent.putExtra("providerID", info.getId());
                intent.putExtra("providerName", info.getName());
                startActivity(intent);
            }
        });
        dataSet = new ArrayList<>();
        adapter = new ArrayAdapter<>(ProviderDetailActivity.this,
                android.R.layout.simple_list_item_1, dataSet);
        ProviderInfo info = (ProviderInfo) lastIntent.getSerializableExtra
                ("data");
        tv.setText(info.toString2());
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                ProviderSpecialPartNo providerSpecialPartNo = dataSet.get(position);
                Intent intent = new Intent(ProviderDetailActivity.this, EditProvider
                        .class);
                ProviderInfo info = (ProviderInfo) lastIntent.getSerializableExtra
                        ("data");
                intent.putExtra("data", providerSpecialPartNo);
                intent.putExtra("providerID", info.getId());
                intent.putExtra("providerName", info.getName());
                startActivity(intent);
            }
        });
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int
                    position, final long id) {
                final AlertDialog.Builder builder = new AlertDialog.Builder
                        (ProviderDetailActivity.this);
                final ProviderSpecialPartNo item = dataSet.get(position);
                builder.setTitle("提示");
                builder.setMessage("是否删除？");
                builder.setNegativeButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        new Thread() {
                            @Override
                            public void run() {
                                LinkedHashMap<String, Object> p1 = new LinkedHashMap<>();
                                p1.put("id", item.getObjID());
                                SoapObject re1 = WebserviceUtils.getRequest(p1,
                                        "DeleteProviderPartInfo");
                                try {
                                    SoapPrimitive res1 = WebserviceUtils
                                            .getSoapPrimitiveResponse(re1,
                                                    WebserviceUtils.MartService);
                                    Log.e("zjy", "ProviderDetailActivity->getProvider" +
                                            "():res1==" + res1
                                            .toString());
                                    if (res1.toString().equals("1")) {
                                        mHandler.sendEmptyMessage(2);
                                        dataSet.remove(item);
                                    } else {
                                        mHandler.sendEmptyMessage(3);
                                    }
                                } catch (IOException e) {
                                    mHandler.sendEmptyMessage(ERROR_NETWORK);
                                    e.printStackTrace();
                                } catch (XmlPullParserException e) {
                                    e.printStackTrace();
                                }
                            }
                        }.start();
                    }
                });
                builder.setPositiveButton("否", null);
                builder.show();
                return true;
            }
        });

    }

    public void getProvider(String id) {
        LinkedHashMap<String, Object> p1 = new LinkedHashMap<>();
        p1.put("id", id);
        SoapObject re1 = WebserviceUtils.getRequest(p1, "GetProviderPartInfo");
        try {
            SoapPrimitive res1 = WebserviceUtils.getSoapPrimitiveResponse(re1,
                    WebserviceUtils.MartService);
            JSONObject root = new JSONObject(res1.toString());
            Log.e("zjy", "ProviderDetailActivity->getProvider():res1==" + res1
                    .toString());
            JSONArray array = root.getJSONArray("表");
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                String objid = obj.getString("objid");
                String providername = obj.getString("供应商名称");
                String objType = obj.getString("类型");
                String objValue = obj.getString("名称");
                String notes = obj.getString("备注");
                ProviderSpecialPartNo info = new ProviderSpecialPartNo(objid,
                        providername, objType, objValue, notes);
                dataSet.add(info);
            }
            mHandler.sendEmptyMessage(0);
        } catch (IOException e) {
            mHandler.sendEmptyMessage(ERROR_NETWORK);
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            mHandler.sendEmptyMessage(1);
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        final ProviderInfo info = (ProviderInfo) getIntent().getSerializableExtra("data");
        if (info != null) {
            dataSet.clear();
            new Thread() {
                @Override
                public void run() {
                    getProvider(info.getName());
                }
            }.start();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("提示");
            builder.setMessage("获取数据失败，请重启程序");
            builder.show();
        }
    }
}
