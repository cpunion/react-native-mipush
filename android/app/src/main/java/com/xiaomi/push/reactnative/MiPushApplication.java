package com.xiaomi.push.reactnative;

import android.app.ActivityManager;
import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.Context;
import android.util.Log;
import android.os.Process;
import android.os.Bundle;

import com.xiaomi.channel.commonutils.logger.LoggerInterface;
import com.xiaomi.mipush.sdk.Logger;
import com.xiaomi.mipush.sdk.MiPushClient;

import java.util.List;

public class MiPushApplication extends Application {
    static private String TAG = "rnmipush";

    private String appID;
    private String appKey;

    @Override
    public void onCreate() {
        super.onCreate();

        ApplicationInfo appInfo;
        try {
            appInfo = getPackageManager().getApplicationInfo(
                getPackageName(), PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Missing application section in AndroidManifest.xml");
        }

        Bundle metaData = appInfo.metaData;

        Object objAppID = metaData.get("MIPUSH_APP_ID");
        Object objAppKey = metaData.get("MIPUSH_APP_KEY");

        if (objAppID == null || objAppKey == null) {
            throw new IllegalArgumentException("Missing MIPUSH_APP_ID and MIPUSH_APP_KEY in AndroidManifest.xml");
        }

        if (!(objAppID instanceof String) || !(objAppKey instanceof String)) {
            throw new IllegalArgumentException("Invalid MIPUSH_APP_ID and MIPUSH_APP_KEY format, MUST start with '\\ ' to force it to String type");
        }

        appID = (String)objAppID;
        appKey = (String)objAppKey;

        //初始化push推送服务
        if(shouldInit()) {
            MiPushClient.registerPush(this, appID, appKey);
        }
        //打开Log
        LoggerInterface newLogger = new LoggerInterface() {

            @Override
            public void setTag(String tag) {
                // ignore
            }

            @Override
            public void log(String content, Throwable t) {
                Log.d(TAG, content, t);
            }

            @Override
            public void log(String content) {
                Log.d(TAG, content);
            }
        };
        Logger.setLogger(this, newLogger);
    }

    private boolean shouldInit() {
        ActivityManager am = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = getPackageName();
        int myPid = Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }
}
