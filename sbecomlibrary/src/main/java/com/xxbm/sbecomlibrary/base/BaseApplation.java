package com.xxbm.sbecomlibrary.base;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.xxbm.sbecomlibrary.utils.NLog;


/**
 * Created by 吕 on 2017/10/26.
 */

public class BaseApplation extends Application {
    private static BaseApplation baseApp = null;
    private static Activity sActivity = null;
    private AppComponent mAppComponent;

    static {

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        NLog.setDebug(true);
        baseApp = this;
        mAppComponent = DaggerAppComponent.create();

    }

    public static BaseApplation getBaseApp() {
        return baseApp;
    }

    public AppComponent getAppComponent(){
        return mAppComponent;
    }

    public static String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }

    public static Activity getActivity(){
        return sActivity;
    }

}
