package com.hsy.tbsuserdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.hsy.word_reader.WordReadHelper;
import com.hsy.word_reader.view.WordReadView;
import com.tencent.smtt.sdk.QbSdk;

public class MainActivity extends AppCompatActivity {

    WordReadView wordReadView ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        wordReadView = new WordReadView(MainActivity.this
        );
        findViewById(R.id.tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ViewGroup VV = findViewById(R.id.ll);
                VV.removeAllViews();
                if(WordReadHelper.initFinish()){
                    wordReadView.destroy();
                    WordReadView wordReadView = new WordReadView(MainActivity.this
                    );
                    VV.addView(wordReadView);
                    wordReadView.loadFile("/storage/emulated/0/Download/64090b60538b4915a2c3be7de32ee954.pdf");
                }else{
                    Toast.makeText(MainActivity.this,"加载中",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(wordReadView!=null){
            wordReadView.destroy();
        }
    }
}