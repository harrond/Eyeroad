package com.example.hoyoung.eyeload;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.location.Location;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Hoyoung on 2016-11-04.
 */
public class ARActivity extends SensorActivity implements OnTouchListener {
    private static final String TAG = "ARActivity";
    private static final DecimalFormat FORMAT = new DecimalFormat("#.##");

    private static PowerManager.WakeLock wakeLock = null;
    private static CameraSurface camScreen = null;
    private static ARView arView = null;
    private static List<Marker> pathmarkers=null;

    private static List<HashMap<String,Double>> path= null;

    public static boolean useCollisionDetection = false;
    public static boolean visibleMarker = true;

    private static final BlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(1);
    private static final ThreadPoolExecutor exeService = new ThreadPoolExecutor(1, 1, 20, TimeUnit.SECONDS, queue);

    private static Bitmap icon1;
    private static Bitmap icon2;
    private static Bitmap icon3;
    private static Bitmap buildingIcon;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);//위에 타이틀 바 안뜨게함
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //반경 결정 0.04=40m 1=1km
        ARData.setRadius(0.03f);
        ARData.setBuildingRadius(0.1f);

        Intent intent=getIntent();
        path=(List<HashMap<String,Double>>)intent.getSerializableExtra("path"); //길을 검색후 넘어온경우 경로를 가져온다.
        if(path!=null){
            createPathMarker();
        }

        icon1= BitmapFactory.decodeResource(this.getResources(),R.drawable.ic_action_name );
        icon2= BitmapFactory.decodeResource(this.getResources(),R.drawable.ic2_action_name );
        icon3= BitmapFactory.decodeResource(this.getResources(),R.drawable.ic3_action_name );
        buildingIcon=BitmapFactory.decodeResource(this.getResources(),R.drawable.building);


        camScreen = new CameraSurface(this); //카메라 화면 셋팅
        setContentView(camScreen);

        arView = new ARView(this);
        arView.setOnTouchListener(this);

        ViewGroup.LayoutParams arLayout = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        addContentView(arView, arLayout); //카메라 화면위에 레이아웃을 덮는다.
        LayoutInflater inflator = getLayoutInflater();
        View over_view = (View) inflator.inflate(R.layout.over, null);
        addContentView(over_view, arLayout);

        findViewById(R.id.btnMemo).setOnClickListener( //메모 만들기버튼를 클릭한 경우
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(ARActivity.this, MakingMemoAcitivity.class);
                        Location loc = ARData.getCurrentLocation();
                        HashMap<String,Double> hashloc = new HashMap<String, Double>();
                        hashloc.put("x",loc.getLatitude());
                        hashloc.put("y",loc.getLongitude());
                        hashloc.put("z",loc.getAltitude());
                        intent.putExtra("cl",hashloc);
                        startActivity(intent);
                        updateData();
                    }
                }
        );
        findViewById(R.id.btnSearch).setOnClickListener( //길검색버튼을 클릭한 경우
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(ARActivity.this, SearchPlaceActivity.class);
                        startActivity(intent);
                    }
                }
        );
        findViewById(R.id.btnOnOff).setOnClickListener( //메모 마커 on, off
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        visibleMarker=!visibleMarker;
                    }
                }
        );


        updateData(); //메모와 건물 마커 다운로드
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "DimScreen");


    }

    @Override
    public void onStart() {
        super.onStart();
        updateData();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateData();
        wakeLock.acquire();
    }

    @Override
    public void onPause() {
        super.onPause();

        wakeLock.release();
    }

    @Override
    public void onSensorChanged(SensorEvent evt) { //센서 변경이 감지 된경우 핸드폰이 움직인 것이므로 화면을 다시 그린다.
        super.onSensorChanged(evt);
        if (evt.sensor.getType() == Sensor.TYPE_ACCELEROMETER ||
                evt.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            arView.postInvalidate();
        }
    }


    private void createPathMarker(){ //받아온 경로를 마커로 만들어서 보관
        pathmarkers =new ArrayList<Marker>();
        int size = path.size();
        int i=0;

        Bitmap destination= BitmapFactory.decodeResource(this.getResources(),R.drawable.destination );
        Bitmap point= BitmapFactory.decodeResource(this.getResources(),R.drawable.waypoint );
        for(i=0;i<path.size();i++){
            if(i==size-1){
                Marker d=new Marker("목적지",path.get(i).get("lat") ,path.get(i).get("lon"),path.get(i).get("ele"),destination,3,0);
                //Marker d=new Marker("목적지",path.get(i).get("lat") ,path.get(i).get("lon"),ARData.getCurrentLocation().getAltitude()-3,destination,3,0);
                pathmarkers.add(d);
                break;
            }
            Marker d=new Marker(i+"",path.get(i).get("lat") ,path.get(i).get("lon"),path.get(i).get("ele"),point,2,0);
           // Marker d=new Marker(i+"",path.get(i).get("lat") ,path.get(i).get("lon"),ARData.getCurrentLocation().getAltitude()-3,point,2,0);
            pathmarkers.add(d);
        }

        ARData.addPath(pathmarkers);
    }


    @Override
    public boolean onTouch(View v, MotionEvent me) { //화면이 터치 되었을 때
        for (Marker marker : ARData.getMarkers()) {
            if (marker.handleClick(me.getX(), me.getY())) { //마커가 눌린 상태인지 검사
                if (me.getAction() == MotionEvent.ACTION_UP) markerTouched(marker);
                return true;
            }
        }
        return super.onTouchEvent(me);
    }

    @Override
    public void onLocationChanged(Location location) { //사용자의 위치가 변경되었을 때
        super.onLocationChanged(location);
        updateData();
    }

    private void markerTouched(Marker marker) { //마커가 터치된 경우
        int type = marker.getMarkerType();
        if(type==0) { //마커가 메모인 경우
            Intent intent = new Intent(ARActivity.this, MemoInfoActivity.class);
            intent.putExtra("memoKey", marker.getKey());
            intent.putExtra("flag", 1);
            startActivity(intent);
        }
        else if(type ==1){ //
            Intent intent = new Intent(ARActivity.this, BuildingInfoActivity.class);
            intent.putExtra("buildingKey", marker.getKey());
            startActivity(intent);
        }

    }

    private void updateData() { //다운로드함수를 스레드로 처리
        try {
            exeService.execute(
                    new Runnable() {
                        public void run() {
                            download();
                        }
                    }
            );
        } catch (RejectedExecutionException rej) {
            Log.w(TAG, "Not running new download Runnable, queue is full.");
        } catch (Exception e) {
            Log.e(TAG, "Exception running download Runnable.", e);
        }
    }

    private static boolean download() {  //db에서 다운로드
        MemoControl memoControl = MemoControl.getInstance();
        BuildingControl buildingControl = BuildingControl.getInstance();
        memoControl.getAllMemo();
        buildingControl.getAllBuilding();
        List<MemoDTO> memoDTOs = memoControl.getMemoList();
        List<BuildingDTO> buildingDTOs=buildingControl.getBuildingList();
        List<Marker> markers = new ArrayList<Marker>();
        for(MemoDTO memo :memoDTOs){
            Bitmap icon;
            switch(memo.getIconId()){ //id로 표시할 아이콘 선택
                case 1:
                    icon = icon1; break;
                case 2:
                    icon = icon2; break;
                case 3:
                    icon = icon3; break;
                default:
                    icon = null;
            }
            if (icon==null) continue;
            //memo.getZ()
            Marker ma = new Marker(memo.getTitle(),memo.getX(),memo.getY(),memo.getZ(),icon,0,memo.getKey());
            markers.add(ma);
        }

        for(BuildingDTO building : buildingDTOs){
            Marker ma = new Marker(building.getName(),building.getX(),building.getY(),building.getZ(),buildingIcon,1,building.getKey());
            markers.add(ma);
        }

        ARData.addMarkers(markers);
        return true;
    }


}
