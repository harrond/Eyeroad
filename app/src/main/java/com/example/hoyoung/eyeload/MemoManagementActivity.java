package com.example.hoyoung.test;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class MemoManagementActivity extends AppCompatActivity {

    private ListView listView;
    private MemoControl control;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_management);

        setupListView();
        memoClicked();
    }

    public void setupListView()
    {
        control = new MemoControl();
        control.getAllMemo();

        listView = (ListView) findViewById(R.id.memoManagementListview1);
        listView.setAdapter(control);
    }

    public void memoClicked() {
        // Memo list 선택 작동하는 부분
        AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MemoManagementActivity.this, control.getMemoList().get(position).getTitle()+" is clicked.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MemoManagementActivity.this,MemoInfoActivity.class);
                intent.putExtra("title",String.valueOf(control.getMemoList().get(position).getTitle()));
                startActivity(intent);

            }
        };
        listView.setOnItemClickListener(listener);
    }
}
