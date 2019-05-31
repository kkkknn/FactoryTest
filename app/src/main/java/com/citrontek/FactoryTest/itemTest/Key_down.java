package com.citrontek.FactoryTest.itemTest;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.citrontek.FactoryTest.R;

public class Key_down extends Activity {
    private Button btn_ok,btn_error;
    private TextView show_msg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_keydown);

        initView();
    }

    private void initView(){
        btn_ok=findViewById(R.id.btn_ok);
        btn_error=findViewById(R.id.btn_error);
        show_msg=findViewById(R.id.show_msg);
        show_msg.setText("请按下按键来测试");
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BackMain(1);
                finish();
            }
        });
        btn_error.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BackMain(-1);
                finish();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        show_msg.setText(KeyEvent.keyCodeToString(keyCode));
        return true;
    }

    private void BackMain(int code){
        Intent intent=new Intent();
        intent.putExtra("检测情况",code);
        setResult(RESULT_OK,intent);
    }
}
