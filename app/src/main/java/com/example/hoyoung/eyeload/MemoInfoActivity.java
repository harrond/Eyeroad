package kr.soen.mypart;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * Created by Jin on 2016-11-5.
 */

public class MemoInfoActivity extends AppCompatActivity implements View.OnClickListener{
    private int key;
    private MemoControl control;
    private TextView memo_name_text;
    private TextView memo_content_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_info);
        findViewById(R.id.memoInfoDelete).setOnClickListener(this);

        memo_name_text=(TextView)findViewById(R.id.memoInfoName);
        memo_content_text=(TextView)findViewById(R.id.memoInfoContent);
        control = MemoControl.getInstance();

        Intent intent = new Intent(this.getIntent());
        String memoKey = intent.getStringExtra("memoKey");
        key = Integer.valueOf(memoKey);

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

    class SelectMemo extends AsyncTask<Integer, Void, MemoDTO> {
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
        protected void onPostExecute(MemoDTO memoDTO) {
            super.onPostExecute(memoDTO);
            loading.dismiss();

            if(memoDTO!=null) {
                memo_name_text.setText(memoDTO.getTitle());
                memo_content_text.setText(memoDTO.getContent());

                //Toast.makeText(getApplicationContext(), "메모 검색 완료", Toast.LENGTH_LONG).show();
            }
            else
                Toast.makeText(getApplicationContext(), "메모 검색 실패!", Toast.LENGTH_LONG).show();
        }

        @Override
        protected MemoDTO doInBackground(Integer... params) {

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


}
