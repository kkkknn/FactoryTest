package com.kkkkkn.module_pen.a33;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.input.InputManager;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.widget.ImageButton;
import com.edata.Edata;
import com.edata.Point;
import com.kkkkkn.module_pen.R;

import android.view.View;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;

import java.util.ArrayList;


public class A33Activity extends AppCompatActivity {
    AppCompatImageButton imageBtnCl,imageBtnCf;
    Edata disData;

    ArrayList<Point> mPointList = new ArrayList<Point>();
    volatile boolean stopReadAndDraw = false;
    volatile boolean stopViewFlush = false;
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private DisThread mDisThread;
    private ImageDlg mImgDlg;
    private ReplayThread mReplayThread;

    public class ImageDlg extends Dialog {
        private AppCompatImageView mIimageView;
        private  AppCompatImageButton mImageBtnCl;
        public ImageDlg(Context context){
            super(context);
            onCreate();
        }
        public void setImage(Bitmap bt)
        {
            mIimageView.setImageBitmap(bt);
        }
        public void onCreate(){
            setContentView(R.layout.dialog_img);
            mIimageView = (AppCompatImageView)findViewById(R.id.image_sign);
            mImageBtnCl = (AppCompatImageButton) findViewById(R.id.ImageCancel);
            mImageBtnCl.setOnTouchListener((v, event) -> {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    cancel();
                    ((AppCompatImageButton)v).setImageDrawable(getResources().getDrawable(R.drawable.cancel1));
                }else if(event.getAction() == MotionEvent.ACTION_UP){
                    ((AppCompatImageButton)v).setImageDrawable(getResources().getDrawable(R.drawable.cancel0));
                }
                return false;
            });
        }
    }

    private class ReplayThread extends Thread {
        Point[] mPoints;
        @Override
        public void run() {
            super.run();
            if(mPointList.size() < 3){
                return;
            }
            stopReadAndDraw = true;
            while (mDisThread.isAlive()){
                try{
                    sleep(1);
                }catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            }
            mDisThread = null;
            mPoints = new Point[mPointList.size()];
            mPointList.toArray(mPoints);
            disData.replaySign(mPoints);
            mDisThread = new DisThread();
            mDisThread.start();
        }
    }

    private class DisThread extends Thread {
        @Override
        public void run() {
            super.run();
            int ret = disData.prepareForSign();
            Log.i("TAG", "run: "+ret);
            stopReadAndDraw = false;
            while(!stopReadAndDraw){
                try {
                    Point point = disData.doSign();
                    if(null != point){
                        mPointList.add(point);
                    }

                }catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }

    private class DoThings implements SurfaceHolder.Callback{
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format,
                                   int width, int height) {
            int[] rDisD = new int[2];
            mSurfaceView.getLocationOnScreen(rDisD);
            disData.initSignView(rDisD[0], rDisD[1], width, height, format);
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder){
            new Thread(){
                public void run() {
                    stopViewFlush = false;
                    while(!stopViewFlush){
                        try {
                            long startTime = System.currentTimeMillis();
                            disData.displaySign(mSurfaceHolder.getSurface());
                            long endTime = System.currentTimeMillis();
                            int diffTime = (int)(endTime - startTime);
                            while(diffTime < 50){
                                diffTime = (int)(System.currentTimeMillis() - startTime);
                                Thread.yield();
                            }

                        } catch (Exception e) {
                        }finally {
                        }
                    }
                };
            }.start();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder)
        {
            stopViewFlush = true;
        }
    }
    protected void showSignImage(){
        Bitmap bm = disData.getSignImage(mSurfaceView.getWidth(), mSurfaceView.getHeight());
        mImgDlg.setImage(bm);
        mImgDlg.show();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a33);
        mSurfaceView = (SurfaceView)this.findViewById(R.id.surface_track);
        mSurfaceView.setZOrderOnTop(true);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(new DoThings());

        mImgDlg = new ImageDlg(this);

        imageBtnCl = (AppCompatImageButton)findViewById(R.id.ImageCancel);
        imageBtnCf = (AppCompatImageButton)findViewById(R.id.ImageConfirm);
        imageBtnCl.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    disData.doCommand(Edata.CLEAR_DISPLAY);
                    mPointList.clear();
                    ((AppCompatImageButton)v).setImageDrawable(getResources().getDrawable(R.drawable.cancel1));
                }else if(event.getAction() == MotionEvent.ACTION_UP){
                    ((AppCompatImageButton)v).setImageDrawable(getResources().getDrawable(R.drawable.cancel0));
                }
                return false;
            }
        });
        imageBtnCf.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    //showSignImage();

                    //以下代码功能为重放
                    disData.doCommand(Edata.CLEAR_DISPLAY);
                    if(mReplayThread == null || !mReplayThread.isAlive()){
                        mReplayThread = new ReplayThread();
                        mReplayThread.start();
                    }

                    ((ImageButton)v).setImageDrawable(getResources().getDrawable(R.drawable.confirm1));
                }else if(event.getAction() == MotionEvent.ACTION_UP){
                    ((ImageButton)v).setImageDrawable(getResources().getDrawable(R.drawable.confirm0));
                }
                return false;
            }
        });
        disData = new Edata();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        disData.restoreSignView();

    }

    @Override
    protected void onResume() {
        super.onResume();
        mDisThread = new DisThread();
        mDisThread.start();
    }

    @Override
    protected void onStop() {
        stopReadAndDraw = true;
        stopViewFlush = true;
        disData.backupSignView();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        stopReadAndDraw = true;
        stopViewFlush = true;
        disData.finishSign();
        super.onDestroy();
    }
}





