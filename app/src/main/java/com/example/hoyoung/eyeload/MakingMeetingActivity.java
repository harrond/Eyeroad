package com.example.hoyoung.eyeload;

/**
 * Created by Jin on 2016-10-8.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
/**
 * Created by Jin on 2016-11-5.
 */

public class MakingMeetingActivity extends Activity {

    MeetingControl control = MeetingControl.getInstance();
    //private EditText editTextKey;
    private EditText editTextTitle;
    private EditText editTextPlaceName;
    private EditText editTextMeetingInfo;
    private EditText editTextPublisher;
    private EditText editTextPassword;
    private ImageView placeSelect;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_making_meeting);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        //editTextKey = (EditText) findViewById(R.id.meetingKey);
        editTextTitle = (EditText) findViewById(R.id.title);
        editTextPlaceName = (EditText) findViewById(R.id.placeName);
        editTextMeetingInfo = (EditText) findViewById(R.id.meetingInfo);
        editTextPublisher = (EditText) findViewById(R.id.publisher);
        editTextPassword = (EditText) findViewById(R.id.password);
        placeSelect=(ImageView) findViewById(R.id.select_place);
        placeSelect.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                PlaceRadio();
            }

        });

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

    private void PlaceRadio(){
        final CharSequence[] Places = {"신공학관", "원흥관", "명진관","정보문화관P동","정보문화관Q동","학술문화관","사회과학관","경영관","다향관","본관"};
        AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);
        alt_bld.setTitle("모임 장소를 선택하세요");
        alt_bld.setSingleChoiceItems(Places, -1, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                Toast.makeText(getApplicationContext(), "모임장소 = "+Places[item], Toast.LENGTH_SHORT).show();
                editTextPlaceName.setText(Places[item]);
                dialog.cancel();
            }
        });
        AlertDialog alert = alt_bld.create();
        alert.show();
    }
}
