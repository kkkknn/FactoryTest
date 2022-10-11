package com.kkkkkn.module_wifi.bluetooth.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.kkkkkn.module_wifi.R;
import com.kkkkkn.module_wifi.wifi.view.WifiAdapter;
import com.kkkkkn.module_wifi.wifi.view.WifiViewItem;

import java.util.List;

public class BluetoothAdapter extends  RecyclerView.Adapter <BluetoothAdapter.ViewHolder>{
    private List<BluetoothViewItem> viewItemList;

    public BluetoothAdapter(List<BluetoothViewItem> wifiViewItemList) {
        this.viewItemList=wifiViewItemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bluetooth_item,parent,false);
        return new BluetoothAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BluetoothAdapter.ViewHolder holder, int position) {
        BluetoothViewItem item = viewItemList.get(position);
        holder.bluetoothName.setText(item.getName());
    }

    @Override
    public int getItemCount() {
        return viewItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        AppCompatTextView bluetoothName;
        public ViewHolder( View itemView) {
            super(itemView);
            bluetoothName = itemView.findViewById(R.id.wifi_name);
        }
    }
}
