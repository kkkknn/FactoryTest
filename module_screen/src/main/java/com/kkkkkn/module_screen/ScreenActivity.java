package com.kkkkkn.module_screen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.kkkkkn.lib_common.router.CommonRouterConstant;
import com.kkkkkn.lib_common.util.OnClickUtil;
import com.kkkkkn.module_screen.bad_points.BadPointsActivity;
import com.kkkkkn.module_screen.black_light.BlackLightActivity;
import com.kkkkkn.module_screen.touch.TouchActivity;

@Route(path = CommonRouterConstant.SCREEN)
public class ScreenActivity extends AppCompatActivity implements View.OnClickListener {
    AppCompatButton btn_back_light,btn_bad_points,btn_touch;
    FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen);
        initView();
    }

    private void initView() {
        btn_back_light=findViewById(R.id.btn_blackLight);
        btn_bad_points=findViewById(R.id.btn_badPoints);
        btn_touch=findViewById(R.id.btn_touch);
        frameLayout=findViewById(R.id.frame_layout);

        btn_back_light.setOnClickListener(this);
        btn_bad_points.setOnClickListener(this);
        btn_touch.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(OnClickUtil.isTooFast()){
            return;
        }
        int id=view.getId();
        Intent intent=null;
        if(id==R.id.btn_blackLight){
            intent=new Intent(ScreenActivity.this, BlackLightActivity.class);
        }else if(id==R.id.btn_badPoints){
            intent=new Intent(ScreenActivity.this, BadPointsActivity.class);
        }else if(id==R.id.btn_touch){
            intent=new Intent(ScreenActivity.this, TouchActivity.class);
        }

        if(intent!=null){
            startActivity(intent);
        }

    }
}