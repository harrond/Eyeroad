package com.example.hoyoung.eyeload;

/**
 * Created by Hoyoung on 2016-11-30.
 */

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;


public class IntroActivity extends Activity {
    /**
     * Called when the activity is first created.
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 23) {  //버전확인
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
        setContentView(R.layout.intro_activity);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                Intent intent = new Intent(IntroActivity.this, MainActivity.class);
                startActivity(intent);
                // 뒤로가기 했을경우 안나오도록 없애주기 >> finish!!
                finish();
            }
        }, 3000);
    }
}
