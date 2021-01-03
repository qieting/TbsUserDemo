package com.hsy.tbsuserdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.hsy.word_reader.view.WordReadView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ViewGroup VV = findViewById(R.id.ll);
                VV.removeAllViews();
                WordReadView wordReadView = new WordReadView(MainActivity.this
                );
                VV.addView(wordReadView);
                wordReadView.loadFile("/storage/emulated/0/Download/64090b60538b4915a2c3be7de32ee954.pdf");


            }
        });
    }
}