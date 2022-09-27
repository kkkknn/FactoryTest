package com.kkkkkn.module_wifi;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.kkkkkn.module_wifi.bluetooth.BluetoothFragment;
import com.kkkkkn.module_wifi.wifi.WifiFragment;

class PageAdapter extends FragmentStateAdapter {

    public PageAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 1) {
            return new BluetoothFragment();
        }
        return new WifiFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
