package com.b1b.js.erpandroid_mgr;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.b1b.js.erpandroid_mgr.adapter.CaigouInsertAdapter;
import com.b1b.js.erpandroid_mgr.adapter.GoodTypeAdapter;
import com.b1b.js.erpandroid_mgr.entity.CaigouGoodType;
import com.b1b.js.erpandroid_mgr.entity.InsertDetialInfo;
import com.b1b.js.erpandroid_mgr.entity.ProviderInfo;
import com.b1b.js.erpandroid_mgr.utils.MyToast;
import com.b1b.js.erpandroid_mgr.utils.WebserviceUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class CaigoudanEditActivity extends AppCompatActivity {

    private Button btnFinish;
    private int times = 1;
    private TextView tvPartNo;
    private Button btnProvider;
    private CaigouInsertAdapter insertAdapter;
    private GoodTypeAdapter gTypeAdapter;
    private ArrayAdapter<String> popAdapter;
    private ArrayList<CaigouGoodType> totalTypeLists = new ArrayList<>();
    private ArrayList<InsertDetialInfo> insertData;
    private ArrayList<CaigouGoodType> saixuanList = new ArrayList<>();
    private ArrayList<ProviderInfo> providerInfos = new ArrayList<>();
    private List<String> providerList = new ArrayList<>();
    private String currentProvider;
    private ProviderInfo currentProviderInfo;
    private CheckBox cboHasFapiao;
    private ListView lsView;
    private AlertDialog editDialog;
    private String storageID;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    //条件有误，显示所有的类别
                    MyToast.showToast(CaigoudanEditActivity.this, "条件有误，请重新输入条件");
                    for (int i = 0; i < totalTypeLists.size(); i++) {
                        saixuanList.add(totalTypeLists.get(i));
                    }
                    gTypeAdapter.notifyDataSetChanged();
                    break;
                case 1:
                    for (int i = 0; i < totalTypeLists.size(); i++) {
                        saixuanList.add(totalTypeLists.get(i));
                    }
                    break;
                case 2:
                    if (saixuanList.size() != 0)
                        gTypeAdapter.notifyDataSetChanged();
                    break;
                case 3:
                    for (ProviderInfo info : providerInfos) {
                        if (info.getName() != null) {
                            providerList.add(info.getName());
                        }
                    }
                    popAdapter.notifyDataSetChanged();
                    break;
                case 4:
                    MyToast.showToast(CaigoudanEditActivity.this, "插入类别失败");
                    break;
                case 5:
                    MyToast.showToast(CaigoudanEditActivity.this, "搜索失败，请重新输入搜索条件");
                    break;
                case 6:
                    insertAdapter.notifyDataSetChanged();
                    MyToast.showToast(CaigoudanEditActivity.this, "插入成功");
                    final AlertDialog.Builder builder = new AlertDialog.Builder(CaigoudanEditActivity.this);
                    builder.setTitle("提示");
                    builder.setMessage("插入成功,是否返回(2秒自动返回)");
                    builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                    builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    editDialog = builder.show();
                    mHandler.sendEmptyMessageDelayed(10, 2000);
                    break;
                case 7:
                    MyToast.showToast(CaigoudanEditActivity.this, "插入进价和批号失败，请重新尝试");
                    break;
                case 8:
                    //获取采购单详情成功
                    insertAdapter.notifyDataSetChanged();
                    break;
                case 9:
                    MyToast.showToast(CaigoudanEditActivity.this, "获取采购单详情出错，请后退并重新尝试！");
                    break;
                case 10:
                    mHandler.removeCallbacksAndMessages(null);
                    finish();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caigoudan_edit);
        tvPartNo = (TextView) findViewById(R.id.caigouedit_partno);
        btnProvider = (Button) findViewById(R.id.caigouedit_provider);
        cboHasFapiao = (CheckBox) findViewById(R.id.caigouedit_fapiao);
        lsView = (ListView) findViewById(R.id.caigouedit_lv);
        btnFinish = (Button) findViewById(R.id.caigouedit_finish);

        //加载库房名称和库房id
        final int[] kfIds = getResources().getIntArray(R.array.storage_id);
        final String[] kfNames = getResources().getStringArray(R.array.storage_name);
        final String oprName = getSharedPreferences("UserInfo", 0).getString("oprName", "");
        insertData = new ArrayList<>();
        //        typeAdapter = new ArrayAdapter<String>(this, R.layout.spinner_simple_item, R.id.spinner_item_tv, typeAdapterList);
        //自定义回调设置监听列表中的每一个按钮
        insertAdapter = new CaigouInsertAdapter(insertData, CaigoudanEditActivity.this, new CaigouInsertAdapter.InsertBtnOnClickListener() {
            @Override
            public void onClick(int position) {
                if (position < insertData.size()) {
                    final InsertDetialInfo info = insertData.get(position);
                    showInsertDialog(info);
                }
            }
        });

        lsView.setAdapter(insertAdapter);
        tvPartNo.setText(getIntent().getStringExtra("pid"));
        //获取采购单详细信息
        GetBillDetailByPid("", getIntent().getStringExtra("pid"));
        Log.e("zjy", "CaigoudanEditActivity.java->onCreate(): phone==" + getPhoneCode(CaigoudanEditActivity.this));
        //        第二步：为下拉列表定义一个适配器，这里就用到里前面定义的list。
        //        第四步：将适配器添加到下拉列表上
        popAdapter = new ArrayAdapter<>(CaigoudanEditActivity.this, R.layout.spinner_simple_item, R.id.spinner_item_tv, providerList);
        btnProvider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CaigoudanEditActivity.this);
                final AlertDialog providerDialog = builder.create();
                View view = LayoutInflater.from(CaigoudanEditActivity.this).inflate(R.layout.popwindow_view, null);
                providerDialog.setView(view);
                providerDialog.show();
                ListView listView = (ListView) view.findViewById(R.id.popwindow_lv);
                Button btnS = (Button) view.findViewById(R.id.popwindow_search);
                final EditText editText = (EditText) view.findViewById(R.id.popwindow_leibie);
                btnS.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String keyword = editText.getText().toString();
                        int did = getSharedPreferences("UserInfo", MODE_PRIVATE).getInt("did", -1);
                        if (did == -1) {
                            MyToast.showToast(CaigoudanEditActivity.this, "部门号不存在，请清理缓存");
                            return;
                        }
                        providerInfos.clear();
                        providerList.clear();
                        getMyProvider("", MyApp.id, did, keyword);
                    }
                });
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        currentProvider = providerList.get(position);
                        currentProviderInfo = providerInfos.get(position);
                        if (currentProvider != null) {
                            MyToast.showToast(CaigoudanEditActivity.this, "选择：" + currentProvider);
                            btnProvider.setText(currentProvider);
                            if (currentProviderInfo.getHasKaipiao().equals("1")) {
                                cboHasFapiao.setEnabled(true);
                            } else {
                                cboHasFapiao.setEnabled(false);
                            }
                            providerDialog.dismiss();
                        }
                    }
                });
                listView.setAdapter(popAdapter);
                int did = getSharedPreferences("UserInfo", MODE_PRIVATE).getInt("did", -1);
                if (did == -1) {
                    MyToast.showToast(CaigoudanEditActivity.this, "部门号不存在，请清理缓存");

                }
                providerInfos.clear();
                providerList.clear();
                getMyProvider("", MyApp.id, did, "");
            }
        });
        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取库房id
                if (currentProviderInfo == null) {
                    MyToast.showToast(CaigoudanEditActivity.this, "请选择供应商");
                    return;
                }
                String hasFapiao;
                if (cboHasFapiao.isChecked()) {
                    hasFapiao = "1";
                } else {
                    hasFapiao = "0";
                }
                final String fapiao = hasFapiao;
                final String providerId = currentProviderInfo.getId();
                final String opName = oprName;
                StringBuilder detailBuilder = new StringBuilder();
                final String id = getIntent().getStringExtra("pid");
                final String pid = getIntent().getStringExtra("pid");
                final String uid = MyApp.id;
                StringBuilder typeBuilder = new StringBuilder();
                for (InsertDetialInfo info : insertData) {
                    String partNo = info.getPartno();
                    String typeId = info.getTypeId();
                    String typeName = info.getType();
                    typeBuilder.append(typeId + "|" + partNo + "|" + typeName + "@");
                    detailBuilder.append(info.getDetailId() + "-" + info.getMoney() + "-" + info.getPihao() + ",");
                }
                final String detail = detailBuilder.deleteCharAt(detailBuilder.length() - 1).toString();
                final String typeDetail = typeBuilder.deleteCharAt(typeBuilder.length() - 1).toString();
                Log.e("zjy", "CaigoudanEditActivity.java->onClick(): typeDetail==" + pid + "\t" + typeDetail);
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        try {
                            String response = setPartTypeInfo(pid, uid, typeDetail);
                            if (response.equals("1")) {
                                insertMartStockInfo("", id, providerId, fapiao, detail, MyApp.id, opName, getPhoneCode(CaigoudanEditActivity.this), String.valueOf(storageID));
                            } else {
                                mHandler.sendEmptyMessage(4);
                            }
                        } catch (IOException | XmlPullParserException e) {
                            mHandler.sendEmptyMessage(7);
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });
        getTypeInfo("", totalTypeLists, 1);
    }

    private void showInsertDialog(final InsertDetialInfo info) {
        AlertDialog.Builder builder = new AlertDialog.Builder(CaigoudanEditActivity.this);
        final AlertDialog providerDialog = builder.create();
        View view = LayoutInflater.from(CaigoudanEditActivity.this).inflate(R.layout.dialog_insert_info, null);
        providerDialog.setView(view);
        providerDialog.show();
        ListView listView = (ListView) view.findViewById(R.id.dialog_insert_lv);
        Button btnS = (Button) view.findViewById(R.id.dialog_insert_search);
        final EditText dialogPihao = (EditText) view.findViewById(R.id.dialog_insert_pihao);
        final EditText dialogMoney = (EditText) view.findViewById(R.id.dialog_insert_money);
        final EditText editText = (EditText) view.findViewById(R.id.dialog_insert_leibie);
        Button btnCommit = (Button) view.findViewById(R.id.dialog_insert_commit);
        if (info.getMoney() != null) {
            dialogMoney.setText(info.getMoney());
        }
        if (info.getPihao() != null) {
            dialogPihao.setText(info.getPihao());
        }
        gTypeAdapter = new GoodTypeAdapter(totalTypeLists, CaigoudanEditActivity.this, R.layout.spinner_simple_item);
        listView.setAdapter(gTypeAdapter);
        btnS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keyword = editText.getText().toString().trim();
                saixuanList.clear();
                if (keyword.equals("")) {
                    for (int i = 0; i < totalTypeLists.size(); i++) {
                        saixuanList.add(totalTypeLists.get(i));
                    }
                    gTypeAdapter.notifyDataSetChanged();
                } else {
                    gTypeAdapter.notifyDataSetChanged();
                    getTypeInfo(keyword, saixuanList, 2);
                }
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position < saixuanList.size()) {
                    CaigouGoodType caigouGoodType = saixuanList.get(position);
                    MyToast.showToast(CaigoudanEditActivity.this, "选择类别：" + caigouGoodType.getTypeName());
                    info.setType(caigouGoodType.getTypeName());
                    info.setTypeId(caigouGoodType.getTypeID());
                }
            }
        });
        btnCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String money = dialogMoney.getText().toString().trim();
                try {
                    Double dMoney = Double.valueOf(money);
                    Double checkPrice = info.getCheckPrice();
                    if (checkPrice != null) {
                        if (checkPrice < dMoney) {
                            MyToast.showToast(CaigoudanEditActivity.this, "采购价格超过批复价格" + checkPrice);
                            return;
                        }
                    }
                } catch (NumberFormatException e) {
                    MyToast.showToast(CaigoudanEditActivity.this, "采购价格只能输入整数或小数");
                    e.printStackTrace();
                    return;
                }
                String pihao = dialogPihao.getText().toString().trim();
                if (pihao.equals("")) {
                    pihao = "无";
                }
                info.setMoney(money);
                info.setPihao(pihao);
                insertAdapter.notifyDataSetChanged();
                providerDialog.dismiss();
            }
        });
    }

    public static String getPhoneCode(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String imei = tm.getSimSerialNumber();
        String deviceId = tm.getDeviceId();
        String phoneModel = Build.MODEL;
        String phoneName = Build.BRAND;
        StringBuilder phoneId = new StringBuilder();
        phoneId.append(phoneModel);
        phoneId.append("-");
        phoneId.append(phoneName);
        phoneId.append("-");
        phoneId.append(deviceId);
        return phoneId.toString();
    }

    ;

    /**
     @param pid
     @param uid
     @param strValue 拼接格式 typeId|typeIdValue|partno
     @return
     @throws IOException
     @throws XmlPullParserException
     */
    public String setPartTypeInfo(String pid, String uid, String strValue) throws IOException, XmlPullParserException {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("pid", pid);
        map.put("uid", uid);
        map.put("strValue", strValue);
        SoapObject request = WebserviceUtils.getRequest(map, "SetSC_BD_PartTypeInfoInfo");
        SoapPrimitive response = WebserviceUtils.getSoapPrimitiveResponse(request, SoapEnvelope.VER11, WebserviceUtils.MartService);
        Log.e("zjy", "CaigoudanEditActivity.java->setPartTypeInfo():response==" + response.toString());
        return response.toString();

    }

    public String getTypeInfo(final String keyword, final List<CaigouGoodType> list, final int targetWhat) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                list.clear();
                LinkedHashMap<String, Object> map = new LinkedHashMap<>();
                map.put("selValue", keyword);
                SoapObject request = WebserviceUtils.getRequest(map, "GetXinHaoManageInfo");
                try {
                    SoapPrimitive response = WebserviceUtils.getSoapPrimitiveResponse(request, SoapEnvelope.VER11, WebserviceUtils.MartService);
                    JSONObject obj = new JSONObject(response.toString());
                    JSONArray jsonArray = obj.getJSONArray("表");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject tObj = jsonArray.getJSONObject(i);
                        String typeID = tObj.getString("objid");
                        String typeName = tObj.getString("objvalue");
                        CaigouGoodType caigouGoodType = new CaigouGoodType(typeID, typeName);
                        list.add(caigouGoodType);
                    }
                    mHandler.sendEmptyMessage(targetWhat);
                } catch (XmlPullParserException | IOException | JSONException e) {
                    mHandler.sendEmptyMessage(0);
                    e.printStackTrace();
                }
            }
        }.start();
        return null;
    }

    //    GetMartStockInfoByID
    //    name="checkWord" nillable="true" type=string" />
    //    name="id" type=int" />
    public void GetBillDetailByPid(String checkWord, String id) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("checkWord", checkWord);
        map.put("id", id);
        final SoapObject request = WebserviceUtils.getRequest(map, "GetMartStockInfoByID");
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    SoapPrimitive response = WebserviceUtils.getSoapPrimitiveResponse(request, SoapEnvelope.VER11, WebserviceUtils.MartService);
                    if (response.toString().startsWith("SUCCESS")) {
                        String json = response.toString().substring(7);
                        JSONObject root = new JSONObject(json);
                        storageID = root.getString("storageid");
                        JSONArray detailsArray = root.getJSONArray("Details");
                        for (int i = 0; i < detailsArray.length(); i++) {
                            JSONObject jObj = detailsArray.getJSONObject(i);
                            InsertDetialInfo tempInfo = new InsertDetialInfo();
                            tempInfo.setCheckPrice(Double.valueOf(jObj.getString("批复价")));
                            tempInfo.setDetailId(jObj.getString("DID"));
                            tempInfo.setPartno(jObj.getString("型号"));
                            tempInfo.setTypeId(jObj.getString("型号信息分类值"));
                            tempInfo.setType(jObj.getString("型号信息分类显示"));
                            tempInfo.setPihao(jObj.getString("申请批号"));
                            insertData.add(tempInfo);
                        }
                        mHandler.sendEmptyMessage(8);
                    }
                    Log.e("zjy", "CaigoudanEditActivity.java->run(): detailRes==" + response.toString());
                } catch (IOException | JSONException e) {
                    mHandler.sendEmptyMessage(9);
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void getMyProvider(String checkWord, String userID, int myDeptID, String providerName) {
        //GetMyProvider
        // paramName="checkWord"  type="string"
        //paramName="userID" type="int"
        //paramName="myDeptID" type="int"
        //paramName="providerName"  type="string"
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("checkWord", checkWord);
        map.put("userID", Integer.valueOf(userID));
        map.put("myDeptID", myDeptID);
        map.put("providerName", providerName);
        final SoapObject request = WebserviceUtils.getRequest(map, "GetMyProvider");
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    SoapPrimitive response = WebserviceUtils.getSoapPrimitiveResponse(request, SoapEnvelope.VER11, WebserviceUtils.MartService);
                    String res = response.toString();
                    if (res.length() > 7) {
                        res = res.substring(7);
                        String[] items = res.split(",");
                        for (int i = 0; i < items.length; i++) {
                            String[] s = items[i].split("-");
                            ProviderInfo info = new ProviderInfo(s[0], s[1], s[2]);
                            providerInfos.add(info);
                        }
                        mHandler.sendEmptyMessage(3);
                    } else {
                        mHandler.sendEmptyMessage(5);
                    }
                } catch (IOException | XmlPullParserException e) {
                    mHandler.sendEmptyMessage(5);
                    e.printStackTrace();
                }
            }
        }.start();
    }

    //    SaveMartStock
    //     paramName="checkWord"  type="string"
    //     paramName="id" type="int"
    //     paramName="providerID" type="int"
    //     paramName="nofapiao" type="int"
    //     paramName="details"  type="string"
    //     paramName="operID" type="int"
    //     paramName="operName"  type="string"
    //     paramName="deviceID"  type="string"
    //     paramName="storageID" type="int"

    public void insertMartStockInfo(String checkWord, String id, String providerID, String nofapiao, String details, String operID, String operName, String deviceID, String storageID) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("checkWord", checkWord);
        map.put("id", Integer.valueOf(id));
        map.put("providerID", Integer.valueOf(providerID));
        map.put("nofapiao", Integer.valueOf(nofapiao));
        map.put("details", details);
        map.put("operID", Integer.valueOf(operID));
        map.put("operName", operName);
        map.put("deviceID", deviceID);
        map.put("storageID", Integer.valueOf(storageID));
        Log.e("zjy", "CaigoudanEditActivity.java->insertMartStockInfo(): pid==" + details);
        final SoapObject request = WebserviceUtils.getRequest(map, "SaveMartStock");
        try {
            SoapPrimitive response = WebserviceUtils.getSoapPrimitiveResponse(request, SoapEnvelope.VER11, WebserviceUtils.MartService);
            if (response.toString().equals("SUCCESS")) {
                mHandler.sendEmptyMessage(6);
            } else {
                mHandler.sendEmptyMessage(7);
            }
            Log.e("zjy", "CaigoudanEditActivity.java->insertMartStockInfo(): response==" + response.toString());
        } catch (IOException e) {
            mHandler.sendEmptyMessage(7);
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            mHandler.sendEmptyMessage(7);
            e.printStackTrace();
        }
        new Thread() {
            @Override
            public void run() {
                super.run();
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (editDialog != null && editDialog.isShowing()) {
            editDialog.cancel();
        }
    }
}

