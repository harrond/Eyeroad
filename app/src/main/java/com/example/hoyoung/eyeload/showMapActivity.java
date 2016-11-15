package com.example.hoyoung.test;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapPolyLine;
import com.skp.Tmap.TMapView;

import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import android.support.v4.app.FragmentActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


/**
 * Created by YoungHoonKim on 11/8/16.
 */



public class showMapActivity extends FragmentActivity implements OnMapReadyCallback{


    //TmapActivity의 경로를 먼저 불러온 뒤, 점에 따른 고도 검색 이후 고도정보까지 함께 저장,
    //HashMap의 Key값으로 lat(x),lon(y),ele(z) 값을 가진다
    private ArrayList<HashMap<String,Double>> path;
    //고도검색을 위한 URL
    static String url;

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Intent intent=getIntent();
        path=new ArrayList<>();
        //TmapActivity에서 찾은 경로정보를 불러옴
        path=(ArrayList<HashMap<String,Double>>)intent.getSerializableExtra("path");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        //url형태로 점의 좌표를 저장하여 google api와 연동, 고도정보를 받아오고 지도에 경로를 표시
        url = getUrl();
        FetchUrl fetchUrl = new FetchUrl();
        fetchUrl.execute(url);
    }
    private synchronized String getUrl() {
        String url = "https://maps.googleapis.com/maps/api/elevation/json?locations=";
        String locations = "";
        for (int i = 0; i < path.size(); i++) {
                locations = locations + String.valueOf(path.get(i).get("lat") + "," + path.get(i).get("lon"));
                if (i < path.size()-1) {
                    locations = locations + "|";
                }
            }
            url = url + locations + "&key=AIzaSyDD88VFMPIfC5sr0XsFL0PDCE-QRN8gQto";

            return url;
        //Google Api 올바른 검색형태
        //https://maps.googleapis.com/maps/api/elevation/json?locations=
        // 39.7391536,-104.9847034|36.455556,-116.866667&key=AIzaSyDD88VFMPIfC5sr0XsFL0PDCE-QRN8gQto
        // Output format

    }



    private class FetchUrl extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

                try {
                    // Fetching the data from web service
                    //downloadURL
                    data = downloadUrl(url[0]);
                    Log.d("Background Task data", data.toString());
                } catch (Exception e) {
                    Log.d("Background Task", e.toString());
                }
                return data;
            }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);


            //ParserTask
            ParserTask parserTask = new ParserTask();

                // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            //읽은 데이터를 버퍼에 저장
            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            Log.d("downloadUrl", data.toString());
            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private class ParserTask extends AsyncTask<String, Integer, ArrayList<Double>> {

        // Parsing the data in non-ui thread
        @Override
        protected ArrayList<Double> doInBackground(String... jsonData) {

            JSONObject jObject;
            ArrayList<Double> altitude = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                Log.d("ParserTask", jsonData[0].toString());
                //DataParser class 호출
                DataParser parser = new DataParser();
                Log.d("ParserTask", parser.toString());

                // Starts parsing data
                altitude = parser.parse(jObject);
                Log.d("ParserTask", "Getting Altitudes");
                Log.d("ParserTask", altitude.toString());

            } catch (Exception e) {
                Log.d("ParserTask", e.toString());
                e.printStackTrace();
            }
            return altitude;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(ArrayList<Double> result) {
            //폴리라인을 그리기 위한 ArrayList<LatLng> 형태의 객체.
            ArrayList<LatLng> points = new ArrayList<>();
            PolylineOptions lineOptions = new PolylineOptions();
            LatLng latLng;

            //1.폴리라인을 그릴때 쓰이는 points에 latitude,longitude정보를 저장함
            //2.검색이 완료된 고도정보를 담고있는 result 어레이리스트에 있는 정보를 path에 추가함
            //path에 저장된 정보에 대한 접근방법 제시
            for (int j = 0; j < path.size(); j++) {
                latLng = new LatLng(path.get(j).get("lat"), path.get(j).get("lon"));
                points.add(latLng);
                path.get(j).put("ele",result.get(j));
                j++;
            }
            lineOptions.addAll(points);
            lineOptions.width(10);
            lineOptions.color(Color.RED);
            //폴리라인을 그림
            if (lineOptions != null) {
                mMap.addPolyline(lineOptions);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(points.get(0)));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(17));
            } else {
                Log.d("onPostExecute", "without Polylines drawn");
            }
            //다음 엑티비티로 넘어가는 기능을 위해선 uncomment하시오
            /*
            Intent intent=new Intent(showMapActivity.this,ARActivity.class);
            intent.putExtra("path",path);
            startActivity(intent);
            */
        }

    }

}

