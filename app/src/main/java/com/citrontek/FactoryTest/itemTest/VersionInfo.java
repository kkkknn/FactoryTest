package com.citrontek.FactoryTest.itemTest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.citrontek.FactoryTest.R;

public class VersionInfo extends AppCompatActivity {

    /*软件版本
       codename.setText(Build.VERSION.CODENAME);
       incremental.setText(Build.VERSION.INCREMENTAL);
       release.setText(Build.VERSION.RELEASE);
       sdk.setText(Build.VERSION.SDK_INT + "");
       baseband.setText(Build.getRadioVersion());
       display.setText(Build.DISPLAY);

       硬件版本
       board.setText(Build.BOARD);
       model.setText(Build.MODEL);
       device.setText(Build.DEVICE);
       manufacture.setText(Build.MANUFACTURER);*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_version_information);
    }
}
