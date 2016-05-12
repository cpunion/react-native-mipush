package com.xiaomi.push.reactnative;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import com.xiaomi.mipush.sdk.PushMessageHelper;
import com.xiaomi.mipush.sdk.MiPushMessage;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import java.util.HashSet;
import java.util.HashMap;
import java.util.Set;
import java.util.Arrays;

/**
 * Created by lijie on 16/5/11.
 */
public class MiPushModule extends ReactContextBaseJavaModule {
    private Intent mIntent;

    public MiPushModule(ReactApplicationContext context, final Activity activity) {
        super(context);

        final ReactApplicationContext ctx = context;

        final BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle bundle = intent.getExtras();
                WritableMap params = Arguments.fromBundle(bundle);

                sendEvent(params);
            }
        };

        final LocalBroadcastManager mgr = LocalBroadcastManager.getInstance(activity);

        LifecycleEventListener listener = new LifecycleEventListener() {
            public void onHostResume() {
                mgr.registerReceiver(receiver, new IntentFilter("xiaomipush"));
            }

            public void onHostPause() {
                try {
                    mgr.unregisterReceiver(receiver);
                } catch (java.lang.IllegalArgumentException e) {
                    Log.e(MiPushModule.class.getName(), "receiver not registered", e);
                }
            }

            public void onHostDestroy() {
                try {
                    mgr.unregisterReceiver(receiver);
                } catch (java.lang.IllegalArgumentException e) {
                    Log.e(MiPushModule.class.getName(), "receiver not registered", e);
                }
            }
        };

        context.addLifecycleEventListener(listener);
    }

    public String getName() {
        return "MiPush";
    }

    @ReactMethod
    public void getInitialMessage(Callback callback) {
        WritableMap params = getDataOfIntent(mIntent);
        // Add missing NOTIFICATION_MESSAGE_CLICKED message in PushMessageReceiver
        if (params != null) {
            params.putString("type", "NOTIFICATION_MESSAGE_CLICKED");
        }
        callback.invoke(params);
    }

    public void onIntent(Intent intent) {
        final ReactApplicationContext ctx = getReactApplicationContext();

        if (!ctx.hasActiveCatalystInstance()) {
            mIntent = intent;
        } else {
            processIntent(intent);
        }

    }

    private WritableMap getDataOfIntent(Intent intent) {
        if (intent == null) {
            return null;
        }

        final Set<String> categories = new HashSet<String>(Arrays.asList(Intent.CATEGORY_LAUNCHER));
        if (intent.getAction() != Intent.ACTION_MAIN || !categories.equals(intent.getCategories())) {
            return null;
        }

        MiPushMessage message = (MiPushMessage)intent.getSerializableExtra(PushMessageHelper.KEY_MESSAGE);
        if (message == null) {
            return null;
        }

        Bundle bundle = message.toBundle();
        HashMap<String, String> extra = (HashMap<String, String>)bundle.getSerializable("extra");
        if (extra != null) {
            Bundle extraBundle = new Bundle();
            for (String key: extra.keySet()) {
                String value = extra.get(key);
                extraBundle.putString(key, value);
            }
            bundle.putBundle("extra", extraBundle);
        }

        WritableMap params = Arguments.fromBundle(bundle);
        return params;
    }

    private void processIntent(Intent intent) {
        WritableMap params = getDataOfIntent(intent);
        sendEvent(params);
    }

    private void sendEvent(WritableMap params) {
        final ReactApplicationContext ctx = getReactApplicationContext();

        if (ctx.hasActiveCatalystInstance()) {
            ctx
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit("xiaomipush", params);
        }
    }
}
