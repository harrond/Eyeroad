package com.example.hoyoung.eyeload;

/**
 * Created by YoungHoonKim on 11/14/16.
 */

        import android.app.Activity;
        import android.content.Context;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.os.Bundle;
        import android.support.v7.app.AlertDialog;
        import android.support.v7.widget.LinearLayoutManager;
        import android.support.v7.widget.RecyclerView;
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

        import java.util.ArrayList;
        import java.util.HashMap;
        import java.util.List;


public class SearchPlaceActivity extends Activity implements
        FindResultAdapter.ItemClickCallback {
    private static final int PLACE_PICKER_REQUESTo = 1;
    private static final int PLACE_PICKER_REQUESTd = 2;
    private LatLng origin = null, dest = null;

    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(37.55719165, 127.00265586), new LatLng(37.559051, 127.005032));


    ArrayList<TMapPoint> points;
    TMapData tmapdata;
    TMapView tmapView = null;
    //경로정보를 경로 순서대로 저장할 ArrayList<HashMap>객체
    ArrayList<HashMap<String, Double>> path;

    private RecyclerView recView;
    private FindResultAdapter adapter;
    private static ArrayList listData;

    private static String[] names = {"", ""};
    private static String[] addresss = {"", ""};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_place);

<<<<<<< HEAD
        //RecyclerView 생성
        listData=(ArrayList) getListData();
        recView=(RecyclerView)findViewById(R.id.findresult_rec_list);
=======
        listData = (ArrayList) getListData();
        recView = (RecyclerView) findViewById(R.id.findresult_rec_list);
>>>>>>> harrond/master
        recView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FindResultAdapter(listData, this);
        recView.setAdapter(adapter);
        adapter.setItemClickCallback(this);
    }
    @Override
    protected void onResume()
    {
        super.onResume();
        if(origin==null)
        {
            names[0]="";
            addresss[0]="";
            listData=(ArrayList) getListData();
            adapter=new FindResultAdapter(listData,this);
            recView.setAdapter(adapter);
            adapter.setItemClickCallback(this);
        }
        if(dest==null)
        {
            names[1]="";
            addresss[1]="";
            listData=(ArrayList) getListData();
            adapter=new FindResultAdapter(listData,this);
            recView.setAdapter(adapter);
            adapter.setItemClickCallback(this);
        }
    }

    //리사이클 뷰에서 출발지,목적지 선택시
    @Override
    public void onItemClick(int p) {
        FindResultListItem item=(FindResultListItem)listData.get(p);
        if(names[p]!="")
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
    //경로검색
    public void searchPath(View view){

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

                //점 저장이 끝나면 다음 액티비티로 경로정보를 넘긴다
                Intent intent=new Intent(SearchPlaceActivity.this,ShowMapActivity.class);
                intent.putExtra("path",path);
                startActivity(intent);



            }
        });

    }
    //검색후, 검색 정보를 View에 출력
    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent data) {

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
        }
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
}

package com.frenzy.ebook.search;

/**
 * Created by YoungHoonKim on 11/14/16.
 */

        import android.app.Activity;
        import android.content.Context;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.os.Bundle;
        import android.support.v7.app.AlertDialog;
        import android.support.v7.widget.LinearLayoutManager;
        import android.support.v7.widget.RecyclerView;
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

        import java.util.ArrayList;
        import java.util.HashMap;
        import java.util.List;


public class SearchPlaceActivity extends Activity implements
        FindResultAdapter.ItemClickCallback{
    private static final int PLACE_PICKER_REQUESTo = 1;
    private static final int PLACE_PICKER_REQUESTd = 2;
    private LatLng origin=null,dest=null;

    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(37.55719165, 127.00265586),new LatLng(37.559051,127.005032));


    ArrayList<TMapPoint> points;
    TMapData tmapdata;
    TMapView tmapView=null;
    //경로정보를 경로 순서대로 저장할 ArrayList<HashMap>객체
    ArrayList<HashMap<String,Double>> path;

    private RecyclerView recView;
    private FindResultAdapter adapter;
    private static ArrayList listData;

    private static String[] names={"",""};
    private static String[] addresss={"",""};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_place);

        //RecyclerView 생성
        listData=(ArrayList) getListData();
        recView=(RecyclerView)findViewById(R.id.findresult_rec_list);
        recView.setLayoutManager(new LinearLayoutManager(this));
        adapter=new FindResultAdapter(listData,this);
        recView.setAdapter(adapter);
        adapter.setItemClickCallback(this);
    }

    //리사이클 뷰에서 출발지,목적지 선택시
    @Override
    public void onItemClick(int p) {
        FindResultListItem item = (FindResultListItem) listData.get(p);
        if (names[p] != "")
            placeInfoPop(p, item);
        else if (p == 0)
            searchOrigin();
        else if (p == 1)
            searchDest();

    }
<<<<<<< HEAD
    //RecyclerView 필수 메소드
=======

>>>>>>> harrond/master
    @Override
    public void onSecondaryIconClick(int p) {

    }

    //검색한 정보를 Dialog로 보여주는 메소드
    public void placeInfoPop(int p, FindResultListItem item) {
        String[] text = {"출발지 정보", "도착지 정보"};
        Context mContext = getApplicationContext();
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);

        //R.layout.dialog는 xml 파일명이고  R.id.popup은 보여줄 레이아웃 아이디
        View layout = inflater.inflate(R.layout.place_dialog, (ViewGroup) findViewById(R.id.popup));
        AlertDialog.Builder aDialog = new AlertDialog.Builder(SearchPlaceActivity.this);


        aDialog.setTitle(text[p]); //타이틀바 제목
        aDialog.setView(layout); //place_dialog.xml 파일을 뷰로 셋팅
        aDialog.setMessage("이름 : " + item.getName() + "\n" + "주소 : " + item.getAddress());

        if (p == 0)
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
    public void searchOrigin() {
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
    public void searchDest() {
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

    //경로검색
    public void searchPath(View view) {

<<<<<<< HEAD
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
=======
        if (origin == null || dest == null) {
            Toast.makeText(getApplicationContext(), "출발지,목적지를 입력해주세요.",
>>>>>>> harrond/master
                    Toast.LENGTH_LONG).show();
        } else {
            HashMap<String, LatLng> coordinates = new HashMap<>();
            coordinates.put("origin", origin);
            coordinates.put("dest", dest);

            path = new ArrayList<>();
            tmapView = new TMapView(getApplicationContext());
            tmapdata = new TMapData();


            //경로를 찾아, 출발지,목적지,경로정보를 보여주는 액티비티로 넘어간다.
            sendPathPoints(coordinates);
        }
    }

    //경로를 검색하여 다음 액티비티로 넘겨주는 메소드
    private synchronized void sendPathPoints(HashMap<String, LatLng> coordinates) {

        TMapPoint origin = new TMapPoint(coordinates.get("origin").latitude, coordinates.get("origin").longitude);
        TMapPoint dest = new TMapPoint(coordinates.get("dest").latitude, coordinates.get("dest").longitude);
        tmapView.setSKPMapApiKey("6bb5b7f3-1274-3c5e-ba93-790aee876673");
        tmapdata.findPathDataWithType(TMapData.TMapPathType.PEDESTRIAN_PATH, origin, dest, new TMapData.FindPathDataListenerCallback() {
            @Override
            public synchronized void onFindPathData(TMapPolyLine polyLine) {
                //검색된 경로가 polyLine에 저장되고, latlon 해쉬맵에 latitude,longitude정보를 저장,
                //다음 액티비티로 넘겨줄 path에 순서대로 점을 저장한다
                points = polyLine.getLinePoint();
                for (TMapPoint point : points) {
                    HashMap<String, Double> latlon = new HashMap<>();
                    latlon.put("lat", point.getLatitude());
                    latlon.put("lon", point.getLongitude());
                    path.add(latlon);
                }

                //점 저장이 끝나면 다음 액티비티로 경로정보를 넘긴다
                Intent intent = new Intent(SearchPlaceActivity.this, ShowMapActivity.class);
                intent.putExtra("path", path);
                startActivity(intent);


            }
        });

    }

    //검색후, 검색 정보를 View에 출력
    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent data) {

        if (requestCode == PLACE_PICKER_REQUESTo
                && resultCode == Activity.RESULT_OK) {
            final Place place = PlacePicker.getPlace(this, data);
            final CharSequence name = place.getName();
            final CharSequence address = place.getAddress();
            final LatLng latLng = place.getLatLng();
            origin = latLng;
            names[0] = String.valueOf(name);
            addresss[0] = String.valueOf(address);
            listData = (ArrayList) getListData();
            adapter = new FindResultAdapter(listData, this);
            recView.setAdapter(adapter);
            adapter.setItemClickCallback(this);
        } else if (requestCode == PLACE_PICKER_REQUESTd
                && resultCode == Activity.RESULT_OK) {
            final Place place = PlacePicker.getPlace(this, data);
            final CharSequence name = place.getName();
            final CharSequence address = place.getAddress();
            final LatLng latLng = place.getLatLng();
            dest = latLng;
            names[1] = String.valueOf(name);
            addresss[1] = String.valueOf(address);
            listData = (ArrayList) getListData();
            adapter = new FindResultAdapter(listData, this);
            recView.setAdapter(adapter);
            adapter.setItemClickCallback(this);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    //리사이클 뷰에 들어갈 자료형에 검색 정보를 넣는 메소드
    public List<FindResultListItem> getListData() {
        int icons = (R.drawable.searchmarker);
        List<FindResultListItem> data = new ArrayList<>();
        for (int i = 0; i < names.length; i++) {
            FindResultListItem item = new FindResultListItem();
            item.setImageResId(icons);
            item.setName(names[i]);
            item.setAddress(addresss[i]);
            item.setId(i);
            data.add(item);
        }

        return data;
    }
}

