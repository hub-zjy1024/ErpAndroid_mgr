package com.b1b.js.erpandroid_mgr;

import android.app.AlertDialog;
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
import android.widget.EditText;
import android.widget.ListView;

import com.b1b.js.erpandroid_mgr.entity.ProviderInfo;
import com.b1b.js.erpandroid_mgr.utils.MyToast;
import com.b1b.js.erpandroid_mgr.utils.SoftKeyboardUtils;
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

public class ProviderManagerActivity extends AppCompatActivity {

    private EditText edName;
    private Button btnCommit;
    private final Object lock = new Object();

    ArrayList<ProviderInfo> data;
    ArrayAdapter<ProviderInfo> adapter;
    ListView lv;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    adapter.notifyDataSetChanged();
                    SoftKeyboardUtils.closeInputMethod(edName, ProviderManagerActivity
                            .this);

                    break;
                case 1:
                    AlertDialog.Builder builder = new AlertDialog.Builder
                            (ProviderManagerActivity.this);
                    builder.setTitle("提示");
                    builder.setMessage("查询条件有误");
                    builder.show();
                    break;
                case 2:
                    AlertDialog.Builder builder2 = new AlertDialog.Builder
                            (ProviderManagerActivity.this);
                    builder2.setTitle("提示");
                    builder2.setMessage("连接服务器失败，请检查网络");
                    builder2.show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_manager);
        edName = (EditText) findViewById(R.id.activity_provider_manager_ed_name);
        btnCommit = (Button) findViewById(R.id.activity_provider_manager_btn_search);
        lv = (ListView) findViewById(R.id.activity_provider_manager_lv);
        data = new ArrayList<>();
        adapter = new ArrayAdapter<>
                (ProviderManagerActivity.this, android.R.layout
                        .simple_list_item_1, android.R.id.text1, data);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                Intent intent = new Intent(ProviderManagerActivity.this,
                        ProviderDetailActivity
                                .class);
                intent.putExtra("data", data.get(position));
                startActivity(intent);
            }
        });
        final int did = getSharedPreferences("UserInfo", MODE_PRIVATE).getInt
                ("did", -1);
        btnCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (data.size() > 0) {
                    data.clear();
                    adapter.notifyDataSetChanged();
                }
                if (MyApp.id == null) {
                    MyToast.showToast(ProviderManagerActivity.this, "程序出现错误，请重新登录");
                    return;
                }
                new Thread() {
                    @Override
                    public void run() {
                        getData(edName.getText().toString(), did);
                    }
                }.start();
            }
        });
    }


    private void getData(String providername, int did) {
        synchronized (lock) {
            LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
            properties.put("checkWord", "");
            properties.put("userID", Integer.parseInt(MyApp.id));
            properties.put("myDeptID", did);
            properties.put("providerName", providername);
            SoapObject request = WebserviceUtils.getRequest(properties,
                    "GetMyProviderInfoByName");
            try {
                SoapPrimitive response = WebserviceUtils
                        .getSoapPrimitiveResponse(request,
                                WebserviceUtils.MartService);
                Log.e("zjy", "ProviderManagerActivity->GetMyProvider():  " +
                        "response==" + response.toString());
                properties.put("providerName", providername);
                SoapObject request2 = WebserviceUtils.getRequest(properties,
                        "GetMyProvider");
                SoapPrimitive response2 = WebserviceUtils.getSoapPrimitiveResponse
                        (request2, WebserviceUtils.MartService);
                String result2 = response2.toString();
                JSONObject root = new JSONObject(response.toString());
                JSONArray array = root.getJSONArray("表");
                result2 = result2.substring(7);
                String[] item = result2.split(",");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    String id = obj.getString("BaseID");
                    String name = obj.getString("名称");
                    String level = obj.getString("等级");
                    String notes = obj.getString("备注");
                    String state = obj.getString("状态");
                    String hasKaipiao = obj.getString("是否开票");
                    String zhangQi = obj.getString("账期");
                    //                    "是否开票":"0","账期":"0"
                    ProviderInfo info = new ProviderInfo(id, name, null);
                    Log.e("zjy", "ProviderManagerActivity->getData(): name==" + name +
                            "\t卡票：" + hasKaipiao);
                    if (hasKaipiao.equals("1")) {
                        info.setHasKaipiao("是");
                    } else {
                        info.setHasKaipiao("否");
                    }
                    info.setZhangQi(zhangQi);
                    info.setState(state);
                    info.setNotes(notes);
                    info.setLevel(level);
                    data.add(info);
                }
                Log.e("zjy", "ProviderManagerActivity->getData(): Thread==" + Thread
                        .currentThread().getId());
                mHandler.sendEmptyMessage(0);
            } catch (IOException e) {
                mHandler.sendEmptyMessage(2);
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                mHandler.sendEmptyMessage(1);
                e.printStackTrace();
            }
        }
    }
}
