package com.hsy.tbsuserdemo;

import android.app.Application;

import com.hsy.word_reader.WordReadHelper;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        WordReadHelper.init(this);
    }
}
