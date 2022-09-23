package com.kkkkkn.module_pen.a83;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.zc_penutil_v6.Zc_Penutil;
import com.example.zc_penutil_v6.Zc_Penutil_Listen;
import com.kkkkkn.module_pen.R;
import com.kkkkkn.module_pen.a83.PenView;

import java.util.Arrays;

public class A83Activity extends AppCompatActivity {
    private final String TAG="Mainactivity";
    private Zc_Penutil zz;
    private boolean start=false;
    private PenView gameView;
    private TextView show_version;
    private Button btnStart,btnStop,btnClear;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a83);
        gameView=(PenView) findViewById(R.id.gameview);
        show_version=findViewById(R.id.show_version);
        btnStart=findViewById(R.id.btn_start);
        btnStop=findViewById(R.id.btn_stop);
        btnClear=findViewById(R.id.btn_clear);

        btnStart.setOnClickListener(onClickListener);
        btnStop.setOnClickListener(onClickListener);
        btnClear.setOnClickListener(onClickListener);

        //gameView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        zz=new Zc_Penutil(listen);

        show_version.setText("SDK版本："+zz.getVersion()+"\nAPP版本："+getVersionName(getApplicationContext()));
        WindowManager windowManager=this.getWindowManager();
        int width=windowManager.getDefaultDisplay().getWidth();
        int height=windowManager.getDefaultDisplay().getHeight();
        Log.i("屏幕大小", "onCreate: "+ width+"   =========="+height+" ");
        //强制横屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    private View.OnClickListener onClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.btn_clear) {
                gameView.clear();
            } else if (id == R.id.btn_start) {
                if (!start) {
                    //全局化变量
                    boolean flag = zz.Init_pen();
                    if (flag) {
                        int[] location = new int[2];
                        gameView.getLocationOnScreen(location);
                        zz.SetRect(location[0], location[1], gameView.getWidth(), gameView.getHeight());
                        //开启线程读取
                        zz.Start_pen();
                        start = true;
                        Log.i(TAG, "初始化成功");

                    } else {
                        Log.i(TAG, "初始化失败");
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "已开启签字", Toast.LENGTH_SHORT).show();
                }
            } else if (id == R.id.btn_stop) {
                if (start) {
                    //关闭线程读取
                    zz.Close_pen();
                    start = false;
                } else {
                    Toast.makeText(getApplicationContext(), "未开始签字，请先点击开始签字", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0,1,0,"开始签字");
        menu.add(0,2,0,"结束签字");
        menu.add(0,3,0,"清屏");
        menu.add(0,4,0,"计算位置");
        return true;
    }

    //事件监听
    Zc_Penutil_Listen listen=new Zc_Penutil_Listen() {
        @Override
        public void GetTrajectory(float[] arrays) {

            switch((int)arrays[0]){
                case 1:
                    //落笔
                    gameView.pendown((float) arrays[1],(float)arrays[2],(float)arrays[3]);
                    // Log.i("落笔事件", "handleMessage: "+(short)arr[1]+ "x坐标  "+(short)arr[2]+"y坐标   "+(short)arr[3]+"z坐标  ");
                    break;
                case 2:
                    //抬笔
                    gameView.penup();
                    //Log.i("抬笔事件","handleMessage" +(short)arrays[1]+ "x坐标  "+(short)arrays[2]+"y坐标   "+(short)arrays[3]+"z坐标  ");
                    break;
                case 3:
                    //移动事件
                    gameView.penmove((float) arrays[1],(float)arrays[2],(float)arrays[3]);
                    // Log.i("移动事件", "handleMessage: "+(short)arr[1]+ "x坐标  "+(short)arr[2]+"y坐标   "+(short)arr[3]+"z坐标  ");
                    break;
                case 4:
                    //超出区域事件
                    gameView.penmove((float) arrays[1],(float)arrays[2],(float)arrays[3]);
                    // Log.i("移动事件", "handleMessage: "+(short)arr[1]+ "x坐标  "+(short)arr[2]+"y坐标   "+(short)arr[3]+"z坐标  ");
                    break;
                default:
                    break;
            }

            //Log.i("AAAA", "GetTrajectory: 收到事件了"+arrays[0]+"事件名    "+arrays[1]+"x坐标    "+arrays[2]+"y坐标    "+arrays[3]+"z坐标    ");
        }
    };



    @Override
    protected void onDestroy() {
        //停止线程
        if(start){
            //关闭线程读取
            zz.Close_pen();
            start=false;
        }
        super.onDestroy();
    }

    private static String getVersionName(Context mContext) {
        String versionCode ="";
        try {
            //获取软件版本号，对应AndroidManifest.xml下android:versionCode
            versionCode = mContext.getPackageManager().
                    getPackageInfo(mContext.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }
}
