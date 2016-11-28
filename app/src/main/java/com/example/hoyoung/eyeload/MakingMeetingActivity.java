package kr.soen.mypart;

/**
 * Created by Jin on 2016-10-8.
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
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

public class MakingMeetingActivity extends Activity {

    MeetingControl control = MeetingControl.getInstance();

    private EditText editTextTitle;
    private EditText editTextPlaceName;
    private EditText editTextMeetingInfo;
    private EditText editTextPublisher;
    private EditText editTextPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_making_meeting);

        //editTextKey = (EditText) findViewById(R.id.meetingKey);
        editTextTitle = (EditText) findViewById(R.id.title);
        editTextPlaceName = (EditText) findViewById(R.id.placeName);
        editTextMeetingInfo = (EditText) findViewById(R.id.meetingInfo);
        editTextPublisher = (EditText) findViewById(R.id.publisher);
        editTextPassword = (EditText) findViewById(R.id.password);

    }

    public void insert(View view){

        String title = editTextTitle.getText().toString();
        String placeName = editTextPlaceName.getText().toString();
        String meetingInfo = editTextMeetingInfo.getText().toString();
        String publisher = editTextPublisher.getText().toString();
        String password = editTextPassword.getText().toString();

        InsertMeeting insertMeeting = new InsertMeeting();
        insertMeeting.execute(title,placeName,meetingInfo,publisher,password);
        finish();

    }

    //Memo를 DB로 보내고 UI에 적용하기 위한 쓰레드
    class InsertMeeting extends AsyncTask<String, Void, Boolean> {
        ProgressDialog loading;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = new ProgressDialog(MakingMeetingActivity.this);
            loading.setMessage("모임을 개최하는 중입니다.");
            //loading.setProgressStyle(loading.STYLE_SPINNER);
            loading.show();
        }

        @Override
        protected void onPostExecute(Boolean flag) {
            super.onPostExecute(flag);
            loading.dismiss();

            if(flag == true) {
                Toast.makeText(getApplicationContext(), "모임 개최 완료", Toast.LENGTH_LONG).show();
            }
            else
                Toast.makeText(getApplicationContext(), "모임 개최 실패!", Toast.LENGTH_LONG).show();
        }

        @Override
        protected Boolean doInBackground(String ...params) {

            String title = (String)params[0];
            String placeName = (String)params[1];
            String meetingInfo = (String)params[2];
            String publisher = (String)params[3];
            String password = (String)params[4];

            return control.setInfo(title,placeName,meetingInfo,publisher,password);
        }
    }
}
