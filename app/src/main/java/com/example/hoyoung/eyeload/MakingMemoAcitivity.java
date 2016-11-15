package com.example.hoyoung.test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MakingMemoAcitivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_making_memo_acitivity);

        final EditText edittext=(EditText)findViewById(R.id.inputMemo);// 입력받을 메모의 id
        Button button=(Button)findViewById(R.id.buttonMakingMemo); // 입력 버튼의 id
        final TextView textView=(TextView)findViewById(R.id.outputMemo); // 메모의 내용을 출력할 id

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                textView.setText(edittext.getText());
            }
        });
    }
    public void makeMemo()
    {
        //Activity창에서 입력된 데이터를 Control의 setInfo()를 통해 DB로 보내는 부분을 구현하면 됨
    }


}
