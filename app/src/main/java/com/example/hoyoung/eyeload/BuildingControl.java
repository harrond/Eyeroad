package com.example.hoyoung.eyeload;
/**
 * Created by Jin on 2016-10-8.
 */

public class BuildingControl {
    //DAO선언
    private BuildingDTO buildingDTO;

    public boolean getInfo(String name) {
        boolean flag = false;
        //DAO를 통해 가져와 BuildingInfoActivity에 반환하거나 xml에 표시해줘야함

        //DTO를 이상없이 가져온 경우
        if (flag == true)
            return true;

            //DTO를 가져오지 못 한 경우
        else
            return false;
    }

}
