package com.example.hoyoung.test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MakingMeetingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_making_meeting);
    }

    public void makeMeeting()
    {
        //Activity창에서 입력된 데이터를 Control의 setInfo()를 통해 DB로 보내는 부분을 구현하면 됨
    }

}
