package com.example.annotationdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.libannotation.FindDeviceInfo;

public class MainActivity extends AppCompatActivity {
    @FindDeviceInfo
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
