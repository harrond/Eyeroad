package com.example.hoyoung.eyeload;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Hoyoung on 2016-11-04.
 */
public class ARView extends View {
    private static final AtomicBoolean drawing = new AtomicBoolean(false);

    private static final float[] locationArray = new float[3];
    private static final List<Marker> cache = new ArrayList<Marker>();
    private static final TreeSet<Marker> updated = new TreeSet<Marker>();
    private static final int COLLISION_ADJUSTMENT = 100;

    public ARView(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (canvas == null) return;
        if (drawing.compareAndSet(false, true)) {
            List<Marker> collection = ARData.getMarkers();
            List<Marker> path=ARData.getPath();

            cache.clear();
            if(ARActivity.visibleMarker) { //메모들 보기 여부
                for (Marker m : collection) { //메모들 추가
                    m.update(canvas, 0, 0);
                    if (m.isOnRadar()) cache.add(m);//반경안에있으면 추가
                }
            }
            if(path!=null) { //길을 받아왔는지
                for (Marker m : path) {
                    m.update(canvas, 0, 0);
                    if (m.isOnRadar()) cache.add(m);//반경안에 있으면 추가
                }
            }
            collection = cache;

            if (ARActivity.useCollisionDetection) adjustForCollisions(canvas, collection); //겹쳐도 되는지

            ListIterator<Marker> iter = collection.listIterator(collection.size());
            while (iter.hasPrevious()) {
                Marker marker = iter.previous();
                marker.draw(canvas);
            }
            drawing.set(false);
        }
    }

    private static void adjustForCollisions(Canvas canvas, List<Marker> collection) { //마커가 겹치는지를 검사해서 분리 시켜줌
        updated.clear();
        for (Marker marker1 : collection) {
            if (updated.contains(marker1) || !marker1.isInView()) continue;

            int collisions = 1;
            for (Marker marker2 : collection) {
                if (marker1.equals(marker2) || updated.contains(marker2) || !marker2.isInView())
                    continue;

                if (marker1.isMarkerOnMarker(marker2)) {
                    marker2.getLocation().get(locationArray);
                    float y = locationArray[1];
                    float h = collisions * COLLISION_ADJUSTMENT;
                    locationArray[1] = y + h;
                    marker2.getLocation().set(locationArray);
                    marker2.update(canvas, 0, 0);
                    collisions++;
                    updated.add(marker2);
                }
            }
            updated.add(marker1);
        }
    }


}
