package com.citrontek.FactoryTest.itemTest;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.citrontek.FactoryTest.R;
/*充电器测试*/
public class Charger extends Activity {
    private Button btn_ok,btn_error;
    private TextView show_view;
    private BroadcastReceiver broadcastReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent!=null){
                String action=intent.getAction();
                switch (action){
                    case Intent.ACTION_BATTERY_CHANGED:
                        int state=intent.getIntExtra("status", BatteryManager.BATTERY_STATUS_UNKNOWN);
                        switch(state){
                            case BatteryManager.BATTERY_PROPERTY_CURRENT_NOW:
                                int type=intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
                                if(type==1){
                                    show_view.setText("正在通过适配器充电");
                                }else if(type==2){
                                    show_view.setText("正在通过USB接口充电");
                                }
                                break;
                            case BatteryManager.BATTERY_STATUS_DISCHARGING:
                                show_view.setText("正在放电");
                                break;
                            case BatteryManager.BATTERY_STATUS_NOT_CHARGING :
                                show_view.setText("没有在进行充电");
                                break;
                            case BatteryManager.BATTERY_STATUS_FULL:
                                show_view.setText("已充满");
                                break;
                            case BatteryManager.BATTERY_STATUS_UNKNOWN:
                                show_view.setText("电池状态未知");
                                break;

                        }
                        break;
                    case Intent.ACTION_POWER_CONNECTED:
                        //接通电源
                        show_view.setText("充电器已插入");
                        break;
                    case Intent.ACTION_POWER_DISCONNECTED:
                        //拔出电源
                        show_view.setText("充电器已拔出");
                        break;
                }
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charger);
        //初始化控件
        initView();
        //开启并添加广播
        if(broadcastReceiver!=null){
            IntentFilter filter=new IntentFilter();
            filter.addAction(Intent.ACTION_POWER_CONNECTED);
            filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
            filter.addAction(Intent.ACTION_BATTERY_CHANGED);
            this.registerReceiver(broadcastReceiver,filter);
        }
    }

    private void initView(){
        btn_ok=findViewById(R.id.btn_ok);
        btn_error=findViewById(R.id.btn_error);
        show_view=findViewById(R.id.show_msg);
        show_view.setText("请插拔充电器来进行测试");

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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //注销广播注册
        if(broadcastReceiver!=null){
            this.unregisterReceiver(broadcastReceiver);
        }
    }

    private void BackMain(int code){
        Intent intent=new Intent();
        intent.putExtra("检测情况",code);
        setResult(RESULT_OK,intent);
    }
}
