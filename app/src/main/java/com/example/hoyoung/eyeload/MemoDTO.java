package com.example.hoyoung.test;

import android.media.Image;

import java.util.Date;

public class MemoDTO extends DTO{

    // listView에 접목시키기 위한 test용 필드
    // 구현 시 실제 DTO의 필드를 listview에 적용해야되는 수정필요
    ////////////////////////////////////////////////////////////////

    private String desc ;

    public void setDesc(String desc) {
        this.desc = desc ;
    }

    public String getDesc() {
        return this.desc ;
    }
    ////////////////////////////////////////////////////////////////

    private int key;
    private String title ;
    private double x;
    private double y;
    private double z;
    private String content;
    private Date date;
    private Image image;
    private int iconId;
    private String deviceID;

    public  int    getKey() {
        return key;
    }

    public void   setKey(int key) {
        this.key = key;
    }
    public String getTitle() {
        return this.title ;
}
    public void setTitle(String title) {
        this.title = title ;
    }
    public double getX() {
        return x;
    }public void   setX(double x) {
        this.x = x;
    }public double getY() {
        return y;
    }public void   setY(double y) {
        this.y = y;
    }public double getZ() {
        return z;
    }public void   setZ(double z) {
        this.z = z;
    }public String getContent() {
        return content;
    }public void   setContent(String content) {
        this.content = content;
    }public Date   getDate() {
        return date;
    }public void   setDate(Date date) {
        this.date = date;
    }public Image  getImage() {
        return image;
    }public void   setImage(Image image) {
        this.image = image;
    }public int    getIconId() {
        return iconId;
    }public void   setIconId(int iconId) {
        this.iconId = iconId;
    }public String getDeviceID() {
        return deviceID;
    }public void   setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }
}