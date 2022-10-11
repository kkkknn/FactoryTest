package com.kkkkkn.module_wifi.bluetooth;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kkkkkn.module_wifi.R;
import com.kkkkkn.module_wifi.bluetooth.util.BluetoothUtil;
import com.kkkkkn.module_wifi.bluetooth.view.BluetoothAdapter;
import com.kkkkkn.module_wifi.bluetooth.view.BluetoothViewItem;
import com.kkkkkn.module_wifi.wifi.bean.WifiConfig;
import com.kkkkkn.module_wifi.wifi.view.DialogConnWifi;
import com.kkkkkn.module_wifi.wifi.view.WifiAdapter;
import com.kkkkkn.module_wifi.wifi.view.WifiViewItem;
import com.suke.widget.SwitchButton;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class BluetoothFragment extends Fragment {
    private BluetoothAdapter bluetoothAdapter;
    private List<BluetoothViewItem> bluetoothViewItems = new ArrayList<>();
    private SwitchButton switchButton;
    private SwipeRefreshLayout swipeRefreshLayout;
    private BluetoothUtil bluetoothUtil;

    public BluetoothFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bluetooth, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    private void initView(View view) {
        switchButton=view.findViewById(R.id.switch_button);
        swipeRefreshLayout=view.findViewById(R.id.refresh);
        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(Color.WHITE);
        swipeRefreshLayout.setColorSchemeResources(R.color.teal_700, R.color.flush_color, R.color.purple_700);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //开始刷新

                bluetoothViewItems.clear();
                bluetoothAdapter.notifyDataSetChanged();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //同步加载网络数据
                        syncWifiList();
                        bluetoothAdapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 1000);  //延迟10秒执行
            }
        });

        switchButton.setOnCheckedChangeListener((view1, isChecked) -> {
            if (isChecked) {
                bluetoothUtil.openBluetooth();
            } else {
                bluetoothUtil.closeBluetooth();
                bluetoothViewItems.clear();
                bluetoothAdapter.notifyDataSetChanged();
            }
        });

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.wifi_recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        recyclerView.setLayoutManager(layoutManager);
        bluetoothAdapter = new BluetoothAdapter(bluetoothViewItems);
        recyclerView.setAdapter(bluetoothAdapter);

    }

    //刷新蓝牙列表设备
    private void syncWifiList() {
        
    }

}