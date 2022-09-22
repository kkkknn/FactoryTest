package com.citrontek.FactoryTest.itemTest;

import android.Manifest;
import android.content.pm.PackageManager;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import com.citrontek.FactoryTest.R;

public class Wifi_search extends AppCompatActivity {
    private Button btn_ok,btn_error;
    private static final int wifi_permission_code=201;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_search);
        //初始化控件
        initView();
    }

    private void initView(){

    }

    //检测wifi权限并申请
    private void Request_permission(){
        //检查位置权限
        int permissionCheck = ContextCompat.checkSelfPermission(Wifi_search.this, Manifest.permission_group.LOCATION);
        if(permissionCheck!= PackageManager.PERMISSION_GRANTED){
            //无权限，申请权限
            ActivityCompat.requestPermissions(this,new String[]{
                    Manifest.permission.ACCESS_WIFI_STATE,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
            },wifi_permission_code);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==wifi_permission_code){
            if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                //同意权限

            }else{
                //拒绝权限,展示对话框然后退出本页面

            }
        }
    }
}
