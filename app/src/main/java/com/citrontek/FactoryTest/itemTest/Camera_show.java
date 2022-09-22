package com.citrontek.FactoryTest.itemTest;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import com.citrontek.FactoryTest.R;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class Camera_show extends Activity {
    private Button btn_ok,btn_error;
    private SurfaceView surfaceView;
    private final static String TAG="相机测试";
    private boolean isView;     //判断相机是否正在预览
    private Camera camera;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_camera);

        //初始化控件
        initView();

        //先开始申请权限，无权限或者不给权限直接退出
        if(Camera_show.this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                //无权限注册权限
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[] {Manifest.permission.CAMERA}, 1);
                }else{
                    //android 6.0 版本以上，查询到拥有权限则直接开始预览展示
                    Log.i(TAG, "android 6.0版本以上有权限");
                    camera_Preview();
                }
            }else{
                //6.0版本以下，不用动态申请权限，如果查询到有此权限则 直接开始预览展示
                Log.i(TAG, "android 6.0版本以下有权限");
                camera_Preview();
            }
        }else{
            //无此模块直接弹出警告框退出
            dialog_warning("此设备不支持相机，程序即将退出");
        }

    }

    private void initView(){
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
        surfaceView=findViewById(R.id.camera_view);
    }

    //相机预览展示
    private void camera_Preview(){
        if(surfaceCallback!=null){
            //开启相机连接，开始预览  ------注：0,1参数是指的前后摄像头，具体定义还需自己测试（一般0为后置，1为前置）
            try {
                camera = Camera.open(0);
                Log.i(TAG, "onCreate:获取成功 ");
                //设置角度
                setCameraDisplayOrientation(Camera_show.this,0,camera);

                //获取surfaceholder对象
                SurfaceHolder surfaceHolder=surfaceView.getHolder();

                //设置surface格式
                //参数:系统支持的透明度样式 int类型 详见 PixelFormat类
                surfaceHolder.setFormat(PixelFormat.TRANSPARENT);
                //设置callback返回接口
                surfaceHolder.addCallback(surfaceCallback);
                //再次调用创建方法
                surfaceCallback.surfaceCreated(surfaceHolder);

            }catch (Exception e){
                Log.i(TAG, "onCreate:摄像头被占用或打开失败，请关闭其他相机程序后再进行尝试 ");
                e.printStackTrace();
            }
        }
    }

    private SurfaceHolder.Callback surfaceCallback=new SurfaceHolder.Callback() {
        //首次创建时
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            try {
                camera.setPreviewDisplay(holder);//通过SurfaceView显示取景画面
            } catch (IOException e) {
                e.printStackTrace();
            }
            camera.startPreview();//开始预览
            isView = true;//设置是否预览参数为真
        }

        //发生改变时
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        //销毁时
        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            if(camera!=null){
                if(isView){ //正在预览
                    camera.stopPreview();
                    camera.release();
                }
            }
        }
    };


    //设置相机角度
    public static void setCameraDisplayOrientation(Activity activity, int cameraId, android.hardware.Camera camera){
        Camera.CameraInfo cameraInfo=new Camera.CameraInfo();
        //获取摄像头信息
        Camera.getCameraInfo(cameraId,cameraInfo);
        int rotation=activity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        //获取的当前摄像头的角度
        switch(rotation){
            case Surface.ROTATION_0:
                degrees=0;
                break;
            case Surface.ROTATION_90:
                degrees=90;
                break;
            case Surface.ROTATION_180:
                degrees=180;
                break;
            case Surface.ROTATION_270:
                degrees=270;
                break;
        }

        int result=0;
        if(cameraInfo.facing==Camera.CameraInfo.CAMERA_FACING_FRONT){
            //前置摄像头
            result=(cameraInfo.orientation+degrees)%360;
            result=(360-result)%360;
        }else{
            //后置摄像头
            result=(cameraInfo.orientation-degrees+360)%360;
        }
        Log.i(TAG, "setCameraDisplayOrientation 变量数字是: "+result+"===222==="+degrees);
        camera.setDisplayOrientation(result);
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
                    Log.i(TAG, "onRequestPermissionsResult: 权限申请通过了");

                    camera_Preview();
                } else {
                    //申请权限没有通过，弹出警告框，然后退出当前页面
                    dialog_warning("没有通过照相机权限");
                }
                break;
            }
            default:
                break;
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
