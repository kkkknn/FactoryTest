package com.citrontek.FactoryTest.itemTest;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import com.citrontek.FactoryTest.R;

import org.jetbrains.annotations.NotNull;

public class Camera_show extends Activity {
    private Button btn_ok,btn_error;
    private SurfaceView surfaceView;
    private final static String TAG="相机测试";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_camera);

        if(!checkCamera()){
            //不支持照相机，弹窗警告并退出
            dialog_warning("此设备不支持照相机");
        }else{
            initView();

            try {
                Camera camera = Camera.open(0);
                Log.i(TAG, "onCreate:获取成功 ");
            }catch (Exception e){
                Log.i(TAG, "onCreate:摄像头被占用 ");
                e.printStackTrace();
            }
            try {
                Camera camera = Camera.open(1);
                Log.i(TAG, "onCreate:获取成功 ");
            }catch (Exception e){
                Log.i(TAG, "onCreate:摄像头被占用 ");
                e.printStackTrace();
            }
        }


    }

    private void initView(){
        surfaceView=findViewById(R.id.camera_view);
        btn_error=findViewById(R.id.btn_error);
        btn_ok=findViewById(R.id.btn_ok);
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

    //检查设备是否支持camera,如果设备在Android6.0版本以上，申请照相机权限
    private boolean checkCamera(){
        if(Camera_show.this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                //无权限注册权限
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[] {Manifest.permission.CAMERA}, 1);
                }
            }
            return true;
        }else{
            return false;
        }
    }

    private void BackMain(int code){
        Intent intent=new Intent();
        intent.putExtra("检测情况",code);
        setResult(RESULT_OK,intent);
    }

    //接收注册权限返回之后，重新申请相机权限并开始测试
    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Log.i(TAG, "onRequestPermissionsResult: 权限申请通过了，耶");

                } else {
                    //申请权限没有通过，弹出警告框，然后退出当前页面
                    dialog_warning("没有通过照相机权限");
                }
                return;
            }
        }
    }


    private void dialog_warning(String msg_str){
        AlertDialog.Builder dialog=new AlertDialog.Builder(Camera_show.this);
        dialog.setTitle("警告");
        dialog.setMessage(msg_str);
        dialog.setCancelable(false);
        dialog.setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //退出程序
                finish();
            }
        });
        dialog.show();
    }
}
