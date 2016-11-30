package com.example.hoyoung.eyeload;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.location.Location;

import java.text.DecimalFormat;

public class Marker implements Comparable<Marker> {
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("@#");

    private static final Vector symbolVector = new Vector(0, 0, 0);
    private static final Vector textVector = new Vector(0, 1, 0);

    private final Vector screenPositionVector = new Vector();
    private final Vector tmpSymbolVector = new Vector();
    private final Vector tmpVector = new Vector();
    private final Vector tmpTextVector = new Vector();
    private final float[] distanceArray = new float[1];
    private final float[] locationArray = new float[3];
    private final float[] screenPositionArray = new float[3];

    private float initialY = 0.0f;

    private volatile static CameraModel cam = null;

    private volatile PaintableBoxedText textBox = null;
    private volatile PaintablePosition textContainer = null;

    private final float[] symbolArray = new float[3];
    private final float[] textArray = new float[3];

    private volatile PaintableObject gpsSymbol = null;
    private volatile PaintablePosition symbolContainer = null;
    private String title = null;
    private volatile PhysicalLocationUtility physicalLocation = new PhysicalLocationUtility(); //마커의 현재 위치
    private volatile double distance = 0.0;
    private volatile boolean isOnRadar = false;
    private volatile boolean isInView = false;
    private final Vector symbolXyzRelativeToCameraView = new Vector();
    private final Vector textXyzRelativeToCameraView = new Vector();
    private final Vector locationXyzRelativeToPhysicalLocation = new Vector();

    private Bitmap bitmap = null;

    private int markerType = 0;//0: memo Marker, 1: Building Marker, 2: path Marker
    private int key;

    public Marker(String title, double latitude, double longitude, double altitude, Bitmap bitmap,int key) {
        set(title, latitude, longitude, altitude, bitmap,key);
    }

    public Marker(String title, double latitude, double longitude, double altitude, Bitmap bitmap, int type,int key) {
        set(title, latitude, longitude, altitude, bitmap,key);
        this.markerType = type;
    }

    public synchronized void set(String title, double latitude, double longitude, double altitude, Bitmap bitmap,int key) {
        if (title == null) throw new NullPointerException();

        this.title = title;
        this.physicalLocation.set(latitude, longitude, altitude);
        this.isOnRadar = false;
        this.isInView = false;
        this.symbolXyzRelativeToCameraView.set(0, 0, 0);
        this.textXyzRelativeToCameraView.set(0, 0, 0);
        this.locationXyzRelativeToPhysicalLocation.set(0, 0, 0);
        this.initialY = 0.0f;
        this.bitmap = bitmap;
        this.key = key;

    }

    public synchronized String getTitle() {
        return this.title;
    }



    public synchronized double getDistance() {
        return this.distance;
    }

    public synchronized float getInitialY() {
        return this.initialY;
    }

    public synchronized int getMarkerType() {
        return this.markerType;
    }

    public synchronized int getKey(){ return this.key; }


    public synchronized boolean isOnRadar() {
        return this.isOnRadar;
    }

    public synchronized boolean isInView() {
        return this.isInView;
    }


    public synchronized Vector getScreenPosition() {
        symbolXyzRelativeToCameraView.get(symbolArray);
        textXyzRelativeToCameraView.get(textArray);
        float x = (symbolArray[0] + textArray[0]) / 2;
        float y = (symbolArray[1] + textArray[1]) / 2;
        float z = (symbolArray[2] + textArray[2]) / 2;

        if (textBox != null) y += (textBox.getHeight() / 2);

        screenPositionVector.set(x, y, z);
        return screenPositionVector;
    }

    public synchronized Vector getLocation() {
        return this.locationXyzRelativeToPhysicalLocation;
    }

    public synchronized float getHeight() {
        if (symbolContainer == null || textContainer == null) return 0f;
        return symbolContainer.getHeight() + textContainer.getHeight();
    }

    public synchronized float getWidth() {
        if (symbolContainer == null || textContainer == null) return 0f;
        float w1 = textContainer.getWidth();
        float w2 = symbolContainer.getWidth();
        return (w1 > w2) ? w1 : w2;
    }

    public synchronized void update(Canvas canvas, float addX, float addY) {//사용자의 현재 위치가 변경 되었을때 다시 그려야 하므로 보이는 위치들을 측정
        if (canvas == null) throw new NullPointerException();

        if (cam == null) cam = new CameraModel(canvas.getWidth(), canvas.getHeight(), true);
        cam.set(canvas.getWidth(), canvas.getHeight(), false);
        cam.setViewAngle(CameraModel.DEFAULT_VIEW_ANGLE);//보이는 각도설정
        populateMatrices(cam, addX, addY);
        updateRadar();
        updateView();
    }

    private synchronized void populateMatrices(CameraModel cam, float addX, float addY) {//화면에 표시할 때 변화를 주어야 하므로 이를 계산
        if (cam == null) throw new NullPointerException();

        tmpSymbolVector.set(symbolVector);
        tmpSymbolVector.add(locationXyzRelativeToPhysicalLocation);
        tmpSymbolVector.prod(ARData.getRotationMatrix());

        tmpTextVector.set(textVector);
        tmpTextVector.add(locationXyzRelativeToPhysicalLocation);
        tmpTextVector.prod(ARData.getRotationMatrix());

        cam.projectPoint(tmpSymbolVector, tmpVector, addX, addY);
        symbolXyzRelativeToCameraView.set(tmpVector);
        cam.projectPoint(tmpTextVector, tmpVector, addX, addY);
        textXyzRelativeToCameraView.set(tmpVector);
    }

    private synchronized void updateRadar() { //radar는 보이는 반경인지를 측정
        isOnRadar = false;
        float range;
        if(markerType==1) {range = ARData.getBuildingRadius()*1000;}//빌딩 타입은 좀 더 멀리 있는 것을 볼 수 있게 한다.
        else {range = ARData.getRadius() * 1000;}
        float scale = range / 48;
        locationXyzRelativeToPhysicalLocation.get(locationArray);
        float x = locationArray[0] / scale;
        float y = locationArray[2] / scale; // z==y Switched on purpose 
        symbolXyzRelativeToCameraView.get(symbolArray);
        if ((symbolArray[2] < -1f) && ((x * x + y * y) < (48 * 48))) {
            isOnRadar = true;
        }
    }

    private synchronized void updateView() { //View는 보일 수 있는 지를 측정
        isInView = false;

        symbolXyzRelativeToCameraView.get(symbolArray);
        float x1 = symbolArray[0] + (getWidth() / 2);
        float y1 = symbolArray[1] + (getHeight() / 2);
        float x2 = symbolArray[0] - (getWidth() / 2);
        float y2 = symbolArray[1] - (getHeight() / 2);
        if (x1 >= -1 && x2 <= (cam.getWidth())
                &&
                y1 >= -1 && y2 <= (cam.getHeight())
                ) {
            isInView = true;
        }
    }

    public synchronized void calcRelativePosition(Location location) {
        if (location == null) throw new NullPointerException();

        updateDistance(location);//거리를 다시 계산

        if (physicalLocation.getAltitude() == 0.0) //0을 가지고 있는 경우가 있는데 0이면 다시 받아서 넣는다.
            physicalLocation.setAltitude(location.getAltitude());

        PhysicalLocationUtility.convLocationToVector(location, physicalLocation, locationXyzRelativeToPhysicalLocation);
        this.initialY = locationXyzRelativeToPhysicalLocation.getY();
        updateRadar();//반경안에 들어왓는지 검사
    }

    private synchronized void updateDistance(Location location) { //입력된 것으로 거리를 측정해서 업데이트 한다.
        if (location == null) throw new NullPointerException();

        Location.distanceBetween(physicalLocation.getLatitude(), physicalLocation.getLongitude(), location.getLatitude(), location.getLongitude(), distanceArray);
        distance = distanceArray[0];
    }

    public synchronized boolean handleClick(float x, float y) { //터치된 x, y로 클릭이 됬는지 검사하는 내부함수를 호출해 그 결과를 리턴
        if (!isOnRadar || !isInView) return false; //화면상에 안보이는 것은 터치에 상관 없으므로 무효
        if(markerType==2||markerType==3) return false; //마커가 길에 해당하는 마커면 아무런 동작을 하지 않도록함
        return isPointOnMarker(x, y, this);
    }

    public synchronized boolean isMarkerOnMarker(Marker marker) {
        return isMarkerOnMarker(marker, true);
    }

    private synchronized boolean isMarkerOnMarker(Marker marker, boolean reflect) { //마커위에 마커가 있는지 검사한다.
        marker.getScreenPosition().get(screenPositionArray);
        float x = screenPositionArray[0];
        float y = screenPositionArray[1];
        boolean middleOfMarker = isPointOnMarker(x, y, this);
        if (middleOfMarker) return true;

        float halfWidth = marker.getWidth() / 2;
        float halfHeight = marker.getHeight() / 2;

        float x1 = x - halfWidth;
        float y1 = y - halfHeight;
        boolean upperLeftOfMarker = isPointOnMarker(x1, y1, this);
        if (upperLeftOfMarker) return true;

        float x2 = x + halfWidth;
        float y2 = y1;
        boolean upperRightOfMarker = isPointOnMarker(x2, y2, this);
        if (upperRightOfMarker) return true;

        float x3 = x1;
        float y3 = y + halfHeight;
        boolean lowerLeftOfMarker = isPointOnMarker(x3, y3, this);
        if (lowerLeftOfMarker) return true;

        float x4 = x2;
        float y4 = y3;
        boolean lowerRightOfMarker = isPointOnMarker(x4, y4, this);
        if (lowerRightOfMarker) return true;

        return (reflect) ? marker.isMarkerOnMarker(this, false) : false;
    }

    private synchronized boolean isPointOnMarker(float x, float y, Marker marker) {
        marker.getScreenPosition().get(screenPositionArray);//현재 마커가 표시되는 화면의 벡터를 screenPositionArray에 얻어온다.
        float myX = screenPositionArray[0];
        float myY = screenPositionArray[1];
        float adjWidth = marker.getWidth() / 2;
        float adjHeight = marker.getHeight() / 2;

        float x1 = myX - adjWidth;
        float y1 = myY - adjHeight;
        float x2 = myX + adjWidth;
        float y2 = myY + adjHeight;

        if (x >= x1 && x <= x2 && y >= y1 && y <= y2) return true;

        return false;
    }

    public synchronized void draw(Canvas canvas) { //Draw함수로 마커의 아이콘과 Text를 그리는 메소드
        if (canvas == null) throw new NullPointerException();

        if (!isOnRadar || !isInView) return;

        drawIcon(canvas); //아이콘을 그린다.
        //if(markerType!=2)//길은 텍스트를 제외
        drawText(canvas);//텍스트를 그린다.
    }

    private synchronized void drawIcon(Canvas canvas) { //마커 아이콘을 화면에 그린다.
        if (canvas == null || bitmap == null) throw new NullPointerException();


        if (gpsSymbol == null) gpsSymbol = new PaintableIcon(bitmap, 96, 96);

        textXyzRelativeToCameraView.get(textArray);
        symbolXyzRelativeToCameraView.get(symbolArray);

        float currentAngle = Utilities.getAngle(symbolArray[0], symbolArray[1], textArray[0], textArray[1]); //현재 각도를 계산
        float angle = currentAngle + 90;

        if (symbolContainer == null)
            symbolContainer = new PaintablePosition(gpsSymbol, symbolArray[0], symbolArray[1], angle, 1); //아이콘과 각도를 화면에 배치를 담당할 클래스에 넘겨 보이게한다.
        else symbolContainer.set(gpsSymbol, symbolArray[0], symbolArray[1], angle, 1);

        symbolContainer.paint(canvas);//캔버스에 그림
    }

    private synchronized void drawText(Canvas canvas) { //타이틀 정보와 남은 거리를 표시하게 한다.
        if (canvas == null) throw new NullPointerException();

        String textStr = null;
        if (distance < 1000.0) { //m를 넘어가면 km로 바꾼다.
            textStr = title + " (" + DECIMAL_FORMAT.format(distance) + "m)";
        } else {
            double d = distance / 1000.0;
            textStr = title + " (" + DECIMAL_FORMAT.format(d) + "km)";
        }

        textXyzRelativeToCameraView.get(textArray);
        symbolXyzRelativeToCameraView.get(symbolArray);

        float maxHeight = Math.round(canvas.getHeight() / 10f) + 1;
        if (textBox == null)
            textBox = new PaintableBoxedText(textStr, Math.round(maxHeight / 2f) + 1, 300);
        else textBox.set(textStr, Math.round(maxHeight / 2f) + 1, 300);

        float currentAngle = Utilities.getAngle(symbolArray[0], symbolArray[1], textArray[0], textArray[1]);
        float angle = currentAngle + 90;

        float x = textArray[0] - (textBox.getWidth() / 2);
        float y = textArray[1] + maxHeight;

        if (textContainer == null) textContainer = new PaintablePosition(textBox, x, y, angle, 1);
        else textContainer.set(textBox, x, y, angle, 1);
        textContainer.paint(canvas);
    }

    public synchronized int compareTo(Marker another) {
        if (another == null) throw new NullPointerException();

        return title.compareTo(another.getTitle());
    } //비교

    @Override
    public synchronized boolean equals(Object marker) {
        if (marker == null || title == null) throw new NullPointerException();

        return title.equals(((Marker) marker).getTitle());
    } //비교
}