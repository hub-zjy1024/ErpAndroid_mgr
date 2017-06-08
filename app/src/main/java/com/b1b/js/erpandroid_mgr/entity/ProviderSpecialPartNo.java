package com.b1b.js.erpandroid_mgr.entity;

import java.io.Serializable;

/**
 * Created by 张建宇 on 2017/6/5.
 */

public class ProviderSpecialPartNo implements Serializable {

    private String objID;
    private String providerName;
    private String objType;
    private String objValue;
    private String notes;

    @Override
    public String toString() {
        return
                "供应商='" + providerName + "'\n" +
                        "特色类型='" + objType + "'\n" +
                        "具体名称='" + objValue + "'\n" +
                        "备注='" + notes + "'";
    }

    public ProviderSpecialPartNo(String objID, String providerName, String objType,
                                 String objValue, String notes) {
        this.objID = objID;
        this.providerName = providerName;
        this.objType = objType;
        this.objValue = objValue;
        this.notes = notes;
    }

    public String getObjID() {
        return objID;
    }

    public void setObjID(String objID) {
        this.objID = objID;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public String getObjType() {
        return objType;
    }

    public void setObjType(String objType) {
        this.objType = objType;
    }

    public String getObjValue() {
        return objValue;
    }

    public void setObjValue(String objValue) {
        this.objValue = objValue;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
