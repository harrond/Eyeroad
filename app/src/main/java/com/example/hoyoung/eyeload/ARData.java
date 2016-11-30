package com.example.hoyoung.eyeload;

import android.location.Location;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class ARData {
    private static final String TAG = "ARData";
    private static final Map<String, Marker> markerList = new ConcurrentHashMap<String, Marker>();
    private static final List<Marker> cache = new CopyOnWriteArrayList<Marker>();
    private static final AtomicBoolean dirty = new AtomicBoolean(false);
    private static final float[] locationArray = new float[3];
    private static List<Marker> path = null;

    public static final Location hardFix = new Location("ATL");

    static { //초기치 값 으로 신공학관 좌표찍어 놓음
        hardFix.setLatitude(37.5583037);
        hardFix.setLongitude(126.9984677);
        hardFix.setAltitude(89.234482);
    }

    private static final Object radiusLock = new Object();
    private static final Object buildingRadiusLock =new Object();
    private static float radius = new Float(20); //반경
    private static float buildingRadius = new Float(20);
    private static Location currentLocation = hardFix;
    private static Matrix rotationMatrix = new Matrix();
    private static final Object azimuthLock = new Object();
    private static float azimuth = 0;
    private static final Object pitchLock = new Object();
    private static float pitch = 0;
    private static final Object rollLock = new Object();
    private static float roll = 0;


    public static void addPath(Collection<Marker> lines){ //경로를 저장
        if (lines==null) throw new NullPointerException();
        if (lines.size()<=0) return;
        path=(List<Marker>)lines;
        for (Marker marker : path) {
            marker.calcRelativePosition(ARData.getCurrentLocation());
        }
    }

    public static void setRadius(float radius) {
        synchronized (ARData.radiusLock) {
            ARData.radius = radius;
        }
    }
    public static void setBuildingRadius(float radius) {
        synchronized (ARData.buildingRadiusLock) {
            ARData.buildingRadius = radius;
        }
    }

    public static float getRadius() { //길,메모 마커들의 반경
        synchronized (ARData.radiusLock) {
            return ARData.radius;
        }
    }
    public static float getBuildingRadius() { //빌딩 마커의 반경
        synchronized (ARData.buildingRadiusLock) {
            return ARData.buildingRadius;
        }
    }

    public static void setCurrentLocation(Location currentLocation) { //현재 위치를 저장
        if (currentLocation == null) throw new NullPointerException();

        Log.d(TAG, "current location. location=" + currentLocation.toString());
        synchronized (currentLocation) {
            ARData.currentLocation = currentLocation;
        }
        onLocationChanged(currentLocation); //현재 위치가 변경되 었을 때 호출되서 마커들의 위치를 계산
    }

    public static Location getCurrentLocation() { //현재 위치를 반환한다.
        synchronized (ARData.currentLocation) {
            return ARData.currentLocation;
        }
    }

    public static void setRotationMatrix(Matrix rotationMatrix) {
        synchronized (ARData.rotationMatrix) {
            ARData.rotationMatrix = rotationMatrix;
        }
    }

    public static Matrix getRotationMatrix() {
        synchronized (ARData.rotationMatrix) {
            return rotationMatrix;
        }
    }

    public static List<Marker> getMarkers() { //경로를 제외한 마커들을 반환한다.
        if (dirty.compareAndSet(true, false)) {
            Log.v(TAG, "DIRTY flag found, resetting all marker heights to zero.");
            for (Marker ma : markerList.values()) {
                ma.getLocation().get(locationArray);
                locationArray[1] = ma.getInitialY();
                ma.getLocation().set(locationArray);
            }

            Log.v(TAG, "Populating the cache.");
            List<Marker> copy = new ArrayList<Marker>();
            copy.addAll(markerList.values());
            Collections.sort(copy, comparator);
            cache.clear();
            cache.addAll(copy);
        }
        return Collections.unmodifiableList(cache);
    }

    public static void setAzimuth(float azimuth) {
        synchronized (azimuthLock) {
            ARData.azimuth = azimuth;
        }
    }
    public static List<Marker> getPath(){ //경로 마커들을 반환 한다.
        if(path == null) return null;
        for(Marker ma: path){
            ma.getLocation().get(locationArray);
            locationArray[1]=ma.getInitialY();
            ma.getLocation().set(locationArray);
        }
        return Collections.unmodifiableList(path);
    }

    public static float getAzimuth() {
        synchronized (azimuthLock) {
            return ARData.azimuth;
        }
    }

    public static void setPitch(float pitch) {
        synchronized (pitchLock) {
            ARData.pitch = pitch;
        }
    }

    public static float getPitch() {
        synchronized (pitchLock) {
            return ARData.pitch;
        }
    }

    public static void setRoll(float roll) {
        synchronized (rollLock) {
            ARData.roll = roll;
        }
    }

    public static float getRoll() {
        synchronized (rollLock) {
            return ARData.roll;
        }
    }

    private static final Comparator<Marker> comparator = new Comparator<Marker>() { //거리로 비교
        public int compare(Marker arg0, Marker arg1) {
            return Double.compare(arg0.getDistance(), arg1.getDistance());
        }
    };

    public static void addMarkers(Collection<Marker> markers) { //경로마커를 제외한 마커들을 추가한다.
        if (markers == null) throw new NullPointerException();

        if (markers.size() <= 0) return;

        Log.d(TAG, "New markers, updating markers. new markers=" + markers.toString());
        for (Marker marker : markers) {
            if (!markerList.containsKey(marker.getTitle())) { //똑같은 마커가 들어있는지 확인
                marker.calcRelativePosition(ARData.getCurrentLocation()); //마커의 위치를 현재 위치를 기준으로 다시계산
                markerList.put(marker.getTitle(), marker); //List에 집어넣는다.
            }
        }

        if (dirty.compareAndSet(false, true)) {
            Log.v(TAG, "Setting DIRTY flag!");
            cache.clear();
        }
    }

    private static void onLocationChanged(Location location) { //위치가 변경 되었을 경우
        Log.d(TAG, "New location, updating markers. location=" + location.toString());
        for (Marker ma : markerList.values()) {//바뀐 위치를 기준으로 마커들의 위치를 다시계산
            ma.calcRelativePosition(location);
        }
        if(path !=null) {
            for (Marker marker : path) {//경로가 있다면 바뀐 위치를 기준으로 경로들의 위치를 다시계산
                Log.d(TAG, "path marker change" + location.toString());
                marker.calcRelativePosition(location);
            }
        }
        if (dirty.compareAndSet(false, true)) {
            Log.v(TAG, "Setting DIRTY flag!");
            cache.clear();
        }
    }
}