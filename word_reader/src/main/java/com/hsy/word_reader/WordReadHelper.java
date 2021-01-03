package com.hsy.word_reader;

import android.content.Context;
import android.util.Log;

import com.tencent.smtt.export.external.TbsCoreSettings;
import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.TbsDownloader;
import com.tencent.smtt.sdk.TbsListener;
import com.tencent.smtt.sdk.TbsReaderView;

import java.lang.annotation.Target;
import java.util.HashMap;

public class WordReadHelper {

    public static final String TAG = "WordReadHelper";
    private static boolean mOnlyWifi = false;
    private static boolean mInit = false;
    private static Context mContext;

    private WordReadHelper() {
    }


    public void setOnlyWifiDownload(boolean onlyWifi) {
        mOnlyWifi = onlyWifi;
    }


    public static void init(Context context) {
        mContext = context;
        resetSdk(context);
        QbSdk.setTbsListener(new TbsListener() {
            @Override
            public void onDownloadFinish(int i) {
                Log.d(TAG, "load" + i);
                //tbs内核下载完成回调
            }

            @Override
            public void onInstallFinish(int i) {
                mInit = true;
                Log.d(TAG, "finish" + i);
                //内核安装完成回调，
            }

            @Override
            public void onDownloadProgress(int i) {
                //下载进度监听
                Log.d(TAG, "progress" + i);
            }
        });
        QbSdk.initX5Environment(context, new QbSdk.PreInitCallback() {
            @Override
            public void onCoreInitFinished() {
                Log.e(TAG, "加载内核完成");
                //x5内核初始化完成回调接口，此接口回调并表示已经加载起来了x5，有可能特殊情况下x5内核加载失败，切换到系统内核。
            }

            @Override
            public void onViewInitFinished(boolean b) {
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                mInit = b;
                Log.e(TAG, "加载内核是否成功:" + b);
                if (!TbsDownloader.needDownload(context, false) && !TbsDownloader.isDownloading()) {
                    initFinish(context);
                }
            }
        });
    }

    private static void resetSdk(Context context) {
        // 在调用TBS初始化、创建WebView之前进行如下配置
        HashMap map = new HashMap();
        map.put(TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER, true);
        map.put(TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE, true);
        QbSdk.initTbsSettings(map);
        QbSdk.setDownloadWithoutWifi(!mOnlyWifi);
        QbSdk.disableAutoCreateX5Webview();
        //强制使用系统内核
        //QbSdk.forceSysWebView();
    }

    public static boolean initFinish(Context context) {
        if (!mInit && !TbsDownloader.isDownloading()) {
            QbSdk.reset(context);
            resetSdk(context);
            TbsDownloader.startDownload(context);
        }
        return mInit;
    }


}
