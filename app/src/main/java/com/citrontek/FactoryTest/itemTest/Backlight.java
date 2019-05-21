package com.citrontek.FactoryTest.itemTest;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.citrontek.FactoryTest.R;

public class Backlight extends Activity {
    private Button btn_ok,btn_error;
    private TextView show_msg;
    private Handler handler;
    private float back_light;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_backlight);

        initView();

        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case 1000:
                        //修改屏幕背光
                        Window window = Backlight.this.getWindow();
                        WindowManager.LayoutParams layoutParams = window.getAttributes();
                        layoutParams.screenBrightness = 0.0f;
                        window.setAttributes(layoutParams);
                        Log.i("测试", "handleMessage: 开始修改1");
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        break;
                    case 1001:
                        //修改屏幕背光
                        Window window1 = Backlight.this.getWindow();
                        WindowManager.LayoutParams layoutParams1 = window1.getAttributes();
                        layoutParams1.screenBrightness = 1f;
                        window1.setAttributes(layoutParams1);
                        Log.i("测试", "handleMessage: 开始修改2");
                        break;
                }
            }
        };

        Window window = Backlight.this.getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        back_light=layoutParams.screenBrightness;

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Window window = Backlight.this.getWindow();
                WindowManager.LayoutParams layoutParams = window.getAttributes();
                layoutParams.screenBrightness = 0.0f;
                window.setAttributes(layoutParams);
                show_msg.setText("屏幕亮度变暗");
            }
        },3000);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Window window = Backlight.this.getWindow();
                WindowManager.LayoutParams layoutParams = window.getAttributes();
                layoutParams.screenBrightness = 1f;
                window.setAttributes(layoutParams);
                show_msg.setText("屏幕亮度变亮");
            }
        },6000);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Window window = Backlight.this.getWindow();
                WindowManager.LayoutParams layoutParams = window.getAttributes();
                layoutParams.screenBrightness = back_light;
                window.setAttributes(layoutParams);
                show_msg.setText("屏幕亮度恢复");
                btn_ok.setEnabled(true);
                btn_error.setEnabled(true);
            }
        },9000);
    }

    private void initView(){
        btn_ok=findViewById(R.id.btn_ok);
        btn_error=findViewById(R.id.btn_error);
        show_msg=findViewById(R.id.show_msg);
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

        btn_ok.setEnabled(false);
        btn_error.setEnabled(false);
        show_msg.setText("请观察屏幕亮度是否变化");
    }
    private void BackMain(int code){
        Intent intent=new Intent();
        intent.putExtra("检测情况",code);
        setResult(RESULT_OK,intent);
    }

    //防止返回键退出造成崩溃
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return false;

    }
}
