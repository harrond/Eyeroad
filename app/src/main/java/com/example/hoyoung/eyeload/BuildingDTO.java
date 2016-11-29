package com.example.hoyoung.eyeload;


import android.media.Image;

/**
 * Created by Jin on 2016-11-5.
 */

public class BuildingDTO extends DTO {

    private int key;
    private String name;
    private double x;
    private double y;
    private double z;
    private String information;
    private String image;

    //BuildingDTO의 생성자
    public BuildingDTO()
    {
        name = null;
        x = 0;
        y = 0;
        z = 0;
        information = null;
        image = null;
    }
    @Override
    public int getKey() {
        return key;
    }

    @Override
    public void setKey(int key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
