package com.example.hoyoung.test;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.HashMap;


public class SearchActivity extends Activity {
    private static final int PLACE_PICKER_REQUESTo = 1;
    private static final int PLACE_PICKER_REQUESTd = 2;
    private TextView mNameo;
    private TextView mAddresso;
    private TextView mAttributionso;
    private TextView mOrigin;
    private TextView mNamed;
    private TextView mAddressd;
    private TextView mAttributionsd;
    private TextView mDest;
    private LatLng origin,dest;
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(37.55757439, 127.00180829), new LatLng(37.5580918, 128.9982178));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_picker);
        mOrigin=(TextView) findViewById(R.id.textView);
        mNameo = (TextView) findViewById(R.id.textView2);
        mAddresso = (TextView) findViewById(R.id.textView4);
        mAttributionso = (TextView) findViewById(R.id.textView3);
        mDest=(TextView) findViewById(R.id.textView7);
        mNamed=(TextView) findViewById(R.id.textView8);
        mAddressd=(TextView) findViewById(R.id.textView9);
        Button pickerButton = (Button) findViewById(R.id.pickerButton);
        pickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    PlacePicker.IntentBuilder intentBuilder =
                            new PlacePicker.IntentBuilder();
                    intentBuilder.setLatLngBounds(BOUNDS_MOUNTAIN_VIEW);
                    Intent intent = intentBuilder.build(SearchActivity.this);
                    intent.putExtra("orgin",1);
                    startActivityForResult(intent, PLACE_PICKER_REQUESTo);

                } catch (GooglePlayServicesRepairableException
                        | GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    public void searchDest(View view){
        try {
            PlacePicker.IntentBuilder intentBuilder =
                    new PlacePicker.IntentBuilder();
            intentBuilder.setLatLngBounds(BOUNDS_MOUNTAIN_VIEW);
            Intent intent = intentBuilder.build(SearchActivity.this);
            intent.putExtra("orgin",1);
            startActivityForResult(intent, PLACE_PICKER_REQUESTd);

        } catch (GooglePlayServicesRepairableException
                | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }

    }

    public void searchPath(View view){
        Intent intent = new Intent(SearchActivity.this, TmapActivity2.class);
        HashMap<String, LatLng> coordinates = new HashMap<>();
        coordinates.put("origin", origin);
        coordinates.put("dest", dest);
        intent.putExtra("coordinate", coordinates);
        startActivity(intent);

    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent data) {

        if (requestCode == PLACE_PICKER_REQUESTo
                && resultCode == Activity.RESULT_OK) {
            final Place place = PlacePicker.getPlace(this, data);
            final CharSequence name = place.getName();
            final CharSequence address = place.getAddress();
            final LatLng latLng=place.getLatLng();
            String attributions = (String) place.getAttributions();
            if (attributions == null) {
                attributions = "";
            }
            origin=latLng;
            mOrigin.setText("출발지");
            mNameo.setText(name);
            mAddresso.setText(address);

        }
        else if (requestCode==PLACE_PICKER_REQUESTd
                && resultCode==Activity.RESULT_OK){
            final Place place = PlacePicker.getPlace(this, data);
            final CharSequence name = place.getName();
            final CharSequence address = place.getAddress();
            final LatLng latLng=place.getLatLng();
            String attributions = (String) place.getAttributions();
            if (attributions == null) {
                attributions = "";
            }
            dest=latLng;
            mDest.setText("도착지");
            mNamed.setText(name);
            mAddressd.setText(address);

        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
