package com.citrontek.FactoryTest;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatTextView;

import android.graphics.Color;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.citrontek.FactoryTest.itemTest.Backlight;
import com.citrontek.FactoryTest.itemTest.Camera_show;
import com.citrontek.FactoryTest.itemTest.Charger;
import com.citrontek.FactoryTest.itemTest.Key_down;
import com.citrontek.FactoryTest.itemTest.Lcd;
import com.citrontek.FactoryTest.itemTest.VersionInfo;
import com.citrontek.FactoryTest.view.ModuleButton;
import com.kkkkkn.lib_common.ModuleConstant;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements View.OnClickListener {
    private static final String TAG="MainActivity";
    private AppCompatTextView deviceName,deviceVersion,firmwareVersion,power;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initInfo();
    }

    /**
     * 获取版本相关信息
     */
    private void initInfo() {
        deviceName.setText(Build.MODEL);
        String str="Android "+Build.VERSION.RELEASE;
        deviceVersion.setText(str);
        firmwareVersion.setText(Build.DISPLAY);
        BatteryManager manager = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            manager = (BatteryManager) getSystemService(BATTERY_SERVICE);
            manager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER);
            manager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_AVERAGE);
            manager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW);
            String batteryStr=manager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)+"%";
            power.setText(batteryStr);
        }

    }

    private void initView() {
        ModuleButton wifiBtn=findViewById(R.id.btn_wifi);
        ModuleButton screenBtn=findViewById(R.id.btn_screen);
        ModuleButton cameraBtn=findViewById(R.id.btn_camera);
        ModuleButton fingerBtn=findViewById(R.id.btn_finger);
        ModuleButton penBtn=findViewById(R.id.btn_pen);
        ModuleButton speaker_micBtn=findViewById(R.id.btn_speaker_mic);

        deviceName=findViewById(R.id.tv_deviceName);
        deviceVersion=findViewById(R.id.tv_androidVersion);
        firmwareVersion=findViewById(R.id.tv_firmwareVersion);
        power=findViewById(R.id.tv_power);

        wifiBtn.setName(ModuleConstant.WIFI,R.drawable.icon_wifi);
        screenBtn.setName(ModuleConstant.SCREEN,R.drawable.icon_screen);
        cameraBtn.setName(ModuleConstant.CAMERA,R.drawable.icon_camera);
        fingerBtn.setName(ModuleConstant.FINGER,R.drawable.icon_finger);
        penBtn.setName(ModuleConstant.PEN,R.drawable.icon_pen);
        speaker_micBtn.setName(ModuleConstant.SPEAKER_MIC,R.drawable.icon_record);

        wifiBtn.setOnClickListener(this);
        screenBtn.setOnClickListener(this);
        cameraBtn.setOnClickListener(this);
        fingerBtn.setOnClickListener(this);
        penBtn.setOnClickListener(this);
        speaker_micBtn.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {

    }
}
