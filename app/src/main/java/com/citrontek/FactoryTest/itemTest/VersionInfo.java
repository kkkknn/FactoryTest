package com.citrontek.FactoryTest.itemTest;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.citrontek.FactoryTest.MainActivity;
import com.citrontek.FactoryTest.R;

public class VersionInfo extends Activity {
    private TextView codename,incremental,release,sdk,baseband,display,
                        board,model,device,manufacture;
    private Button btn_ok,btn_error;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_version_information);

        //初始化view控件
        initView();
    }

    private void initView(){
        //绑定控件
        codename=findViewById(R.id.codename_value);
        incremental=findViewById(R.id.incremental_value);
        release=findViewById(R.id.release_value);
        sdk=findViewById(R.id.SDK_value);
        baseband=findViewById(R.id.baseband_value);
        display=findViewById(R.id.version_value);

        board=findViewById(R.id.board_value);
        model=findViewById(R.id.model_value);
        device=findViewById(R.id.device_value);
        manufacture=findViewById(R.id.manufacture_value);
        btn_ok=findViewById(R.id.btn_ok);
        btn_error=findViewById(R.id.btn_error);

        //按钮控件事件的绑定
        showVersion();

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //通过按钮
                BackMain(1);
                finish();
            }
        });
        btn_error.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //失败按钮
                BackMain(-1);
                finish();
            }
        });
    }

    //展示版本信息
    private void showVersion(){
        //通过Build这个类来获取Android版本信息
        codename.setText(Build.VERSION.CODENAME);
        incremental.setText(Build.VERSION.INCREMENTAL);
        release.setText(Build.VERSION.RELEASE);
        String sdk_str=Build.VERSION.SDK_INT+" ";
        sdk.setText(sdk_str);
        baseband.setText(Build.getRadioVersion());
        display.setText(Build.DISPLAY);

        board.setText(Build.BOARD);
        model.setText(Build.MODEL);
        device.setText(Build.DEVICE);
        manufacture.setText(Build.MANUFACTURER);
    }

    private void BackMain(int code){
        Intent intent=new Intent();
        intent.putExtra("检测情况",code);
        setResult(RESULT_OK,intent);
    }
}
