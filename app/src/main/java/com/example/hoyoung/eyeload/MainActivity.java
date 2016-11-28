package com.example.hoyoung.eyeload;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by Jin on 2016-10-8.
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if(Build.VERSION.SDK_INT>=23) {  //버전확인
            String[] permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
            int i = 0;
            int permissionCode = 0;
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {

                } else {//권한 없음
                    ActivityCompat.requestPermissions(this, permissions, permissionCode);
                }
            }
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.searchButton).setOnClickListener(this);
        findViewById(R.id.memoManagementButton).setOnClickListener(this);
        findViewById(R.id.arViewButton).setOnClickListener(this);
        findViewById(R.id.meetingButton).setOnClickListener(this);

    }

    public void onClick(View v) { // 메뉴의 버튼 선택 시 activity 이동
        switch (v.getId()) {
            case R.id.searchButton:
                startActivity(new Intent(this, SearchPlaceActivity.class));
                break;
            case R.id.memoManagementButton:
                startActivity(new Intent(this, MemoManagementActivity.class));
                break;
            case R.id.arViewButton:
                startActivity(new Intent(this, ARActivity.class));
                break;
            case R.id.meetingButton:
                startActivity(new Intent(this, MeetingListActivity.class));
                break;
        }
    }
}
