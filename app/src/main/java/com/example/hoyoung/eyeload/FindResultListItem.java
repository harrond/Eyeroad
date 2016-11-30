package com.example.hoyoung.eyeload;

/**
 * Created by YoungHoonKim on 11/14/16.
 * FindresultAdpater에서 입력을 받아 View에 출력시켜주는 내용을 한번에 다루기 위한 Class
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
