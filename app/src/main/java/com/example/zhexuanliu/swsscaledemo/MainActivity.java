package com.example.zhexuanliu.swsscaledemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView tv = (TextView) findViewById(R.id.test_text);

        tv.setText(stringFromNative());
    }

    public native String stringFromNative();

    static {
        System.loadLibrary("demo_scaling");
    }
}
