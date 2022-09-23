package com.kkkkkn.module_screen.bad_points;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.kkkkkn.lib_common.util.OnClickUtil;
import com.kkkkkn.module_screen.R;

import java.util.Timer;
import java.util.TimerTask;

public class BadPointsActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int[] COLORS = {
            Color.RED,
            Color.GREEN,
            Color.WHITE,
            Color.BLACK,
            Color.BLUE,
            Color.rgb(187,255,255)
    };
    private int mColorIndex;
    private AppCompatTextView vc,vt;
    private long lastPress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bad_points);

        initView();


        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                long time=System.currentTimeMillis();
                if(lastPress!=0L&&(time-lastPress)>3000){
                    changeBackground();
                }
            }
        };
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(task, 500, 1000);//重复执行任务,第二个参数为延迟执行任务的时间,第三个参数为后续重复任务的延迟时间

    }

    private void initView() {
        vc = findViewById(R.id.lcd_color);
        vt = findViewById(R.id.lcd_text);

        vc.setOnClickListener(this);
        vt.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(OnClickUtil.isTooFast()){
            return;
        }
        int id =view.getId();
        if(id==R.id.lcd_text){
            vc.setVisibility(View.VISIBLE);
            int index = mColorIndex++ % (COLORS.length + 1);
            vc.setBackgroundColor(COLORS[index]);
            vt.setVisibility(View.GONE);
        }else{
             changeBackground();
         }
    }

    private void changeBackground(){
        lastPress=System.currentTimeMillis();
        int index = mColorIndex++ % (COLORS.length + 1);
        if (index == COLORS.length) {
            /*vc.setVisibility(View.GONE);
            vt.setVisibility(View.VISIBLE);*/
            //跳出activity
            finish();
        } else {
            runOnUiThread(() -> vc.setBackgroundColor(COLORS[index]));
        }
    }
}