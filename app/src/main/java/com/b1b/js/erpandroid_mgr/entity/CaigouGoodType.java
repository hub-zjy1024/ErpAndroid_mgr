package com.b1b.js.erpandroid_mgr.entity;

/**
 Created by 张建宇 on 2017/2/9. */

public class CaigouGoodType {
    //    "StrValue": "43",
    //            "typeID": "型号",
    //            "typeName": "集成电路"
    private int index;
    private String StrValue;
    private String typeID;
    private String typeName;

    public CaigouGoodType(int index, String strValue, String typeID, String typeName) {
        this.index = index;
        StrValue = strValue;
        this.typeID = typeID;
        this.typeName = typeName;
    }

    public CaigouGoodType(String typeID, String typeName) {
        this.typeID = typeID;
        this.typeName = typeName;
    }

    public CaigouGoodType(String strValue, String typeID, String typeName) {
        StrValue = strValue;
        this.typeID = typeID;
        this.typeName = typeName;
    }

    public String getStrValue() {
        return StrValue;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setStrValue(String strValue) {
        StrValue = strValue;
    }

    public String getTypeID() {
        return typeID;
    }

    public void setTypeID(String typeID) {
        this.typeID = typeID;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
}
