package com.example.hoyoung.eyeload;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by YoungHoonKim on 11/28/16.
 */

public class MapViewFragment extends Fragment {

    MapView mMapView;
    private GoogleMap googleMap;
    //HashMap의 Key값으로 lat(x),lon(y),ele(z) 값을 가진다
    private ArrayList<HashMap<String, Double>> path;
    //고도검색을 위한 URL
    static String url;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_search_place, container, false);


        //SearchPlaceActvitiy(Parent)에서 경로정보를 받아옴
        path=new ArrayList<>();
        Bundle bundle=this.getArguments();
        if(bundle!=null){
            path= (ArrayList<HashMap<String, Double>>) bundle.getSerializable("path");
        }

        //맵 생성
        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        //맵을 컨트롤하는 부분
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {

                googleMap = mMap;

                // Default location
                LatLng dongguk = new LatLng(37.5575367, 127.0007751);

                // For zooming automatically to the location of the marker
                CameraPosition cameraPosition = new CameraPosition.Builder().target(dongguk).zoom(12).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


                //경로에 대한 폴리라인을 맵에 표현
                PolylineOptions lineOptions = new PolylineOptions();
                LatLng latLng;
                ArrayList<LatLng> points = new ArrayList<>();
                for (int j = 0; j < path.size(); j++) {
                    latLng = new LatLng(path.get(j).get("lat"), path.get(j).get("lon"));
                    points.add(latLng);
                }
                //경로를 그리기위한 polylineoption객체에 경로정보를 저장
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.RED);
                //맵에 폴리라인 그리고, 출발지로 지정된 마커로 맵을 zoom
                if(lineOptions!=null) {
                    MarkerOptions options = new MarkerOptions();
                    latLng = new LatLng(path.get(0).get("lat"), path.get(0).get("lon"));
                    options.position(latLng);
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    mMap.addMarker(options);
                    latLng = new LatLng(path.get(path.size() - 1).get("lat"), path.get(path.size() - 1).get("lon"));
                    options.position(latLng);
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    mMap.addMarker(options);

                    mMap.addPolyline(lineOptions);
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(lineOptions.getPoints().get(0)));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(17));
                }
                else
                    Log.d("onMapReady", "Polyline Empty");


            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}