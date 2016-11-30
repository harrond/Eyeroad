package com.example.hoyoung.eyeload;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Jin on 2016-11-5.
 */

public class BuildingInfoActivity extends AppCompatActivity {

    private BuildingControl control;
    private int key;
    private TextView buildingName;
    private TextView buildingInfo;
    private ImageView buildingImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_building_info);
        buildingName = (TextView) findViewById(R.id.building_name);
        buildingInfo = (TextView) findViewById(R.id.building_content);
        buildingImage = (ImageView) findViewById(R.id.building_image);
        control = BuildingControl.getInstance();
        Intent intent = new Intent(this.getIntent());
        key = intent.getIntExtra("buildingKey",-1);
        showBuildingInfo();
    }

    public void showBuildingInfo() {
        SelectBuilding selectBuilding = new SelectBuilding();
        selectBuilding.execute(key);
    }

    public static Bitmap stringToBitmap(String bitmapString) {
        byte[] bytes = Base64.decode(bitmapString, Base64.DEFAULT);
        Bitmap  bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        return bitmap;
    }
    //선택된 Building을 UI에 적용하기 위한 쓰레드
    class SelectBuilding extends AsyncTask<Integer, Void, Boolean> {
        ProgressDialog loading;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = new ProgressDialog(BuildingInfoActivity.this);
            loading.setMessage("불러오는 중입니다.");
            //loading.setProgressStyle(loading.STYLE_SPINNER);
            loading.show();
        }

        @Override
        protected void onPostExecute(Boolean flag) {
            super.onPostExecute(flag);
            loading.dismiss();

            if(flag == true) {
                buildingName.setText(control.getBuildingDTOSelected().getName());
                buildingInfo.setText(control.getBuildingDTOSelected().getInformation());
                buildingImage.setImageBitmap((Bitmap)stringToBitmap(control.getBuildingDTOSelected().getImage()));
            }
            else
                Toast.makeText(getApplicationContext(), "빌딩 검색 실패!", Toast.LENGTH_LONG).show();

        }

        @Override
        protected Boolean doInBackground(Integer... params) {

            return control.getInfo(params[0]);

        }
    }

}
