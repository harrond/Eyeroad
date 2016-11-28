package com.example.hoyoung.eyeload;
/**
 * Created by YoungHoonKim on 11/8/16.
 */

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DataParser {

    /**
     * Receives a JSONObject and returns a list of lists containing latitude and longitude
     */
    public ArrayList<Double> parse(JSONObject jObject) {

        ArrayList<Double> altitude = new ArrayList<>();
        JSONArray jResults;
        String elevation;
        double ele;

        try {

            jResults = jObject.getJSONArray("results");

            /** Traversing all routes */
            for (int i = 0; i < jResults.length(); i++) {
                Object ob = ((JSONObject) jResults.get(i)).get("elevation");
                //elevation=(String)ob;
                ele = new Double(ob.toString());

                altitude.add(ele);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
        }


        return altitude;
    }
}
