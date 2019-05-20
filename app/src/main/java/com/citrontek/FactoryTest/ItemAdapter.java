package com.citrontek.FactoryTest;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class ItemAdapter extends ArrayAdapter<Item> {
    private int resourceId;

    public ItemAdapter( Context context, int resource, List<Item> objects) {
        super(context, resource, objects);
        resourceId=resource;
    }


    @Override
    public View getView(int position,  View convertView, ViewGroup parent) {
        Item item=getItem(position);
        View view;
        ViewHolder viewHolder;
        if(convertView==null){
            view= LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
            viewHolder=new ViewHolder();
            viewHolder.item_name=view.findViewById(R.id.item_name);
            viewHolder.item_value=view.findViewById(R.id.item_value);
            view.setTag(viewHolder);
        }else{
            view=convertView;
            viewHolder=(ViewHolder) view.getTag();
        }
        viewHolder.item_name.setText(item.getName());

        switch (item.getValue()){
            case 1:
                viewHolder.item_value.setText("通过");
                viewHolder.item_value.setTextColor(Color.GREEN);
                break;
            case 0:
                viewHolder.item_value.setText(" ");
                break;
            case -1:
                viewHolder.item_value.setText("失败");
                viewHolder.item_value.setTextColor(Color.RED);
                break;
        }

        return view;
    }

    class ViewHolder{
        TextView item_name;
        TextView item_value;
    }

    //局部刷新
    public void updateData(View view, int position) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        Item item=getItem(position);
        switch (item.getValue()){
            case 1:
                viewHolder.item_value.setText("通过");
                viewHolder.item_value.setTextColor(Color.GREEN);
                break;
            case 0:
                viewHolder.item_value.setText(" ");
                break;
            case -1:
                viewHolder.item_value.setText("失败");
                viewHolder.item_value.setTextColor(Color.RED);
                break;
        }
    }
}
