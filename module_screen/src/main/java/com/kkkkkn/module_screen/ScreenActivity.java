package com.kkkkkn.module_screen;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.kkkkkn.lib_common.router.CommonRouterConstant;

@Route(path = CommonRouterConstant.SCREEN)
public class ScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen);

    }
}