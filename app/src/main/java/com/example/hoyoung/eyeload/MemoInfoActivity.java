package com.example.hoyoung.test;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MemoInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_info);

        Intent intent = new Intent(this.getIntent());
        String title = intent.getStringExtra("title");
        TextView textView=(TextView)findViewById(R.id.memoInfoTextView);
        textView.setText(title);
    }

    private void showMemoInfo()
    {
        //Control의 DTO를 가져와 Activity에 반환하거나 xml에 표시해줘야함
    }
    private void deleteMemo()
    {
        //Control의 delete()를 이용하여 Memo를 삭제함
    }
}
