package com.citrontek.FactoryTest.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;

import com.citrontek.FactoryTest.R;

public class ModuleButton extends LinearLayoutCompat {
    private Context context;
    private AppCompatTextView textView;
    private AppCompatImageView imageView;

    public ModuleButton(@NonNull Context context) {
        super(context);
        this.context=context;
        initView(context);
    }

    public ModuleButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.module_btn,this,true);
        textView=findViewById(R.id.moduleBtn_name);
        imageView=findViewById(R.id.moduleBtn_image);
    }

    public void setName(String name,int resourceId){
        textView.setText(name);
        imageView.setImageResource(resourceId);
    }


}
