package kr.soen.mypart;

import android.media.Image;

import java.util.Date;

/**
 * Created by Jin on 2016-11-5.
 */

public class MemoDTO extends DTO{


    private int key;
    private String title ;
    private double x;
    private double y;
    private double z;
    private String content;
    private Date date;
    private String image;
    private int iconId;
    private String deviceID;
    private int visibility;

    public MemoDTO(){
        title = null;
        x = 0;
        y = 0;
        z = 0;
        content = null;
        date = null;
        image = "null";
        iconId = 1;
        deviceID = "null";
        visibility = 0;
    }

    @Override
    public int getKey() {
        return key;
    }

    @Override
    public void setKey(int key) {
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getIconId() {
        return iconId;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public int getVisibility() {
        return visibility;
    }

    public void setVisibility(int visibility) {
        this.visibility = visibility;
    }
}