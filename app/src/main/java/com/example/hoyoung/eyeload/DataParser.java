package com.example.hoyoung.eyeload;

/**
 * Created by YoungHoonKim on 11/8/16.
 */

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DataParser {

    //JSONObject형태로 url검색에 대한 결과물을 받아, String형태로 변환
    /** Receives a JSONObject and returns a list of lists containing elevation*/
    public ArrayList<Double> parse(JSONObject jObject){

        ArrayList<Double> altitude = new ArrayList<>() ;
        JSONArray jResults;
        double ele;

        try {

            jResults = jObject.getJSONArray("results");

            for(int i=0;i<jResults.length();i++){
                Object ob=((JSONObject)jResults.get(i)).get("elevation");
                ele=new Double(ob.toString());

                altitude.add(ele);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }catch (Exception e){
        }


        return altitude;
    }
}
