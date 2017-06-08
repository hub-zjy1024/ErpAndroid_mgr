package com.b1b.js.erpandroid_mgr;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.b1b.js.erpandroid_mgr.entity.ProviderSpecialPartNo;
import com.b1b.js.erpandroid_mgr.utils.MyToast;
import com.b1b.js.erpandroid_mgr.utils.WebserviceUtils;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.LinkedHashMap;

public class EditProvider extends AppCompatActivity {

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(EditProvider
                            .this);
                    builder1.setTitle("提示");
                    builder1.setMessage("提交失败");
                    builder1.show();
                    break;
                case 2: AlertDialog.Builder builder2 = new AlertDialog.Builder
                        (EditProvider
                                .this);
                    builder2.setTitle("提示");
                    builder2.setMessage("提交失败，连接服务器失败");
                    builder2.show();
                    break;
                case 0:
                    AlertDialog.Builder builder = new AlertDialog.Builder(EditProvider
                            .this);
                    builder.setTitle("提示");
                    builder.setMessage("提交成功，是否返回");
                    builder.setPositiveButton("否", null);
                    builder.setNegativeButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                    builder.show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_provider);
        final RadioGroup rgType = (RadioGroup) findViewById(R.id
                .activity_edit_provider_rg_type);
        Button btnCommit = (Button) findViewById(R.id.activity_edit_provider_commit);
        Button btnBack = (Button) findViewById(R.id.activity_edit_provider_back);
        final EditText edNotes = (EditText) findViewById(R.id
                .activity_edit_provider_ed_notes);
        final TextView edProviderName = (TextView) findViewById(R.id
                .activity_edit_provider_ed_providerName);
        final EditText edValue = (EditText) findViewById(R.id
                .activity_edit_provider_ed_content);

        final ProviderSpecialPartNo data = (ProviderSpecialPartNo) getIntent()
                .getSerializableExtra("data");
        final String providerID = getIntent().getStringExtra("providerID");
        if (data != null) {
            edNotes.setText(data.getNotes());
            if (data.getObjType().equals("品牌")) {
                rgType.check(R.id.activity_edit_provider_rb_brand);
            } else {
                rgType.check(R.id.activity_edit_provider_rb_partNO);
            }

            edValue.setText(data.getObjValue());

        }
        String providerName = getIntent().getStringExtra("providerName");
        edProviderName.setText(providerName);
        btnCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (providerID == null) {
                    MyToast.showToast(EditProvider.this, "获取数据失败，请返回重新进入");
                    return;
                }
                String type = "型号";
                if (rgType.getCheckedRadioButtonId() == R.id
                        .activity_edit_provider_rb_brand) {
                    type = "品牌";
                }
                final String tempType = type;
                final String notes = edNotes.getText().toString();
                final String objValue = edValue.getText().toString();
                new Thread() {
                    @Override
                    public void run() {
                        String objID = "0";
                        if (data != null) {
                            objID = data.getObjID();
                        }
                        SetProviderPartInfoAdd(objID, "1", providerID,
                                objValue, tempType,
                                notes);
                    }
                }.start();
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void SetProviderPartInfoAdd(String objid, String parentid, String objname,
                                       String objvalue, String
                                               objtype, String objexpress) {
        //        int SetProviderPartInfoAdd(string objid, string parentid, string
        // objname, string objvalue, string
        //        objtype, string objexpress);
        LinkedHashMap<String, Object> params = new LinkedHashMap<>();
        params.put("objid", objid);
        params.put("parentid", parentid);
        params.put("objname", objname);
        params.put("objvalue", objvalue);
        params.put("objtype", objtype);
        params.put("objexpress", objexpress);
        SoapObject re1 = WebserviceUtils.getRequest(params, "SetProviderPartInfoAdd");
        try {
            SoapPrimitive res1 = WebserviceUtils.getSoapPrimitiveResponse(re1,
                    WebserviceUtils.MartService);
            Log.e("zjy", "ProviderManagerActivity->GetProviderPartInfoByObjID(): " +
                    "response==" +
                    res1.toString());
            if (res1.toString().equals("1")) {
                mHandler.sendEmptyMessage(0);
            } else {
                mHandler.sendEmptyMessage(1);
            }
        } catch (IOException e) {
            mHandler.sendEmptyMessage(2);
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
    }
    //    int DeleteProviderPartInfo(string id);
}
