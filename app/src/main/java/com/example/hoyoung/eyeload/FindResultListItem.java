package com.example.hoyoung.eyeload;


/**
 * Created by YoungHoonKim on 2016-11-19.
 */

public class FindResultListItem {

    private String name;
    private String address;
    private int id;
    private int ImageResId;

    public int getImageResId() {
        return ImageResId;
    }

    public void setImageResId(int imageResId) {
        ImageResId = imageResId;
    }

    public int getId() {
        return id;


    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}
