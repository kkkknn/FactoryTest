package com.kkkkkn.module_wifi.wifi.view;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.kkkkkn.module_wifi.R;

import java.util.List;

public class WifiAdapter extends  RecyclerView.Adapter <WifiAdapter.ViewHolder> {
    private List<WifiViewItem> viewItemList;
    private OnItemOnClickListener listener;

    public WifiAdapter(List<WifiViewItem> wifiViewItemList) {
        this.viewItemList=wifiViewItemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.wifi_item,parent,false);
        return new ViewHolder(view);
    }

    public void setOnItemOnClickListener(OnItemOnClickListener onItemOnClickListener) {
        this.listener = onItemOnClickListener;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder,int position) {
        WifiViewItem item = viewItemList.get(position);
        int level=item.getSignalLevel();
        if (listener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onClick(holder.getAdapterPosition());
                }
            });
        }
        switch (level){
            case 4:
                holder.imageView.setImageResource(R.drawable.ic_wifi_4);
                break;
            case 3:
                holder.imageView.setImageResource(R.drawable.ic_wifi_3);
                break;
            case 2:
                holder.imageView.setImageResource(R.drawable.ic_wifi_2);
                break;
            case 1:
                holder.imageView.setImageResource(R.drawable.ic_wifi_1);
                break;
            case 0:
                holder.imageView.setImageResource(R.drawable.ic_wifi_none);
                break;
        }
        holder.wifiName.setText(item.getName());

    }


    public interface OnItemOnClickListener{
        void onClick(int position);
    }

    @Override
    public int getItemCount() {
        return viewItemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        AppCompatImageView imageView;
        AppCompatTextView wifiName;
        public ViewHolder( View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.wifi_level);
            wifiName = itemView.findViewById(R.id.wifi_name);
        }
    }
}
