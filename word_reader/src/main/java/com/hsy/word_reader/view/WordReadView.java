package com.hsy.word_reader.view;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.TbsReaderView;

public class WordReadView extends FrameLayout implements TbsReaderView.ReaderCallback {

    private static final String TAG = "WordReadView";
    private TbsReaderView mTbsView;

    public WordReadView(@NonNull Context context) {
        this(context, null);
    }

    public WordReadView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WordReadView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public WordReadView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        mTbsView = new TbsReaderView(getContext(), this);
        this.addView(mTbsView);
    }

    @Override
    public void onCallBackAction(Integer integer, Object o, Object o1) {

    }


    public void loadFile(@NonNull String filePath) {
        if (filePath == null) {
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putString("filePath", filePath);
        bundle.putString("tempPath", Environment.getExternalStorageDirectory()
                .getPath()+"/dsadsa");
        boolean result = mTbsView.preOpen(parseFileType(filePath), false);
        if (result) {
            mTbsView.openFile(bundle);
        } else {
            Log.e(TAG, "Type is not support");
        }
    }

    public void destroy() {
        if (mTbsView != null) {
            mTbsView.onStop();
        }
    }

    private String parseFileType(String path) {
        if (TextUtils.isEmpty(path)) {
            return "";
        } else {
            return path.substring(path.lastIndexOf(".")+1);
        }
    }


}
