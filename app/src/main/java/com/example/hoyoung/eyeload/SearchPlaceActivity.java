package com.example.hoyoung.eyeload;

/**
 * Created by YoungHoonKim on 11/14/16.
 */

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
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
import java.util.List;


public class SearchPlaceActivity extends Activity implements
        FindResultAdapter.ItemClickCallback{
    //GooglePlaceActvity에서 onActivityResult로 넘어오는 result값. 1이면 origin,,2이면 dest이다.
    private static final int PLACE_PICKER_REQUESTo = 1;
    private static final int PLACE_PICKER_REQUESTd = 2;
    //출발지,목적지 정보 저장
    private LatLng origin=null,dest=null;
    //고도정보를 인터넷검색으로 불러오기 위한 스트링
    private String url;
    //맵이 표시되었는지를 확인
    private boolean isMapOn=false;
    //GooglePlace의 Default 시작위치
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(37.55719165, 127.00265586),new LatLng(37.559051,127.005032));


    //점의 x,y값을 저장하기 위한 TMapPoint.
    //경로를 구성하는 점들을 다루기위해 선언
    ArrayList<TMapPoint> points;
    //TMap 검색을 위한 변수
    TMapData tmapdata;
    TMapView tmapView=null;
    //경로정보를 경로 순서대로 저장할 ArrayList<HashMap>객체
    ArrayList<HashMap<String,Double>> path;
    //메모안내,모임안내로 넘어왔을 경우의 출발지,도착지 정보를 intent로부터 받아옴
    ArrayList<HashMap<String,Double>> navi;

    //UI를 위한
    private RecyclerView recView;
    private FindResultAdapter adapter;
    private static ArrayList listData;
    //RecyclerView에 넣기위한 item.
    private static String[] names={"출발지를 검색해주세요","도착지를 검색해주세요"};
    private static String[] addresss={"",""};
    private static final String univ="동국대학교 ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_place);

        //RecyclerView 생성
        path=null;
        listData=(ArrayList) getListData();
        recView=(RecyclerView)findViewById(R.id.findresult_rec_list);
        recView.setLayoutManager(new LinearLayoutManager(this));
        adapter=new FindResultAdapter(listData,this);
        recView.setAdapter(adapter);
        adapter.setItemClickCallback(this);

        //다른 Activity에서 넘긴값을 받아옴
        Intent intent = getIntent();
        String name=intent.getStringExtra("dest");
        //다른 Activity에서 넘어온 정보가 있을 경우, 그 정보에 대한 경로를 보인다.
        if(name!=null)
        {
            LatLng latLng;

            List<android.location.Address> geocodeMatches = null;
            try {
                geocodeMatches =
                        new Geocoder(this).getFromLocationName(univ+name,1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            names[1]=univ+name;
            addresss[1]=geocodeMatches.get(0).getAddressLine(0);
            latLng=new LatLng(geocodeMatches.get(0).getLatitude(),geocodeMatches.get(0).getLongitude());
            dest=latLng;
            listData=(ArrayList) getListData();
            adapter=new FindResultAdapter(listData,this);
            recView.setAdapter(adapter);
            adapter.setItemClickCallback(this);
        }
    }
    @Override
    //Resume시 리스트를 초기화 시키고, 다른 Activity에서 넘어온 값이 있을경우 그 값에대한 경로를 안내한다
    protected void onResume()
    {
        super.onResume();
        if(origin==null)
        {
            names[0]="출발지를 검색해주세요";
            addresss[0]="";
            listData=(ArrayList) getListData();
            adapter=new FindResultAdapter(listData,this);
            recView.setAdapter(adapter);
            adapter.setItemClickCallback(this);
        }
        if(dest==null)
        {
            names[1]="도착지를 검색해주세요";
            addresss[1]="";
            listData=(ArrayList) getListData();
            adapter=new FindResultAdapter(listData,this);
            recView.setAdapter(adapter);
            adapter.setItemClickCallback(this);
        }
        Intent intent = getIntent();
        String name=intent.getStringExtra("dest");

        if(name!=null)
        {
            LatLng latLng;

            List<android.location.Address> geocodeMatches = null;
            try {
                geocodeMatches =
                        new Geocoder(this).getFromLocationName(univ+name,1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            names[1]=univ+name;
            addresss[1]=geocodeMatches.get(0).getAddressLine(0);
            latLng=new LatLng(geocodeMatches.get(0).getLatitude(),geocodeMatches.get(0).getLongitude());
            dest=latLng;
            listData=(ArrayList) getListData();
            adapter=new FindResultAdapter(listData,this);
            recView.setAdapter(adapter);
            adapter.setItemClickCallback(this);
        }
    }
    //MapViewFragment를 Fragment에 호출하는 함수
    private void addMapFragment(Bundle bundle) {
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        MapViewFragment fragment= new MapViewFragment();
        //검색된 점들의 정보를 bundle로 넘긴다
        fragment.setArguments(bundle);
        transaction.add(R.id.mapView, fragment);
        transaction.commit();
        isMapOn=true;
    }

    //리사이클 뷰에서 출발지,목적지 선택시
    @Override
    public void onItemClick(int p) {
        FindResultListItem item=(FindResultListItem)listData.get(p);
        if(addresss[p]!="")
            placeInfoPop(p,item);
        else if(p==0)
            searchOrigin();
        else if(p==1)
            searchDest();

    }
    //RecyclerView 필수 메소드
    @Override
    public void onSecondaryIconClick(int p) {

    }
    //검색한 정보를 Dialog로 보여주는 메소드
    public void placeInfoPop(int p,FindResultListItem item)
    {
        String[] text={"출발지 정보","도착지 정보"};
        Context mContext = getApplicationContext();
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);

        //R.layout.dialog는 xml 파일명이고  R.id.popup은 보여줄 레이아웃 아이디
        View layout = inflater.inflate(R.layout.place_dialog,(ViewGroup) findViewById(R.id.popup));
        AlertDialog.Builder aDialog = new AlertDialog.Builder(SearchPlaceActivity.this);


        aDialog.setTitle(text[p]); //타이틀바 제목
        aDialog.setView(layout); //place_dialog.xml 파일을 뷰로 셋팅
        aDialog.setMessage("이름 : "+item.getName()+"\n"+"주소 : "+item.getAddress());

        if(p==0)
            aDialog.setNeutralButton("재검색", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    searchOrigin();
                }
            });
        else
            aDialog.setNeutralButton("재검색", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    searchDest();
                }
            });
        aDialog.setNegativeButton("확인", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        //팝업창 생성
        AlertDialog ad = aDialog.create();
        ad.show();//보여줌!
    }
    //출발지 검색
    public void searchOrigin(){
        try {
            PlacePicker.IntentBuilder intentBuilder =
                    new PlacePicker.IntentBuilder();
            intentBuilder.setLatLngBounds(BOUNDS_MOUNTAIN_VIEW);
            Intent intent = intentBuilder.build(SearchPlaceActivity.this);
            startActivityForResult(intent, PLACE_PICKER_REQUESTo);

        } catch (GooglePlayServicesRepairableException
                | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }
    //도착지 검색
    public void searchDest(){
        try {
            PlacePicker.IntentBuilder intentBuilder =
                    new PlacePicker.IntentBuilder();
            intentBuilder.setLatLngBounds(BOUNDS_MOUNTAIN_VIEW);
            Intent intent = intentBuilder.build(SearchPlaceActivity.this);
            startActivityForResult(intent, PLACE_PICKER_REQUESTd);

        } catch (GooglePlayServicesRepairableException
                | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }

    }

    //안내시작 버튼을 누르면, ARactivity로 넘어간다
    public void gotoARactivity(View view){
        //ARactivity로 넘어가기 전, 넘겨줄 정보가 잘 저장되어있는지 확인하고, 아닐시 재검색 유도
        if(origin==null)
        {
            names[0]="";
            addresss[0]="";
            listData=(ArrayList) getListData();
            adapter=new FindResultAdapter(listData,this);
            recView.setAdapter(adapter);
            adapter.setItemClickCallback(this);
            Toast.makeText(getApplicationContext(), "출발지를 입력해주세요",
                    Toast.LENGTH_LONG).show();
        }
        else if(dest==null)
        {
            names[1]="";
            addresss[1]="";
            listData=(ArrayList) getListData();
            adapter=new FindResultAdapter(listData,this);
            recView.setAdapter(adapter);
            adapter.setItemClickCallback(this);
            Toast.makeText(getApplicationContext(), "도착지를 입력해주세요",
                    Toast.LENGTH_LONG).show();
        }
        else if(!isMapOn)
        {
            Toast.makeText(getApplicationContext(), "경로를 검색해주세요", Toast.LENGTH_LONG).show();
        }
        else {

            Toast.makeText(getApplicationContext(), "증강현실로 넘어갑니다", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(SearchPlaceActivity.this, ARActivity.class);
            intent.putExtra("path", path);
            startActivity(intent);
        }
    }
    //경로검색
    public void searchPath(){

        if(origin==null)
        {
            names[0]="";
            addresss[0]="";
            listData=(ArrayList) getListData();
            adapter=new FindResultAdapter(listData,this);
            recView.setAdapter(adapter);
            adapter.setItemClickCallback(this);
            Toast.makeText(getApplicationContext(), "출발지를 입력해주세요",
                    Toast.LENGTH_LONG).show();
        }
        else if(dest==null)
        {
            names[1]="";
            addresss[1]="";
            listData=(ArrayList) getListData();
            adapter=new FindResultAdapter(listData,this);
            recView.setAdapter(adapter);
            adapter.setItemClickCallback(this);
            Toast.makeText(getApplicationContext(), "도착지를 입력해주세요",
                    Toast.LENGTH_LONG).show();
        }
        else {
            HashMap<String, LatLng> coordinates = new HashMap<>();
            coordinates.put("origin", origin);
            coordinates.put("dest", dest);

            path=new ArrayList<>();
            tmapView=new TMapView(getApplicationContext());
            tmapdata=new TMapData();


            //경로를 찾아, 출발지,목적지,경로정보를 보여주는 액티비티로 넘어간다.
            sendPathPoints(coordinates);
        }
    }
    //경로를 검색하여 다음 액티비티로 넘겨주는 메소드
    private synchronized void sendPathPoints(final HashMap<String,LatLng> coordinates)
    {

        TMapPoint origin = new TMapPoint(coordinates.get("origin").latitude, coordinates.get("origin").longitude);
        final TMapPoint dest = new TMapPoint(coordinates.get("dest").latitude, coordinates.get("dest").longitude);
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
                //경로검색에서 검색된 점들의 간격을 좀더 좁히기위해, 두점 사이에 점을 추가함.
                ArrayList<HashMap<String,Double>> copy=new ArrayList<HashMap<String, Double>>();

                for(int i=0;i<path.size();i++)
                {
                    copy.add(path.get(i));
                }
                path=new ArrayList<HashMap<String, Double>>();
                for(int i=0;i<copy.size()-1;i++)
                {
                    ArrayList<HashMap<String,Double>> devided=new ArrayList<>();
                    double x1,y1;
                    double x2,y2;
                    x1=copy.get(i).get("lat");
                    x2=copy.get(i+1).get("lat");
                    y1=copy.get(i).get("lon");
                    y2=copy.get(i+1).get("lon");
                    ArrayList<HashMap<String,Double>> result=new ArrayList<>();
                    double interval = 0.000089;
                    double distance = Math.sqrt((x1-x2)*(x1-x2)+(y1-y2)*(y1-y2));
                    int numOfMarker = (int) (distance / interval);
                    double changeX = x2 - x1;
                    double changeY = y2 - y1;
                    double newX;
                    double newY;


                    for(int j=0;j<numOfMarker;j++)
                    {
                        HashMap<String,Double> point=new HashMap<>();
                        newX = x1 + changeX*j/numOfMarker;
                        newY = y1 + changeY*j/numOfMarker;
                        point.put("lat",newX);
                        point.put("lon",newY);
                        if(j==numOfMarker-1)
                        {
                            point.put("lat",x2);
                            point.put("lon",y2);
                            result.add(point);
                        }
                        result.add(point);

                    }
                    /*
                    for(int j=0;j<devided.size();j++)
                        path.add(devided.get(j));
                        */
                    for(int k=0;k<result.size();k++)
                        path.add(result.get(k));
                }
                path.add(copy.get(copy.size()-1));

                //경로검색이 끝나면, 경로에 대한 고도정보를 검색하는 함수를 호출한다.
                url = getUrl();
                FetchUrl fetchUrl = new FetchUrl();
                fetchUrl.execute(url);
            }
        });

    }
    public  ArrayList<HashMap<String,Double>> makeMarker2(double x1,double y1,double x2,double y2)
    {
        System.out.println("chamgeX :" + x2);
        System.out.println("changeY :" + y2);
        ArrayList<HashMap<String,Double>> result=new ArrayList<>();
        double interval = Math.sqrt(2);
        double distance = Math.sqrt((x1-x2)*(x1-x2)+(y1-y2)*(y1-y2));
        int numOfMarker = (int) (distance / interval);
        System.out.println("numOfMarker :" + numOfMarker);
        double changeX = x2 - x1;
        double changeY = y2 - y1;
        double newX;
        double newY;


        for(int i=0;i<numOfMarker;i++)
        {
            HashMap<String,Double> point=new HashMap<>();
            newX = x1 + changeX*i/numOfMarker;
            newY = y1 + changeY*i/numOfMarker;
            point.put("lat",newX);
            point.put("lon",newY);
            result.add(point);
        }
        return result;
    }
    //검색후, 검색 정보를 View에 출력
    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent data) {

        //출발지 정보 View에입력
        if (requestCode == PLACE_PICKER_REQUESTo
                && resultCode == Activity.RESULT_OK) {
            final Place place = PlacePicker.getPlace(this, data);
            final CharSequence name = place.getName();
            final CharSequence address = place.getAddress();
            final LatLng latLng=place.getLatLng();
            origin=latLng;
            names[0]=String.valueOf(name);
            addresss[0]=String.valueOf(address);
            listData=(ArrayList) getListData();
            adapter=new FindResultAdapter(listData,this);
            recView.setAdapter(adapter);
            adapter.setItemClickCallback(this);
            searchPath();

        }
        //도착지 정보 View에 입력
        else if (requestCode==PLACE_PICKER_REQUESTd
                && resultCode==Activity.RESULT_OK){
            final Place place = PlacePicker.getPlace(this, data);
            final CharSequence name = place.getName();
            final CharSequence address = place.getAddress();
            final LatLng latLng=place.getLatLng();
            dest=latLng;
            names[1]=String.valueOf(name);
            addresss[1]=String.valueOf(address);
            listData=(ArrayList) getListData();
            adapter=new FindResultAdapter(listData,this);
            recView.setAdapter(adapter);
            adapter.setItemClickCallback(this);
            searchPath();
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    //리사이클 뷰에 들어갈 자료형에 검색 정보를 넣는 메소드
    public List<FindResultListItem> getListData()
    {
        int icons = (R.drawable.searchmarker);
        List<FindResultListItem> data = new ArrayList<>();
        for(int i=0;i<names.length;i++) {
            FindResultListItem item = new FindResultListItem();
            item.setImageResId(icons);
            item.setName(names[i]);
            item.setAddress(addresss[i]);
            item.setId(i);
            data.add(item);
        }

        return data;
    }
    //고도정보 검색을 위한 url을 불러온뒤, url을 사용한 httpconection을 하여 고도정보를 검색한다
    // 검색된 정보를 parsetask에 입력한다.
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
            JSONObject jObject;
            ArrayList<Double> altitude = null;

            try {
                jObject = new JSONObject(data);
                Log.d("ParserTask", data.toString());
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

    //검색정보 url에 대한 httpconnection을 진행한다
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
    //url로 검색된 정보를 사용
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
            LatLng latLng;

            //1.폴리라인을 그릴때 쓰이는 points에 latitude,longitude정보를 저장함
            //2.검색이 완료된 고도정보를 담고있는 result 어레이리스트에 있는 정보를 path에 추가함
            //path에 저장된 정보에 대한 접근방법 제시
            for (int j = 0; j < path.size(); j++) {
                latLng = new LatLng(path.get(j).get("lat"), path.get(j).get("lon"));
                points.add(latLng);
                path.get(j).put("ele", result.get(j));
            }
            //3.경로를 그리기위한 polylineoption객체에 경로정보를 저장
            Bundle bundle=new Bundle();
            bundle.putSerializable("path",path);
            addMapFragment(bundle);

        }

    }
    //경로에서 찾은 점들의 고도검색을 위한 url을 생성한다
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
}

