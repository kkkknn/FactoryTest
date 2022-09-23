package com.kkkkkn.module_pen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.kkkkkn.lib_common.router.CommonRouterConstant;
import com.kkkkkn.lib_common.util.OnClickUtil;
import com.kkkkkn.module_pen.a33.A33Activity;
import com.kkkkkn.module_pen.a83.A83Activity;

import es.dmoral.toasty.Toasty;

@Route(path = CommonRouterConstant.PEN)
public class PenActivity extends AppCompatActivity implements View.OnClickListener {
    private AppCompatButton btn_a33,btn_a83;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pen);
        initView();
        //判断当前系统版本 非android4.4 6.0 的设备，直接弹窗提示然后退出
        int api=getDeviceAndroidVersion();
        if(api==Build.VERSION_CODES.KITKAT){
            //android 4.4 a33 设备
            btn_a33.setVisibility(View.VISIBLE);
            btn_a83.setVisibility(View.GONE);
        }else if(api==Build.VERSION_CODES.M){
            //android 6.0 a83 设备
            btn_a83.setVisibility(View.VISIBLE);
            btn_a33.setVisibility(View.GONE);
        }else {
            //弹窗然后直接退出
            Toasty.error(this, "此设备非本公司设备，不支持手写笔SDK", Toast.LENGTH_SHORT, true).show();
            finish();
        }

    }

    private void initView() {
        btn_a33=findViewById(R.id.pen_btn_a33);
        btn_a83=findViewById(R.id.pen_btn_a83);

        btn_a33.setOnClickListener(this);
        btn_a83.setOnClickListener(this);
    }

    /**
     * 获取设备androidAPI版本
     * @return android API 版本
     */
    private int getDeviceAndroidVersion(){
        return Build.VERSION.SDK_INT;
    }

    @Override
    public void onClick(View view) {
        if(OnClickUtil.isTooFast()){
            return;
        }
        //页面跳转，跳转到相对应的SDK测试页面
        int id = view.getId();
        Intent intent=null;
        if(id==R.id.pen_btn_a33){
            intent=new Intent(PenActivity.this, A33Activity.class);
        }else if(id==R.id.pen_btn_a83){
            intent=new Intent(PenActivity.this, A83Activity.class);
        }

        if(intent!=null){
            startActivity(intent);
        }
    }
}