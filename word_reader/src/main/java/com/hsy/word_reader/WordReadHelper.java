package com.hsy.word_reader;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.util.Log;

import com.tencent.smtt.export.external.TbsCoreSettings;
import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.TbsDownloader;
import com.tencent.smtt.sdk.TbsListener;

import java.util.HashMap;

public class WordReadHelper {

    public static final String TAG = "WordReadHelper";
    private static boolean mOnlyWifi = true;
    private static boolean mInit = false;
    private static Context mContext;
    private static NetworkCallbackImpl networkCallback;

    private WordReadHelper() {
    }

    public void setOnlyWifiDownload(boolean onlyWifi) {
        mOnlyWifi = onlyWifi;
    }

    public static void init(Context context) {
        if (context == null) {
            throw new NullPointerException("init fail");
        }
        mContext = context;
        QbSdk.reset(mContext);
        resetSdk(context);
        QbSdk.setTbsListener(new TbsListener() {
            @Override
            public void onDownloadFinish(int i) {
                //成功时i为100
                if (i != 100) {
                    //此处存在一种情况，第一次启动app，init不会自动回调，此处额外加一层，判断网络监听器是否为空并作出处理
                    initNetWorkCallBack();
                }
                Log.d(TAG, "load" + i);
                //tbs内核下载完成回调
            }

            @Override
            public void onInstallFinish(int i) {
                mInit = true;
                if (networkCallback != null) {
                    ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                    connMgr.unregisterNetworkCallback(networkCallback);
                }
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
                //该方法在第一次安装app打开不会回调
                mInit = b;
                Log.e(TAG, "加载内核是否成功:" + b);
                if (!mInit) {
                    initNetWorkCallBack();
                }

                if (!mInit && TbsDownloader.needDownload(context, false) && !TbsDownloader.isDownloading()) {
                    initFinish();
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

    public static boolean initFinish() {
        if (!mInit && !TbsDownloader.isDownloading()) {
            QbSdk.reset(mContext);
            resetSdk(mContext);
            if (!mOnlyWifi || isWifi(mContext))
                TbsDownloader.startDownload(mContext);
        }
        return mInit;
    }

    private static boolean isWifi(Context mContext) {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info != null
                && info.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }


    public static void initNetWorkCallBack() {
        if (networkCallback == null) {
            networkCallback = new NetworkCallbackImpl();
            NetworkRequest.Builder builder = new NetworkRequest.Builder();
            NetworkRequest request = builder.build();
            ConnectivityManager connMgr = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connMgr != null) {
                connMgr.registerNetworkCallback(request, networkCallback);
            }
        }
    }


    public static class NetworkCallbackImpl extends ConnectivityManager.NetworkCallback {

        @Override
        public void onAvailable(Network network) {
            super.onAvailable(network);
            WordReadHelper.initFinish();
            Log.d(TAG, "onAvailable: 网络已连接");
        }

        @Override
        public void onLost(Network network) {
            super.onLost(network);
            Log.d(TAG, "onLost: 网络已断开");
        }
    }

}
