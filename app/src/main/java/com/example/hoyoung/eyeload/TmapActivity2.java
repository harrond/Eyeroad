package com.example.hoyoung.test;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapPolyLine;
import com.skp.Tmap.TMapView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by YoungHoonKim on 11/15/16.
 */

public class TmapActivity2 extends Activity implements View.OnClickListener{
    private HashMap<String,LatLng> coordinates;
    ArrayList<TMapPoint> points;
    TMapData tmapdata;
    TMapView tmapView=null;
    //경로정보를 경로 순서대로 저장할 ArrayList<HashMap>객체
    ArrayList<HashMap<String,Double>> path;
    LinearLayout linearLayout;
    String url;
    private Button go;
    private ImageView start;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tmap_activity2);
        linearLayout = (LinearLayout) findViewById(R.id.tmapview);
        coordinates = new HashMap<>();
        path=new ArrayList<>();
        tmapView=new TMapView(getApplicationContext());
        Intent intent = getIntent();
        tmapdata=new TMapData();

        coordinates = (HashMap<String, LatLng>) intent.getSerializableExtra("coordinate");
        linearLayout.addView(tmapView);
        tmapView.setCenterPoint(127.0007751,37.5575367);

        //사용자가 검색한 출발지목적지 정보를 불러온다
        sendPathPoints(coordinates);
        Log.d("경로검색",String.valueOf(coordinates.get("origin").latitude));
        Toast.makeText(getApplicationContext(), "경로를 검색합니다",
                Toast.LENGTH_LONG).show();
        go=(Button) findViewById(R.id.go);
        go.setOnClickListener(this);




    }
    private synchronized void sendPathPoints(HashMap<String,LatLng> coordinates)
    {

        TMapPoint origin = new TMapPoint(coordinates.get("origin").latitude, coordinates.get("origin").longitude);
        TMapPoint dest = new TMapPoint(coordinates.get("dest").latitude, coordinates.get("dest").longitude);
        tmapView.setSKPMapApiKey("6bb5b7f3-1274-3c5e-ba93-790aee876673");
        tmapdata.findPathDataWithType(TMapData.TMapPathType.PEDESTRIAN_PATH,origin, dest, new TMapData.FindPathDataListenerCallback() {
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
                tmapView.addTMapPath(polyLine);

                url=getUrl();
                FetchUrl fetchUrl=new FetchUrl();
                fetchUrl.execute(url);

            }
        });

    }


    @Override
    public void onClick(View view)
    {
        Toast.makeText(getApplicationContext(), "증강뷰로 경로를 안내합니다",
                Toast.LENGTH_LONG).show();

        Intent intent=new Intent(TmapActivity2.this,ARActivity.class);
        intent.putExtra("path",path);
        startActivity(intent);

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
            }

        }

    }
}
