package com.b1b.js.erpandroid_mgr.entity;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by 张建宇 on 2017/2/15.
 */

public class ProviderInfo implements Serializable {
    private String id;
    private String name;
    private String notes;
    private String zhangQi;
    private String level;
    private String state;
    private String color;
    private static HashMap<String, String> colorAndNumber = new HashMap<>();


    static {
        colorAndNumber.put("1", "绿色");
        colorAndNumber.put("2", "蓝色");
        colorAndNumber.put("3", "黄色");
        colorAndNumber.put("4", "红色");
        colorAndNumber.put("5", "黑色");
        colorAndNumber.put("6", "橙色");
        colorAndNumber.put("7", "紫色");
        colorAndNumber.put("8", "灰色");
        colorAndNumber.put("9", "玫瑰红");
        colorAndNumber.put("10", "茶色");
        colorAndNumber.put("11", "淡紫色");
    }

    /**
     * 0为不能开票，1为可以开票
     */
    private String hasKaipiao;

    public ProviderInfo() {
    }

    public ProviderInfo(String id, String name, String hasKaipiao) {

        this.id = id;
        this.name = name;
        this.hasKaipiao = hasKaipiao;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level + "-" + colorAndNumber.get(level);
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHasKaipiao() {
        return hasKaipiao;
    }

    public void setHasKaipiao(String hasKaipiao) {
        this.hasKaipiao = hasKaipiao;
    }


    @Override
    public String toString() {
        return
                name;
    }

    public String toString2() {
        return "名称：'" + name + "'\n" +
                "编号：'" + id + "'\n" +
                "等级：'" + level + "'\n" +
                "状态：'" + state + "'\n" +
                "备注：'" + notes + "'\n" +
                "能否开票：'" + hasKaipiao + "'\t\t" + "账期：'" + zhangQi
                ;
    }

    public String getZhangQi() {
        return zhangQi;
    }

    public void setZhangQi(String zhangQi) {
        this.zhangQi = zhangQi;
    }
}
