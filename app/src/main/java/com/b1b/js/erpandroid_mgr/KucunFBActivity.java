package com.b1b.js.erpandroid_mgr;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.b1b.js.erpandroid_mgr.adapter.TableAdapter;
import com.b1b.js.erpandroid_mgr.entity.KucunFBInfo;
import com.b1b.js.erpandroid_mgr.utils.MyToast;
import com.b1b.js.erpandroid_mgr.utils.SoftKeyboardUtils;
import com.b1b.js.erpandroid_mgr.utils.WebserviceUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class KucunFBActivity extends AppCompatActivity {

    private List<KucunFBInfo> data;
    private ListView lv;
    private static final int ERROR_NET = 1;
    private static final int SUCCESS_SEARCH = 0;
    private static final int ERROR_OPTION = 2;
    private static final int SUCCESS_PRICE = 3;
    private static final int ERROR_PRICE = 4;
    private static final int ERROR_CHANGE = 5;
    private static final int SUCCESS_CHANGE = 6;
    Button dialogLock;
    Button dialogCommit;
    Button dialogUnlock;
    private CheckBox cboBeihuo;
    private CheckBox cboFabu;
    private CheckBox cboZero;
    TableAdapter tableAdapter;
    private String currentIp = "";
    AlertDialog temDialog;
    private EditText edPart;
    private final Object lock = new Object();
    private ProgressDialog pdDialog;
    private Handler zHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SUCCESS_SEARCH:
                    if (data.size() > 0) {
                        tableAdapter.notifyDataSetChanged();
                        SoftKeyboardUtils.closeInputMethod(edPart, KucunFBActivity.this);
                    }
                    if (pdDialog != null && pdDialog.isShowing()) {
                        pdDialog.cancel();
                    }
                    break;
                case ERROR_NET:
                    MyToast.showToast(KucunFBActivity.this, "连接服务器失败，请检查网络");
                    if (pdDialog != null && pdDialog.isShowing()) {
                        pdDialog.cancel();
                    }
                    break;
                case ERROR_OPTION:
                    MyToast.showToast(KucunFBActivity.this, "查询条件有误，请更改");
                    if (pdDialog != null && pdDialog.isShowing()) {
                        pdDialog.cancel();
                    }
                    break;
                case SUCCESS_PRICE:
                    AlertDialog.Builder builder = new AlertDialog.Builder(KucunFBActivity.this);
                    builder.setTitle("成功");
                    builder.setMessage("发布库存价格成功");
                    builder.show();
                    dismissDialog();
                    tableAdapter.notifyDataSetChanged();
                    break;
                case ERROR_PRICE:
                    AlertDialog.Builder builder2 = new AlertDialog.Builder(KucunFBActivity.this);
                    builder2.setTitle("失败");
                    builder2.setMessage("发布库存价格失败");
                    builder2.show();
                    break;
                case ERROR_CHANGE:
                    AlertDialog.Builder builder3 = new AlertDialog.Builder(KucunFBActivity.this);
                    builder3.setTitle("失败");
                    builder3.setMessage("更改失败");
                    builder3.show();
                    break;
                case SUCCESS_CHANGE:
                    tableAdapter.notifyDataSetChanged();
                    AlertDialog.Builder builder4 = new AlertDialog.Builder(KucunFBActivity.this);
                    builder4.setTitle("成功");
                    builder4.setMessage("更改发布状态成功");
                    builder4.show();
                    boolean flag = (boolean) msg.obj;
                    if (flag) {
                        dialogUnlock.setVisibility(View.INVISIBLE);
                        dialogCommit.setVisibility(View.VISIBLE);
                        dialogLock.setVisibility(View.VISIBLE);
                    } else {
                        dialogUnlock.setVisibility(View.VISIBLE);
                        dialogCommit.setVisibility(View.INVISIBLE);
                        dialogLock.setVisibility(View.INVISIBLE);
                        dismissDialog();
                    }
                    break;
            }
        }
    };


    private void dismissDialog() {
        if (temDialog != null && temDialog.isShowing()) {
            temDialog.dismiss();
            temDialog = null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kucun_fb);
        lv = (ListView) findViewById(R.id.kucunfb_lv);
        data = new ArrayList<>();
        cboBeihuo = (CheckBox) findViewById(R.id.kucunfb_isbeihuo);
        cboFabu = (CheckBox) findViewById(R.id.kucunfb_cbo_only_fabu);
        cboZero = (CheckBox) findViewById(R.id.kucunfb_cbo_zero);
        pdDialog = new ProgressDialog(this);
        pdDialog.setTitle("提示");
        pdDialog.setMessage("正在查询。。");
        Button btnSearch = (Button) findViewById(R.id.kucunfb_btn_search);
        edPart = (EditText) findViewById(R.id.kucunfb_ed_partno);
        final EditText edCouts = (EditText) findViewById(R.id.kucunfb_ed_1);
        tableAdapter = new TableAdapter(this, data);
        lv.setAdapter(tableAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                showEditDialog(position);
            }
        });
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(KucunFBActivity.this);
                builder.setTitle("详情");
                builder.setMessage(data.get(position).toString2());
                builder.show();
                return true;
            }

        });
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                synchronized (KucunFBActivity.this) {
                    if (data.size() > 0) {
                        data.clear();
                        tableAdapter.notifyDataSetChanged();
                    }
                    final String partno = edPart.getText().toString();
                    final String pCouts = edCouts.getText().toString().trim();
                    pdDialog.show();
                    getData(partno, pCouts);
                }

            }

        });
        getIP();
    }

    private void showEditDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(KucunFBActivity.this);
        View v = LayoutInflater.from(KucunFBActivity.this).inflate(R.layout.kucunfb_dialog_fabujiage, null);
        final EditText editText = (EditText) v.findViewById(R.id.kucunfb_dialog_price);
        final TextView tvInfo = (TextView) v.findViewById(R.id.kucunfb_dialog_info);
        final Button btnLock = (Button) v.findViewById(R.id.kucunfb_dialog_lock);
        final Button btnUnlcok = (Button) v.findViewById(R.id.kucunfb_dialog_unlock);
        final Button btnCommit = (Button) v.findViewById(R.id.kucunfb_dialog_commit);
        final KucunFBInfo fbInfo = data.get(position);
        tvInfo.setText("型号：" + fbInfo.getPartNo() + "\t剩余数量:" + fbInfo.getLeftCount());
        dialogLock = btnLock;
        dialogUnlock = btnUnlcok;
        dialogCommit = btnCommit;
        if (data.get(position).isFabu()) {
            btnUnlcok.setVisibility(View.INVISIBLE);
        } else {
            btnLock.setVisibility(View.INVISIBLE);
            btnCommit.setVisibility(View.INVISIBLE);
        }
        btnLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFBState(position, false);
            }
        });
        btnUnlcok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFBState(position, true);
            }
        });
        btnCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String price = editText.getText().toString().trim();
                if (price.equals("")) {
                    MyToast.showToast(KucunFBActivity.this, "请输入发布价格");
                    return;
                }
                if (currentIp.equals("")) {
                    MyToast.showToast(KucunFBActivity.this, "请稍等，正在获取信息");
                    return;
                }
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        String ip = currentIp;
                        String dogSN = CaigoudanEditActivity.getPhoneCode(KucunFBActivity.this);
                        try {
                            String res = setFabuPrice(fbInfo.getDetailID(), price, MyApp.id, ip, dogSN);
                            Message msg = zHandler.obtainMessage(ERROR_PRICE);
                            if (res.equals("1")) {
                                msg = zHandler.obtainMessage(SUCCESS_PRICE);
                                fbInfo.setFabuPrice(price);
                                msg.obj = fbInfo.getPartNo();
                            }
                            Log.e("zjy", "KucunFBActivity->run():send partno==" + fbInfo.getPartNo());
                            msg.sendToTarget();
                        } catch (IOException e) {
                            zHandler.sendEmptyMessage(ERROR_NET);
                            e.printStackTrace();
                        } catch (XmlPullParserException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });
        builder.setView(v);
        builder.setTitle("发布库存价格");
        builder.setPositiveButton("返回", null);
        temDialog = builder.show();
    }

    private void changeFBState(final int position, final boolean isFB) {
        if (currentIp.equals("")) {
            MyToast.showToast(KucunFBActivity.this, "请稍等，正在获取信息");
            return;
        }
        new Thread() {
            @Override
            public void run() {
                super.run();
                KucunFBInfo fbInfo = data.get(position);
                String ip = currentIp;
                String dogSN = CaigoudanEditActivity.getPhoneCode(KucunFBActivity.this);
                try {
                    String res = setFBState(fbInfo.getDetailID(), isFB, MyApp.id, ip, dogSN);
                    Message msg = zHandler.obtainMessage(ERROR_CHANGE);
                    if (res.equals("1")) {
                        fbInfo.setFabu(isFB);
                        msg = zHandler.obtainMessage(SUCCESS_CHANGE);
                        msg.obj = isFB;
                    }
                    Log.e("zjy", "KucunFBActivity->run():send partno==" + fbInfo.getPartNo());
                    msg.sendToTarget();
                } catch (IOException e) {
                    zHandler.sendEmptyMessage(ERROR_NET);
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void getIP() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    URL url = new URL("http://172.16.6.101:802/ErpV5IP.asp");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    InputStream is = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                    String result = "";
                    String s;
                    while ((s = reader.readLine()) != null) {
                        result = result + s;
                    }
                    currentIp = result;
                    Log.e("zjy", "SettingActivity->run():getip==" + result);
                    reader.close();
                } catch (IOException e) {
                    try {
                        Thread.sleep(200);
                        getIP();
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void getData(final String partno, final String pCouts) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    int count = 10000;
                    if (!pCouts.equals("")) {
                        count = Integer.parseInt(pCouts);
                    }
                    getFabuInfo(partno, count, true);
                    zHandler.sendEmptyMessage(SUCCESS_SEARCH);
                } catch (IOException e) {
                    zHandler.sendEmptyMessage(ERROR_NET);
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    zHandler.sendEmptyMessage(ERROR_OPTION);
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void getFabuInfo(String part, int pcount, boolean isbhbm) throws IOException,
            XmlPullParserException, JSONException {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("part", part);
        map.put("pcount", pcount);
        map.put("isbhbm", isbhbm);
        SoapObject request = WebserviceUtils.getRequest(map, "GetInstorageBalanceInfoNew");
        SoapPrimitive response = WebserviceUtils.getSoapPrimitiveResponse(request, SoapEnvelope
                .VER11, WebserviceUtils.MartService);
        Log.e("zjy", "KucunFBActivity->getFabuInfo(): reponse==" + response.toString());
        JSONObject root = new JSONObject(response.toString());
        JSONArray array = root.getJSONArray("表");
        for (int i = 0; i < array.length(); i++) {
            JSONObject tem = array.getJSONObject(i);
            String pid = tem.getString("单据号");
            String detailID = tem.getString("明细ID");
            String is = tem.getString("是否发布");
            boolean isFabu = false;
            if (is.equals("True")) {
                isFabu = true;
            }
            String fabuprice = tem.getString("发布价格");
            String limittedPrice = tem.getString("限价");
            String inPrice = tem.getString("进价");
            String dollar = tem.getString("美金");
            String partNO = tem.getString("型号");
            String leftCounts = tem.getString("剩余数量");
            String totalInPrice = tem.getString("金额");
            String deptNo = tem.getString("部门号");
            String factory = tem.getString("厂家");
            String pihao = tem.getString("批号");
            String description = tem.getString("描述");
            String fengzhuang = tem.getString("封装");
            String storage = tem.getString("仓库");
            String rukuID = tem.getString("入库编号");
            String rukuDate = tem.getString("入库日期");
            String place = tem.getString("位置");
            String forcePrice = tem.getString("强限价");
            String pidType = tem.getString("单据类型");
            String kaipiaoType = tem.getString("开票类型");
            String kpCompany = tem.getString("开票公司");
            String xdCompany = tem.getString("下单公司");
            String isBeihuo = tem.getString("是否备货部门");
            String notes = tem.getString("备注");
            String pankuFlag = tem.getString("PanKuFlag");
            String pankuDate = tem.getString("盘库日期");
            String prePartNo = tem.getString("盘库前型号");
            String preCounts = tem.getString("盘库前数量");
            String pankuPartNo = tem.getString("盘库型号");
            String pankuCounts = tem.getString("盘库数量");
            String pankuFactory = tem.getString("盘库厂家");
            String pankuDescription = tem.getString("盘库描述");
            String pankuPack = tem.getString("PKPack");
            String minPack = tem.getString("MinPack");
            String operID = tem.getString("OperID");
            String operName = tem.getString("OperName");
            String publishid = tem.getString("publishid");
            String updateDate = tem.getString("uptime");
            KucunFBInfo fbInfo = new KucunFBInfo(partNO, factory, pihao, fengzhuang, description,
                    pid, detailID, isFabu, fabuprice, limittedPrice, inPrice, dollar, leftCounts,
                    totalInPrice, deptNo, storage, rukuID, rukuDate, place, forcePrice, pidType,
                    kaipiaoType, kpCompany, xdCompany, notes, pankuFlag, pankuDate, prePartNo,
                    preCounts, pankuPartNo, pankuCounts, pankuFactory, pankuDescription,
                    pankuPack, minPack, operID, operName, publishid, updateDate);
            fbInfo.setIsBeihuo(isBeihuo);
            if (cboBeihuo.isChecked()) {
                if (isBeihuo.equals("是")) {
                    if (cboFabu.isChecked()) {
                        if (isFabu) {
                            if (cboZero.isChecked()) {
                                if (leftCounts.equals("0")) {
                                    data.add(fbInfo);
                                }
                            } else {
                                data.add(fbInfo);
                            }
                        }
                    } else {
                        if (cboZero.isChecked()) {
                            if (leftCounts.equals("0")) {
                                data.add(fbInfo);
                            }
                        } else {
                            data.add(fbInfo);
                        }
                    }
                }
            } else {
                if (cboFabu.isChecked()) {
                    if (isFabu) {
                        if (cboZero.isChecked()) {
                            if (leftCounts.equals("0")) {
                                data.add(fbInfo);
                            }
                        } else {
                            data.add(fbInfo);
                        }
                    }
                } else {
                    if (cboZero.isChecked()) {
                        if (leftCounts.equals("0")) {
                            data.add(fbInfo);
                        }
                    } else {
                        data.add(fbInfo);
                    }
                }
            }
        }
    }

    public String setFabuPrice(String detailID, String price, String uid, String ip, String dogSN) throws IOException,
            XmlPullParserException {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("detailID", detailID);
        map.put("price", price);
        map.put("uid", uid);
        map.put("ip", ip);
        map.put("dogSN", dogSN);
        SoapObject request = WebserviceUtils.getRequest(map, "SetPriceInfo");
        SoapPrimitive response = WebserviceUtils.getSoapPrimitiveResponse(request, SoapEnvelope.VER11, WebserviceUtils
                .MartService);
        Log.e("zjy", "KucunFBActivity->setFabuPrice():price response==" + response.toString());
        return response.toString();
    }

    public String setFBState(String detailID, boolean isfb, String uid, String ip, String dogSN) throws IOException,
            XmlPullParserException {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("detailID", detailID);
        map.put("isfb", isfb);
        map.put("uid", uid);
        map.put("ip", ip);
        map.put("dogSN", dogSN);
        SoapObject request = WebserviceUtils.getRequest(map, "SetStypeInfo");
        SoapPrimitive response = WebserviceUtils.getSoapPrimitiveResponse(request, SoapEnvelope.VER11, WebserviceUtils
                .MartService);
        Log.e("zjy", "KucunFBActivity->setFBState(): state response==" + response.toString());
        return response.toString();
    }


}
