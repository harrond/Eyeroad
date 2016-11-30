package com.example.hoyoung.eyeload;

import java.util.ArrayList;

/**
 * Created by Jin on 2016-10-8.
 */

public class BuildingControl {

    private ArrayList<BuildingDTO> buildingList = new ArrayList<>();
    private BuildingDTO buildingDTOSelected = new BuildingDTO();
    private BuildingDAO buildingDAO = new BuildingDAO();

    private static BuildingControl buildingControl = new BuildingControl();

    public ArrayList<BuildingDTO> getBuildingList() {
        return buildingList;
    }

    public BuildingDTO getBuildingDTOSelected() {
        return buildingDTOSelected;
    }

    //생성자
    private BuildingControl() {

    }

    //싱글톤 return
    public static BuildingControl getInstance() {
        return buildingControl;
    }

    public boolean getInfo(int key) {
        boolean flag = buildingDAO.select(key);
        if(flag)
        {
            buildingDTOSelected = buildingDAO.getBuildingDTOSelected();
            return true;
        }
        else
            return false;

    }

    public boolean getAllBuilding(){

        if(buildingDAO.selectAll()==true) {
            buildingList = buildingDAO.getArrayListBuildingDTO();
            return true;
        }
        else
            return false;

    }

}
