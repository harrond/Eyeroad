package com.example.hoyoung.eyeload;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
/**
 * Created by Jin on 2016-11-5.
 */

public class MemoInfoActivity extends AppCompatActivity implements View.OnClickListener{
    private int key;
    private MemoControl control;
    private TextView memo_name_text;
    private TextView memo_content_text;
    private ImageView memo_image;
    private ImageView memo_icon;
    private TextView memo_data_text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_info);

        memo_name_text=(TextView)findViewById(R.id.memoInfoName);
        memo_content_text=(TextView)findViewById(R.id.memoInfoContent);
        memo_icon=(ImageView) findViewById(R.id.memoInfoIcon);
        memo_image=(ImageView)findViewById(R.id.memoInfoImage);
        memo_data_text=(TextView)findViewById(R.id.memoInfoDate);
        control = MemoControl.getInstance();

        int flag = 0;
        Intent intent = new Intent(this.getIntent());
        key = intent.getIntExtra("memoKey",-1);
        flag = (int)intent.getSerializableExtra("flag");
        if(!(flag==1))
            findViewById(R.id.memoInfoDelete).setOnClickListener(this);

        showMemoInfo();

    }

    public void onClick(View v) { // 메뉴의 버튼 선택 시 activity 이동
        switch (v.getId()) {
            case R.id.memoInfoDelete:
                deleteMemo();
        }
    }

    public void showMemoInfo()
    {
        SelectMemo selectMemo = new SelectMemo();
        selectMemo.execute(key);
    }

    public void deleteMemo()
    {

        DeleteMemo deleteMemo = new DeleteMemo();
        deleteMemo.execute(key);
        finish();
    }

    class SelectMemo extends AsyncTask<Integer, Void, Boolean> {
        ProgressDialog loading;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = new ProgressDialog(MemoInfoActivity.this);
            loading.setMessage("불러오는 중입니다.");
            //loading.setProgressStyle(loading.STYLE_SPINNER);
            loading.show();
        }

        @Override
        protected void onPostExecute(Boolean flag) {
            super.onPostExecute(flag);
            loading.dismiss();

            if(flag == true)
            {
                memo_name_text.setText(control.getMemoDTOSelected().getTitle());
                memo_content_text.setText(control.getMemoDTOSelected().getContent());
                switch (control.getMemoDTOSelected().getIconId())
                {
                    case 1:
                        memo_icon.setImageResource(R.drawable.ic_action_name);
                        break;
                    case 2:
                        memo_icon.setImageResource(R.drawable.ic2_action_name);
                        break;
                    case 3:
                        memo_icon.setImageResource(R.drawable.ic3_action_name);
                        break;
                }
                memo_image.setImageBitmap((Bitmap)stringToBitmap(control.getMemoDTOSelected().getImage()));
                //Toast.makeText(getApplicationContext(), "메모 검색 완료", Toast.LENGTH_LONG).show();
                SimpleDateFormat transFormat=new SimpleDateFormat("yyyy-MM-dd");
                memo_data_text.setText(transFormat.format(control.getMemoDTOSelected().getDate()));
            }
            else
                Toast.makeText(getApplicationContext(), "메모 검색 실패!", Toast.LENGTH_LONG).show();
        }

        @Override
        protected Boolean doInBackground(Integer... params) {

            return control.getMemo(params[0]);

        }
    }

    class DeleteMemo extends AsyncTask<Integer, Void, Boolean> {
        ProgressDialog loading;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = new ProgressDialog(MemoInfoActivity.this);
            loading.setMessage("삭제하는 중입니다.");
            //loading.setProgressStyle(loading.STYLE_SPINNER);
            loading.show();
        }

        @Override
        protected void onPostExecute(Boolean flag) {
            super.onPostExecute(flag);
            loading.dismiss();

            if(flag==true) {
                Toast.makeText(getApplicationContext(), "메모 삭제 완료", Toast.LENGTH_LONG).show();
            }
            else
                Toast.makeText(getApplicationContext(), "메모 삭제 실패!", Toast.LENGTH_LONG).show();
        }

        @Override
        protected Boolean doInBackground(Integer... params) {

            return control.deleteInfo(params[0]);

        }
    }
    public static Bitmap stringToBitmap(String bitmapString) {
        byte[] bytes = Base64.decode(bitmapString, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        return bitmap;
    }

}