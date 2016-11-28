package kr.soen.mypart;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Created by Jin on 2016-10-8.
 */

public class MemoManagementActivity extends AppCompatActivity {

    private ListView listView;
    private MemoControl control;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_management);
        setupListView();

    }

    @Override
    public void onResume() {
        super.onResume();

        setupListView();

    }

    //List내용을 xml에 추가하는 부분
    public void setupListView()
    {

        SelectAllMemo selectAllMemo = new SelectAllMemo();
        selectAllMemo.execute();

    }

    public void memoClicked() {

        // Memo list 선택 작동하는 부분
        AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(MemoManagementActivity.this,MemoInfoActivity.class);
                intent.putExtra("memoKey",String.valueOf(control.getMemoList().get(position).getKey()));
                startActivity(intent);

            }
        };
        listView.setOnItemClickListener(listener);
    }


    class SelectAllMemo extends AsyncTask<Void, Void, Void> {
        ProgressDialog loading;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = new ProgressDialog(MemoManagementActivity.this);
            loading.setMessage("불러오는 중입니다.");

            listView = (ListView) findViewById(R.id.memoManagementListview1);

            control = MemoControl.getInstance();

            loading.show();
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            loading.dismiss();

            listView.setAdapter(control);

            memoClicked();

        }


        @Override
        protected Void doInBackground(Void... params) {

            control.getAllPersonalMemo();

            return null;
        }
    }
}
