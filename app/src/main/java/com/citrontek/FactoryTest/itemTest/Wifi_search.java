package com.citrontek.FactoryTest.itemTest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.citrontek.FactoryTest.R;

public class Wifi_search extends AppCompatActivity {

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
    private void checkpermisson(){

    }

}
