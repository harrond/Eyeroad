package com.example.hoyoung.test;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class MeetingListActivity extends AppCompatActivity {

    private MeetingControl control;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setupListView();
        meetingClicked();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//Activity 안의 버튼이 눌린 경우
                Intent intent = new Intent(MeetingListActivity.this,MakingMemoAcitivity.class);
                startActivity(intent);
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();
            }
        });
    }

    //List내용을 xml에 추가하는 부분
    public void setupListView()
    {

        control = new MeetingControl();
        control.getAllMeeting();

        listView = (ListView) findViewById(R.id.meetingListview1);
        listView.setAdapter(control);

    }

    public void meetingClicked() {
        // Meeting list 선택 작동하는 부분
        AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MeetingListActivity.this, control.getMeetingList().get(position).getTitle() + " is clicked.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MeetingListActivity.this,MeetingInfoActivity.class);
                intent.putExtra("title",String.valueOf(control.getMeetingList().get(position).getTitle()));
                startActivity(intent);
            }
        };

        listView.setOnItemClickListener(listener);
    }
}
