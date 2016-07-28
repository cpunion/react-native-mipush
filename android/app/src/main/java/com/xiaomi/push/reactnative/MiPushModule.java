package com.xiaomi.push.reactnative;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Process;
import android.util.Log;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import com.xiaomi.mipush.sdk.PushMessageHelper;
import com.xiaomi.mipush.sdk.MiPushMessage;
import com.xiaomi.mipush.sdk.MiPushClient;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import java.util.HashSet;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Arrays;

/**
 * Created by lijie on 16/5/11.
 */
public class MiPushModule extends ReactContextBaseJavaModule {
    private Intent mIntent;

    public MiPushModule(ReactApplicationContext context) {
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

        final LocalBroadcastManager mgr = LocalBroadcastManager.getInstance(context);

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
    public void getInitialMessage(Promise promise) {
        WritableMap params = MiPushHelper.getDataOfIntent(mIntent);
        // Add missing NOTIFICATION_MESSAGE_CLICKED message in PushMessageReceiver
        if (params != null) {
            params.putString("type", "NOTIFICATION_MESSAGE_CLICKED");
        }
        promise.resolve(params);
    }

    @ReactMethod
    public void setAlias(String alias, String category) {
        MiPushClient.setAlias(getReactApplicationContext(), alias, category);
    }

    @ReactMethod
    public void unsetAlias(String alias, String category) {
        MiPushClient.unsetAlias(getReactApplicationContext(), alias, category);
    }

    @ReactMethod
    public void setUserAccount(String userAccount, String category) {
        MiPushClient.setUserAccount(getReactApplicationContext(), userAccount, category);
    }

    @ReactMethod
    public void unsetUserAccount(String userAccount, String category) {
        MiPushClient.unsetUserAccount(getReactApplicationContext(), userAccount, category);
    }

    @ReactMethod
    public void subscribe(String topic, String category) {
        MiPushClient.subscribe(getReactApplicationContext(), topic, category);
    }

    @ReactMethod
    public void unsubscribe(String topic, String category) {
        MiPushClient.unsubscribe(getReactApplicationContext(), topic, category);
    }

    @ReactMethod
    public void pausePush(String category) {
        MiPushClient.pausePush(getReactApplicationContext(), category);
    }

    @ReactMethod
    public void resumePush(String category) {
        MiPushClient.resumePush(getReactApplicationContext(), category);
    }

    @ReactMethod
    public void setAcceptTime(int startHour, int startMin, int endHour, int endMin, String category) {
        MiPushClient.setAcceptTime(getReactApplicationContext(), startHour, startMin, endHour, endMin, category);
    }

    @ReactMethod
    public void getAllAlias(Promise promise) {
        List<String> allAlias = MiPushClient.getAllAlias(getReactApplicationContext());
        String[] allAliasArray = allAlias.toArray(new String[allAlias.size()]);
        promise.resolve(Arguments.fromArray(allAliasArray));
    }

    @ReactMethod
    public void getAllTopics(Promise promise) {
        List<String> allTopics = MiPushClient.getAllAlias(getReactApplicationContext());
        String[] allTopicsArray = allTopics.toArray(new String[allTopics.size()]);
        promise.resolve(Arguments.fromArray(allTopicsArray));
    }

    @ReactMethod
    public void reportMessageClicked(String msgId) {
        MiPushClient.reportMessageClicked(getReactApplicationContext(), msgId);
    }

    @ReactMethod
    public void clearNotification(int notifyId) {
        MiPushClient.clearNotification(getReactApplicationContext(), notifyId);
    }

    @ReactMethod
    public void clearAllNotification() {
        MiPushClient.clearNotification(getReactApplicationContext());
    }

    @ReactMethod
    public void setLocalNotificationType(int notifyType) {
        MiPushClient.setLocalNotificationType(getReactApplicationContext(), notifyType);
    }

    @ReactMethod
    public void clearLocalNotificationType() {
        MiPushClient.clearLocalNotificationType(getReactApplicationContext());
    }

    @ReactMethod
    public void getRegId(Promise promise) {
        MiPushClient.getRegId(getReactApplicationContext());
    }

    public void onIntent(Intent intent) {
        final ReactApplicationContext ctx = getReactApplicationContext();

        if (!ctx.hasActiveCatalystInstance()) {
            mIntent = intent;
        } else {
            processIntent(intent);
        }

    }

    private void processIntent(Intent intent) {
        WritableMap params = MiPushHelper.getDataOfIntent(intent);
        if (params != null) {
            params.putString("type", "NOTIFICATION_MESSAGE_CLICKED");
        }
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
