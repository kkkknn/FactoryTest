package com.citrontek.FactoryTest;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.citrontek.FactoryTest.itemTest.VersionInfo;

public class MainActivity extends AppCompatActivity {
    private ListView listView;
    private String item;
    private String[] itemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //查看String.xml文件，如果没有相应的测试方法直接弹警告框，然后退出软件
        itemList=getItemListName();
        if(itemList.length==0){
            WarringDialog("警告","配置文件中没有测试项目，请添加后再次尝试");
        }else{
            //绑定控件
            initView();
        }
    }

    //初始化布局
    private void initView(){
        listView=findViewById(R.id.item_list);
        listView.setAdapter(new ArrayAdapter<String>(MainActivity.this,R.layout.item_layout,R.id.item_name,itemList));
        setContentView(listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //获取点击到的id号，然后调用searchByID来查找相应的测试方法
                searchByID(position);
            }
        });
    }

    //获取要测试的项目名称从xml文件当中
    private String[] getItemListName(){
        return getResources().getStringArray(R.array.item_test);
    }

    private void WarringDialog(String title,String msg){
        AlertDialog.Builder dialog=new AlertDialog.Builder(MainActivity.this);
        dialog.setTitle(title);
        dialog.setMessage(msg);
        //不允许通过返回键来退出此警告框
        dialog.setCancelable(false);
        dialog.setPositiveButton("我知道了",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //退出主界面
                finish();
            }
        });
        dialog.show();
    }

    //根据点击的id来进行跳转
    private void searchByID(int id){
        Intent intent=null;
        switch (itemList[id]){
            case "版本信息":
                intent=new Intent(MainActivity.this,VersionInfo.class);
                break;
            case "背光测试":
                break;
            case "屏幕测试":
                break;
            case "按键测试":
                break;
            case "相机测试":
                break;
            case "充电器测试":
                break;
            case "wifi测试":
                break;
            case "录音测试":
                break;
            case "电容屏测试":
                break;
            case "喇叭测试":
                break;
            case "指纹测试":
                break;
            case "手写笔测试":
                break;
        }
        //查找到了就直接跳转开始测试
        if(intent!=null){
            startActivity(intent);
        }
    }
}
