package com.example.hoyoung.test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


import com.google.android.gms.maps.model.LatLng;
import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapPolyLine;
import com.skp.Tmap.TMapView;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by YoungHoonKim on 11/11/16.
 */

public class TmapAcitivity extends Activity {

    private HashMap<String,LatLng> coordinates;
    ArrayList<TMapPoint> points;
    TMapData tmapdata;
    TMapView tmapView;
    //경로정보를 경로 순서대로 저장할 ArrayList<HashMap>객체
    ArrayList<HashMap<String,Double>> path;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tmap_activity);
        coordinates = new HashMap<>();
        path=new ArrayList<>();

        Intent intent = getIntent();
        tmapView = new TMapView(this);
        tmapdata=new TMapData();

        //사용자가 검색한 출발지목적지 정보를 불러온다
        coordinates = (HashMap<String, LatLng>) intent.getSerializableExtra("coordinate");
        sendPathPoints(coordinates);

    }
    private synchronized void sendPathPoints(HashMap<String,LatLng> coordinates)
    {

        TMapPoint origin = new TMapPoint(coordinates.get("origin").latitude, coordinates.get("origin").longitude);
        TMapPoint dest = new TMapPoint(coordinates.get("dest").latitude, coordinates.get("dest").longitude);
        tmapView.setSKPMapApiKey("6bb5b7f3-1274-3c5e-ba93-790aee876673");
        tmapdata.findPathData(origin, dest, new TMapData.FindPathDataListenerCallback() {
            @Override
            public synchronized void onFindPathData(TMapPolyLine polyLine) {
                //검색된 경로가 polyLine에 저장되고, latlon 해쉬맵에 latitude,longitude정보를 저장,
                //다음 액티비티로 넘겨줄 path에 순서대로 점을 저장한다
                points = polyLine.getLinePoint();
                for (TMapPoint point : points) {
                    HashMap <String,Double> latlon=new HashMap<>();
                    latlon.put("lat",point.getLatitude());
                    latlon.put("lon",point.getLongitude());
                    path.add(latlon);
                }
                //점 저장이 끝나면 다음 액티비티로 경로정보를 넘긴다
                Intent intent=new Intent(TmapAcitivity.this,showMapActivity.class);
                intent.putExtra("path",path);
                startActivity(intent);
            }
        });

    }


}
