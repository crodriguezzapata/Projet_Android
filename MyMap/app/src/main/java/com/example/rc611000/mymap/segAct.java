package com.example.rc611000.mymap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class segAct extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seg);

        Intent i = getIntent();

        String name = i.getStringExtra("name");
        double temp = i.getDoubleExtra("temp",0.0);
        double tempmax = i.getDoubleExtra("tempMax",0.0);
        double tempmin = i.getDoubleExtra("tempMin",0.0);
        String icon = i.getStringExtra("icon");

    }
}
