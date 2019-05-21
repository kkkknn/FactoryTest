package com.citrontek.FactoryTest.itemTest;

import android.app.Activity;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.citrontek.FactoryTest.R;

public class Lcd extends Activity {
    private TextView show_msg,lcd_test;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_lcd);

        initView();
    }

    private void initView(){
        show_msg=findViewById(R.id.show_msg);
        lcd_test=findViewById(R.id.test_lcd);
        lcd_test.setBackgroundColor(Color.GREEN);
        show_msg.setText("dhgslkahsdf");
    }
}
