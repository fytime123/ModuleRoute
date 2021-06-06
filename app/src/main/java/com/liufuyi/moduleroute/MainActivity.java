package com.liufuyi.moduleroute;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.liufuyi.routecompiler.Route;

@Route("/main")
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}