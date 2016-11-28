package com.example.hoyoung.eyeload;
/**
 * Created by Jin on 2016-10-8.
 */

public class BuildingControl {

    private BuildingDTO buildingDTO;
    private BuildingDAO buildingDAO;

    public BuildingControl()
    {
        buildingDAO = new BuildingDAO();
    }

    public boolean getInfo(int key) {
        return buildingDAO.select(key);
    }

}
