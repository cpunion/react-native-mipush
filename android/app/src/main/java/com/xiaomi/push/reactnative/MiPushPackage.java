package com.xiaomi.push.reactnative;

import android.app.Activity;
import android.content.Intent;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.JavaScriptModule;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;

import java.util.ArrayList;
import java.util.List;

public class MiPushPackage implements ReactPackage {
    private MiPushModule mMiPushModule;
    private Intent mIntent;

    public void onIntent(Intent intent) {
        if (mMiPushModule != null) {
            mMiPushModule.onIntent(intent);
        } else {
            mIntent = intent;
        }
    }

    @Override
    public List<NativeModule> createNativeModules(ReactApplicationContext reactApplicationContext) {
        mMiPushModule = new MiPushModule(reactApplicationContext);
        if (mIntent != null) {
            mMiPushModule.onIntent(mIntent);
            mIntent = null;
        }

        List<NativeModule> modules = new ArrayList<>();
        modules.add(mMiPushModule);
        return modules;
    }

    @Override
    public List<Class<? extends JavaScriptModule>> createJSModules() {
        return new ArrayList<>();
    }

    @Override
    public List<ViewManager> createViewManagers(ReactApplicationContext reactApplicationContext) {
        return new ArrayList<>();
    }
}
