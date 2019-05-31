package com.citrontek.FactoryTest.itemTest;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.citrontek.FactoryTest.R;

public class Lcd extends Activity {
    private TextView show_msg,lcd_test;
    private Button btn_ok,btn_error;
    private final String TAG="LCD屏幕测试";
    private final static int red=0,
            yellow=1,
            green=2,
            black=3,
            white=4,
            blue=5,
            pale=6;
    private Handler mhandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case red:
                    lcd_test.setBackgroundColor(Color.rgb(255,0,0));
                    break;
                case yellow:
                    lcd_test.setBackgroundColor(Color.rgb(255,255,0));
                    break;
                case black:
                    lcd_test.setBackgroundColor(Color.rgb(0,0,0));
                    break;
                case white:
                    lcd_test.setBackgroundColor(Color.rgb(255,255,255));
                    break;
                case green:
                    lcd_test.setBackgroundColor(Color.rgb(0,255,0));
                    break;
                case pale:
                    lcd_test.setBackgroundColor(Color.rgb(187,255,255));
                    break;
                case blue:
                    lcd_test.setBackgroundColor(Color.rgb(0,0,255));
                    break;
                case 200:
                    show_msg.setText("测试完成");
                    show_msg.setVisibility(View.VISIBLE);
                    lcd_test.setVisibility(View.INVISIBLE);
                    btn_ok.setVisibility(View.VISIBLE);
                    btn_error.setVisibility(View.VISIBLE);
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_lcd);
        initView();
    }

    private void initView(){
        btn_ok=findViewById(R.id.btn_ok);
        btn_error=findViewById(R.id.btn_error);
        show_msg=findViewById(R.id.show_msg);
        lcd_test=findViewById(R.id.test_lcd);
        //lcd_test.setBackgroundColor(Color.GREEN);
        show_msg.setText("点击此处开始屏幕测试");
        btn_ok.setVisibility(View.INVISIBLE);
        btn_error.setVisibility(View.INVISIBLE);
        lcd_test.setVisibility(View.INVISIBLE);
        show_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick: 屏幕测试开始");
                //弹出toast开始测试
                Toast.makeText(getApplicationContext(),"开始屏幕测试",Toast.LENGTH_SHORT).show();
                //隐藏提示信息框
                show_msg.setVisibility(View.INVISIBLE);
                show_msg.setEnabled(false);
                lcd_test.setVisibility(View.VISIBLE);
                //调用handler机制来运行测试线程
                Thread thread=new Thread(){
                    @Override
                    public void run() {
                        for (int i=0;i<7;i++){
                            Message msg=mhandler.obtainMessage();
                            msg.what=i;
                            mhandler.sendMessage(msg);
                            //休眠发送下一个消息
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        //测试完成之后发送消息通知测试完成
                        Message msg=mhandler.obtainMessage();
                        msg.what=200;
                        mhandler.sendMessage(msg);
                    }
                };
                thread.start();
            }
        });

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


    private void BackMain(int code){
        Intent intent=new Intent();
        intent.putExtra("检测情况",code);
        setResult(RESULT_OK,intent);
    }
}
