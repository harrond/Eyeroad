package com.example.hoyoung.eyeload;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class MakingMemoAcitivity extends AppCompatActivity {
    private MemoControl control = MemoControl.getInstance();
    private EditText mTitleText;
    private EditText mBodyText;
    private ImageView mImage;
    private ImageView mIcon1;
    private ImageView mIcon2;
    private ImageView mIcon3;
    private ImageView selectedIcon;
    private String mImageString;
    private boolean image_added = false;
    private int mIconID;
    final static int ACT_EDIT = 0;
    private int mSelectedIndexSet;
    private HashMap<String,Double> loc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_making_memo_acitivity);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setTitle("메모 만들기");

        Intent intent = new Intent(this.getIntent());
        loc = (HashMap<String,Double>) intent.getSerializableExtra("cl");
        mTitleText = (EditText) findViewById(R.id.title);
        mBodyText = (EditText) findViewById(R.id.body);
        selectedIcon = (ImageView) findViewById(R.id.selected_icon_img);
        mImage = (ImageView) findViewById(R.id.imageresult);
        findViewById(R.id.imageupload).setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(MakingMemoAcitivity.this, CameraCropActivity.class);
                        startActivityForResult(intent, ACT_EDIT);
                    }
                }
        );
        mIcon1 = (ImageView) findViewById(R.id.icon1);
        mIcon1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedIcon.setImageResource(R.drawable.ic_action_name);
                mIconID = 1;
            }
        });
        mIcon2 = (ImageView) findViewById(R.id.icon2);
        mIcon2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedIcon.setImageResource(R.drawable.ic2_action_name);
                mIconID = 2;
            }
        });
        mIcon3 = (ImageView) findViewById(R.id.icon3);
        mIcon3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedIcon.setImageResource(R.drawable.ic3_action_name);
                mIconID = 3;
            }
        });

        CheckBox checkbox = (CheckBox) findViewById(R.id.public_or_private);
        checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (buttonView.getId() == R.id.public_or_private) {
                    if (isChecked) {
                        mSelectedIndexSet = 1;
                    } else {
                        mSelectedIndexSet = 0;
                    }
                }
            }
        });
        Button saveButton = (Button) findViewById(R.id.save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                setResult(RESULT_OK);
                makeMemo();
                finish();
            }

        });

    }

    public void makeMemo() {
        String deviceID = null;
        InsertMemo insertMemo = new InsertMemo();

        //Device ID를 가져오는 부분
        try {
            deviceID = String.valueOf(Build.class.getField("SERIAL").get(null));
        } catch (Exception ignored) {
            ignored.getMessage();
        }

        Date date = new Date();
        //date->string 처리
        DateFormat sdFormat = new SimpleDateFormat("yyyyMMdd");
        String stringDate = sdFormat.format(date);
        if (image_added == false)
            mImageString = "";
        String lat = loc.get("x")+"";
        String lon = loc.get("y")+"";
        String alt = loc.get("z")+"";

        insertMemo.execute(mTitleText.getText().toString(), lat,lon,alt, mBodyText.getText().toString(), stringDate, mImageString, String.valueOf(mIconID), deviceID, String.valueOf(mSelectedIndexSet));
        //insertMemo.execute(mTitleText.getText().toString(), , "2","3", mBodyText.getText().toString(), stringDate, mImageString, String.valueOf(mIconID), deviceID, String.valueOf(mSelectedIndexSet));

        finish();
    }

    class InsertMemo extends AsyncTask<String, Void, Boolean> {
        ProgressDialog loading;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = new ProgressDialog(MakingMemoAcitivity.this);
            loading.setMessage("메모를 저장하는 중입니다.");

            loading.show();
        }

        @Override
        protected void onPostExecute(Boolean flag) {
            super.onPostExecute(flag);
            loading.dismiss();

            if (flag == true) {
                Toast.makeText(getApplicationContext(), "메모 저장 완료", Toast.LENGTH_LONG).show();
            } else
                Toast.makeText(getApplicationContext(), "메모 저장 실패!", Toast.LENGTH_LONG).show();
        }

        @Override
        protected Boolean doInBackground(String... params) {

            String title = (String) params[0];
            Double x = Double.valueOf(params[1]);
            Double y = Double.valueOf(params[2]);
            Double z = Double.valueOf(params[3]);
            String content = (String) params[4];
            String stringDate = (String) params[5];
            String image = (String) params[6];
            int iconId = Integer.valueOf(params[7]);
            String deviceID = (String) params[8];
            int visibility = Integer.valueOf(params[9]);

            //String -> date 변환
            DateFormat sdFormat = new SimpleDateFormat("yyyyMMdd");
            Date date = new Date();
            try {
                date = sdFormat.parse(stringDate);
            } catch (ParseException e) {
                e.getMessage();
            }

            return control.setInfo(title, x, y, z, content, date, image, iconId, deviceID, visibility);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ACT_EDIT:
                if (resultCode == RESULT_OK) {
                    image_added = true;
                    mImage.setImageBitmap((Bitmap) data.getParcelableExtra("image"));
                    //mImageString=bitmapToByteArray((Bitmap)data.getParcelableExtra("image"));
                    mImageString = BitMapToString((Bitmap) data.getParcelableExtra("image"));
                }
                break;
        }
    }

    public String BitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] arr = baos.toByteArray();
        String result = Base64.encodeToString(arr, Base64.DEFAULT);
        return result;
    }
}

