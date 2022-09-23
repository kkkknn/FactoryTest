package com.kkkkkn.module_screen.black_light;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.kkkkkn.module_screen.R;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class BlackLightActivity extends AppCompatActivity implements View.OnClickListener,Runnable {
    private static final int MSG_BACKLIGHT_ON    = 1;
    private static final int MSG_BACKLIGHT_OFF   = 2;
    private static final int MSG_BACKLIGHT_DEF   = 3;
    private static final int MSG_BACKLIGHT_END   = 4;
    private static final int MSG_DELAY_TIME   = 3000;

    private float mDefaultBrightness;
    private boolean isStart=false;

    private ScheduledThreadPoolExecutor mTimerTask;
    private int mIndex;

    private final Handler mHandler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(Message msg) {
            onHandleMessage(msg.arg1);
        }
    };

    private void onHandleMessage(final int index) {
        switch (index) {
            case MSG_BACKLIGHT_ON:
                setStepText(R.string.backlight_on);
                setScreenBrightness(1.0f);
                setTimerTask(MSG_BACKLIGHT_OFF, MSG_DELAY_TIME);
                break;
            case MSG_BACKLIGHT_OFF:
                setStepText(R.string.backlight_off);
                setScreenBrightness(0.1f);
                setTimerTask(MSG_BACKLIGHT_DEF, MSG_DELAY_TIME);
                break;
            case MSG_BACKLIGHT_DEF:
                setStepText(R.string.backlight_def);
                setScreenBrightness(mDefaultBrightness);
                setTimerTask(MSG_BACKLIGHT_END, MSG_DELAY_TIME);
                break;
            case MSG_BACKLIGHT_END:
                /*setStepVisibility(false);*/
                isStart=false;
                //结束
                finish();
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_black_light);

        mDefaultBrightness = getWindow().getAttributes().screenBrightness;

        findViewById(R.id.backlight_text).setOnClickListener(this);
    }


    private void setScreenBrightness(float brightness) {
        WindowManager.LayoutParams p = getWindow().getAttributes();
        p.screenBrightness = brightness;
        getWindow().setAttributes(p);
    }

    private void setStepVisibility(boolean visible) {
        findViewById(R.id.backlight_step).setVisibility(
                visible ? View.VISIBLE : View.GONE);
        findViewById(R.id.backlight_text).setVisibility(
                visible ? View.GONE : View.VISIBLE);
    }

    private void setStepText(int resId) {
        ((AppCompatTextView) findViewById(R.id.backlight_step)).setText(resId);
        setStepVisibility(true);
    }


    private void setTimerTask(final int index, int delay) {
        cancelTimerTask();

        mIndex = index;
        mTimerTask = new ScheduledThreadPoolExecutor(10);
        mTimerTask.schedule(this, delay, TimeUnit.MILLISECONDS);
    }


    private void cancelTimerTask() {
        if (mTimerTask != null) {
            mTimerTask.remove(this);
            mTimerTask.shutdownNow();
            mTimerTask = null;
        }
    }


    @Override
    public void run() {
        Message message = mHandler.obtainMessage();
        message.arg1 = mIndex;
        mHandler.sendMessage(message);
    }

    @Override
    public void onClick(View view) {
        if(!isStart){
            setTimerTask(MSG_BACKLIGHT_ON, 0);
            isStart=true;
        }
    }
}