package com.kkkkkn.module_wifi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.FrameStats;
import android.view.View;
import android.widget.TableLayout;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.kkkkkn.lib_common.router.CommonRouterConstant;

import java.util.List;

@Route(path = CommonRouterConstant.WIFI)
public class WifiActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    public static final String TAG="WifiActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi);

        initView();

        checkPermission();
    }

    private void checkPermission() {
        Log.i(TAG, "checkPermission: 开始检查权限");
        XXPermissions.with(this)
                // 申请单个权限
                .permission(Permission.Group.BLUETOOTH)
                // 申请多个权限
                .permission(Permission.ACCESS_FINE_LOCATION)
                .permission(Permission.ACCESS_COARSE_LOCATION)
                // 设置权限请求拦截器（局部设置）
                //.interceptor(new PermissionInterceptor())
                // 设置不触发错误检测机制（局部设置）
                //.unchecked()
                .request(new OnPermissionCallback() {
                    @Override
                    public void onGranted(List<String> permissions, boolean all) {
                        if (!all) {
                            Log.i(TAG,"获取部分权限成功，但部分权限未正常授予");
                            return;
                        }
                        Log.i(TAG,"获取录音和日历权限成功");
                    }

                    @Override
                    public void onDenied(List<String> permissions, boolean never) {
                        if (never) {
                            Log.i(TAG,"被永久拒绝授权，请手动授予录音和日历权限");
                           // toast("被永久拒绝授权，请手动授予录音和日历权限");
                            // 如果是被永久拒绝就跳转到应用权限系统设置页面
                            XXPermissions.startPermissionActivity(getApplicationContext(), permissions);
                        } else {
                            Log.i(TAG,"获取录音和日历权限失败");
                            //toast("获取录音和日历权限失败");
                        }
                    }
                });
    }

    private void initView() {
        tabLayout=findViewById(R.id.tab_layout);
        viewPager2=findViewById(R.id.view_pager2);

        viewPager2.setAdapter(new PageAdapter(this));

        //禁止左右滑动
        viewPager2.setUserInputEnabled(false);

        new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                if (position==1){
                    tab.setText("蓝牙模块");
                }else {
                    tab.setText("wifi模块");
                }
            }
        }).attach();
    }


}