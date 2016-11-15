package com.example.hoyoung.test;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MeetingInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_info);

        Intent intent = new Intent(this.getIntent());
        String title = intent.getStringExtra("title");
        TextView textView=(TextView)findViewById(R.id.meetingInfoTextView);
        textView.setText(title);
    }

    public void navigateMeeting()
    {
        //showMapActivity를 호출하여 길을 안내하면 됨
    }
    public void deleteMeeting()
    {
        //Control의 delete()를 통해 Meeting을 삭제하면 됨
    }
}
